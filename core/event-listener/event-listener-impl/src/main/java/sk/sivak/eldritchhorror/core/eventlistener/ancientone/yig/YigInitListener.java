package sk.sivak.eldritchhorror.core.eventlistener.ancientone.yig;

import java.util.*;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.*;
import sk.sivak.eldritchhorror.core.constants.question.Question;
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
    /** Explains Yig's automatic effects before they change the board, like the awakening notice. */
    private static void notice(String title, Runnable effect) {
        Question<Object> notice = new Question<>();
        notice.setTitle(title);
        notice.setPortraitBeforeTitle(sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId.YIG);
        notice.setOptions(Question.Option.okOption);
        p().getGameService().ask(notice).subscribe(ok -> sequence(effect));
    }
    public void unregister() {
        for (EventListener listener : listeners) p().getEventQueue().unregisterListener(listener);
        listeners.clear();
    }
    @Override public void register() {
        sequence(() -> {
            if (awakened()) {
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
                        notice("ancientOne.yig.reckoning.cultist", () -> {
                            spawn(NonEpicMonsterId.CULTIST, expedition);
                            later(() -> {
                                if (p().getMonsterCup().getMonstersAtLocation(expedition).size() >= 2)
                                    notice("ancientOne.yig.reckoning.doom", () -> p().getDoomOmenService().advanceDoom());
                            });
                        });
                    } else {
                        notice("ancientOne.yig.reckoning.cultistRandom",
                                () -> spawn(NonEpicMonsterId.CULTIST, LocationId.getRandomLocations(1).get(0)));
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
                        unregister();
                        p().getDoomOmenService().ancientOneAwakens();
                    });
                }
            };
            listeners.add(awaken); p().getEventQueue().addAfterEventListener(awaken, BeforeAfterEvent.ADVANCE_DOOM);
        } else {
            EventListenerImpl<Void> advance = new EventListenerImpl<Void>() {
                public Class<Void> getDataClass() { return Void.class; }
                public void onNotify(Void ignored) {
                    if (p().getDoomTrack().getCurrentDoom() < 0) return;
                    sequence(() -> {
                        p().getTutorialService().displayChalkboard(
                                p().getModel().getAncientOne().getAncientOneInfo().getEndGameText(),
                                sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH / 2,
                                sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT / 2, true);
                        p().getGameService().restartGame();
                    });
                }
            };
            listeners.add(advance); p().getEventQueue().addAfterEventListener(advance, BeforeAfterEvent.ADVANCE_DOOM);
            EventListenerImpl<Object> retreat = new EventListenerImpl<Object>() {
                public Class<Object> getDataClass() { return Object.class; }
                public void onNotify(Object ignored) {
                    boolean cursed = false;
                    for (InvestigatorRead i : p().getInvestigators().getOnBoardInvestigators())
                        cursed |= p().getConditionsDeck().hasCondition(i.getInfo().getInvestigatorId(), ConditionId.CURSED);
                    if (!cursed) return;
                    Question<Boolean> choice = new Question<>();
                    choice.setTitle(YigEffects.text("awaken.retreatOrDiscard.title"));
                    choice.addOption(new Question.Option<>(YigEffects.text("awaken.retreatOrDiscard.retreat"), false));
                    choice.addOption(new Question.Option<>(YigEffects.text("awaken.retreatOrDiscard.discard"), true));
                    p().getGameService().ask(choice).subscribe(answer -> sequence(() -> {
                        if (answer.getResponseData()) {
                            cureCurse();
                            p().getService().skipAfterEvent(BeforeAfterEvent.RETREAT_DOOM, null);
                        }
                    }));
                }
            };
            listeners.add(retreat); p().getEventQueue().addBeforeEventListener(retreat, BeforeAfterEvent.RETREAT_DOOM);
            // Deliberately keep the normal REPLACE_INVESTIGATORS flow after awakening.
        }
    }
}
