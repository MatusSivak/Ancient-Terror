package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class ResearchSea14Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchSea14Encounter() {
        super(14, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new InfoFlavorTemplate(getTextBuilder(), () -> {
            ServicePlatform.get().getDoomOmenService().advanceDoom();
        }, () -> {
            ServicePlatform.get().getTokenService().canSpend(0,0,0,2).subscribe(canSpend -> {
                if (!canSpend.hasEnough()) {
                    TypewriterUtils.confirmInfos("Not enough Sanity to listen until the end.").subscribe(() -> {
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    });
                } else {
                    TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), null, () -> {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().hideTypewriterPaper();
                        ServicePlatform.get().getTokenService().spend(0, 0, 0, 2).subscribe(spend -> {
                            ServicePlatform.get().getService().hold();
                            if (!spend.hasEnough()) {
                                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                                TypewriterUtils.confirmInfos("Not enough Sanity to listen until the end.").subscribe(() -> {
                                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                                });
                                return;
                            }
                            spend.pay();
                            ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                            ServicePlatform.get().getEncounterService().typeFlavor(" \n" + getTextBuilder().withPass().withFlavor().build());
                            TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
                                ServicePlatform.get().getService().hold();
                                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                                gainThisClue();
                                ServicePlatform.get().getService().release();
                            });
                            ServicePlatform.get().getService().release();
                        });

                        ServicePlatform.get().getService().release();
                    });
                }
            });

        }).execute();
    }

}
