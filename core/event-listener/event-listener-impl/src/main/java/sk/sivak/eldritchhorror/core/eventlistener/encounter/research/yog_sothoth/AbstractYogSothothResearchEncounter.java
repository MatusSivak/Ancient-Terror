package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.research.AbstractResearchEncounter;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public abstract class AbstractYogSothothResearchEncounter extends AbstractResearchEncounter {

    public AbstractYogSothothResearchEncounter(int page, LocationType locationType) {
        super(page, locationType, AncientOneId.YOG_SOTHOTH);
    }

    protected void chooseOneFail(Runnable option1, Runnable option2) {

        ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.CHOOSE_ONE);
        ServicePlatform.get().getEncounterService().displayButtons(
                getTextBuilder().withFail().withOption(1).build(),
                getTextBuilder().withFail().withOption(2).build()
        ).subscribe(x -> {
            ServicePlatform.get().getEncounterService().hold();
            if (x == 0) {
                option1.run();
            } else {
                option2.run();
            }
            ServicePlatform.get().getEncounterService().release();
        });
    }
}
