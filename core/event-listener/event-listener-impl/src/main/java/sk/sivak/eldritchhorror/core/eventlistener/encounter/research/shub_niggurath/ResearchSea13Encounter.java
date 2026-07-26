package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchSea13Encounter extends AbstractShubNiggurathResearchEncounter {

    public ResearchSea13Encounter() {
        super(13, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new InfoFlavorTemplate(getTextBuilder(), () -> {
            ServicePlatform.get().getTokenService().loseSanity(1);
        }, () -> {
            if (ServicePlatform.get().getConditionsDeck().hasCondition(getActiveInvestigatorId(), ConditionId.DARK_PACT)) {
                TypewriterUtils.confirmInfos(
                        getTextBuilder().withFail().withInfo(1).build(),
                        getTextBuilder().withFail().withInfo(2).build()).subscribe(() -> {
                    ServicePlatform.get().getEncounterService().hold();
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    selectSingleMonster();
                    ServicePlatform.get().getTokenService().loseHealth(3);
                    ServicePlatform.get().getEncounterService().release();
                });
            } else {
                TypewriterUtils.confirmInfos(
                        getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
                    ServicePlatform.get().getEncounterService().hold();
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    ServicePlatform.get().getEncounterService().release();
                });
            }
        }).execute();
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
        ServicePlatform.get().getMonsterService().dealDamageToMonster(monsterInfo, 3);
        ServicePlatform.get().getService().release();
    }
}
