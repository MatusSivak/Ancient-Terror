package sk.sivak.eldritchhorror.core.eventlistener.encounter.template;

import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.EncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class SpendCluePassFlavorInfoTemplate extends AbstractEncounterTemplate {

    private Runnable onSpendAction;

    private boolean typePassFlavor = true;
    private boolean typePassInfo = true;
    private Action0 onNoAction = null;

    public SpendCluePassFlavorInfoTemplate(EncounterTextBuilder textBuilder, Runnable onSpendAction) {
        super(textBuilder);
        this.onSpendAction = onSpendAction;
    }

    @Override
    public void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), onNoAction, this::onYes);
    }

    private void onYes() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().hideTypewriterPaper();
        ServicePlatform.get().getTokenService().spend(1, 0, 0, 0).subscribe(
                spendData -> {
                    if (spendData.hasEnough()) {
                        ServicePlatform.get().getService().hold();

                        spendData.pay();
                        ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                        if (typePassFlavor) {
                            ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withPass().withFlavor().build());
                        }
                        if (typePassInfo) {
                            TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
                                ServicePlatform.get().getService().hold();
                                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                                onSpendAction.run();
                                ServicePlatform.get().getService().release();
                            });
                        } else {
                            ServicePlatform.get().getService().hold();
                            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                            onSpendAction.run();
                            ServicePlatform.get().getService().release();
                        }

                        ServicePlatform.get().getService().release();
                    } else if (onNoAction != null) {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                        TypewriterUtils.confirmInfos("[#BAD]You don't have a Clue[]").subscribe(() -> {
                            onNoAction.call();
                        });
                        ServicePlatform.get().getService().release();
                    } else {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                        TypewriterUtils.confirmInfos("[#BAD]You don't have a Clue[]")
                                .subscribe(() -> ServicePlatform.get().getEncounterService().finishTypewriterPaper());
                        ServicePlatform.get().getService().release();
                    }
                }
        );
        ServicePlatform.get().getService().release();
    }

    public SpendCluePassFlavorInfoTemplate withoutPassFlavor() {
        typePassFlavor = false;
        return this;
    }

    public SpendCluePassFlavorInfoTemplate withoutPassInfo() {
        typePassInfo = false;
        return this;
    }

    public SpendCluePassFlavorInfoTemplate withOnNoAction(Action0 onNoAction) {
        this.onNoAction = onNoAction;
        return this;
    }
}
