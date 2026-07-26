package sk.sivak.eldritchhorror.core.eventlistener.encounter.template;

import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.EncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class SpendClueTemplate extends AbstractEncounterTemplate{
    private Runnable onSpendAction;

    public SpendClueTemplate(EncounterTextBuilder textBuilder, Runnable onSpendAction) {
        super(textBuilder);
        this.onSpendAction = onSpendAction;
    }

    @Override
    public void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), null, this::onYes);
    }

    private void onYes() {
        ServicePlatform.get().getTokenService().spend(1, 0, 0, 0).subscribe(
                spendData -> {
                    if (spendData.hasEnough()) {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        spendData.pay();
                        onSpendAction.run();
                        ServicePlatform.get().getService().release();
                    } else {
                        TypewriterUtils.confirmInfos("[#BAD]You don't have a Clue[]")
                                .subscribe(() -> ServicePlatform.get().getEncounterService().finishTypewriterPaper());
                    }
                }
        );
    }
}
