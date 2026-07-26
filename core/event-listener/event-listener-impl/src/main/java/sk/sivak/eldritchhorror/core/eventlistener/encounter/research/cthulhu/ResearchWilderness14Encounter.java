package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;

public class ResearchWilderness14Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchWilderness14Encounter() {
        super(14, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new InfoFlavorTemplate(getTextBuilder(), () -> {
            ServicePlatform.get().getTokenService().loseSanity(1);
        }, new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1, () -> {
            discardOneVortex();
        }).withResearchFlavor()
                .withoutPassFlavor()
        ).execute();
    }
}
