package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import org.junit.*;
import rx.Single;
import java.lang.reflect.Proxy;
import java.util.*;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.condition.*;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.*;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.constants.location.*;
import sk.sivak.eldritchhorror.core.constants.question.*;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.*;
import sk.sivak.eldritchhorror.core.eventtype.data.CloseGateData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.*;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.model.action.PerformedActions;
import sk.sivak.eldritchhorror.core.eventlistener.action.ActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventlistener.action.character.*;
import sk.sivak.eldritchhorror.core.eventlistener.action.basic.TradeActionListener;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventtype.data.CollectAvailableActionsData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.TravelData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.service.*;
import static org.junit.Assert.*;

public class NewInvestigatorAbilitiesTest {
    private Investigators investigators;
    private ConditionsDeck conditions;
    private EventQueueImpl events;
    private boolean accept;
    private int questions, tickets, boons;
    private int successes, discarded, transfers, freeActions;
    private TravelData movement;
    private PerformedActions performed;
    private final Map<InvestigatorId, AssetId> assets = new HashMap<>();
    private final Set<InvestigatorId> clues = new HashSet<>();
    private final List<ShowCardRequest> shownCards = new ArrayList<>();
    private final List<List<? extends CardInfo>> cardChoices = new ArrayList<>();
    private boolean chooseLastCard, deferDiscard, finishMadness;
    private rx.SingleSubscriber<? super ShowCardResponse> pendingDiscard;
    private boolean deferNotice;
    private Question<?> lastQuestion;
    private rx.SingleSubscriber<? super Answer<Object,Object>> pendingNotice;

    @Before public void setup() {
        ServicePlatform.nullifyInstance();
        investigators = new Investigators();
        investigators.initAvailableInvestigators(true);
        InvestigatorId[] ids = {InvestigatorId.THE_EXPLORER, InvestigatorId.THE_PRIEST, InvestigatorId.THE_BUTLER, InvestigatorId.THE_NUN};
        investigators.initWithPlayers(ids.length);
        InvestigatorInfo[] selected = new InvestigatorInfo[ids.length];
        for (int i=0;i<ids.length;i++)
            for (InvestigatorInfo info:investigators.getAvailableInvestigators())
                if(info.getInvestigatorId()==ids[i])selected[i]=info;
        investigators.initSelectedInvestigators(selected);
        investigators.changeActiveInvestigator(InvestigatorId.THE_EXPLORER);
        for(InvestigatorWrite investigator:investigators.getOnBoardInvestigators()) investigator.setCurrentLocationId(LocationId.LONDON);
        conditions=new ConditionsDeck(); conditions.createDeck();
        events=new EventQueueImpl();
        ServicePlatform.get().setInvestigators(investigators);
        ServicePlatform.get().setConditionsDeck(conditions);
        ServicePlatform.get().setEventQueue(events);
        performed=new PerformedActions();performed.clearPerformedActionsMap();performed.init(ids);
        ServicePlatform.get().setPerformedActions(performed);
        LocationMap map=new LocationMap();map.initLocations();ServicePlatform.get().setLocationMap(map);
        ServicePlatform.get().setService(proxy(Service.class,(method,args)-> {
            if(method.equals("gainAssetFromDeck"))assets.put((InvestigatorId)args[0],(AssetId)args[1]);
            if(method.equals("discardConditionFromInvestigator")){conditions.discard((InvestigatorId)args[0],(ConditionInfo)args[1]);discarded++;}
            if(method.equals("transferCondition")){conditions.transfer((InvestigatorId)args[0],(InvestigatorId)args[1],(ConditionInfo)args[2]);transfers++;}
            if(method.equals("addEventCommand"))((java8.features.function.Consumer<Object>)args[0]).accept(null);
            return null;
        }));
        ServicePlatform.get().setTokenService(proxy(TokenService.class,(method,args)-> {
            if(method.equals("gainClueFromPool"))clues.add((InvestigatorId)args[0]);
            return null;
        }));
        ServicePlatform.get().setBasicActionService(proxy(BasicActionService.class,(method,args)-> {
            if(method.equals("gainTravelTicket")) { tickets++; events.fireAfterEvent(BeforeAfterEvent.GAIN_TRAVEL_TICKET,null); }
            if(method.equals("addFreeAction"))freeActions++;
            if(method.equals("moveSpaces"))movement=(TravelData)args[0];
            return null;
        }));
        ServicePlatform.get().setGameService(proxy(GameService.class,(method,args)-> {
            if(method.equals("ask")) {
                questions++;
                Answer<Object,Object> answer=new Answer<>();
                Question<?> question=(Question<?>)args[0];
                lastQuestion=question;
                if(deferNotice)return Single.<Answer<Object,Object>>create(subscriber -> pendingNotice=subscriber);
                if(question.getTitle().startsWith("Carson:"))answer.setResponseData(PathType.TRAIN);
                else if(question.getTitle().startsWith("Use Ursula"))answer.setResponseData(accept);
                else if(question.getTitle().startsWith("Discard a Madness") && finishMadness)answer.setResponseData(null);
                else if(question.getTitle().startsWith("Discard a Madness") && conditions.hasTrait(InvestigatorId.THE_NUN,ConditionTrait.MADNESS))
                    answer.setResponseData(InvestigatorId.THE_NUN);
                else answer.setResponseData(question.getOptions().get(0).getValue());
                return Single.just(answer);
            }
            if(method.equals("gainCondition"))boons++;
            if(method.equals("showCard")) {
                shownCards.add((ShowCardRequest)args[0]);
                if(deferDiscard)return Single.create(subscriber -> pendingDiscard=subscriber);
                return Single.just(ShowCardResponse.OK);
            }
            return null;
        }));
        ServicePlatform.get().setCardService(proxy(CardService.class,(method,args)-> {
            if(method.equals("selectSingleCard")) {
                List<? extends CardInfo> cards=((SelectCardData)args[0]).getAvailableCards();
                cardChoices.add(new ArrayList<>(cards));
                return Single.just(cards.get(chooseLastCard ? cards.size()-1 : 0));
            }
            return null;
        }));
        ServicePlatform.get().setTestService(proxy(TestService.class,(method,args)-> {
            if(method.equals("test")){
                assertEquals(Stat.WILL,args[0]);
                TestData data=new TestData();data.setDiceRolls(Collections.emptyList());data.setScoreBonus(successes);
                return Single.just(data);
            }
            return null;
        }));
    }
    @After public void cleanup(){ServicePlatform.nullifyInstance();}

    @Test public void explorerExplainsStartingImprovementBeforeOpeningSkillSelection() {
        List<String> calls = new ArrayList<>();
        ServicePlatform.get().setInvestigatorService(proxy(InvestigatorService.class, (method,args) -> {
            calls.add(method);
            return null;
        }));
        deferNotice = true;
        TheExplorerInitListener listener = new TheExplorerInitListener();
        listener.onNotify(InvestigatorId.THE_EXPLORER);
        events.fireAfterEvent(BeforeAfterEvent.SHOW_PHASE, null);
        assertEquals("The Explorer starts with one skill improved.", lastQuestion.getTitle());
        assertNotNull(pendingNotice);
        assertFalse(calls.contains("improveSkill"));
        pendingNotice.onSuccess(new Answer<>());
        assertEquals(1, Collections.frequency(calls, "improveSkill"));
        events.fireAfterEvent(BeforeAfterEvent.SHOW_PHASE, null);
        assertEquals(1, questions);
        listener.unregisterInvestigator();
        listener.justRegisterListeners();
        events.fireAfterEvent(BeforeAfterEvent.SHOW_PHASE, null);
        assertEquals(1, questions);
    }

    @Test public void startingAssetsAndCluesAreGrantedOnlyOnNewInitialization() {
        AbstractInvestigatorInitListener[] listeners={new TheButlerInitListener(),new ThePriestInitListener(),new TheNunInitListener(),new TheExplorerInitListener()};
        for(AbstractInvestigatorInitListener listener:listeners) listener.onNotify(listener.getInvestigatorId());
        assertEquals(AssetId.LUCKY_CIGARETTE_CASE,assets.get(InvestigatorId.THE_BUTLER));
        assertEquals(AssetId.KING_JAMES_BIBLE,assets.get(InvestigatorId.THE_PRIEST));
        assertEquals(AssetId.HOLY_WATER,assets.get(InvestigatorId.THE_NUN));
        assertEquals(AssetId.MINERALOGY_RESEARCH,assets.get(InvestigatorId.THE_EXPLORER));
        assertEquals(new HashSet<>(Arrays.asList(InvestigatorId.THE_BUTLER,InvestigatorId.THE_NUN)),clues);
        assets.clear(); clues.clear();
        for(AbstractInvestigatorInitListener listener:listeners){listener.unregisterInvestigator();listener.justRegisterListeners();}
        assertTrue(assets.isEmpty()); assertTrue(clues.isEmpty());
    }

    @Test public void ursulaDiscountIsSharedOptionalAndConsumedOnlyOnPayment() {
        new TheExplorerInitListener().justRegisterListeners();
        investigators.changeActiveInvestigator(InvestigatorId.THE_PRIEST);
        SpendData preview=spend(true);
        assertEquals(Integer.valueOf(0),preview.getTokenAmountMap().get(TokenType.FOCUS));
        assertEquals(0,questions); assertFalse(exhausted());
        accept=false;
        assertEquals(Integer.valueOf(1),spend(false).getTokenAmountMap().get(TokenType.FOCUS));
        accept=true;
        SpendData actual=spend(false);
        assertEquals(Integer.valueOf(0),actual.getTokenAmountMap().get(TokenType.FOCUS));
        assertFalse(exhausted());
        actual.setPayAction(()->{}); actual.pay();
        assertTrue(exhausted());
        assertEquals(Integer.valueOf(1),spend(true).getTokenAmountMap().get(TokenType.FOCUS));
        actual.revert();
        assertFalse(exhausted());
        actual.pay();
        events.fireDirectEvent(DirectEvent.REENABLE_DISABLED_ABILITIES,null);
        assertFalse(exhausted());
    }
    @Test public void ursulaDoesNotDiscountOtherSpacesOrWhileLostAndUnregisters() {
        TheExplorerInitListener listener=new TheExplorerInitListener(); listener.justRegisterListeners();
        investigators.changeActiveInvestigator(InvestigatorId.THE_PRIEST);
        investigators.getActiveInvestigator().setCurrentLocationId(LocationId.ROME);
        assertEquals(Integer.valueOf(1),spend(true).getTokenAmountMap().get(TokenType.FOCUS));
        investigators.getActiveInvestigator().setCurrentLocationId(LocationId.LONDON);
        investigators.getInvestigator(InvestigatorId.THE_EXPLORER).setLostInTimeAndSpace(true);
        assertEquals(Integer.valueOf(1),spend(true).getTokenAmountMap().get(TokenType.FOCUS));
        investigators.getInvestigator(InvestigatorId.THE_EXPLORER).setLostInTimeAndSpace(false);
        listener.unregisterInvestigator();
        assertEquals(Integer.valueOf(1),spend(true).getTokenAmountMap().get(TokenType.FOCUS));
    }
    @Test public void carsonGrantsOneTicketForPreparingOnHisSpaceAndNeverRecurses() {
        new TheButlerInitListener().justRegisterListeners();
        events.fireAfterEvent(BeforeAfterEvent.GAIN_TRAVEL_TICKET,null);
        assertEquals(0,tickets);
        events.fireAfterEvent(BeforeAfterEvent.SELECT_TRAVEL_TICKET,PathType.SHIP);
        events.fireAfterEvent(BeforeAfterEvent.GAIN_TRAVEL_TICKET,null);
        assertEquals(1,tickets); assertEquals(1,questions);
        events.fireAfterEvent(BeforeAfterEvent.GAIN_TRAVEL_TICKET,null);
        assertEquals(1,tickets);
        investigators.getActiveInvestigator().setCurrentLocationId(LocationId.ROME);
        events.fireAfterEvent(BeforeAfterEvent.SELECT_TRAVEL_TICKET,PathType.SHIP);
        events.fireAfterEvent(BeforeAfterEvent.GAIN_TRAVEL_TICKET,null);
        assertEquals(1,tickets);
    }
    @Test public void mateoOnlyGainsBoonForHisOtherWorldClosureWhenHeHasNone() {
        new ThePriestInitListener().justRegisterListeners();
        CloseGateData otherWorld=new CloseGateData(LocationId.ROME,null,true);
        events.fireAfterEvent(BeforeAfterEvent.CLOSE_GATE,otherWorld);
        assertEquals(0,boons);
        investigators.changeActiveInvestigator(InvestigatorId.THE_PRIEST);
        events.fireAfterEvent(BeforeAfterEvent.CLOSE_GATE,new CloseGateData(LocationId.ROME,null,false));
        assertEquals(0,boons);
        events.fireAfterEvent(BeforeAfterEvent.CLOSE_GATE,otherWorld);
        assertEquals(1,boons);
        conditions.gainCondition(ConditionId.RIGHTEOUS,InvestigatorId.THE_PRIEST);
        events.fireAfterEvent(BeforeAfterEvent.CLOSE_GATE,otherWorld);
        assertEquals(1,boons);
    }
    private boolean exhausted(){return investigators.getInvestigator(InvestigatorId.THE_EXPLORER).isPassiveAbilityDisabled();}
    @Test public void priestExplainsHisBoonAndWaitsForOk() {
        new ThePriestInitListener().justRegisterListeners();
        investigators.changeActiveInvestigator(InvestigatorId.THE_PRIEST);
        deferNotice=true;
        events.fireAfterEvent(BeforeAfterEvent.CLOSE_GATE,new CloseGateData(LocationId.ROME,null,true));
        assertEquals(0,boons);
        assertEquals(1,questions);
        assertEquals("investigator.thePriest.boonNotice",lastQuestion.getTitle());
        assertEquals("investigator/THE_PRIEST.png",lastQuestion.getPortraitTextureName());
        assertEquals(Question.Option.okOption,lastQuestion.getOptions());
        pendingNotice.onSuccess(new Answer<>());
        assertEquals(1,boons);
    }
    @Test public void maryDiscardsOneMadnessPerSuccessAcrossTheBoard() {
        investigators.changeActiveInvestigator(InvestigatorId.THE_NUN);
        investigators.getInvestigator(InvestigatorId.THE_PRIEST).setCurrentLocationId(LocationId.ROME);
        conditions.gainCondition(ConditionId.AMNESIA,InvestigatorId.THE_NUN);
        conditions.gainCondition(ConditionId.PARANOIA,InvestigatorId.THE_PRIEST);
        conditions.gainCondition(ConditionId.HALLUCINATIONS,InvestigatorId.THE_PRIEST);
        conditions.gainCondition(ConditionId.DEBT,InvestigatorId.THE_NUN);
        successes=2; action(new TheNunActionListener()).execute();
        assertEquals(2,discarded);
        assertTrue(conditions.hasCondition(InvestigatorId.THE_NUN,ConditionId.DEBT));
        assertEquals(1,conditions.getCondition(InvestigatorId.THE_PRIEST,ConditionTrait.MADNESS).size());
        assertEquals(2,shownCards.size());
        assertEquals(ConditionId.AMNESIA,shownCards.get(0).getConditionInfo().getId());
        assertEquals("investigator/THE_NUN.png",shownCards.get(0).getPortraitTextureName());
        assertEquals("investigator/THE_PRIEST.png",shownCards.get(1).getPortraitTextureName());
    }
    @Test public void maryLetsAnotherInvestigatorChooseWhichMadnessAndShowsItBeforeDiscarding() {
        investigators.changeActiveInvestigator(InvestigatorId.THE_NUN);
        ConditionInfo paranoia=conditions.gainCondition(ConditionId.PARANOIA,InvestigatorId.THE_PRIEST);
        ConditionInfo hallucinations=conditions.gainCondition(ConditionId.HALLUCINATIONS,InvestigatorId.THE_PRIEST);
        conditions.gainCondition(ConditionId.DEBT,InvestigatorId.THE_PRIEST);
        chooseLastCard=true; deferDiscard=true; successes=1;
        action(new TheNunActionListener()).execute();

        assertEquals(1,cardChoices.size());
        assertEquals(2,cardChoices.get(0).size());
        assertTrue(cardChoices.get(0).containsAll(Arrays.asList(paranoia,hallucinations)));
        ConditionInfo chosen=(ConditionInfo)cardChoices.get(0).get(1);
        ShowCardRequest shown=shownCards.get(0);
        assertSame(chosen,shown.getConditionInfo());
        assertEquals(HideType.DISCARD_ALWAYS,shown.getHideType());
        assertEquals(DisplayAssetResponseType.OK,shown.getDisplayAssetResponseType());
        assertEquals("investigator/THE_PRIEST.png",shown.getPortraitTextureName());
        assertTrue(shown.getTitle().contains(chosen.getName()));
        assertEquals(0,discarded);
        assertEquals(1,questions);

        pendingDiscard.onSuccess(ShowCardResponse.OK);
        assertEquals(1,discarded);
        assertFalse(conditions.hasCondition(InvestigatorId.THE_PRIEST,chosen.getId()));
        assertEquals(1,conditions.getCondition(InvestigatorId.THE_PRIEST,ConditionTrait.MADNESS).size());
        assertTrue(conditions.hasCondition(InvestigatorId.THE_PRIEST,ConditionId.DEBT));
        assertEquals(InvestigatorId.THE_NUN,investigators.getActiveInvestigatorId());
    }
    @Test public void maryCanSpendMultipleSuccessesOnOneInvestigatorAndStopsWhenMadnessRunsOut() {
        investigators.changeActiveInvestigator(InvestigatorId.THE_NUN);
        conditions.gainCondition(ConditionId.PARANOIA,InvestigatorId.THE_PRIEST);
        conditions.gainCondition(ConditionId.HALLUCINATIONS,InvestigatorId.THE_PRIEST);
        successes=3; action(new TheNunActionListener()).execute();
        assertEquals(2,discarded);
        assertEquals(2,shownCards.size());
        assertEquals(2,cardChoices.get(0).size());
        assertEquals(1,cardChoices.get(1).size());
        assertNotSame(shownCards.get(0).getConditionInfo(),shownCards.get(1).getConditionInfo());
        assertEquals(2,questions);
    }
    @Test public void maryShowsNoDiscardOnFailedTestOrWhenPlayerChoosesDone() {
        investigators.changeActiveInvestigator(InvestigatorId.THE_NUN);
        conditions.gainCondition(ConditionId.AMNESIA,InvestigatorId.THE_NUN);
        successes=0; action(new TheNunActionListener()).execute();
        assertEquals(0,questions);
        successes=2; finishMadness=true; action(new TheNunActionListener()).execute();
        assertEquals(1,questions);
        assertEquals(0,discarded);
        assertTrue(cardChoices.isEmpty());
        assertTrue(shownCards.isEmpty());
    }
    @Test public void mateoTransfersHisExistingBoonToAnotherSpace() {
        investigators.changeActiveInvestigator(InvestigatorId.THE_PRIEST);
        investigators.getInvestigator(InvestigatorId.THE_EXPLORER).setCurrentLocationId(LocationId.ROME);
        ConditionInfo boon=conditions.gainCondition(ConditionId.BLESSED,InvestigatorId.THE_PRIEST);
        action(new ThePriestActionListener()).execute();
        assertEquals(1,transfers);
        assertSame(boon,conditions.getCondition(InvestigatorId.THE_EXPLORER,ConditionId.BLESSED));
        assertFalse(conditions.hasCondition(InvestigatorId.THE_PRIEST,ConditionId.BLESSED));
    }
    @Test public void ursulaMovesOnlyOnUnchartedPathsAndGetsOneExtraAction() {
        investigators.getActiveInvestigator().setCurrentLocationId(LocationId.THE_HEART_OF_AFRICA);
        ActionPhaseAction action=action(new TheExplorerActionListener());
        assertFalse(action.isDisabled()); action.execute();
        assertEquals(1,freeActions);
        assertNotNull(movement);
        for(LocationInfo.Connection connection:movement.getConnectionRestrictions())assertEquals(PathType.WALK,connection.getPathType());
        investigators.getActiveInvestigator().setCurrentLocationId(LocationId.LONDON);
        assertTrue(action(new TheExplorerActionListener()).isDisabled());
    }
    @Test public void carsonCannotRepeatTradeThroughHisCharacterAction() {
        investigators.changeActiveInvestigator(InvestigatorId.THE_BUTLER);
        ActionPhaseAction special=action(new TheButlerActionListener());
        assertFalse(special.isDisabled());special.execute();assertEquals(1,freeActions);
        performed.registerActionPerformed(InvestigatorId.THE_BUTLER,special);
        assertTrue(action(new TradeActionListener()).isDisabled());
        performed.reset();
        ActionPhaseAction trade=action(new TradeActionListener());
        performed.registerActionPerformed(InvestigatorId.THE_BUTLER,trade);
        assertTrue(action(new TheButlerActionListener()).isDisabled());
    }
    private ActionPhaseAction action(ActionPhaseListener listener) {
        CollectAvailableActionsData data=new CollectAvailableActionsData();listener.onNotify(data);
        assertEquals(1,data.getActionPhaseActions().size());
        return data.getActionPhaseActions().get(0);
    }
    private SpendData spend(boolean silent){
        SpendData data=new SpendData();data.setSilent(silent);data.getTokenAmountMap().put(TokenType.FOCUS,1);
        events.fireBeforeEvent(BeforeAfterEvent.SPEND,data);return data;
    }
    private interface Call {Object invoke(String method,Object[] args);}
    private <T>T proxy(Class<T> type,Call call){
        return type.cast(Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{type},(p,m,a)->call.invoke(m.getName(),a)));
    }
}

