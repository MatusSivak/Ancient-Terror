package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.GainConditionTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class Shanghai13Encounter extends AbstractLocationEncounter{

    public Shanghai13Encounter() {
        super(13, LocationEncounter.LocationEncounterType.SHANGHAI);
    }

    @Override
    protected void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), this::onNo, this::onYes);
    }

    private void onYes() {
        ConditionId conditionId = ConditionId.DARK_PACT;
        boolean hasCondition = ServicePlatform.get().getConditionsDeck().hasCondition(getActiveInvestigatorId(), conditionId);
        if (hasCondition) {
            TypewriterUtils.confirmInfos("Cannot gain another " + conditionId.asString() + " Condition").subscribe(this::onNo);
        } else {
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getGameService().gainCondition(conditionId);
            ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
            ServicePlatform.get().getInvestigatorService().improveSkill(null);
        }
    }

    private void onNo() {
        ServicePlatform.get().getEncounterService().hold();
        ServicePlatform.get().getEncounterService().typeFlavor(" \n"+getTextBuilder().withFail().withFlavor().build());
        TypewriterUtils.confirmInfos(getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getGameService().gainCondition(ConditionId.PARANOIA);
            ServicePlatform.get().getEncounterService().release();
        });
        ServicePlatform.get().getEncounterService().release();
    }
}
