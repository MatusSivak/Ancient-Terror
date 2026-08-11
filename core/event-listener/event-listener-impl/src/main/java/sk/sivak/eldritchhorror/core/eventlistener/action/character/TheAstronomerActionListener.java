package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.model.CluePoolRead;
import sk.sivak.eldritchhorror.core.model.GateStackRead;

import java.util.List;

/**
 * @author msivak
 */
public class TheAstronomerActionListener extends AbstractActionPhaseListener<TheAstronomerActionListener.TheAstronomerAction> {

    private static final Logger logger = LogManager.getLogger(TheAstronomerActionListener.class);

    public TheAstronomerActionListener() {
        name = "Discard\nMonster";
    }

    private boolean enoughClues = false;

    public void registerInitActionButtonListener() {
        ServicePlatform.get().getEventQueue().addDirectEventListener(
                new InitActionButtonListener(), DirectEvent.INIT_ACTION_BUTTON);
    }

    private class InitActionButtonListener extends EventListenerImpl<Void> {
        @Override
        public void onNotify(Void eventData) {
            init();
        }

        @Override
        public Class<Void> getDataClass() {
            return Void.class;
        }

        @Override
        public String toString() {
            return "TheAstronomer - InitActionButton";
        }
    }

    private void init() {
        if (!isVisible()) {
            return;
        }
        ServicePlatform.get().getTokenService().canSpend(2,0,0,0).subscribe(spendData -> {
            enoughClues = spendData.hasEnough();
        });
    }

    @Override
    protected String getAdditionalInfo() {
        if (isDisabled()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        List<MonsterInfo> monstersAtGates = getMonstersAtGates();
        for (MonsterInfo monsterAtLocation : monstersAtGates) {
            sb
                    .append(monsterAtLocation.getName())
                    .append(" (")
                    .append(monsterAtLocation.getCurrentHealth())
                    .append(")")
                    .append("\n");
        }
        return sb.toString();
    }

    private List<MonsterInfo> getMonstersAtGates() {
        GateStackRead gateStackRead = ServicePlatform.get().getGateStackRead();
        List<MonsterInfo> monsters = ServicePlatform.get().getMonsterCup().getMonsters();
        monsters = Stream.collectToList(monsters, monsterInfo ->
                gateStackRead.isGateAtLocation(monsterInfo.getCurrentLocation()) &&
                        !monsterInfo.isEpic());
        return monsters;
    }

    @Override
    protected String getGeneralDescription() {
        return "Spend 2 Clues\n" +
                "to discard 1 Monster\n" +
                "on a space containing a Gate.\n";
    }

    @Override
    protected TheAstronomerActionListener.TheAstronomerAction createAction() {
        return new TheAstronomerActionListener.TheAstronomerAction();
    }

    @Override
    protected boolean isVisible() {
        return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_ASTRONOMER;
    }

    @Override
    protected boolean isDisabled() {
        boolean noMonsters = getMonstersAtGates().isEmpty();
        if (noMonsters) {
            disabledReason = "No Monsters at Gates";
            return true;
        }
        if (!enoughClues) {
            disabledReason = "You do not have enough Clues.";
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
        return "investigator/ICON_THE_ASTRONOMER.png";
    }

    @Override
    protected float getScaleDownPercentage() {
        return 0.75f;
    }

    @Override
    protected boolean isNotRecommended() {
        return false;
    }

    protected class TheAstronomerAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            ServicePlatform.get().getTokenService().spend(2,0,0,0).subscribe(this::onSpend);
        }

        private void onSpend(SpendData spendData) {
            if (!spendData.hasEnough()) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getBasicActionService().addFreeAction();
                ServicePlatform.get().getBasicActionService().removePerformedAction(this);
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
                return;
            }
            ServicePlatform.get().getService().hold();
            spendData.pay();
            selectMonsterToDiscard();
            ServicePlatform.get().getService().release();
        }

        private void selectMonsterToDiscard() {
            List<MonsterInfo> monsters = getMonstersAtGates();

            SelectMonsterData selectMonsterData = new SelectMonsterData();
            selectMonsterData.setTitleText("Select Monster");
            selectMonsterData.setHideText("Display Monsters?");
            selectMonsterData.setAvailableMonsters(monsters);

            ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData).subscribe(this::onMonsterSelect);
        }

        private void onMonsterSelect(MonsterInfo monsterInfo) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().discardMonster(monsterInfo);
            if (ServicePlatform.get().getPerformedActions().canPerformAction(InvestigatorId.THE_ASTRONOMER)) {
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            }
            ServicePlatform.get().getService().convertToNull();
            ServicePlatform.get().getService().release();
        }

    }
}
