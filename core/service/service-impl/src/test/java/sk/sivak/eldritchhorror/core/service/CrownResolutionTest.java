package sk.sivak.eldritchhorror.core.service;

import java.util.*;
import org.junit.*;
import rx.Completable;
import rx.Single;
import rx.subjects.PublishSubject;
import sk.sivak.eldritchhorror.core.action.*;
import sk.sivak.eldritchhorror.core.action.impl.action.investigator.*;
import sk.sivak.eldritchhorror.core.action.impl.game.*;
import sk.sivak.eldritchhorror.core.action.provider.*;
import sk.sivak.eldritchhorror.core.commandqueue.*;
import sk.sivak.eldritchhorror.core.constants.*;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.*;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.constants.question.*;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.yig.YigMysteryListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.command.ActionCommand;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** The reported save has no active investigator, two Spy Clues and an empty Priest HUD. */
public class CrownResolutionTest {
    private final InvestigatorId owner = InvestigatorId.THE_SPY;
    private InvestigatorId active;
    private int clues, hudClues;
    private CommandQueueImpl queue;
    private ServiceImpl service;
    private final List<Throwable> errors = new ArrayList<>();
    private final List<String> effects = new ArrayList<>();

    @After public void cleanup() {
        CommandQueueImpl.nullifyInstance();
        ServicePlatform.nullifyInstance();
        sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform.nullifyInstance();
    }

    @Test public void paymentWaitsForMysteryAndHolderHudWithNoPreviousInvestigator() {
        resolve(null, 0, false);
    }

    @Test public void paymentRestoresPreviousInvestigatorAndHud() {
        resolve(InvestigatorId.THE_PRIEST, 0, false);
    }

    @Test public void clueCreditReducesActualPayment() {
        resolve(null, 1, false);
    }

    @Test public void fullyCreditedMysteryDoesNotSpendClues() {
        resolve(null, 2, false);
    }

    @Test public void rerollUsesHolderCluesBeforeCheckingMysteryAffordability() {
        resolve(null, 0, true);
    }

    private void resolve(InvestigatorId previous, int credit, boolean reroll) {
        cleanup();
        active = previous; clues = 2; hudClues = 0;
        queue = CommandQueueImpl.get(); queue.setThrowableConsumer(errors::add);
        CommandQueueFacadeImpl facade = new CommandQueueFacadeImpl();
        EventQueueImpl events = new EventQueueImpl();
        ActionProvider actions = mock(ActionProvider.class);
        when(actions.getInitValueAction(any())).thenAnswer(call -> new InitValueActionImpl<>(call.getArgument(0)));
        when(actions.getBeforeEventAction(any())).thenAnswer(call -> {
            BeforeEventActionImpl<Object> action = new BeforeEventActionImpl<>();
            action.setBeforeAfterEvent(call.getArgument(0)); return action;
        });
        when(actions.getAfterEventAction(any())).thenAnswer(call -> {
            AfterEventActionImpl<Object> action = new AfterEventActionImpl<>();
            action.setBeforeAfterEvent(call.getArgument(0)); return action;
        });
        service = spy(new ServiceImpl());
        service.setCommandQueueFacade(facade); service.setActionProvider(actions);
        ServicePlatform p = ServicePlatform.get(); p.setService(service); p.setEventQueue(events);
        sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform listeners =
                sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform.get();
        listeners.setService(service); listeners.setEventQueue(events);

        InvestigatorWrite holder = mock(InvestigatorWrite.class, RETURNS_DEEP_STUBS);
        when(holder.getInfo().getInvestigatorId()).thenReturn(owner);
        when(holder.getCurrentHealth()).thenReturn(7); when(holder.getCurrentSanity()).thenReturn(5);
        InvestigatorsWrite investigators = mock(InvestigatorsWrite.class);
        when(investigators.getActiveInvestigatorId()).thenAnswer(call -> active);
        when(investigators.getActiveInvestigator()).thenAnswer(call -> {
            assertEquals(owner, active); return holder;
        });
        when(investigators.getInvestigator(any())).thenReturn(holder);
        when(investigators.getOnBoardInvestigators()).thenAnswer(call -> Collections.singletonList(holder));
        when(investigators.getPlayers()).thenReturn(2);
        doAnswer(call -> { active = call.getArgument(0); effects.add("active:" + active); return null; })
                .when(investigators).changeActiveInvestigator(any(InvestigatorId.class));
        doAnswer(call -> { active = null; effects.add("active:null"); return null; })
                .when(investigators).unsetActiveInvestigator();
        p.setInvestigators(investigators); listeners.setInvestigators(investigators);
        CluePoolWrite pool = mock(CluePoolWrite.class);
        when(pool.getClueCount(owner)).thenAnswer(call -> clues);
        doAnswer(call -> { assertEquals(owner, active); assertTrue(clues > 0); clues--; return null; })
                .when(pool).loseClue(owner);
        p.setCluePool(pool);
        ModelWrite model = mock(ModelWrite.class);
        when(model.getBackgroundModel()).thenReturn(mock(BackgroundModelWrite.class));
        p.setModel(model);

        InvestigatorActionProvider investigatorActions = mock(InvestigatorActionProvider.class);
        when(investigatorActions.changeActiveInvestigator(any())).thenAnswer(call -> new ChangeActiveInvestigatorAction(call.getArgument(0)));
        when(investigatorActions.unsetActiveInvestigator()).thenAnswer(call -> new UnsetActiveInvestigatorAction());
        when(investigatorActions.findActiveInvestigator()).thenAnswer(call -> new FindActiveInvestigatorAction());
        when(investigatorActions.showActiveInvestigator(false)).thenAnswer(call -> new ShowActiveInvestigatorAction(false));
        InvestigatorServiceImpl investigatorService = new InvestigatorServiceImpl();
        investigatorService.setCommandQueueFacade(facade); investigatorService.setActionProvider(actions);
        investigatorService.setInvestigatorActionProvider(investigatorActions);
        listeners.setInvestigatorService(investigatorService);

        TokenActionProvider tokenActions = mock(TokenActionProvider.class);
        when(tokenActions.spend()).thenAnswer(call -> new SpendAction());
        when(tokenActions.loseClue()).thenAnswer(call -> new LoseClueAction());
        TokenServiceImpl tokens = new TokenServiceImpl();
        tokens.setCommandQueueFacade(facade); tokens.setActionProvider(actions); tokens.setTokenActionProvider(tokenActions);
        p.setTokenService(tokens); listeners.setTokenService(tokens);

        PublishSubject<Void> mysteryDismissal = PublishSubject.create();
        PublishSubject<Void> hudAnimation = PublishSubject.create();
        GameController controller = mock(GameController.class);
        when(controller.showCurrentMysteryCard(false)).thenAnswer(call -> {
            effects.add("mystery"); return mysteryDismissal.toCompletable();
        });
        when(controller.showActiveInvestigator(false, false)).thenAnswer(call -> {
            effects.add("show:" + active);
            if (active != owner) { hudClues = 0; return Completable.complete(); }
            return hudAnimation.toCompletable().doOnCompleted(() -> hudClues = clues);
        });
        when(controller.loseClue(1)).thenAnswer(call -> {
            // Same precondition as ContainerBar.getFullContainerPosition().
            if (hudClues == 0) throw new ArrayIndexOutOfBoundsException("index=-1: empty displayed clue bar");
            assertEquals(owner, active); hudClues--; effects.add("clue"); return Completable.complete();
        });
        p.setGameController(controller);
        MysteryCardInfo mystery = mock(MysteryCardInfo.class);
        when(mystery.getMysteryCardId()).thenReturn(MysteryCardId.Yig.CROWN_OF_THE_SERPENT);
        when(mystery.getMysteryComplexity()).thenReturn(1); when(mystery.getClueCredit()).thenReturn(credit);
        MysteryDeckWrite mysteries = mock(MysteryDeckWrite.class);
        when(mysteries.getCurrentMysteryCard()).thenReturn(mystery); p.setMysteryDeck(mysteries);
        ArtifactsDeckRead artifacts = mock(ArtifactsDeckRead.class);
        when(artifacts.hasArtifact(owner, ArtifactId.SERPENT_CROWN)).thenReturn(true);
        listeners.setArtifactsDeck(artifacts);
        List<AssetInfo> allies = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            AssetInfo ally = mock(AssetInfo.class);
            when(ally.getTraits()).thenReturn(Collections.singleton(AssetTrait.ALLY)); allies.add(ally);
        }
        AssetDeckRead assets = mock(AssetDeckRead.class);
        when(assets.getAssets(owner)).thenAnswer(call -> new ArrayList<>(allies)); listeners.setAssetDeck(assets);
        doAnswer(call -> {
            AssetInfo ally = call.getArgument(1);
            service.addEventCommand(in -> { assertEquals(owner, active); assertTrue(allies.remove(ally)); effects.add("ally"); });
            return null;
        }).when(service).discardAssetFromInvestigator(eq(owner), any(AssetInfo.class), eq(true));
        GameService game = mock(GameService.class);
        doAnswer(call -> {
            facade.execute(new ActionCommand<>(new ShowCurrentMysteryCardAction(call.getArgument(0), call.getArgument(1)), "SHOW_MYSTERY"));
            return null;
        }).when(game).showCurrentMysteryCard(anyBoolean(), anyBoolean());
        when(game.ask(any())).thenAnswer(call -> Single.create(sub -> service.addEventCommand(in -> {
            assertEquals(owner, active); effects.add("ask");
            Answer<Boolean, Object> answer = new Answer<>(); answer.setResponseData(true); sub.onSuccess(answer);
        })));
        when(game.showCard(any(ShowCardRequest.class))).thenAnswer(call ->
                Single.create(sub -> service.addEventCommand(in -> { sub.onSuccess(null); })));
        doAnswer(call -> { service.addEventCommand(in -> { effects.add("progress"); }); return null; })
                .when(game).advanceCurrentMysteryCard(1);
        listeners.setGameService(game);
        TestService tests = mock(TestService.class);
        when(tests.test(Stat.WILL, -1, 1)).thenAnswer(call -> Single.create(sub -> {
            service.hold();
            if (reroll) tokens.loseClue();
            service.addEventCommand(in -> {
                assertEquals(owner, active); effects.add("test");
                TestData result = new TestData(); result.setDiceRolls(Collections.emptyList()); result.setScoreBonus(1);
                sub.onSuccess(result);
            });
            service.release();
        }));
        listeners.setTestService(tests);
        YigMysteryListener listener = new YigMysteryListener(mystery); listener.justRegisterListeners(0);
        service.addEventCommand(in -> { events.fireBeforeEvent(BeforeAfterEvent.RESOLVE_CURRENT_MYSTERY, null); });
        tick();
        assertEquals(Arrays.asList("active:" + owner, "mystery"), effects);
        mysteryDismissal.onCompleted(); tick();
        assertEquals(Arrays.asList("active:" + owner, "mystery", "show:" + owner), effects);
        assertEquals("Do not pay before the HUD finishes refreshing", 2, clues);
        hudAnimation.onCompleted(); tick();
        assertEquals(previous, active);
        assertEquals(reroll ? 1 : credit, clues);
        assertEquals(previous == null ? clues : 0, hudClues);
        assertEquals(reroll ? 2 : 0, allies.size());
        assertEquals(reroll ? 0 : 1, Collections.frequency(effects, "progress"));
        assertEquals(reroll ? 1 : 2 - credit, Collections.frequency(effects, "clue"));
        assertTrue(effects.indexOf("test") < effects.indexOf("active:" + previous));
        if (previous != null) assertEquals("show:" + previous, effects.get(effects.size() - 1));
        listener.unregister();
    }

    private void tick() {
        for (int i = 0; i < 250 && errors.isEmpty(); i++) queue.tick();
        assertTrue(errors.toString(), errors.isEmpty());
    }
}
