package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchWilderness13Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchWilderness13Encounter() {
        super(13, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        boolean hasAnySpell = !ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()).isEmpty();

        if (hasAnySpell) {
            TypewriterUtils.confirmInfos("You have at least one Spell:\n[#BAD]Lose two Sanity[]").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().hideTypewriterPaper();
                ServicePlatform.get().getTokenService().loseSanity(2);
                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                typeFlavor();
                ServicePlatform.get().getService().release();
            });
        } else {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().typeInfo("You don't have any Spells.");
            typeFlavor();
            ServicePlatform.get().getService().release();
        }
    }

    private void typeFlavor() {
        ServicePlatform.get().getEncounterService().typeFlavor(" \n"+getTextBuilder().withFlavor(2).build());
        new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, -1, this::gainThisClue).withoutPassFlavor().withResearchFlavor().execute();
    }
}
