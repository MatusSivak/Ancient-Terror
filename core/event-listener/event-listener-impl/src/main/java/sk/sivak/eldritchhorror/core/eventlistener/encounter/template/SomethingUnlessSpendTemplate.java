package sk.sivak.eldritchhorror.core.eventlistener.encounter.template;

import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.EncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.service.data.ClueFocusHealthSanity;

public class SomethingUnlessSpendTemplate extends AbstractEncounterTemplate{


    private final ClueFocusHealthSanity clueFocusHealthSanity;
    private final Runnable defaultAction;

    private Runnable complementaryOnSpendAction;

    public SomethingUnlessSpendTemplate(EncounterTextBuilder textBuilder, ClueFocusHealthSanity clueFocusHealthSanity, Runnable defaultAction) {
        super(textBuilder);
        this.clueFocusHealthSanity = clueFocusHealthSanity;
        this.defaultAction = defaultAction;
    }

    public SomethingUnlessSpendTemplate setComplementaryOnSpendAction(Runnable complementaryOnSpendAction) {
        this.complementaryOnSpendAction = complementaryOnSpendAction;
        return this;
    }

    @Override
    public void execute() {
        ServicePlatform.get().getTokenService().canSpend(clueFocusHealthSanity).subscribe(canSpendData -> {
            if (canSpendData.hasEnough()) {
                ServicePlatform.get().getEncounterService().hold();
                ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.SELECT_ONE);
                ServicePlatform.get().getEncounterService().displayButtons(
                        getTextBuilder().withOption(1).build(),
                        getTextBuilder().withOption(2).build()
                ).subscribe(x -> {
                    if (x == 0) {
                        ServicePlatform.get().getEncounterService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        defaultAction.run();
                        ServicePlatform.get().getEncounterService().release();
                    } else if (complementaryOnSpendAction != null) {
                        ServicePlatform.get().getTokenService().hold();
                        ServicePlatform.get().getEncounterService().hideTypewriterPaper();
                        ServicePlatform.get().getTokenService().spend(clueFocusHealthSanity).subscribe(spendData -> {
                            if (spendData.hasEnough()) {
                                ServicePlatform.get().getService().hold();
                                spendData.pay();
                                complementaryOnSpendAction.run();
                                ServicePlatform.get().getService().release();
                            } else {
                                ServicePlatform.get().getTokenService().hold();
                                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                                defaultAction.run();
                                ServicePlatform.get().getTokenService().release();
                            }
                        });
                        ServicePlatform.get().getTokenService().release();
                    } else {
                        ServicePlatform.get().getTokenService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        ServicePlatform.get().getTokenService().spend(clueFocusHealthSanity).subscribe(spendData -> {
                            if (spendData.hasEnough()) {
                                spendData.pay();
                            } else {
                                defaultAction.run();
                            }
                        });
                        ServicePlatform.get().getTokenService().release();
                    }
                });
                ServicePlatform.get().getEncounterService().release();
            } else {
                TypewriterUtils.confirmInfos(getTextBuilder().withOption(1).build()).subscribe(() -> {
                    ServicePlatform.get().getEncounterService().hold();
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    defaultAction.run();
                    ServicePlatform.get().getEncounterService().release();
                });
            }
        });
    }
}
