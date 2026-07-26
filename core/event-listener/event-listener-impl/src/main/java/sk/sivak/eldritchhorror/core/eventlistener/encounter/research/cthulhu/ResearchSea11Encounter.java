package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class ResearchSea11Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchSea11Encounter() {
        super(11, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1, () -> {
            ServicePlatform.get().getService().hold();
            if (ServicePlatform.get().getVortexes().isAtLocation(getLocationId())) {
                ServicePlatform.get().getEncounterService().hideTypewriterPaper();
                gainThisClue();
                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                ServicePlatform.get().getEncounterService().typeFlavor(" \n"+getTextBuilder().withPass().withFlavor(2).build());
                TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo(2).build()).subscribe(() -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    ServicePlatform.get().getMonsterService().ambush(NonEpicMonsterId.DEEP_ONE).subscribe();
                    ServicePlatform.get().getService().release();
                });
            } else {
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                gainThisClue();
            }
            ServicePlatform.get().getService().release();
        }){

        }.withoutHidingPaperBeforePass().withResearchFlavor().execute();
    }
}
