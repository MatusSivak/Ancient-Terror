package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchSea1Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchSea1Encounter() {
        super(1, LocationType.SEA);
    }

    @Override
    protected void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), this::onNo, this::onYes);

    }

    private void onYes() {
        if (ServicePlatform.get().getConditionsDeck().canGetConditionId(getActiveInvestigatorId(), ConditionId.DARK_PACT)) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getGameService().gainCondition(ConditionId.DARK_PACT);
            ServicePlatform.get().getGameService().gainSpell(SpellId.MISTS_OF_RELEH);
            gainThisClue();
            ServicePlatform.get().getService().release();
        } else {
            onNo();
        }

    }

    private void onNo() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().typeFlavor("");
        TypewriterUtils.confirmInfos(
                getTextBuilder().withFail().withInfo(1).build(),
                getTextBuilder().withFail().withInfo(2).build()).subscribe(( )-> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getService().hideBackground();
            ServicePlatform.get().getTokenService().discardClue(getLocationId());
            ServicePlatform.get().getMonsterService().ambush(NonEpicMonsterId.STAR_SPAWN).subscribe();
            ServicePlatform.get().getService().release();
        });
        ServicePlatform.get().getService().release();
    }
}
