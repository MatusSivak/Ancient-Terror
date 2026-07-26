package sk.sivak.eldritchhorror.core.eventlistener.encounter.template;

import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.EncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.location.AbstractLocationEncounter;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class BecomeDelayedPassFlavorInfoTemplate extends AbstractEncounterTemplate {
    private final Runnable onDelayedAction;
    private boolean withPassFlavor = true;

    public BecomeDelayedPassFlavorInfoTemplate(EncounterTextBuilder textBuilder, Runnable onDelayedAction) {
        super(textBuilder);
        this.onDelayedAction = onDelayedAction;
    }

    @Override
    public void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), null, this::onYes);
    }

    private void onYes() {
        if (ServicePlatform.get().getInvestigators().getActiveInvestigator().isDelayed()) {
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(getActiveInvestigatorId(), false));
            return;
        }
        ServicePlatform.get().getEncounterService().hold();
        ServicePlatform.get().getEncounterService().hideTypewriterPaper();
        ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(getActiveInvestigatorId(), false));
        ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
        if (withPassFlavor) {
            ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withPass().withFlavor().build());
        }
        TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            if (ServicePlatform.get().getInvestigators().getActiveInvestigator().isDelayed()) {
                onDelayedAction.run();
            }
            ServicePlatform.get().getService().release();
        });
        ServicePlatform.get().getEncounterService().release();
    }

    public BecomeDelayedPassFlavorInfoTemplate withoutPassFlavor() {
        withPassFlavor = false;
        return this;
    }
}
