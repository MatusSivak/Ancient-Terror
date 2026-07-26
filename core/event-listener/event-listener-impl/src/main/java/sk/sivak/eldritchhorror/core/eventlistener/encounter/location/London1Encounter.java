package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.general.AbstractGeneralEncounter;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.GainConditionTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class London1Encounter extends AbstractLocationEncounter {

    public London1Encounter() {
        super(1, LocationEncounter.LocationEncounterType.LONDON);
    }

    @Override
    protected void execute() {
        new GainConditionTemplate(getTextBuilder(), ConditionId.DEBT, () -> {
            ServicePlatform.get().getTokenService().gainClueFromPool();
            ServicePlatform.get().getTokenService().gainClueFromPool();
        }).execute();
    }
}
