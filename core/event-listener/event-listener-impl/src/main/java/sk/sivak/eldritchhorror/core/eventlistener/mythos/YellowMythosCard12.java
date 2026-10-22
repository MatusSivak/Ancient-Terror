package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.ArrayList;
import java.util.List;

/**
 * The World Fights Back: each investigator may recover 2 Health, recover 2 Sanity,
 * or discard 1 Monster from his space.
 */
public class YellowMythosCard12 implements MythosCardEventListener {

    private static final int HEALTH = 1;
    private static final int SANITY = 2;
    private static final int MONSTER = 3;

    @Override
    public void execute() {
        List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        ServicePlatform.get().getService().hold();
        for (InvestigatorRead investigator : investigators) {
            InvestigatorId investigatorId = investigator.getInfo().getInvestigatorId();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            // Monsters are evaluated lazily, a previous investigator may have discarded one on the same space.
            ServicePlatform.get().getService().addEventCommand(in -> { askInvestigator(investigatorId); });
        }
        ServicePlatform.get().getService().release();
    }

    private void askInvestigator(InvestigatorId investigatorId) {
        List<MonsterInfo> monsters = getMonstersOnSpace(investigatorId);
        List<Question.Option<Integer>> options = new ArrayList<>();
        options.add(new Question.Option<>("Recover 2 Health", HEALTH));
        options.add(new Question.Option<>("Recover 2 Sanity", SANITY));
        if (!monsters.isEmpty()) {
            options.add(new Question.Option<>("Discard Monster", MONSTER));
        }
        Question<Integer> question = new Question<>();
        question.setTitle("The World Fights Back");
        question.setOptions(options);
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getGameService().ask(question).map(Answer::getResponseData).subscribe(option -> {
            ServicePlatform.get().getService().hold();
            switch (option) {
                case HEALTH:
                    ServicePlatform.get().getTokenService().gainHealth(2);
                    break;
                case SANITY:
                    ServicePlatform.get().getTokenService().gainSanity(2);
                    break;
                case MONSTER:
                    SelectMonsterData selectMonsterData = new SelectMonsterData();
                    selectMonsterData.setAvailableMonsters(monsters);
                    selectMonsterData.setTitleText("Select Monster to discard");
                    selectMonsterData.setHideText("Display Monsters?");
                    ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData)
                            .subscribe(monsterInfo -> ServicePlatform.get().getMonsterService().discardMonster(monsterInfo));
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            ServicePlatform.get().getService().release();
        });
        ServicePlatform.get().getService().release();
    }

    private List<MonsterInfo> getMonstersOnSpace(InvestigatorId investigatorId) {
        LocationId locationId = ServicePlatform.get().getInvestigators().getInvestigator(investigatorId).getCurrentLocationId();
        if (locationId == null) {
            return new ArrayList<>();
        }
        return Stream.collectToList(ServicePlatform.get().getMonsterCup().getMonstersAtLocation(locationId),
                monsterInfo -> !monsterInfo.isEpic());
    }
}
