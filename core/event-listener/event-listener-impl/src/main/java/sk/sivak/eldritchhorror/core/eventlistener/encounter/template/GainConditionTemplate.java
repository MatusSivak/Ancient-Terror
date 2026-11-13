package sk.sivak.eldritchhorror.core.eventlistener.encounter.template;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.EncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class GainConditionTemplate extends AbstractEncounterTemplate {
    private final ConditionId conditionId;
    private final Runnable onGainAction;
    private boolean answerFlavors;

    public GainConditionTemplate(EncounterTextBuilder textBuilder, ConditionId conditionId, Runnable onGainAction) {
        super(textBuilder);
        this.conditionId = conditionId;
        this.onGainAction = onGainAction;
    }

    /**
     * Narrates every answer: fail.flavor on No, pass.flavor on Yes,
     * and pass.flavor.2 when the investigator already has the Condition.
     */
    public GainConditionTemplate withAnswerFlavors() {
        answerFlavors = true;
        return this;
    }

    @Override
    public void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), answerFlavors ? this::onNo : null, this::onYes);
    }

    private void onNo() {
        typeFlavor(getTextBuilder().withFail().withFlavor().build());
        TestPassFlavorInfoFailFlavorInfoTemplate.confirmWithOk().subscribe(() ->
                ServicePlatform.get().getEncounterService().finishTypewriterPaper());
    }

    private void onYes() {
        boolean hasCondition = ServicePlatform.get().getConditionsDeck().hasCondition(getActiveInvestigatorId(), conditionId);
        if (hasCondition) {
            if (answerFlavors) {
                typeFlavor(getTextBuilder().withPass().withFlavor(2).build());
            }
            TypewriterUtils.confirmInfos("Cannot gain another " + conditionId.asString() + " Condition").subscribe(() ->{
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            });
        } else if (answerFlavors) {
            typeFlavor(getTextBuilder().withPass().withFlavor().build());
            TestPassFlavorInfoFailFlavorInfoTemplate.confirmWithOk().subscribe(this::gain);
        } else {
            gain();
        }

    }

    private void gain() {
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        ServicePlatform.get().getGameService().gainCondition(conditionId);
        onGainAction.run();
    }

    private static void typeFlavor(String flavor) {
        ServicePlatform.get().getEncounterService().typeFlavor(" \n" + flavor);
    }
}
