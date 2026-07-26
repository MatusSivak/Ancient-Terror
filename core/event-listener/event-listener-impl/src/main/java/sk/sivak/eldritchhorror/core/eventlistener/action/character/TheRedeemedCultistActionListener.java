package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventtype.data.CollectAvailableActionsData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.data.test.TestData.JUST_ONE;

/**
 * @author msivak
 */
public class TheRedeemedCultistActionListener extends AbstractActionPhaseListener<TheRedeemedCultistActionListener.TheRedeemedCultistAction> {

    private static final Logger logger = LogManager.getLogger(TheRedeemedCultistActionListener.class);

    public TheRedeemedCultistActionListener() {
        name = "Discard\nMonsters";
    }

    @Override
    protected String getAdditionalInfo() {
        return null;
    }

    @Override
    protected String getGeneralDescription() {
        return "If there is a Cultist Monster\n" +
                "on your space, discard all\n" +
                "Monsters on your space\n" +
                "or move the Cultist Monster\n" +
                "to any other space.";
    }

    @Override
    protected TheRedeemedCultistAction createAction() {
        return new TheRedeemedCultistAction();
    }

    @Override
    protected boolean isVisible() {
        return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_REDEEMED_CULTIST;
    }

    @Override
    protected boolean isDisabled() {
        LocationId currentLocationId = getActiveInvestigator().getCurrentLocationId();
        if (!ServicePlatform.get().getMonsterCup().containsMonster(currentLocationId, NonEpicMonsterId.CULTIST)) {
            disabledReason = "No Cultist at this place.";
            return true;
        }
        return false;
    }

    @Override
    protected ActionButtonData.ActionButtonId getActionButtonId() {
        return ActionButtonData.ActionButtonId.INVESTIGATOR;
    }

    @Override
    protected String getTexturePath() {
        return "investigator/THE_REDEEMED_CULTIST.png";
    }

    @Override
    protected boolean getNeedsScaleDown() {
        return true;
    }

    @Override
    protected boolean isNotRecommended() {
        return false;
    }

    protected class TheRedeemedCultistAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            Question<Boolean> question = new Question<>();
            question.setPortraitBeforeTitle(InvestigatorId.THE_REDEEMED_CULTIST);
            question.setTitle("Move Cultist?");
            question.setOptions(Arrays.asList(
                    new Question.Option<>("No", false, 0x800000ff),
                    new Question.Option<>("Yes", true, 0x008000ff)
            ));
            ServicePlatform.get().getGameService().ask(question).subscribe(this::onMoveCultistAnswer);
        }

        private void onMoveCultistAnswer(Answer<Boolean, Object> moveCultistAnswer) {
            if (moveCultistAnswer.getResponseData() == Boolean.TRUE) {
                ServicePlatform.get().getGameService().selectLocation(new SelectLocationData(null)).subscribe(locationId -> {
                    ServicePlatform.get().getService().hold();
                    LocationId currentLocationId = getActiveInvestigator().getCurrentLocationId();
                    List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(currentLocationId);
                    MonsterInfo cultist = Stream.findFirstOrException(monstersAtLocation,
                            monsterInfo -> monsterInfo.getMonsterId() == NonEpicMonsterId.CULTIST);
                    ServicePlatform.get().getMonsterService().moveMonster(cultist, locationId);
                    ServicePlatform.get().getService().convertToNull();
                    ServicePlatform.get().getService().release();
                });
            } else {
                Question<Boolean> question = new Question<>();
                question.setPortraitBeforeTitle(InvestigatorId.THE_REDEEMED_CULTIST);
                question.setTitle("Discard all Monsters?");
                question.setOptions(Arrays.asList(
                        new Question.Option<>("No", false, 0x800000ff),
                        new Question.Option<>("Yes", true, 0x008000ff)
                ));
                ServicePlatform.get().getGameService().ask(question).subscribe(this::onDiscardAllMonstersAnswer);
            }
        }

        private void onDiscardAllMonstersAnswer(Answer<Boolean, Object> discardAllMonstersAnswer) {
            if (discardAllMonstersAnswer.getResponseData() == Boolean.TRUE) {
                LocationId currentLocationId = getActiveInvestigator().getCurrentLocationId();
                List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(currentLocationId);
                monstersAtLocation = Stream.collectToList(monstersAtLocation, monsterInfo -> !monsterInfo.isEpic());
                ServicePlatform.get().getService().hold();
                for (MonsterInfo monsterInfo : monstersAtLocation) {
                    ServicePlatform.get().getMonsterService().discardMonster(monsterInfo);
                }
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
            } else {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getBasicActionService().addFreeAction();
                ServicePlatform.get().getBasicActionService().removePerformedAction(this);
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
            }

        }
    }
}
