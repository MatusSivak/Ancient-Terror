package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchWilderness15Encounter extends AbstractShubNiggurathResearchEncounter{

    public ResearchWilderness15Encounter() {
        super(15, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), null, this::onYes);

    }

    private void onYes() {
        if (ServicePlatform.get().getConditionsDeck().canGetConditionId(getActiveInvestigatorId(), ConditionId.DARK_PACT)) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            ServicePlatform.get().getService().hideBackground();
            ServicePlatform.get().getGameService().gainCondition(ConditionId.DARK_PACT);
            gainThisClue();
            ServicePlatform.get().getService().showResearchBackground(getLocationType());
            ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
            TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                selectSingleMonster();
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
        } else {
            TypewriterUtils.confirmInfos("Can't get Dark Pact Condition").subscribe(() -> {
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            });
        }
    }

    private void selectSingleMonster() {
        SelectMonsterData selectMonsterData = new SelectMonsterData();
        List<MonsterInfo> monsters = ServicePlatform.get().getMonsterCup().getMonsters();
        selectMonsterData.setAvailableMonsters(monsters);
        ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData).subscribe(this::onMonsterSelect);
    }

    private void onMonsterSelect(MonsterInfo monsterInfo) {
        if (monsterInfo == null) {
            return;
        }
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().moveCameraToLocation(monsterInfo.getCurrentLocation());
        ServicePlatform.get().getMonsterService().dealDamageToMonster(monsterInfo, 2);
        ServicePlatform.get().getService().release();
    }
}
