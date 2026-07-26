package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchWilderness9Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchWilderness9Encounter() {
        super(9, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        if (ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()).isEmpty()) {
            TypewriterUtils.confirmInfos("You don't have any Spell.").subscribe(() -> {
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            });
            return;
        }
        new InfoFlavorTemplate(getTextBuilder(), () -> {
            ServicePlatform.get().getGameService().gainCondition(ConditionId.HALLUCINATIONS);
        }, new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1, this::gainThisClue)
                .withResearchFlavor().withoutPassFlavor()
        ).execute();
    }
}
