package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchCity10Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchCity10Encounter() {
        super(10, LocationType.CITY);
    }

    @Override
    protected void execute() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().typeInfo("[#BAD]Roll one less die for each Spell you have.[]");

        int spellsCount = ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()).size();
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -spellsCount,
                this::gainThisClue,
                () -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getService().hideBackground();
                    ServicePlatform.get().getTokenService().discardClue(getLocationId());
                    ServicePlatform.get().getService().release();

                }
        ).withResearchFlavor().execute();

        ServicePlatform.get().getService().release();
    }
}
