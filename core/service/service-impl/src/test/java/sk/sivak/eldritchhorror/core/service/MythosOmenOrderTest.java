package sk.sivak.eldritchhorror.core.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.action.provider.ActionProviderImpl;
import sk.sivak.eldritchhorror.core.action.provider.DoomOmenActionProviderImpl;
import sk.sivak.eldritchhorror.core.commandqueue.CommandQueueFacadeImpl;
import sk.sivak.eldritchhorror.core.commandqueue.CommandQueueImpl;
import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.mythos.*;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.controller.*;
import sk.sivak.eldritchhorror.core.eventlistener.provider.EventListenerProvider;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.model.*;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MythosOmenOrderTest {
    private final List<String> effects = new ArrayList<>();
    private final DoomOmenServiceImpl mythos = new DoomOmenServiceImpl();
    private final ServiceImpl service = new ServiceImpl();
    private final MythosDeckWrite deck = mock(MythosDeckWrite.class);
    private final GateStackWrite gates = mock(GateStackWrite.class);
    private final EventListenerProvider text = mock(EventListenerProvider.class);
    private final OmenInfo omen = mock(OmenInfo.class);

    @Before public void setUp() {
        resetPlatforms();
        CommandQueueImpl.get().setThrowableConsumer(error -> { throw new AssertionError(error); });
        CommandQueueFacadeImpl queue = new CommandQueueFacadeImpl();
        ActionProviderImpl actions = new ActionProviderImpl();
        service.setCommandQueueFacade(queue);
        service.setActionProvider(actions);
        mythos.setCommandQueueFacade(queue);
        mythos.setActionProvider(actions);
        mythos.setDoomOmenActionProvider(new DoomOmenActionProviderImpl());

        ServicePlatform p = ServicePlatform.get();
        p.setService(service);
        p.setEventQueue(new EventQueueImpl());
        p.setDoomOmenService(mythos);
        p.setMythosDeck(deck);
        p.setGateStack(gates);
        p.setEventListenerProvider(text);
        p.setInvestigators(mock(InvestigatorsWrite.class));
        p.setModel(mock(ModelWrite.class, RETURNS_DEEP_STUBS));
        when(p.getModel().getReferenceCard().getSpawnClues()).thenReturn(1);
        when(p.getModel().getReferenceCard().getSpawnGates()).thenReturn(1);
        when(p.getModel().getReferenceCard().getMonsterSurge()).thenReturn(1);
        when(p.getModel().getAncientOne().getAncientOneInfo().getMythosCardCount()).thenReturn(16);
        p.setMysteryDeck(mock(MysteryDeckWrite.class, RETURNS_DEEP_STUBS));
        when(p.getMysteryDeck().getCurrentMysteryCard().getMysteryComplexity()).thenAnswer(call -> {
            effects.add("mystery");
            return 1;
        });

        p.setOmenTrack(mock(OmenTrackWrite.class));
        when(p.getOmenTrack().getCurrentOmen()).thenReturn(omen);
        when(omen.getOmenColor()).thenReturn(OmenColor.GREEN);
        doAnswer(call -> {
            effects.add("move");
            when(omen.getOmenColor()).thenReturn(OmenColor.BLUE);
            return null;
        }).when(p.getOmenTrack()).advanceOmen();
        p.setDoomTrack(mock(DoomTrackWrite.class));
        when(p.getDoomTrack().advanceDoom()).thenAnswer(call -> {
            effects.add("doom " + omen.getOmenColor());
            return 5;
        });
        p.setDoomOmenController(mock(DoomOmenController.class));
        when(p.getDoomOmenController().advanceDoom(anyInt(), anyInt())).thenReturn(Completable.complete());
        when(p.getDoomOmenController().advanceOmen(any())).thenReturn(Completable.complete());
        p.setGameController(mock(GameController.class));
        when(p.getGameController().showWholeWorld()).thenReturn(Completable.complete());
        when(p.getGameController().showWorldBackground()).thenReturn(Completable.complete());
        when(p.getGameController().showCustomBackground(anyString())).thenReturn(Completable.complete());
        when(p.getGameController().moveCameraToLocation(any())).thenReturn(Completable.complete());
        p.setGameService(mock(GameService.class));
        when(p.getGameService().ask(any())).thenAnswer(call -> queuedResponse(null));
        when(p.getGameService().spawnGates(1, 1)).thenReturn(Single.just(Collections.emptyList()));
        p.setTokenService(mock(TokenService.class));
        when(p.getTokenService().spawnClues(1)).thenReturn(Single.just(Collections.emptyList()));
        p.setEncounterService(mock(EncounterService.class));
        when(p.getEncounterService().displayButtons("OK")).thenAnswer(call -> queuedResponse(0));
        sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform listeners =
                sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform.get();
        listeners.setService(service);
        listeners.setEncounterService(p.getEncounterService());
        doAnswer(call -> { effects.add("text"); return null; }).when(text).executeMythosText(any(), anyInt(), any());
    }

    private <T> Single<T> queuedResponse(T value) {
        return Single.create(sub -> service.addEventCommand(in -> { sub.onSuccess(value); }));
    }

    @After public void resetPlatforms() {
        CommandQueueImpl.nullifyInstance();
        ServicePlatform.nullifyInstance();
        sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform.nullifyInstance();
    }

    @Test public void greenCardCountsEachCurrentGateBeforeTheSurgeAndMovesAfterMystery() {
        when(gates.getSpawnedGates(GateColor.GREEN)).thenReturn(Arrays.asList(LocationId.LONDON, LocationId.ROME));
        doAnswer(call -> { effects.add("surge " + omen.getOmenColor()); return null; })
                .when(ServicePlatform.get().getGameService()).monsterSurge(1);
        runPhase(new GreenMythosCard());
        verify(ServicePlatform.get().getEncounterService()).typeInfo("1 Doom per Green Gate.");
        assertEquals(Arrays.asList("doom GREEN", "doom GREEN", "surge GREEN", "text", "mystery", "move", "end"), effects);
        verify(gates, never()).getSpawnedGates(GateColor.BLUE);
    }

    @Test public void yellowCardStillMovesAfterMysteryWithNoMatchingGates() {
        EncounterService paper = ServicePlatform.get().getEncounterService();
        doAnswer(call -> {
            verify(paper).typeInfo("No open Green Gates.");
            verify(paper, never()).hideTypewriterPaper();
            return null;
        }).when(paper).typeInfo("No reckoning effects.");
        runPhase(new YellowMythosCard());
        verify(paper).typeInfo("No reckoning effects.");
        assertEquals(Arrays.asList("text", "mystery", "move", "end"), effects);
    }

    @Test public void unprintedAdvanceDuringCardTextUsesTheNewOmenImmediately() {
        when(gates.getSpawnedGates(GateColor.BLUE)).thenReturn(Collections.singletonList(LocationId.ROME));
        doAnswer(call -> {
            effects.add("text");
            mythos.advanceOmen();
            return null;
        }).when(text).executeMythosText(any(), anyInt(), any());
        runPhase(new BlueMythosCard());
        assertEquals(Arrays.asList("text", "move", "doom BLUE", "mystery", "end"), effects);
    }

    private void runPhase(MythosCard card) {
        when(deck.drawMythosCard()).thenReturn(card);
        service.hold();
        mythos.resolveMythosPhase();
        service.addEventCommand(in -> { effects.add("end"); });
        service.release();
        for (int tick = 0; tick < 500 && !effects.contains("end"); tick++) {
            CommandQueueImpl.get().tick();
        }
    }
}
