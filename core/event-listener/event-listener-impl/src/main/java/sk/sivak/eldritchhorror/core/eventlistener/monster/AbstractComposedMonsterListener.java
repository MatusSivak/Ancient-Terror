package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_MONSTER;

/**
 * Base for monsters whose abilities are composed from small hooks. Tracks every registered
 * listener so {@link #unregister()} cleans everything up, and separates one-off spawn effects
 * (run only when actually spawned) from listeners (also re-registered on game load).
 */
abstract class AbstractComposedMonsterListener extends AbstractMonsterListener {

    interface Handler<T> {
        void handle(T data);
    }

    private final List<EventListener> listeners = new LinkedList<>();

    @Override
    public final void register(MonsterInfo monsterInfo) {
        justRegisterListeners(monsterInfo);
        onSpawn();
    }

    @Override
    public final void justRegisterListeners(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
        registerListeners();
    }

    protected abstract void registerListeners();

    /** One-off effect when the monster is spawned (not called when a saved game is loaded). */
    protected void onSpawn() {
    }

    @Override
    public void unregister() {
        for (EventListener listener : listeners) {
            ServicePlatform.get().getEventQueue().unregisterListener(listener);
        }
        listeners.clear();
    }

    protected final <T> void before(BeforeAfterEvent event, Class<T> dataClass, Handler<T> handler) {
        ServicePlatform.get().getEventQueue().addBeforeEventListener(track(dataClass, handler), event);
    }

    protected final <T> void after(BeforeAfterEvent event, Class<T> dataClass, Handler<T> handler) {
        ServicePlatform.get().getEventQueue().addAfterEventListener(track(dataClass, handler), event);
    }

    protected final <T> void direct(DirectEvent event, Class<T> dataClass, Handler<T> handler) {
        ServicePlatform.get().getEventQueue().addDirectEventListener(track(dataClass, handler), event);
    }

    protected final void reckoning(Runnable effect) {
        AbstractMonsterReckoningListener listener = new AbstractMonsterReckoningListener() {
            @Override
            void onNotify() {
                effect.run();
            }
        };
        listeners.add(listener);
        ServicePlatform.get().getEventQueue().addDirectEventListener(listener, RECKONING_MONSTER);
    }

    protected final void resistance(boolean magical) {
        MonsterAbilities.ResistanceListener listener = new MonsterAbilities.ResistanceListener(monsterInfo, magical);
        listeners.add(listener);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(listener, BeforeAfterEvent.CONFIRM_TEST);
    }

    /** Runs {@code effect} (synchronously queued) when the investigator loses Health in the Strength test. */
    protected final void onHealthLostInCombat(Handler<Integer> effect) {
        before(BeforeAfterEvent.DESTROY_MONSTER_HEALTH, CombatData.class, combatData -> {
            if (combatData.getMonsterInfo() != monsterInfo || combatData.getHealthLost() == 0) {
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            effect.handle(combatData.getHealthLost());
            ServicePlatform.get().getService().convertTo(CombatData.class, () -> combatData);
            ServicePlatform.get().getService().release();
        });
    }

    /** Runs {@code effect} when this monster is defeated during a Combat Encounter. */
    protected final void onDefeatedInCombat(Runnable effect) {
        before(BeforeAfterEvent.DEFEAT_MONSTER, DefeatMonsterData.class, data -> {
            if (!data.isInCombat() || data.getMonsterInfo() != monsterInfo) {
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            effect.run();
            ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> data);
            ServicePlatform.get().getService().release();
        });
    }

    /** The given test against this monster was failed; {@code effect} receives a callback that must be run when done. */
    protected final void onTestFailed(boolean horrorTest, Handler<Runnable> effect) {
        after(horrorTest ? BeforeAfterEvent.AFTER_HORROR_CHECK : BeforeAfterEvent.AFTER_DAMAGE_CHECK, CombatData.class, combatData -> {
            if (combatData.getMonsterInfo() != monsterInfo) {
                return;
            }
            if (horrorTest ? combatData.getHorrorTestResult() == null || combatData.getHorrorTestResult().getScore() != 0
                    : combatData.getDamageTestResult() == null || combatData.getDamageTestResult().getScore() != 0) {
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            effect.handle(() -> ServicePlatform.get().getService().convertTo(CombatData.class, () -> combatData));
            ServicePlatform.get().getService().release();
        });
    }

    /** Runs a spawn effect as a queued command; optionally restores the active investigator afterwards. */
    protected final void spawnEffect(boolean switchesInvestigator, Runnable effect) {
        ServicePlatform.get().getService().addEventCommand(in -> {
            sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId previouslyActive =
                    ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpawnText(monsterInfo);
            effect.run();
            if (switchesInvestigator) {
                MonsterAbilities.restoreActive(previouslyActive);
            }
            ServicePlatform.get().getService().convertTo(Object.class, () -> in);
            ServicePlatform.get().getService().release();
        });
    }

    private <T> EventListenerImpl<T> track(Class<T> dataClass, Handler<T> handler) {
        EventListenerImpl<T> listener = new EventListenerImpl<T>() {
            @Override
            public void onNotify(T eventData) {
                handler.handle(eventData);
            }

            @Override
            public Class<T> getDataClass() {
                return dataClass;
            }
        };
        listeners.add(listener);
        return listener;
    }
}
