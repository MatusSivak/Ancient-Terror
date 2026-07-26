package sk.sivak.eldritchhorror.core.eventlistener.encounter.template;

import java8.features.function.Supplier;
import rx.Completable;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.EncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class InfoFlavorTemplate extends AbstractEncounterTemplate{

    private final Runnable autoRewardAction;
    private final EncounterTemplate template;

    private Supplier<Completable> confirmInfo = () -> TypewriterUtils.confirmInfos(getTextBuilder().withInfo().build());

    public InfoFlavorTemplate(EncounterTextBuilder textBuilder, Runnable autoRewardAction, EncounterTemplate template) {
        super(textBuilder);
        this.autoRewardAction = autoRewardAction;
        this.template = template;
    }

    @Override
    public void execute() {
        confirmInfo.get().subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            autoRewardAction.run();
            afterOnReward();
            ServicePlatform.get().getEncounterService().release();
        });
    }

    protected void afterOnReward() {
        displayTestButton();
    }

    public void displayTestButton() {
        ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
        ServicePlatform.get().getEncounterService().typeFlavor(" \n"+getTextBuilder().withFlavor(2).build());
        template.execute();
    }

    public InfoFlavorTemplate withTwoInfos() {
        confirmInfo = () -> TypewriterUtils.confirmInfos(
                getTextBuilder().withInfo(1).build(),
                getTextBuilder().withInfo(2).build());
        return this;
    }
}
