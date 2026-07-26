package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import java8.features.function.Consumer;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.mythos.BlueMythosCard;
import sk.sivak.eldritchhorror.core.constants.mythos.MythosCard;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.constants.reference.ReferenceInfo;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;
import sk.sivak.eldritchhorror.core.model.BackgroundData;
import sk.sivak.eldritchhorror.core.model.BackgroundModelRead;

import java.util.List;


import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_ANCIENT_ONE;
import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_CARDS_1;
import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_CARDS_2;
import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_CARDS_3;
import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_MONSTER;
import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_RUMORS;
import static sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType.CHARGE;
import static sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType.FIRE;
import static sk.sivak.eldritchhorror.core.model.BackgroundModelRead.BackgroundType.CUSTOM;

public class DrawMythosCardAction extends AbstractHookableAction<Object, Void> {

    public DrawMythosCardAction() {
        super(BeforeAfterEvent.DRAW_MYTHOS_CARD);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Void> ss) {
        GoogleServicesHolder.getAnalyticsTracker().trackScreenName("Phase: Mythos ("+ ServicePlatform.get().getModel().getCurrentRound()+")");
        ServicePlatform.get().getInvestigators().unsetActiveInvestigator();
        MythosCard mythosCard = ServicePlatform.get().getMythosDeck().drawMythosCard();
        ServicePlatform.get().getGameController().hideButtonsAndInvestigatorHud(false);
        ReferenceInfo referenceCard = ServicePlatform.get().getModel().getReferenceCard();
        showBackground();
        int currentRound = ServicePlatform.get().getMythosDeck().getDrawnCardsCount();
        int totalRounds = ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo().getMythosCardCount();


        ServicePlatform.get().getGameController().hideCityInfoLabels();

        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().showTypewriterPaper(true);
        ServicePlatform.get().getEncounterService().typeHeader("End of round "+currentRound+"/" + totalRounds);
        for (MythosCard.BasicMythosAction basicMythosAction : mythosCard.getBasicMythosActions()) {
            switch (basicMythosAction) {
                case ADVANCE_OMEN:
                    advanceOmen();
                    break;
                case MONSTER_SURGE:
                    monsterSurge(referenceCard.getMonsterSurge());
                    break;
                case SPAWN_CLUE:
                    spawnClue(referenceCard.getSpawnClues());
                    break;
                case RECKONING:
                    reckoning();
                    break;
                case SPAWN_GATE:
                    spawnGate(referenceCard.getSpawnGates());
                    break;
            }
        }
        ServicePlatform.get().getEncounterService().typeHeader(" \n" +mythosCard.getTitle());
        ServicePlatform.get().getEncounterService().typeFlavor(mythosCard.getFlavor());
        TypewriterUtils.confirmInfos(mythosCard.getInfos()).subscribe(() -> {
            unsetActiveInvestigator();
            ServicePlatform.get().getModel().getBackgroundModel().setCurrentBackground(null);
            ServicePlatform.get().getGameController().showWorldBackground().subscribe();
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            ServicePlatform.get().getService().addEventCommand(in -> {ServicePlatform.get().getModel().setMythosCardTextEffect(true);});
            ServicePlatform.get().getEventListenerProvider().executeMythosText(mythosCard.getMythosColor(), mythosCard.getId());
            ServicePlatform.get().getService().addEventCommand(in -> {ServicePlatform.get().getModel().setMythosCardTextEffect(false);});
            ServicePlatform.get().getService().addEventCommand(in -> {
                unsetActiveInvestigator();
            });
            ServicePlatform.get().getGameService().checkIfSomeoneIsDefeated();
            ServicePlatform.get().getService().convertToNull();
            ServicePlatform.get().getService().release();
        });
        ServicePlatform.get().getService().release();
        ss.onSuccess(null);
    }

    private void spawnGate(int gatesCount) {
        String message;
        switch (gatesCount) {
            case 1:
                message = " \n[#BAD]One Gate is spawned![]";
                break;
            case 2:
                message = " \n[#BAD]Two Gates are spawned![]";
                break;
            case 3:
                message = " \n[#BAD]Three Gates are spawned![]";
                break;
            default:
                throw new IllegalArgumentException();
        }
        TypewriterUtils.confirmInfos(message).subscribe(() -> {
            unsetActiveInvestigator();
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            ServicePlatform.get().getModel().getBackgroundModel().setCurrentBackground(null);
            ServicePlatform.get().getGameController().showWorldBackground().subscribe();
            ServicePlatform.get().getGameService().spawnGates(gatesCount, 1).subscribe();
            ServicePlatform.get().getService().addEventCommand(in -> {
                unsetActiveInvestigator();
            });
            ServicePlatform.get().getGameService().checkIfSomeoneIsDefeated();
            ServicePlatform.get().getService().addEventCommand(x -> {
                showBackground();
            });
            ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
            ServicePlatform.get().getService().release();
        });

    }

    private void reckoning() {
        TypewriterUtils.confirmInfos(" \n[#BAD]Reckoning![]").subscribe(() -> {
            unsetActiveInvestigator();

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getEventQueue().fireDirectEvent(RECKONING_MONSTER, CHARGE);
                ServicePlatform.get().getEventQueue().fireDirectEvent(RECKONING_ANCIENT_ONE, CHARGE);
                ServicePlatform.get().getEventQueue().fireDirectEvent(RECKONING_RUMORS, CHARGE);
                ServicePlatform.get().getEventQueue().fireDirectEvent(RECKONING_CARDS_1, CHARGE);
                ServicePlatform.get().getEventQueue().fireDirectEvent(RECKONING_CARDS_2, CHARGE);
                ServicePlatform.get().getEventQueue().fireDirectEvent(RECKONING_CARDS_3, CHARGE);
            });
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getEventQueue().fireDirectEvent(RECKONING_MONSTER, FIRE);
            });
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getEventQueue().fireDirectEvent(RECKONING_ANCIENT_ONE, FIRE);
            });
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getEventQueue().fireDirectEvent(RECKONING_RUMORS, FIRE);
            });
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEventQueue().fireDirectEvent(RECKONING_CARDS_1, FIRE);
                ServicePlatform.get().getEventQueue().fireDirectEvent(RECKONING_CARDS_2, FIRE);
                ServicePlatform.get().getEventQueue().fireDirectEvent(RECKONING_CARDS_3, FIRE);
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().addEventCommand(x -> {
                if (ServicePlatform.get().getModel().isReckoningTriggered()) {
                    ServicePlatform.get().getModel().setReckoningTriggered(false);
                    ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                    unsetActiveInvestigator();
                    showBackground();
                } else {
                    ServicePlatform.get().getEncounterService().typeInfo("No reckoning effects.");
                }
            });
            ServicePlatform.get().getService().release();
        });
    }

    private void spawnClue(int cluesCount) {
        String message;
        switch (cluesCount) {
            case 1:
                message = " \n[#GOOD]One Clue is spawned![]";
                break;
            case 2:
                message = " \n[#GOOD]Two Clues are spawned![]";
                break;
            case 3:
                message = " \n[#GOOD]Three Clues are spawned![]";
                break;
            case 4:
                message = " \n[#GOOD]Four Clues are spawned![]";
                break;
            default:
                throw new IllegalArgumentException();
        }
        TypewriterUtils.confirmInfos(message).subscribe(() -> {
            unsetActiveInvestigator();
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            ServicePlatform.get().getTokenService().spawnClues(cluesCount).subscribe();
            ServicePlatform.get().getService().addEventCommand(x -> {
                unsetActiveInvestigator();
                showBackground();
            });
            ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
            ServicePlatform.get().getService().release();
        });

    }

    private void monsterSurge(int monsterSurge) {
        ServicePlatform.get().getService().addEventCommand((Consumer<?>) in -> {
            OmenColor omenColor = ServicePlatform.get().getOmenTrack().getCurrentOmen().getOmenColor();
            String omenColorPretty = omenColor.getPrettyString();
            List<LocationId> gateLocations = ServicePlatform.get().getGateStack().getSpawnedGates(omenColor.toGateColor());
            if (gateLocations.isEmpty()) {
                spawnGate(1);
                return;
            }
            String message;
            switch (monsterSurge) {
                case 1:
                    message = " \n[#BAD]One Monster surges from ";
                    break;
                case 2:
                    message = " \n[#BAD]Two Monsters surge from ";
                    break;
                case 3:
                    message = " \n[#BAD]Three Monsters surge from ";
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            if (gateLocations.size() == 1){
                message += omenColorPretty+" Gate![]";
            } else {
                message += omenColorPretty+" Gates![]";
            }
            TypewriterUtils.confirmInfos(message).subscribe(() -> {
                unsetActiveInvestigator();
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().hideTypewriterPaper();
                ServicePlatform.get().getModel().getBackgroundModel().setCurrentBackground(null);
                ServicePlatform.get().getGameController().showWorldBackground().subscribe();
                ServicePlatform.get().getGameService().monsterSurge(monsterSurge);
                ServicePlatform.get().getGameService().checkIfSomeoneIsDefeated();
                ServicePlatform.get().getService().addEventCommand(x -> {
                    unsetActiveInvestigator();
                    showBackground();
                });
                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                ServicePlatform.get().getService().release();
            });
        });
    }

    private void advanceOmen() {
        TypewriterUtils.confirmInfos("[#BAD]Omen advances![]").subscribe(() -> {
            unsetActiveInvestigator();
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            ServicePlatform.get().getDoomOmenService().advanceOmen();
            ServicePlatform.get().getService().addEventCommand(x -> {
                OmenColor omenColor = ServicePlatform.get().getOmenTrack().getCurrentOmen().getOmenColor();
                if (ServicePlatform.get().getGateStack().getSpawnedGates(omenColor.toGateColor()).size() > 0) {
                    unsetActiveInvestigator();
                    showBackground();
                }
            });
            ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
            ServicePlatform.get().getService().release();
        });

    }

    private void showBackground() {
        ServicePlatform.get().getModel().getBackgroundModel().setCurrentBackground(
                new BackgroundData(CUSTOM, "mythos", "encounter/background/mythos.jpg"));
        ServicePlatform.get().getGameController().showCustomBackground("mythos").subscribe();
        ServicePlatform.get().getGameController().showWholeWorld().subscribe();
    }

    private void unsetActiveInvestigator() {
        ServicePlatform.get().getGameController().hideButtonsAndInvestigatorHud(false);
        ServicePlatform.get().getInvestigators().unsetActiveInvestigator();
        ServicePlatform.get().getGameController().unsetActiveInvestigator();
    }


}
