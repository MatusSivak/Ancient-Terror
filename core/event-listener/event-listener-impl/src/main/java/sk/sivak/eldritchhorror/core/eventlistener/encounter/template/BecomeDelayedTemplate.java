package sk.sivak.eldritchhorror.core.eventlistener.encounter.template;

import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.EncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class BecomeDelayedTemplate extends AbstractEncounterTemplate{

    private final Runnable onDelayedAction;
    public BecomeDelayedTemplate(EncounterTextBuilder textBuilder, Runnable onDelayedAction) {
        super(textBuilder);
        this.onDelayedAction = onDelayedAction;
    }

    @Override
    public void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), null, this::onYes);
    }

    private void onYes() {
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(getActiveInvestigatorId(), false));
        if (!ServicePlatform.get().getInvestigators().getActiveInvestigator().isDelayed()) {
            onDelayedAction.run();
        }

    }
}
