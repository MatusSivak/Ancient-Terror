package sk.sivak.eldritchhorror.core.eventlistener.ancientone.yig;

import java.util.*;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.*;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AncientOneInitListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.*;
import sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType;
import sk.sivak.eldritchhorror.core.model.*;
import static sk.sivak.eldritchhorror.core.eventlistener.ancientone.yig.YigEffects.*;

public class YigInitListener extends AncientOneInitListener {
    private final List<EventListener> listeners = new ArrayList<>();
    private boolean awakened() { return p().getModel().getAncientOne().getAncientOneInfo().isAwaken(); }
    @Override public void register() {
        sequence(() -> {
            if (awakened()) {
                p().getDoomOmenService().increaseAncientOnePower(8);
                for (InvestigatorRead i : p().getInvestigators().getOnBoardInvestigators()) {
                    if (p().getConditionsDeck().hasCondition(i.getInfo().getInvestigatorId(), ConditionId.POISONED))
                        p().getService().gainConditionFromDeck(i.getInfo().getInvestigatorId(), ConditionId.CURSED);
                }
                for (MonsterInfo monster : p().getMonsterCup().getMonsters()) {
                    if (monster.getMonsterId() == NonEpicMonsterId.CULTIST)
                        sk.sivak.eldritchhorror.core.eventlistener.monster.YigCultistMonsterListener.configure(monster, true, true);
                }
            } else {
                p().getMythosDeck().initSelectedMythosCards(new MythosDeckRead.MythosStageImpl(1,2,1),
                        new MythosDeckRead.MythosStageImpl(2,3,1), new MythosDeckRead.MythosStageImpl(2,4,0));
            }
            p().getGameService().showAncientOneCard();
        });
        load();
    }
    @Override public void load() {
        EventListenerImpl<ReckoningFireType> reckoning = new EventListenerImpl<ReckoningFireType>() {
            public Class<ReckoningFireType> getDataClass() { return ReckoningFireType.class; }
            public void onNotify(ReckoningFireType type) {
                if (type == ReckoningFireType.CHARGE) return;
                sequence(() -> {
                    p().getDoomOmenService().onTriggeredReckoning();
                    if (!awakened()) {
                        LocationId expedition = p().getExpeditionDeck().getExpeditionTokenLocation();
                        spawn(NonEpicMonsterId.CULTIST, expedition);
                        later(() -> { if (p().getMonsterCup().getMonstersAtLocation(expedition).size() >= 2) p().getDoomOmenService().advanceDoom(); });
                    } else {
                        Set<LocationId> spaces = new LinkedHashSet<>(p().getVortexes().getSpawnedVortexes());
                        // Only actual Mystery/Eldritch tokens, not informational research pins.
                        sk.sivak.eldritchhorror.core.constants.MysteryCardInfo mystery = p().getMysteryDeck().getCurrentMysteryCard();
                        if (mystery.getMysteryCardId() == sk.sivak.eldritchhorror.core.constants.MysteryCardId.Yig.KN_YAN_UNEARTHED
                                || mystery.getMysteryCardId() == sk.sivak.eldritchhorror.core.constants.MysteryCardId.Yig.RISE_OF_THE_SERPENT_PEOPLE)
                            spaces.addAll(mystery.getPinLocations());
                        for (MonsterInfo m : p().getMonsterCup().getMonsters()) if (m.isEpic()) spaces.add(m.getCurrentLocation());
                        chooseLocation(spaces, "Spawn Yig's Cultist", l -> spawn(NonEpicMonsterId.CULTIST, l));
                    }
                });
            }
        };
        listeners.add(reckoning); p().getEventQueue().addDirectEventListener(reckoning, DirectEvent.RECKONING_ANCIENT_ONE);
        if (!awakened()) {
            EventListenerImpl<Void> awaken = new EventListenerImpl<Void>() {
                public Class<Void> getDataClass() { return Void.class; }
                public void onNotify(Void ignored) {
                    if (p().getDoomTrack().getCurrentDoom() > 0) return;
                    later(() -> {
                        for (EventListener listener : listeners) p().getEventQueue().unregisterListener(listener);
                        p().getDoomOmenService().ancientOneAwakens();
                    });
                }
            };
            listeners.add(awaken); p().getEventQueue().addAfterEventListener(awaken, BeforeAfterEvent.ADVANCE_DOOM);
        } else {
            EventListenerImpl<Integer> advance = new EventListenerImpl<Integer>() {
                public Class<Integer> getDataClass() { return Integer.class; }
                public void onNotify(Integer amount) { sequence(() -> {
                    if (amount == null || amount <= 0) return;
                    p().getDoomOmenService().increaseAncientOnePower(-Math.min(amount, p().getModel().getAncientOne().getAncientOneInfo().getPower()));
                    later(() -> {
                        if (p().getModel().getAncientOne().getAncientOneInfo().getPower() <= 0) {
                            p().getGameService().displayText("Yig's last Eldritch token is gone. The investigators lose.");
                            p().getGameService().restartGame();
                        }
                    });
                    p().getService().skipAfterEvent(BeforeAfterEvent.ADVANCE_DOOM, null);
                }); }
            };
            listeners.add(advance); p().getEventQueue().addBeforeEventListener(advance, BeforeAfterEvent.ADVANCE_DOOM);
            EventListenerImpl<Object> retreat = new EventListenerImpl<Object>() {
                public Class<Object> getDataClass() { return Object.class; }
                public void onNotify(Object ignored) { sequence(() -> { cureCurse(); p().getService().skipAfterEvent(BeforeAfterEvent.RETREAT_DOOM, null); }); }
            };
            listeners.add(retreat); p().getEventQueue().addBeforeEventListener(retreat, BeforeAfterEvent.RETREAT_DOOM);
            // Deliberately keep the normal REPLACE_INVESTIGATORS flow after awakening.
        }
    }
}
