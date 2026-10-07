package sk.sivak.eldritchhorror.core.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.AfterEventActionImpl;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.action.impl.doomomen.ResolveCurrentMysteryAction;
import sk.sivak.eldritchhorror.core.commandqueue.*;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.phase.PhaseType;
import sk.sivak.eldritchhorror.core.controller.*;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.command.ActionCommand;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class MysteryResolutionTest {
    private CommandQueueImpl queue;
    private ServiceImpl service;
    private MysteryCardInfo mystery;
    private InitGameService init;
    private final List<Throwable> errors = new ArrayList<>();

    @Before public void setUp() {
        CommandQueueImpl.nullifyInstance();
        ServicePlatform.nullifyInstance();
        sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform.nullifyInstance();
        queue = CommandQueueImpl.get();
        queue.setThrowableConsumer(errors::add);
        service = new ServiceImpl();
        service.setCommandQueueFacade(new CommandQueueFacadeImpl());
        ServicePlatform p = ServicePlatform.get();
        p.setService(service);
        p.setEventQueue(new EventQueueImpl());
        p.setDoomOmenService(mock(DoomOmenService.class));
        init = mock(InitGameService.class);
        p.setInitGameService(init);
        p.setEncounterService(mock(EncounterService.class));

        ModelWrite model = mock(ModelWrite.class, RETURNS_DEEP_STUBS);
        when(model.getAncientOne().getAncientOneInfo().getMysteriesRequired()).thenReturn(3);
        when(model.getAncientOne().getAncientOneInfo().getMythosCardCount()).thenReturn(16);
        p.setModel(model);
        PhaseWrite phase = mock(PhaseWrite.class);
        when(phase.getPhaseType()).thenReturn(PhaseType.ENCOUNTER);
        p.setPhaseWrite(phase);
        p.setMythosDeck(mock(MythosDeckWrite.class));

        mystery = mock(MysteryCardInfo.class);
        when(mystery.getMysteryComplexity()).thenReturn(1);
        MysteryDeckWrite deck = mock(MysteryDeckWrite.class);
        when(deck.getCurrentMysteryCard()).thenReturn(mystery);
        when(deck.getSolvedMysteriesCount()).thenReturn(1);
        p.setMysteryDeck(deck);

        GameController game = mock(GameController.class);
        when(game.showWholeWorld()).thenReturn(Completable.complete());
        when(game.showCurrentMysteryCard(false)).thenReturn(Completable.complete());
        when(game.showWorldBackground()).thenReturn(Completable.complete());
        when(game.showCustomBackground(anyString())).thenReturn(Completable.complete());
        when(game.ask(any())).thenReturn(Single.just(null));
        p.setGameController(game);
        MonsterController monsters = mock(MonsterController.class);
        when(monsters.tearMysteryCardApart()).thenReturn(Completable.complete());
        p.setMonsterController(monsters);
    }

    @After public void tearDown() {
        CommandQueueImpl.nullifyInstance();
        ServicePlatform.nullifyInstance();
        sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform.nullifyInstance();
    }

    @Test public void newMysteryMonsterSpawnPreservesSolvedResult() {
        when(mystery.getProgress()).thenReturn(1);
        // Registering Yig's next mystery queues a spawn with a different result type.
        doAnswer(call -> {
            service.convertTo(SpawnMonsterData.class, SpawnMonsterData::new);
            return null;
        }).when(init).registerCurrentMysteryCard();

        assertResolution(true);
        verify(init).registerCurrentMysteryCard();
    }

    @Test public void unsolvedMysteryPreservesFalseAfterQueuedDisplayWork() {
        when(mystery.getProgress()).thenReturn(0);
        sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform listeners =
                sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform.get();
        listeners.setService(service);
        EncounterService encounters = mock(EncounterService.class);
        when(encounters.displayButtons(any(String[].class))).thenAnswer(call -> {
            service.convertTo(String.class, () -> "display result");
            return Single.just(0);
        });
        listeners.setEncounterService(encounters);
        assertResolution(false);
    }

    private void assertResolution(boolean expected) {
        AfterEventActionImpl<Boolean> after = new AfterEventActionImpl<>();
        after.setBeforeAfterEvent(BeforeAfterEvent.RESOLVE_CURRENT_MYSTERY);
        ActionCommand<Boolean, Boolean> afterCommand = new ActionCommand<>(after, "AFTER_RESOLVE_CURRENT_MYSTERY");
        List<Boolean> results = new ArrayList<>();
        afterCommand.addOnCompletedListener(new rx.SingleSubscriber<Boolean>() {
            @Override public void onSuccess(Boolean result) { results.add(result); }
            @Override public void onError(Throwable error) { errors.add(error); }
        });
        queue.addCommands(new Command[]{new ActionCommand<>(new ResolveCurrentMysteryAction(), "RESOLVE_CURRENT_MYSTERY"), afterCommand});
        for (int i = 0; i < 100 && results.isEmpty() && errors.isEmpty(); i++) queue.tick();
        assertTrue(errors.toString(), errors.isEmpty());
        assertEquals(java.util.Collections.singletonList(expected), results);
    }
}
