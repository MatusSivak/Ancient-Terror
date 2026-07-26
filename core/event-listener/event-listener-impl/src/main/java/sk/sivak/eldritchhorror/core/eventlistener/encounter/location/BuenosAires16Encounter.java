package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.GainConditionTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class BuenosAires16Encounter extends AbstractLocationEncounter{

    public BuenosAires16Encounter() {
        super(16, LocationEncounter.LocationEncounterType.BUENOS_AIRES);
    }

    @Override
    protected void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), this::onNo, this::onYes);
    }

    private void onYes() {
        boolean hasCondition = ServicePlatform.get().getConditionsDeck().hasCondition(getActiveInvestigatorId(), ConditionId.POISONED);
        if (hasCondition) {
            TypewriterUtils.confirmInfos("Cannot gain another " + ConditionId.POISONED.asString() + " Condition").subscribe(this::onNo);
        } else {
            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            ServicePlatform.get().getGameService().gainCondition(ConditionId.POISONED);
            ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
            ServicePlatform.get().getEncounterService().typeFlavor(" \n"+getTextBuilder().withPass().withFlavor().build());
            TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
                ServicePlatform.get().getEncounterService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getGameService().gainSpell(SpellId.POISON_MIST);
                ServicePlatform.get().getEncounterService().release();
            });
        }
    }

    private void onNo() {
        ServicePlatform.get().getEncounterService().hold();
        ServicePlatform.get().getEncounterService().typeFlavor(" \n"+getTextBuilder().withFail().withFlavor().build());
        TypewriterUtils.confirmInfos(getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseSanity(2);
            ServicePlatform.get().getEncounterService().release();
        });
        ServicePlatform.get().getEncounterService().release();
    }

}
