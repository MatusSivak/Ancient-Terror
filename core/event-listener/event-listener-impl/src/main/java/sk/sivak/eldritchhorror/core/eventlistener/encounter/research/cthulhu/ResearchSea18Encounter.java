package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.AmbushTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ResearchSea18Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchSea18Encounter() {
        super(18, LocationType.SEA);
    }

    @Override
    protected void execute() {
        AmbushTemplate ambushTemplate = new AmbushTemplate(getTextBuilder(), NonEpicMonsterId.DEEP_ONE, () -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTokenService().loseSanity(1);
            gainThisClue();
            ServicePlatform.get().getService().release();
        }, () -> {
            ServicePlatform.get().getDoomOmenService().advanceDoom();
        }).withoutFailFlavor().withTwoPassInfos();

        if (ServicePlatform.get().getInvestigators().getDefeatedInvestigators().isEmpty()) {
            ambushTemplate.execute();
        } else {
            TypewriterUtils.confirmInfos("Discard defeated investigator.").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().hideTypewriterPaper();
                ServicePlatform.get().getService().hideBackground();
                LinkedList<? extends InvestigatorRead> defeatedInvestigators = new LinkedList<>(ServicePlatform.get().getInvestigators().getDefeatedInvestigators());
                Collections.shuffle(defeatedInvestigators);
                ServicePlatform.get().getInvestigatorService().discardDefeatedInvestigator(defeatedInvestigators.get(0).getInfo().getInvestigatorId());
                ServicePlatform.get().getService().showResearchBackground(LocationType.SEA);
                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                ambushTemplate.execute();
                ServicePlatform.get().getService().release();
            });
        }
    }


}
