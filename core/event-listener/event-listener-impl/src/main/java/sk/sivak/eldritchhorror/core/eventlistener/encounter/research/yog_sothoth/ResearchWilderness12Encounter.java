package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class ResearchWilderness12Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchWilderness12Encounter() {
        super(12, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.LORE, +1,
                () -> {
                    TypewriterUtils.noYesQuestion(getTextBuilder().withPass().withQuestion().build(), () -> {
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    }, () -> {
                        ServicePlatform.get().getService().hold(); // hold-release-1
                        ServicePlatform.get().getEncounterService().hideTypewriterPaper();
                        ServicePlatform.get().getService().hideBackground();

                        ServicePlatform.get().getGameService().spawnGates(1,1).subscribe(gateSpawnData -> {
                                    ServicePlatform.get().getService().hold(); // hold-release-2
                                    ServicePlatform.get().getService().showResearchBackground(LocationType.WILDERNESS);
                                    ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                                    ServicePlatform.get().getEncounterService().typeFlavor(" \n"+getTextBuilder().withPass().withFlavor(2).build());
                                    TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
                                        ServicePlatform.get().getService().hold();
                                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                                        gainThisClue();
                                        ServicePlatform.get().getService().release();
                                    });
                                    ServicePlatform.get().getService().release();
                                }
                        );

                        ServicePlatform.get().getService().release(); // hold-release-1
                    });

                }
        ).withoutHidingPaperBeforePass().withoutPassInfo().withResearchFlavor().execute();
    }
}
