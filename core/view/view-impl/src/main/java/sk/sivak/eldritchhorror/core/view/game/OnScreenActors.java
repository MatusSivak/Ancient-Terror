package sk.sivak.eldritchhorror.core.view.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import rx.functions.Action0;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OnScreenActors {
    private static OnScreenActors instance;
    private Map<ActorKey, Set<HideableActor>> map = new HashMap<>();

    private boolean lock;

    public static boolean isLock() {
        return getInstance().lock;
    }

    public static void lock() {
        getInstance().lock = true;
    }

    public static void unlock() {
        getInstance().lock = false;
    }

    private OnScreenActors() {
    }

    private static OnScreenActors getInstance() {
        if (instance == null) {
            instance = new OnScreenActors();
        }
        return instance;
    }

    public static void nullifyInstance() {
        instance = null;
    }

    public static void register(ActorKey actorKey, Actor actor, Action0 hideAction, Action0 showAction) {
        /*
        System.out.println("Registering: " + actorKey + " START");
        Map<ActorKey, Set<HideableActor>> map = getInstance().map;

        if (!map.containsKey(actorKey)) {
            map.put(actorKey, new HashSet<>());
        }

        map.get(actorKey).add(new HideableActor(actor, hideAction, showAction));

        List<Action0> actionsToCall = new LinkedList<>();
        for (Map.Entry<ActorKey, Set<HideableActor>> entry : map.entrySet()) {
            if (entry.getKey() == actorKey) {
                continue;
            }
            for (HideableActor otherHideableActor : entry.getValue()) {
                actionsToCall.add(otherHideableActor.hideAction);
            }
        }

        IterableUtils.forEach(actionsToCall, Action0::call);

        if (actorKey == ActorKey.PASSPORT) {
            InfoStage.getInvestigatorHud().hide().subscribe();
            InfoStage.getTrackHud().hide().subscribe();
        }

        System.out.println("Registering: " + actorKey + " FINISHED");
        */
    }

    public static void unregister(ActorKey actorKey) {
        System.out.println("Unregistering: " + actorKey);
        Map<ActorKey, Set<HideableActor>> map = getInstance().map;
        map.remove(actorKey);

        for (Map.Entry<ActorKey, Set<HideableActor>> entry : map.entrySet()) {
            if (entry.getKey() == actorKey) {
                continue;
            }
            for (HideableActor otherHideableActor : entry.getValue()) {
                otherHideableActor.show();
            }
        }

        if (actorKey == ActorKey.PASSPORT) {
            InfoStage.getInvestigatorHud().show().subscribe();
            HudButtons.getTrackHud().show().subscribe();
        }

        System.out.println("Unregistering: " + actorKey + " FINISHED");
    }

    public enum ActorKey {
        CARD_TEMPLATE,
        TEST_INFO_TABLE,
        MYSTERY_CARD,
        RUMOR_CARD,
        MONSTER_CARD,
        ANCIENT_ONE_CARD,
        PAPER,
        RESERVE_CARD,
        DISCARD_CARD,
        DIE, PASSPORT, SELECT_ACTION_TABLE, DRAG_AND_DROP
    }

    private static class HideableActor {
        private Actor actor;
        private Action0 hideAction;
        private Action0 showAction;

        public HideableActor(Actor actor, Action0 hideAction) {
            this(actor, hideAction, null);
        }

        public HideableActor(Actor actor, Action0 hideAction, Action0 showAction) {
            this.actor = actor;
            this.hideAction = hideAction;
            this.showAction = showAction;
        }


        public void show() {
            if (showAction != null) {
                showAction.call();
            }
        }
    }
}
