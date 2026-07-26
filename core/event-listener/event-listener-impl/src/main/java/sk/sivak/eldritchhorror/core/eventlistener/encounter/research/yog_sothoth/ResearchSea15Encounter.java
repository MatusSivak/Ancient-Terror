package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class ResearchSea15Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchSea15Encounter() {
        super(15, LocationType.SEA);
    }

    @Override
    protected void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), () -> {
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        }, () -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();

            ServicePlatform.get().getDoomOmenService().advanceDoom();

            gainThisClue();


            ServicePlatform.get().getService().release();
        });
    }
}
