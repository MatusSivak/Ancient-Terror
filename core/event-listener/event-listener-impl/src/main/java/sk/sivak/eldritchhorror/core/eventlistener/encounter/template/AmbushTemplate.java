package sk.sivak.eldritchhorror.core.eventlistener.encounter.template;

import java8.features.function.Function;
import java8.features.function.Supplier;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.EncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

import java.util.concurrent.RunnableFuture;

public class AmbushTemplate extends AbstractEncounterTemplate {

    private final NonEpicMonsterId monsterId;
    private final Runnable onSuccess;
    private final Runnable onFail;
    private Function<MonsterInfo, String> onSuccessMessageFunction = monsterInfo -> "[#GOOD]" + monsterInfo.getName() + " was defeated.[]";
    private Function<MonsterInfo, String> onFailMessageFunction = monsterInfo -> "[#BAD]"+monsterInfo.getName()+" was not defeated.[]";

    private Function<CombatData, Boolean> isCombatSuccessfulFunction = combatData -> !combatData.getMonsterInfo().isAlive();
    private Runnable typePassFlavor = () -> ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withPass().withFlavor().build());
    private Runnable typeFailFlavor = () -> ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withFail().withFlavor().build());
    private Supplier<Completable> confirmPassInfo = () -> TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build());
    private Supplier<Completable> confirmFailInfo = () -> TypewriterUtils.confirmInfos(getTextBuilder().withFail().withInfo().build());

    private Supplier<Completable> confirmAmbushCompletable;
    private Runnable justAfterAmbushAction = () -> {};

    public AmbushTemplate(EncounterTextBuilder textBuilder, NonEpicMonsterId monsterId, Runnable onSuccess, Runnable onFail) {
        super(textBuilder);
        this.monsterId = monsterId;
        this.onSuccess = onSuccess;
        this.onFail = onFail;
        if (monsterId == null) {
            confirmAmbushCompletable = () -> TypewriterUtils.confirmInfos("[#BAD]Monster ambushes you![]");
        } else {
            confirmAmbushCompletable = () -> TypewriterUtils.confirmInfos("[#BAD]"+monsterId.asString()+" ambushes you![]");
        }
    }

    public AmbushTemplate withJustAfterAmbushAction(Runnable justAfterAmbushAction) {
        this.justAfterAmbushAction = justAfterAmbushAction;
        return this;
    }

    @Override
    public void execute() {
        confirmAmbushCompletable.get().subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            ServicePlatform.get().getMonsterService().ambush(monsterId).subscribe(combatData -> {
                ServicePlatform.get().getService().hold();
                justAfterAmbushAction.run();
                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                if (isCombatSuccessfulFunction.apply(combatData)) {
                    onSuccess(combatData.getMonsterInfo());
                } else {
                    onFail(combatData.getMonsterInfo());
                }
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getEncounterService().release();
        });
    }

    public AmbushTemplate withIsCombatSuccessfulFunction(Function<CombatData, Boolean> isCombatSuccessfulFunction) {
        this.isCombatSuccessfulFunction = isCombatSuccessfulFunction;
        return this;
    }

    public AmbushTemplate withOnSuccessMessageFunction(Function<MonsterInfo, String> onSuccessMessageFunction) {
        this.onSuccessMessageFunction = onSuccessMessageFunction;
        return this;
    }

    public AmbushTemplate withOnFailMessageFunction(Function<MonsterInfo, String> onFailMessageFunction) {
        this.onFailMessageFunction = onFailMessageFunction;
        return this;
    }

    protected final void onSuccess(MonsterInfo monsterInfo) {
        if (onSuccess == null) {
            TypewriterUtils.confirmInfos(onSuccessMessageFunction.apply(monsterInfo)).subscribe(() -> {
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            });
            return;
        }
        ServicePlatform.get().getEncounterService().typeInfo(onSuccessMessageFunction.apply(monsterInfo)+"\n ");
        typePassFlavor.run();
        confirmPassInfo.get().subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            onSuccess.run();
            ServicePlatform.get().getService().release();
        });
    }

    protected final void onFail(MonsterInfo monsterInfo) {
        if (onFail == null) {
            TypewriterUtils.confirmInfos(onFailMessageFunction.apply(monsterInfo)).subscribe(() -> {
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            });
            return;
        }
        ServicePlatform.get().getEncounterService().typeInfo(onFailMessageFunction.apply(monsterInfo)+"\n ");
        typeFailFlavor.run();
        confirmFailInfo.get().subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            onFail.run();
            ServicePlatform.get().getEncounterService().release();
        });
    }

    public AmbushTemplate withoutPassFlavor() {
        typePassFlavor = () -> {};
        return this;
    }

    public AmbushTemplate withoutFailFlavor() {
        typeFailFlavor = () -> {};
        return this;
    }

    public AmbushTemplate withTwoPassInfos() {
        confirmPassInfo = () -> TypewriterUtils.confirmInfos(
                getTextBuilder().withPass().withInfo(1).build(),
                getTextBuilder().withPass().withInfo(2).build());
        return this;
    }

    public AmbushTemplate withTwoFailInfos() {
        confirmFailInfo = () -> TypewriterUtils.confirmInfos(
                getTextBuilder().withFail().withInfo(1).build(),
                getTextBuilder().withFail().withInfo(2).build());
        return this;
    }


}
