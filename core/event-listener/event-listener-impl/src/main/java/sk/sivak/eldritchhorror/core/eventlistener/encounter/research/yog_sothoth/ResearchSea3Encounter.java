package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class ResearchSea3Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchSea3Encounter() {
        super(3, LocationType.SEA);
    }

    @Override
    protected void execute() {
        boolean anyHoundOfTindalos = Stream.anyMatch(ServicePlatform.get().getMonsterCup().getMonsters(),
                monsterInfo -> monsterInfo.getMonsterId() == NonEpicMonsterId.HOUND_OF_TINDALOS);

        String otherEffect;
        if (anyHoundOfTindalos) {
            otherEffect = getTextBuilder().withInfo(2).build();
        } else {
            otherEffect = getTextBuilder().withInfo(3).build();
        }

        TypewriterUtils.confirmInfos(getTextBuilder().withInfo(1).build(), otherEffect).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            gainThisClue();
            if (anyHoundOfTindalos) {
                MonsterInfo houndOfTindalos = Stream.findFirstOrException(ServicePlatform.get().getMonsterCup().getMonsters(),
                        monsterInfo -> monsterInfo.getMonsterId() == NonEpicMonsterId.HOUND_OF_TINDALOS);
                ServicePlatform.get().getMonsterService().moveMonster(houndOfTindalos, ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId());
                ServicePlatform.get().getTestService().combat(houndOfTindalos);
            } else {
                ServicePlatform.get().getTokenService().loseSanity(1);
            }
            ServicePlatform.get().getService().release();
        });
    }



}
