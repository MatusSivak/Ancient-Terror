package sk.sivak.eldritchhorror.core.view.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java8.features.function.Function;
import java8.features.function.Supplier;
import rx.Completable;
import rx.Single;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorBasics;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.constants.phase.PhaseType;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.constants.trade.TradeData;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.GameView;
import sk.sivak.eldritchhorror.core.view.action.SelectActionView;
import sk.sivak.eldritchhorror.core.view.action.focus.TokenView;
import sk.sivak.eldritchhorror.core.view.action.rest.RestActionView;
import sk.sivak.eldritchhorror.core.view.action.ticket.TicketActionView;
import sk.sivak.eldritchhorror.core.view.action.trade.TradeView;
import sk.sivak.eldritchhorror.core.view.action.travel.TravelActionView;
import sk.sivak.eldritchhorror.core.view.asset.AssetView;
import sk.sivak.eldritchhorror.core.view.asset.PhaseView;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.FpsLogger;
import sk.sivak.eldritchhorror.core.view.components.effect.NoiseEffect;
import sk.sivak.eldritchhorror.core.view.components.select.SelectSingleComponent;
import sk.sivak.eldritchhorror.core.view.components.sheet.ancientone.AncientOneCard;
import sk.sivak.eldritchhorror.core.view.handler.ChangeScreenHandler;
import sk.sivak.eldritchhorror.core.view.handler.StartGameHandler;
import sk.sivak.eldritchhorror.core.view.investigator.InvestigatorView;
import sk.sivak.eldritchhorror.core.view.map.CompositeMap;
import sk.sivak.eldritchhorror.core.view.map.MapUtils;
import sk.sivak.eldritchhorror.core.view.map.clue.ClueUtils;
import sk.sivak.eldritchhorror.core.view.map.gate.NewGateAnimatedImage;
import sk.sivak.eldritchhorror.core.view.map.helper.MoveActorHelper;
import sk.sivak.eldritchhorror.core.view.map.helper.MoveCameraToLocationHelper;
import sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorUtils;
import sk.sivak.eldritchhorror.core.view.map.vortex.VortexImageUtils;
import sk.sivak.eldritchhorror.core.view.music.NewMusicBox;
import sk.sivak.eldritchhorror.core.view.question.QuestionView;
import sk.sivak.eldritchhorror.core.view.utils.BackgroundUtils;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import sk.sivak.eldritchhorror.core.view.utils.LostInTimeAndSpaceHelper;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.AD_MOB;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_ADLER;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.PURE_WHITE_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.SPACE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTextureRegionDrawable;
import static sk.sivak.eldritchhorror.core.view.game.InfoStage.getStageSafe;
import static sk.sivak.eldritchhorror.core.view.game.MapStage.getInvestigatorLayer;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/**
 * @author msivak
 */
public class GameViewImpl implements Screen, GameView {


    private Skin skin;
    private StartGameHandler startGameHandler;
    private ShowCurrentMysteryCardDialog showCurrentMysteryCardDialog;
    private GameController controller;
    private TravelActionView travelActionView;
    private TradeView tradeView;
    private TicketActionView ticketActionView;
    private RestActionView restActionView;
    private TokenView tokenView;
    private PrepareBoardView prepareBoardView;
    private SelectActionView selectActionView;
    private AssetView assetView;
    private PhaseView phaseView;
    private InvestigatorView investigatorView;
    private QuestionView questionView;
    private FpsLogger fpsLogger;
    private CompositeMap compositeMap;
    private Stage backgroundStage;
    private BackgroundUtils backgroundUtils;
    private LostInTimeAndSpaceHelper lostInTimeAndSpaceHelper;
    private NoiseEffect noiseEffect;
    private ChangeScreenHandler changeScreenHandler;
    private RestartGameAction restartGameAction;
    private boolean screenInitialized;
    private Runnable removeOnRewardedAdLoadedListener = () -> {};


    public GameViewImpl() {

    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public void setDefaultSkin(Skin skin) {
        this.skin = skin;
    }

    @Override
    public void show() {
        if (screenInitialized) {
            checkIfTutorialWasPassed();
            MapUtils.resetCamera();
            Gdx.input.setInputProcessor(new InputMultiplexer(getStageSafe(), MapStage.getStage()));
            initHudButtonsAndNoiseEffect();
            initOmenAndDoomTrack();
            addCurtainToInfoStage();
            restartGameAction = new RestartGameAction(changeScreenHandler, skin);
            InfoStage.getMenuButton().setRestartGameAction(restartGameAction);
            InfoStage.displayFastForwardButton();
            initRestartButton();
            return;
        }

        initRestartButton();

        InfoStage.displayFastForwardButton();
        screenInitialized = true;
        checkIfTutorialWasPassed();
        backgroundStage = new Stage(new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
        backgroundUtils = new BackgroundUtils(backgroundStage, controller);
        MapUtils.resetCamera();
        Gdx.input.setInputProcessor(new InputMultiplexer(getStageSafe(), MapStage.getStage()));
        showCurrentMysteryCardDialog = new ShowCurrentMysteryCardDialog(get("game.currentMysteryCard"), skin);

        initHudButtonsAndNoiseEffect();
        initOmenAndDoomTrack();

        compositeMap = new CompositeMap();
        compositeMap.init();

        travelActionView = new TravelActionView(compositeMap, controller);
        travelActionView.setBackgroundUtils(backgroundUtils);
        tradeView = new TradeView();
        ticketActionView = new TicketActionView(skin);
        restActionView = new RestActionView();
        tokenView = new TokenView();
        prepareBoardView = new PrepareBoardView(controller);
        prepareBoardView.setBackgroundUtils(backgroundUtils);
        selectActionView = new SelectActionView(controller, skin);
        assetView = new AssetView();
        questionView = new QuestionView();
        phaseView = new PhaseView();
        investigatorView = new InvestigatorView(controller);

        Image backgroundImage = new Image(getTextureRegionDrawable(SPACE));
        backgroundImage.setScaling(Scaling.fill);
        backgroundImage.setSize(ViewProperties.VIEWPORT_WIDTH, ViewProperties.VIEWPORT_HEIGHT);
        backgroundStage.addActor(backgroundImage);

        NewMusicBox.getInstance().changeMusic(NewMusicBox.PREPARE_BOARD_OR_TYPEWRITER);
        lostInTimeAndSpaceHelper = new LostInTimeAndSpaceHelper(controller, backgroundStage);
        lostInTimeAndSpaceHelper.setBackgroundUtils(backgroundUtils);

        addCurtainToInfoStage();

        restartGameAction = new RestartGameAction(changeScreenHandler, skin);
        InfoStage.getMenuButton().setRestartGameAction(restartGameAction);
    }

    private void checkIfTutorialWasPassed() {
        if (!GoogleServicesHolder.isTutorialPassed()) {
            MoveCameraToLocationHelper.setEnabled(false);
            TextButton skipTutorial = createNiceButton(get("game.skipTutorial"));
            addClickListener(skipTutorial, () -> {
                GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.TUTORIAL, "Skip!");
                GoogleServicesHolder.executeTutorialPassedAction();
                restartGameAction.justRestartGame();
            });
            skipTutorial.setPosition(VIEWPORT_WIDTH-skipTutorial.getWidth(), VIEWPORT_HEIGHT - skipTutorial.getHeight());
            InfoStage.getStageSafe().addActor(skipTutorial);
        }
    }

    private void initRestartButton() {
        removeOnRewardedAdLoadedListener.run();
        removeOnRewardedAdLoadedListener = () -> {};
        InfoStage.displayMenuButton(skin);
        if (Gdx.app.getPreferences("AncientTerror.xml").getBoolean("no_ads", false)) {
            InfoStage.getMenuButton().displayRestartButton();
            return;
        }
        GoogleServicesHolder.getAdHandler().isRewardedVideoAdLoaded().subscribe(isLoaded -> {
            if (isLoaded) {
                GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AD_MOB, "rewarded_ad", "loaded");
                InfoStage.getMenuButton().displayRestartButton();
            }
        });
        removeOnRewardedAdLoadedListener = GoogleServicesHolder.getAdHandler().addOnRewardedAdLoadedAction(() -> {
            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AD_MOB, "rewarded_ad", "loaded");
            InfoStage.getMenuButton().displayRestartButton();
        });
    }

    private void addCurtainToInfoStage() {
        Image curtain = new Image(CustomAssetManager.getTexture(PURE_WHITE_BACKGROUND));
        curtain.setTouchable(Touchable.disabled);
        curtain.setColor(new Color(0,0,0,1f));
        curtain.setSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        curtain.addAction(Actions.sequence(
                Actions.alpha(0,3),
                Actions.removeActor()
        ));
        getStageSafe().addActor(curtain);
    }

    @Override
    public void restartGame() {
        restartGameAction.justRestartGame();
    }

    @Override
    public void startGame() {
        startGameHandler.startGame();
    }

    @Override
    public void setClearQueueAction(Runnable input) {
        restartGameAction.setClearQueueAction(input);
    }

    private void initOmenAndDoomTrack() {
        controller.updateDoom();
        controller.updateOmen();
    }

    private void initHudButtonsAndNoiseEffect() {
        HudButtons hudButtons = new HudButtons(controller);
        hudButtons.init();

        noiseEffect = new NoiseEffect();
        noiseEffect.setTouchable(Touchable.disabled);
        noiseEffect.getColor().a = 0.20f;
        noiseEffect.setBounds(0, 0, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        InfoStage.getNoiseEffectLayer().addActor(noiseEffect);
    }

    public void setStartGameHandler(StartGameHandler startGameHandler) {
        this.startGameHandler = startGameHandler;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        backgroundStage.act();
        backgroundStage.draw();

        MapStage.render(delta);

        getStageSafe().act();
        getStageSafe().draw();

    }

    @Override
    public Completable confirmExpeditionLocation(LocationId locationId) {
        return prepareBoardView.confirmExpeditionLocation(locationId);
    }

    @Override
    public void justPlaceExpeditionToken(LocationId expeditionTokenLocation) {
        prepareBoardView.justPlaceExpeditionToken(expeditionTokenLocation);
    }

    @Override
    public Completable discardExpeditionToken() {
        return prepareBoardView.discardExpeditionToken();
    }

    @Override
    public Completable confirmClueLocation(LocationId spawnLocationId, LocationId currentLocationId) {
        return prepareBoardView.confirmClueLocation(spawnLocationId, currentLocationId);
    }

    @Override
    public void justSpawnClue(LocationId spawnLocationId, LocationId currentLocationId) {
        prepareBoardView.justSpawnClue(spawnLocationId, currentLocationId);
    }

    @Override
    public Completable confirmGateSpawn(GateInfo gateInfo, OmenColor omenColor) {
        return prepareBoardView.confirmGateSpawn(gateInfo, omenColor);
    }

    @Override
    public void justPlaceGate(GateInfo gateInfo, OmenColor omenColor) {
        prepareBoardView.justPlaceGate(gateInfo, omenColor);
    }

    @Override
    public Completable confirmMonsterSpawn(MonsterInfo monsterInfo) {
        return prepareBoardView.confirmMonsterSpawn(monsterInfo);
    }

    @Override
    public void justPlaceMonster(MonsterInfo monsterInfo) {
        prepareBoardView.justPlaceMonster(monsterInfo);
    }

    @Override
    public void showReserve(List<AssetInfo> reserve) {
        BigActorsManager.initReserveSheet(reserve, BigActorsManager::displayOrHideReserve);
        BigActorsManager.displayOrHideReserve();
    }

    @Override
    public void showDiscard(List<CardInfo> discardedCards) {
        BigActorsManager.initDiscardSheet(discardedCards, BigActorsManager::displayOrHideDiscard);
        BigActorsManager.displayOrHideDiscard();
    }

    @Override
    public Completable showCurrentMysteryCard(MysteryCardInfo currentMysteryCard, boolean moveCamera) {
        if (currentMysteryCard == null) {
            return Completable.complete();
        }
        return Completable.create(onSub -> {
            if (BigActorsManager.getMysteryCard().getStage() == null) {
                BigActorsManager.getMysteryCard().init(currentMysteryCard);
                BigActorsManager.getMysteryCard().setMoveCamera(moveCamera);
            }
            BigActorsManager.getMysteryCard().setAfterHideAction(onSub::onCompleted);
            addClickListener(BigActorsManager.getMysteryCard(), BigActorsManager::displayOrHideCurrentMystery);
            showMysteryCardAgain();
        });
    }

    @Override
    public Completable showRumorCard(RumorCardInfo rumorCardInfo) {
        return Completable.create(onSub -> {
            if (BigActorsManager.getRumorCard().getStage() == null) {
                BigActorsManager.getRumorCard().init(rumorCardInfo);
            }
            BigActorsManager.getRumorCard().setAfterHideAction(onSub::onCompleted);
            addClickListener(BigActorsManager.getRumorCard(), BigActorsManager::displayOrHideRumor);
            showRumorCardAgain();
        });
    }

    @Override
    public Completable justShowRumorCard(RumorCardInfo rumorCardInfo) {
        return Completable.create(onSub -> {
            BigActorsManager.getRumorCard().init(rumorCardInfo);
            BigActorsManager.getRumorCard().setAfterDisplayAction(onSub::onCompleted);
            showRumorCardAgain();
        });
    }

    @Override
    public Completable justHideRumorCard() {
        return Completable.create(onSub -> {
            BigActorsManager.getRumorCard().setAfterHideAction(onSub::onCompleted);
            BigActorsManager.displayOrHideRumor();
        });
    }

    @Override
    public Completable countdownRumorCard(int amount) {
        return BigActorsManager.getRumorCard().countdown(amount);
    }

    @Override
    public Completable highlightRumorFailure() {
        return BigActorsManager.getRumorCard().highlightRumorFailure();
    }

    @Override
    public Completable highlightRumorObjective() {
        return BigActorsManager.getRumorCard().highlightRumorObjective();
    }

    @Override
    public Completable highlightRumorReckoning() {
        return BigActorsManager.getRumorCard().highlightRumorReckoning();
    }

    private void showRumorCardAgain() {
        if (BigActorsManager.isLocked()) {
            Gdx.app.postRunnable(this::showRumorCardAgain);
        } else {
            BigActorsManager.displayOrHideRumor();
        }
    }

    private void showMysteryCardAgain() {
        if (BigActorsManager.isLocked()) {
            Gdx.app.postRunnable(this::showMysteryCardAgain);
        } else {
            BigActorsManager.displayOrHideCurrentMystery();
        }
    }

    @Override
    public Completable advanceCurrentMysteryCard(MysteryCardInfo currentMysteryCard, int amount) {
        return Completable.create(onSub -> {
            BigActorsManager.advanceCurrentMystery(
                    currentMysteryCard, BigActorsManager::displayOrHideCurrentMystery, onSub::onCompleted, amount);
            BigActorsManager.displayOrHideCurrentMystery();
        });
    }

    @Override
    public Completable showAncientOneCard(AncientOneInfo ancientOneInfo) {
        return Completable.create(onSub -> {
            BigActorsManager.initAncientOne(ancientOneInfo, BigActorsManager::displayOrHideAncientOne, onSub::onCompleted);
            BigActorsManager.displayOrHideAncientOne();
        });
    }

    @Override
    public Completable increaseAncientOnePower(int increment) {
        return Completable.create(onSub -> {

            if (BigActorsManager.getAncientOneCard() == null) {
                controller.showAncientOneCard().subscribe();
                BigActorsManager.getAncientOneCard().setAfterDisplayAction(() -> {
                    BigActorsManager.getAncientOneCard().increaseAncientOnePower(increment).subscribe(onSub::onCompleted);
                });
            } else {
                AncientOneCard ancientOneCard = BigActorsManager.getAncientOneCard();
                ancientOneCard.setAfterDisplayAction(() -> {
                    ancientOneCard.increaseAncientOnePower(increment).subscribe(onSub::onCompleted);
                });
                BigActorsManager.displayOrHideAncientOne();
            }
        });
    }

    @Override
    public void updateDoom(int doom) {
        HudButtons.getDoomTrack().updateDoom(doom);
        noiseEffect.updateDoom(doom);
    }

    @Override // only on initImproveSkill called
    public void updateOmen(OmenInfo omenInfo) {
        HudButtons.getOmenTrack().updateOmen(omenInfo.getOmenId());
        noiseEffect.updateOmenColor(omenInfo.getOmenColor());
    }

    @Override
    public Single<ActionPhaseAction> selectAction(List<ActionPhaseAction> actionPhaseActions) {
        return selectActionView.selectAction(actionPhaseActions);
    }

    @Override
    public Single<LocationInfo.Connection> selectTravelLocation(
            LocationId currentLocationId, List<LocationInfo.Connection> connections, boolean optional) {
        return travelActionView.selectTravelLocation(currentLocationId, connections, optional);
    }

    @Override
    public Completable travelToLocation(LocationId locationFrom, LocationId locationTo) {
        return travelActionView.travelToLocation(locationFrom, locationTo);
    }

    @Override
    public Completable initInvestigator(InvestigatorBasics selectedInvestigators) {
        return prepareBoardView.initInvestigator(selectedInvestigators);
    }

    @Override
    public void justPlaceInvestigator(InvestigatorBasics investigatorBasics) {
        prepareBoardView.justPlaceInvestigator(investigatorBasics);
    }

    @Override
    public Single<LocationInfo.Connection> selectTicketTravelLocation(LocationId currentLocationId, List<LocationInfo.Connection> connections) {
        return travelActionView.selectTicketTravelLocation(currentLocationId, connections);
    }

    @Override
    public void loseTicket(PathType input) {
        travelActionView.loseTicket(input).subscribe();
    }

    @Override
    public Single<PathType> selectTravelTicket() {
        return ticketActionView.selectTravelTicket();
    }

    @Override
    public Completable gainTravelTicket(PathType ticketType) {
        return ticketActionView.gainTravelTicket(ticketType);
    }

    @Override
    public Completable discardTicket(PathType pathType) {
        return travelActionView.loseTicket(pathType);
    }

    @Override
    public Completable loseFocus(Integer input) {
        return tokenView.loseFocus();
    }

    @Override
    public Completable loseHealth(Integer input) {
        return loseHealthOrSanity(input, offset -> tokenView.loseHealth(offset));
    }


    @Override
    public Completable loseSanity(Integer input) {
        return loseHealthOrSanity(input, offset -> tokenView.loseSanity(offset));
    }

    private Completable loseHealthOrSanity(Integer amount, Function<Float, Completable> function) {
        List<Completable> completables = new LinkedList<>();
        float tokenWidth = 100;
        float baseOffset = - (amount - 1) * tokenWidth / 2f;
        for (int i = 0; i < amount; i++) {
            completables.add(function.apply(baseOffset));
            baseOffset += tokenWidth;
        }
        return Completable.amb(completables);
    }


    @Override
    public Completable loseClue(Integer input) {
        return tokenView.loseClue();
    }

    @Override
    public Completable gainHealth(int amount) {
        return restActionView.gainHealth(amount);
    }

    @Override
    public Completable gainSanity(int amount) {
        return restActionView.gainSanity(amount);
    }

    @Override
    public Completable justRest() {
        return restActionView.justRest();
    }

    @Override
    public Completable justFocus() {
        return tokenView.justFocus();
    }

    @Override
    public Completable gainFocus(int amount) {
        return tokenView.gainFocus(amount);
    }

    @Override
    public Completable gainClueFromPool() {
        if (tokenView == null) {
            return Completable.complete();
        }
        return tokenView.gainClueFromPool();
    }

    @Override
    public Completable gainClueFromSpace(LocationId locationId) {
        if (tokenView == null) {
            return Completable.complete();
        }
        return tokenView.gainClueFromSpace(locationId);
    }

    @Override
    public Single<ShowCardResponse> showCard(ShowCardRequest showCardRequest) {
        return assetView.showCard(showCardRequest);
    }

    @Override
    public Completable sewCard(CardInfo cardInfo) {
        return assetView.sewCard(cardInfo);
    }

    @Override
    public Completable gainCard(CardInfo cardInfo) {
        return assetView.gainCard(cardInfo);
    }

    @Override
    public <RD, AD> Single<Answer<RD, AD>> ask(Question<RD> question) {
        return questionView.ask(question);
    }

    @Override
    public Completable showPhase(PhaseType input) {
        return phaseView.showPhase(input);
    }

    @Override
    public Completable showActiveInvestigator(boolean displayTransition, boolean lostInTimeAndSpace) {
        return investigatorView.showActiveInvestigator(displayTransition, lostInTimeAndSpace);
    }

    @Override
    public Single<Stat> improveSkill(Stat skillRestriction) {
        return investigatorView.improveSkill(skillRestriction);
    }

    @Override
    public Single<Stat> loseImprovement(Stat stat, int amount) {
        return investigatorView.loseImprovement(stat, amount);
    }

    @Override
    public Completable showImproveSkillTable() {
        return investigatorView.showImproveSkillTable();
    }

    @Override
    public Completable hideImproveSkillTable() {
        return investigatorView.hideImproveSkillTable();
    }

    @Override
    public Completable updateHud() {
        return investigatorView.updateHud(controller.getInvestigatorBasics());
    }

    @Override
    public void displayText(String text) {
        InfoStage.displayText(text);
    }

    @Override
    public void resize(int width, int height) {
        backgroundStage.getViewport().update(width, height, false);
        getStageSafe().getViewport().update(width, height, false);
    }

    @Override
    public Single<InvestigatorId> selectInvestigator(List<InvestigatorId> list, String title) {

        if (list.size() == 1) {
            return Single.just(list.get(0));
        }
        return investigatorView.selectInvestigator(list, title);
    }

    @Override
    public Single<GateInfo> selectSingleGate(List<GateInfo> gates, String titleText, String hideText) {
        return Single.create(onSub -> {
            SelectSingleComponent<? extends GateInfo> selectSingleComponent = new SelectSingleComponent<>(gates, onSub);
            selectSingleComponent.init(hideText, titleText, alpha -> new Color(0.0f, 0.0f, 0.0f, 0.5f * alpha), PURE_WHITE_BACKGROUND);
            selectSingleComponent.show();
        });
    }

    @Override
    public Single<TradeData> trade(TradeData input) {
        return tradeView.init(input);
    }

    @Override
    public Completable discardClue(LocationId spawnLocationId, LocationId currentLocationId) {
        return prepareBoardView.discardClue(spawnLocationId, currentLocationId);
    }

    @Override
    public void displayInvestigatorPassport(InvestigatorBasics investigatorBasics, List<CardInfo> cards) {
        if (investigatorBasics.getLocationId() != null) {
            MapUtils.moveCameraToLocation(investigatorBasics.getLocationId()).subscribe();
        }
        BigActorsManager.initPassport(investigatorBasics, cards);
        BigActorsManager.displayOrHidePassport();
    }

    private void displayOrHideMonsterCard(MonsterInfo monsterInfo, Action0 onClickAction, Action0 onDisplayHideAction) {
        if (monsterInfo.getCurrentLocation() != null) {
            MapUtils.moveCameraToLocation(monsterInfo.getCurrentLocation()).subscribe();
        }
        BigActorsManager.initMonsterCard(monsterInfo, onClickAction, onDisplayHideAction);
        BigActorsManager.displayOrHideMonsterCard();
    }

    @Override
    public void displayMonsterCard(MonsterInfo monsterInfo, Action0 onClickAction, Action0 onDisplayAction) {
        if (BigActorsManager.getMonsterCard().getStage() != null) {
            if (onDisplayAction != null) {
                onDisplayAction.call();
            }
            if (onClickAction != null) {
                onClickAction.call();
            }
        } else {
            displayOrHideMonsterCard(monsterInfo, onClickAction, onDisplayAction);
        }
    }

    @Override
    public void hideMonsterCard(MonsterInfo monsterInfo, Action0 onHideAction) {
        if (BigActorsManager.getMonsterCard().getStage() == null) {
            if (onHideAction != null) {
                onHideAction.call();
            }
        } else {
            displayOrHideMonsterCard(monsterInfo, null, onHideAction);
        }
    }

    @Override
    public Completable displayTrackWidget() {
        return HudButtons.getTrackHud().show();
    }

    @Override
    public Single<LocationInfo.Connection> selectLocation(List<LocationId> locations) {
        return travelActionView.selectLocation(locations);
    }

    @Override
    public Completable showRedPinsLocation(List<LocationId> pinLocations) {
        return prepareBoardView.showRedPinsLocation(pinLocations);
    }

    @Override
    public Completable showStormLocation(String stormId, LocationId locationId) {
        return prepareBoardView.showStormLocation(stormId, locationId);
    }

    @Override
    public void justAddRedPins(List<LocationId> pinLocations) {
        prepareBoardView.justAddRedPins(pinLocations);
    }

    @Override
    public void justSpawnStorm(String stormId, LocationId locationId) {
        prepareBoardView.justSpawnStorm(stormId, locationId);
    }

    @Override
    public void clearRedPins(List<LocationId> pinLocations) {
        prepareBoardView.clearRedPins(pinLocations);
    }

    @Override
    public void clearStorm(String rumorId) {
        prepareBoardView.clearStorm(rumorId);
    }

    @Override
    public Completable removeVortex(LocationId locationId) {
        return VortexImageUtils.discardVortex(locationId);
    }

    @Override
    public void showHud() {
        HudButtons.getInstance().show().subscribe();
        InfoStage.getInvestigatorHud().show().subscribe();
        HudButtons.getTrackHud().show().subscribe();
    }

    @Override
    public void hideCityInfoLabels() {
        compositeMap.hideCityInfoLabels();
    }

    @Override
    public void showCityInfoLabels() {
        compositeMap.showCityInfoLabels();
    }

    @Override
    public void enableDiscardButton() {
        HudButtons.getInstance().enableDiscardButton();
    }

    @Override
    public void updateRumorButtonVisibility(boolean visible) {
        if (visible) {
            HudButtons.getInstance().enableRumorsButton();
        } else {
            HudButtons.getInstance().disableRumorsButton();
        }
    }

    @Override
    public Completable spawnVortex(LocationId locationId) {
        return prepareBoardView.spawnVortex(locationId);
    }

    @Override
    public void justAddVortex(LocationId locationId) {
        prepareBoardView.justAddVortex(locationId);
    }

    @Override
    public Completable showRumorCard(List<RumorCardInfo> activeRumors) {
        if (BigActorsManager.getRumorCard().getRumorCardInfo() == null) {
            return showRumorCard(activeRumors.get(0));
        }

        int index = activeRumors.indexOf(BigActorsManager.getRumorCard().getRumorCardInfo());
        int newIndex = index + 1;
        if (newIndex >= activeRumors.size()) {
            newIndex = 0;
        }
        if (BigActorsManager.getRumorCard().getStage() == null) {
            return showRumorCard(activeRumors.get(newIndex));
        } else {
            return justHideRumorCard().andThen(showRumorCard(activeRumors.get(newIndex)));
        }

    }

    @Override
    public Completable showDelayedAnimation(InvestigatorId investigatorId) {
        return investigatorView.showDelayedAnimation(investigatorId);
    }

    @Override
    public void justAddDelayedAnimation(InvestigatorId investigatorId) {
        investigatorView.justAddDelayedAnimation(investigatorId);
    }

    @Override
    public Completable hideSelectEncounterTable() {
        return Completable.create(onSub -> {
            InfoStage.addActionToInfoStage(new FastForwardAction<>(Actions.sequence(
                    Actions.run(BigActorsManager::displayOrHideEncounterTable),
                    Actions.delay(0.5f),
                    Actions.run(onSub::onCompleted)
            )));
        });
    }

    @Override
    public Completable moveCameraToLocation(LocationId locationId) {
        return backgroundUtils.hideBackground().andThen(MapUtils.moveCameraToLocation(locationId));
    }


    @Override
    public Completable moveClue(LocationId spawnLocationId, LocationId currentLocationId, LocationId targetLocationId) {
        return MoveActorHelper.moveActor(currentLocationId, targetLocationId, new ClueUtils.ClueIdLayerResolver(spawnLocationId));
    }

    @Override
    public Completable removeInvestigator(InvestigatorId investigatorId, LocationId locationId) {
        return lostInTimeAndSpaceHelper.removeInvestigator(investigatorId, locationId);
    }

    @Override
    public Completable showInvestigatorLostInTimeAndSpace(InvestigatorId investigatorId) {
        return lostInTimeAndSpaceHelper.showInvestigatorLostInTimeAndSpace(investigatorId);
    }

    @Override
    public Completable hideInvestigatorLostInTimeAndSpace(InvestigatorId investigatorId) {
        return lostInTimeAndSpaceHelper.hideInvestigatorLostInTimeAndSpace(investigatorId);
    }

    @Override
    public Completable showWholeWorld() {
        return MapUtils.showWholeWorld();
    }

    @Override
    public Completable showLocationBackground(LocationId locationId, List<InvestigatorBasics> investigatorsAtLocation) {
        return backgroundUtils.showLocationBackground(locationId, investigatorsAtLocation);
    }

    @Override
    public Completable showLocationTypeBackground(LocationType locationType, List<InvestigatorBasics> investigatorsAtLocation) {
        return backgroundUtils.showLocationTypeBackground(locationType, investigatorsAtLocation);
    }

    @Override
    public Completable showResearchBackground(LocationType locationType, List<InvestigatorBasics> investigatorsAtLocation) {
        return backgroundUtils.showResearchBackground(locationType, investigatorsAtLocation);
    }

    @Override
    public Completable showCombatBackground(List<InvestigatorBasics> investigatorsAtLocation) {
        return backgroundUtils.showCombatBackground(investigatorsAtLocation);
    }

    @Override
    public Completable showWorldBackground() {
        return backgroundUtils.showWorldBackground();
    }

    @Override
    public Completable showCustomBackground(String backgroundId, List<InvestigatorBasics> investigatorsAtLocation) {
        return backgroundUtils.showCustomBackground(backgroundId, investigatorsAtLocation);
    }

    @Override
    public Completable hideBackground() {
        return backgroundUtils.hideBackground();
    }

    @Override
    public Completable hideDelayedAnimation(InvestigatorId investigatorId) {
        return investigatorView.hideDelayedAnimation(investigatorId);
    }

    @Override
    public Completable closeGate(LocationId location) {
        return prepareBoardView.closeGate(location);
    }

    @Override
    public void swapInvestigatorAndGateLayer() {
        MapStage.getStage().getRoot().swapActor(MapStage.getInvestigatorLayer(), MapStage.getGateLayer());
    }

    @Override
    public void updateNoiseColor(OmenColor omenColor) {
        noiseEffect.updateOmenColor(omenColor);
    }

    @Override
    public void updateNoiseIntensity(int doom) {
        noiseEffect.updateDoom(doom);
    }

    @Override
    public void updateGatesTransparency(OmenColor omenColor) {
        for (Actor child : MapStage.getGateLayer().getChildren()) {
            ((NewGateAnimatedImage) child).updateGateTransparency(omenColor);
        }
    }

    @Override
    public Completable devourInvestigator(InvestigatorId investigatorId) {
        return investigatorView.devourInvestigator(investigatorId);
    }

    @Override
    public Completable defeatInvestigator(InvestigatorId investigatorId, boolean health) {
        return investigatorView.defeatInvestigator(investigatorId, health);
    }

    @Override
    public Completable loadDefeatedInvestigator(InvestigatorId investigatorId, LocationId locationId, boolean defeatedByHealth) {
        return investigatorView.loadDefeatedInvestigator(investigatorId, locationId, defeatedByHealth);
    }

    @Override
    public Completable removeDefeatedInvestigator(InvestigatorId investigatorId, LocationId locationId) {
        return investigatorView.removeDefeatedInvestigator(investigatorId, locationId);
    }

    @Override
    public Single<InvestigatorInfo[]> selectReplacingInvestigator(InvestigatorId removedInvestigator, List<InvestigatorInfo> availableInvestigators,
                                                                  Supplier<List<InvestigatorInfo>> initInvestigatorsAction) {
        return investigatorView.selectReplacingInvestigator(removedInvestigator, availableInvestigators, initInvestigatorsAction);
    }

    @Override
    public void hideButtonsAndInvestigatorHud(boolean hideDoomOmenTrack) {
        HudButtons.getInstance().hide().subscribe();
        InfoStage.getInvestigatorHud().hide().subscribe();
        if (hideDoomOmenTrack) {
            HudButtons.getTrackHud().hide().subscribe();
        }
    }

    @Override
    public void unsetActiveInvestigator() {
        InvestigatorUtils.highlightInvestigators(MapStage.getAllActors(getInvestigatorLayer()), false);
    }



    @Override
    public void postRunnable(Runnable runnable) {
        Gdx.app.postRunnable(runnable);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        if (fpsLogger != null) {
            fpsLogger.print();
        }
        removeOnRewardedAdLoadedListener.run();
        removeOnRewardedAdLoadedListener = () -> {};
    }

    @Override
    public void dispose() {
        removeOnRewardedAdLoadedListener.run();
        removeOnRewardedAdLoadedListener = () -> {};
    }

    public void setChangeScreenHandler(ChangeScreenHandler changeScreenHandler) {
        this.changeScreenHandler = changeScreenHandler;
    }

    private TextButton createNiceButton(String text) {
        TextButton niceButton = new TextButton(text, skin);
        niceButton.getLabel().setFontScale(0.25f);
        niceButton.getLabel().setStyle(new Label.LabelStyle(CustomAssetManager.getBitmapFont(FONT_ADLER), Color.WHITE));
        return niceButton;
    }
}
