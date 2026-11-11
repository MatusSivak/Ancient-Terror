package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.EncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.research.AbstractResearchEncounter;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public abstract class AbstractAzathothResearchEncounter extends AbstractResearchEncounter {

    protected static final Runnable NOTHING = () -> {};

    public AbstractAzathothResearchEncounter(int page, LocationType locationType) {
        super(page, locationType, AncientOneId.AZATHOTH);
    }

    /** Narrates an accepted (pass) or declined (fail) choice, then resolves its effect once confirmed. */
    protected void narrateOutcome(boolean accepted, Runnable effect) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().typeFlavor(" \n" + outcome(accepted).withFlavor().build());
        TypewriterUtils.confirmInfos(outcome(accepted).withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            effect.run();
            ServicePlatform.get().getService().release();
        });
        ServicePlatform.get().getService().release();
    }

    private EncounterTextBuilder outcome(boolean accepted) {
        return accepted ? getTextBuilder().withPass() : getTextBuilder().withFail();
    }
}
