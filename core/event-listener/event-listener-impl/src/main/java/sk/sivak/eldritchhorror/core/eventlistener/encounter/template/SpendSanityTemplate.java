package sk.sivak.eldritchhorror.core.eventlistener.encounter.template;

import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.EncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class SpendSanityTemplate extends AbstractEncounterTemplate{
    private final int amount;
    private Runnable onSpendAction;
    private boolean finishAfterSpend = true;
    private rx.functions.Action0 onNo;
    private rx.functions.Action0 onMissingSanity;

    public SpendSanityTemplate(EncounterTextBuilder textBuilder, int amount, Runnable onSpendAction) {
        super(textBuilder);
        this.amount = amount;
        this.onSpendAction = onSpendAction;
    }

    @Override
    public void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), onNo, this::onYes);
    }

    private void onYes() {
        ServicePlatform.get().getTokenService().spend(0, 0, 0, amount).subscribe(
                spendData -> {
                    if (spendData.hasEnough()) {
                        ServicePlatform.get().getService().hold();
                        if (finishAfterSpend) {
                            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        } else {
                            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
                        }
                        spendData.pay();
                        if (!finishAfterSpend) {
                            ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                        }
                        onSpendAction.run();
                        ServicePlatform.get().getService().release();
                    } else {
                        TypewriterUtils.confirmInfos("Not enough Sanity.")
                                .subscribe(() -> {
                                    if (onMissingSanity != null) onMissingSanity.call();
                                    else ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                                });
                    }
                }
        );
    }

    public SpendSanityTemplate withFinishAfterSpend(boolean finishAfterSpend) {
        this.finishAfterSpend = finishAfterSpend;
        return this;
    }

    public SpendSanityTemplate withOnNoAction(rx.functions.Action0 action) { onNo = action; return this; }
    public SpendSanityTemplate withOnMissingSanity(rx.functions.Action0 action) { onMissingSanity = action; return this; }
}
