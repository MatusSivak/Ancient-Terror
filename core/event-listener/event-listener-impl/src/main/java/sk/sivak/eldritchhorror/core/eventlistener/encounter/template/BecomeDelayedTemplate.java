package sk.sivak.eldritchhorror.core.eventlistener.encounter.template;

import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.EncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class BecomeDelayedTemplate extends AbstractEncounterTemplate{

    private final Runnable onDelayedAction;
    private boolean answerFlavors;
    public BecomeDelayedTemplate(EncounterTextBuilder textBuilder, Runnable onDelayedAction) {
        super(textBuilder);
        this.onDelayedAction = onDelayedAction;
    }

    @Override
    public void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(),
                answerFlavors ? () -> finishAnswer(getTextBuilder().withFail().withFlavor().build(), false) : null,
                this::onYes);
    }

    public BecomeDelayedTemplate withAnswerFlavors() {
        answerFlavors = true;
        return this;
    }

    private void finishAnswer(String flavor, boolean accept) {
        ServicePlatform.get().getEncounterService().typeFlavor(flavor);
        TestPassFlavorInfoFailFlavorInfoTemplate.confirmWithOk().subscribe(() -> {
            if (accept) applyDelay();
            else ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        });
    }

    private void onYes() {
        if (answerFlavors) {
            boolean alreadyDelayed = ServicePlatform.get().getInvestigators().getActiveInvestigator().isDelayed();
            finishAnswer(alreadyDelayed ? getTextBuilder().withPass().withFlavor(2).build()
                    : getTextBuilder().withPass().withFlavor().build(), true);
        } else {
            applyDelay();
        }
    }

    private void applyDelay() {
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(getActiveInvestigatorId(), false));
        if (!ServicePlatform.get().getInvestigators().getActiveInvestigator().isDelayed()) {
            onDelayedAction.run();
        }

    }
}
