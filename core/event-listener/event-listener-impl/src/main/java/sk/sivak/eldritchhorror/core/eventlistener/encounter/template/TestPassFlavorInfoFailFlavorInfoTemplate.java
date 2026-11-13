package sk.sivak.eldritchhorror.core.eventlistener.encounter.template;

import java8.features.function.Supplier;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.EncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventtype.data.test.TestData.JUST_ONE;

public class TestPassFlavorInfoFailFlavorInfoTemplate extends AbstractEncounterTemplate {

    protected final Stat testStat;
    protected final int modifier;
    private final Runnable onSuccess;
    private final Runnable onFail;

    private Runnable typePassFlavor = () -> ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withPass().withFlavor().build());
    private Runnable typeFailFlavor = () -> ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withFail().withFlavor().build());
    private Supplier<Completable> confirmPassInfo = () -> TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build());
    private Supplier<Completable> confirmFailInfo = () -> TypewriterUtils.confirmInfos(getTextBuilder().withFail().withInfo().build());

    protected Runnable displayTestButton;

    private boolean hidePaperBeforePass = true;
    private boolean hidePaperBeforeFail = true;

    public TestPassFlavorInfoFailFlavorInfoTemplate(EncounterTextBuilder textBuilder, Stat testStat, int modifier, Runnable onSuccess, Runnable onFail) {
        super(textBuilder);
        this.testStat = testStat;
        this.modifier = modifier;
        this.onSuccess = onSuccess;
        this.onFail = onFail;
        displayTestButton = () -> TypewriterUtils.displayTestButton(testStat, modifier, onSuccess != null ? this::onSuccess : null, onFail != null ? this::onFail : null);
    }

    public TestPassFlavorInfoFailFlavorInfoTemplate withExpeditionFlavor() {
        this.displayTestButton = () -> TypewriterUtils.displayTestExpeditionButton(testStat, modifier, onSuccess != null ? this::onSuccess : null, onFail != null ? this::onFail : null);
        return this;
    }

    public TestPassFlavorInfoFailFlavorInfoTemplate withOtherWorldFlavor() {
        this.displayTestButton = () -> TypewriterUtils.displayTestOtherWorldButton(testStat, modifier, onSuccess != null ? this::onSuccess : null, onFail != null ? this::onFail : null);
        return this;
    }

    public TestPassFlavorInfoFailFlavorInfoTemplate withResearchFlavor() {
        this.displayTestButton = () -> TypewriterUtils.displayTestResearchButton(testStat, modifier, onSuccess != null ? this::onSuccess : null, onFail != null ? this::onFail : null);
        return this;
    }

    @Override
    public void execute() {
        displayTestButton.run();
    }

    protected void onSuccess() {
        typePassFlavor.run();
        confirmPassInfo.get().subscribe(() -> {
            ServicePlatform.get().getService().hold();
            if (hidePaperBeforePass) {
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            }
            onSuccess.run();
            ServicePlatform.get().getService().release();
        });
    }

    protected void onFail() {
        typeFailFlavor.run();
        confirmFailInfo.get().subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            if (hidePaperBeforeFail) {
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            }
            onFail.run();
            ServicePlatform.get().getEncounterService().release();
        });
    }

    public TestPassFlavorInfoFailFlavorInfoTemplate withoutPassFlavor() {
        typePassFlavor = () -> {};
        return this;
    }

    public TestPassFlavorInfoFailFlavorInfoTemplate withoutFailFlavor() {
        typeFailFlavor = () -> {};
        return this;
    }

    public TestPassFlavorInfoFailFlavorInfoTemplate withTwoPassInfos() {
        confirmPassInfo = () -> TypewriterUtils.confirmInfos(
                getTextBuilder().withPass().withInfo(1).build(),
                getTextBuilder().withPass().withInfo(2).build());
        return this;
    }

    public TestPassFlavorInfoFailFlavorInfoTemplate withTwoFailInfos() {
        confirmFailInfo = () -> TypewriterUtils.confirmInfos(
                getTextBuilder().withFail().withInfo(1).build(),
                getTextBuilder().withFail().withInfo(2).build());
        return this;
    }

    public TestPassFlavorInfoFailFlavorInfoTemplate withThreeFailInfos() {
        confirmFailInfo = () -> TypewriterUtils.confirmInfos(
                getTextBuilder().withFail().withInfo(1).build(),
                getTextBuilder().withFail().withInfo(2).build(),
                getTextBuilder().withFail().withInfo(3).build());
        return this;
    }

    public TestPassFlavorInfoFailFlavorInfoTemplate withoutPassInfo() {
        confirmPassInfo = Completable::complete;
        return this;
    }

    public TestPassFlavorInfoFailFlavorInfoTemplate withoutFailInfo() {
        confirmFailInfo = Completable::complete;
        return this;
    }

    /** Lets the player read the pass flavor and confirm it with OK when the pass gives nothing. */
    public TestPassFlavorInfoFailFlavorInfoTemplate withOkAfterPassFlavor() {
        confirmPassInfo = TestPassFlavorInfoFailFlavorInfoTemplate::confirmWithOk;
        return this;
    }

    /** Lets the player read the fail flavor and confirm it with OK when the fail costs nothing. */
    public TestPassFlavorInfoFailFlavorInfoTemplate withOkAfterFailFlavor() {
        confirmFailInfo = TestPassFlavorInfoFailFlavorInfoTemplate::confirmWithOk;
        return this;
    }

    public TestPassFlavorInfoFailFlavorInfoTemplate withPassFlavorVariant(int flavorNr) {
        typePassFlavor = () -> ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withPass().withFlavor(flavorNr).build());
        return this;
    }

    /** Replaces the fail flavor with its numbered variant, e.g. when the usual loss cannot happen. */
    public TestPassFlavorInfoFailFlavorInfoTemplate withFailFlavorVariant(int flavorNr) {
        typeFailFlavor = () -> ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withFail().withFlavor(flavorNr).build());
        return this;
    }

    static Completable confirmWithOk() {
        return Completable.create(onSub -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().displayButtons("OK").subscribe(buttonId -> onSub.onCompleted());
            ServicePlatform.get().getService().release();
        });
    }


    public TestPassFlavorInfoFailFlavorInfoTemplate withoutHidingPaperBeforePass() {
        hidePaperBeforePass = false;
        return this;
    }

    public TestPassFlavorInfoFailFlavorInfoTemplate withoutHidingPaperBeforeFail() {
        hidePaperBeforeFail = false;
        return this;
    }


}
