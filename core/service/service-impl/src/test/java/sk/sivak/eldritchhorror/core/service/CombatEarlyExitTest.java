package sk.sivak.eldritchhorror.core.service;

import java.util.*;
import org.junit.*;
import rx.subjects.PublishSubject;
import sk.sivak.eldritchhorror.core.action.*;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.HideCombatTableAction;
import sk.sivak.eldritchhorror.core.action.provider.*;
import sk.sivak.eldritchhorror.core.commandqueue.*;
import sk.sivak.eldritchhorror.core.controller.TestController;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.constants.monster.*;
import sk.sivak.eldritchhorror.core.constants.monster.epic.ChildrenOfYigMonster;
import sk.sivak.eldritchhorror.core.constants.monster.epic.DoppelgangerMonster;
import sk.sivak.eldritchhorror.core.eventlistener.monster.*;
import sk.sivak.eldritchhorror.core.model.ModelRead;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

public class CombatEarlyExitTest {
    @After public void cleanup() {
        CommandQueueImpl.nullifyInstance();
        ServicePlatform.nullifyInstance();
        sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform.nullifyInstance();
    }

    @Test public void earlyExitSkipsDamageAndWaitsForFadeBeforeContinuing() {
        runCombat(null, null, false);
    }

    @Test public void childrenOfYigEscapesOnlyAfterFadeOnFailedObservation() {
        runCombat(new ChildrenOfYigMonster(), new YigEpicMonsterListener(), false);
    }

    @Test public void childrenOfYigSuccessfulObservationContinuesToDamage() {
        runCombat(new ChildrenOfYigMonster(), new YigEpicMonsterListener(), true);
    }

    @Test public void vampireFailedHorrorSkipsDamage() {
        runCombat(new VampireMonster(), new VampireMonsterListener(), false);
    }

    @Test public void doppelgangerFailedHorrorSkipsDamage() {
        runCombat(new DoppelgangerMonster(), new DoppelgangerMonsterListener(), false);
    }

    private void runCombat(MonsterInfo monster, AbstractMonsterListener listener, boolean success) {
        cleanup();
        CommandQueueImpl queue = CommandQueueImpl.get();
        List<Throwable> errors = new ArrayList<>();
        List<String> events = new ArrayList<>();
        queue.setThrowableConsumer(errors::add);
        TestServiceImpl tests = new TestServiceImpl();
        tests.setCommandQueueFacade(new CommandQueueFacadeImpl());
        ActionProvider actions = mock(ActionProvider.class);
        when(actions.getBeforeEventAction(any())).thenAnswer(call -> {
            BeforeEventActionImpl<Object> action = new BeforeEventActionImpl<>();
            action.setBeforeAfterEvent(call.getArgument(0)); return action;
        });
        when(actions.getAfterEventAction(any())).thenAnswer(call -> {
            AfterEventActionImpl<Object> action = new AfterEventActionImpl<>();
            action.setBeforeAfterEvent(call.getArgument(0)); return action;
        });
        tests.setActionProvider(actions);
        TestActionProvider testActions = mock(TestActionProvider.class);
        when(testActions.hideCombatTable(any())).thenAnswer(call -> new HideCombatTableAction(call.getArgument(0)));
        tests.setTestActionProvider(testActions);
        EventQueueImpl eventQueue = new EventQueueImpl();
        ServicePlatform.get().setEventQueue(eventQueue);
        PublishSubject<Void> fading = PublishSubject.create();
        TestController controller = mock(TestController.class);
        when(controller.hideCombatTable()).thenAnswer(call -> {
            events.add("fade"); return fading.toCompletable();
        });
        ServicePlatform.get().setTestController(controller);
        CombatData combat = new CombatData();
        if (listener != null) {
            combat.setMonsterInfo(monster);
            TestData result = new TestData(); result.setDiceRolls(Collections.emptyList());
            result.setScoreBonus(success ? 1 : 0); combat.setHorrorTestResult(result);
            sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform platform =
                    sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform.get();
            platform.setEventQueue(eventQueue);
            ModelRead model = mock(ModelRead.class, RETURNS_DEEP_STUBS);
            when(model.getReferenceCard().getPlayers()).thenReturn(2);
            platform.setModel(model);
            Service service = mock(Service.class);
            doAnswer(call -> { tests.hold(); return null; }).when(service).hold();
            doAnswer(call -> { tests.release(); return null; }).when(service).release();
            doAnswer(call -> { tests.skipBeforeEvent(call.getArgument(0), call.getArgument(1)); return null; })
                    .when(service).skipBeforeEvent(any(), any());
            platform.setService(service);
            MonsterService monsters = mock(MonsterService.class);
            doAnswer(call -> { tests.addEventCommand(in -> { events.add("escape"); }); return null; })
                    .when(monsters).moveMonster(any(), any());
            platform.setMonsterService(monsters);
            listener.register(monster);
        }
        tests.hold();
        tests.addEventCommand(in -> { });
        // The same queued skip used by Children of Yig, Vampire and Doppelganger.
        tests.addEventCommand(in -> {
            if (listener == null) tests.skipBeforeEvent(BeforeAfterEvent.HIDE_COMBAT_TABLE, null);
            else eventQueue.fireAfterEvent(BeforeAfterEvent.AFTER_HORROR_CHECK, combat);
        });
        tests.addEventCommand(in -> { events.add("damage"); });
        tests.hideCombatTable(combat);
        tests.addEventCommand(in -> { events.add("continue"); });
        tests.release();
        for (int i = 0; i < 40; i++) queue.tick();
        assertTrue(errors.toString(), errors.isEmpty());
        assertEquals(success ? Arrays.asList("damage", "fade") : Collections.singletonList("fade"), events);
        fading.onCompleted();
        for (int i = 0; i < 40; i++) queue.tick();
        assertTrue(errors.toString(), errors.isEmpty());
        assertEquals(success ? Arrays.asList("damage", "fade", "continue")
                : monster instanceof ChildrenOfYigMonster ? Arrays.asList("fade", "escape", "continue")
                : Arrays.asList("fade", "continue"), events);
        verify(controller, times(1)).hideCombatTable();
        if (listener != null) listener.unregister();
    }
}
