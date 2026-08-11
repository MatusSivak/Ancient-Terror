package sk.sivak.eldritchhorror.core.view.initgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java8.features.function.Supplier;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.AdHandler;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.difficulty.DifficultyId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.constants.tutorial.TouchBlockerData;
import sk.sivak.eldritchhorror.core.controller.InitGameController;
import sk.sivak.eldritchhorror.core.view.InitGameView;
import sk.sivak.eldritchhorror.core.view.ScreenType;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.combat.ThunderEffect;
import sk.sivak.eldritchhorror.core.view.components.investigator.SelectMultipleInvestigators;
import sk.sivak.eldritchhorror.core.view.components.sheet.monster.MonsterCard;
import sk.sivak.eldritchhorror.core.view.components.table.ActorFrame;
import sk.sivak.eldritchhorror.core.view.components.tutorial.Chalkboard;
import sk.sivak.eldritchhorror.core.view.components.tutorial.TouchBlocker;
import sk.sivak.eldritchhorror.core.view.game.HudButtons;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.handler.ChangeScreenHandler;
import sk.sivak.eldritchhorror.core.view.map.helper.MoveCameraToLocationHelper;
import sk.sivak.eldritchhorror.core.view.music.NewMusicBox;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;
import sk.sivak.eldritchhorror.core.view.utils.MyMoveToAction;
import sk.sivak.eldritchhorror.core.view.utils.UiText;

import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.AD_MOB;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_ADLER;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.SPLASH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.SPLASH_TITLE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.loadTextures1;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;
import static sk.sivak.eldritchhorror.core.view.utils.RectangleUtils.randomPointInRectangle;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/**
 * @author msivak
 */
public class InitGameViewImpl implements Screen, InitGameView {

    private NrPlayersDialog nrPlayersDialog;
    private SelectDifficultyDialog selectDifficultyDialog;
    private SelectAncientOneDialog selectAncientOneDialog;
    private Stage stage;
    private Skin skin;
    private ChangeScreenHandler changeScreenHandler;
    private InitGameController controller;
    private MonsterCard monsterCard;
    private Image background;
    private Chalkboard chalkboard;
    private boolean screenInitialized;
    private TextButton collectionButton;
    private TextButton hallOfFameButton;
    private TextButton replayTutorialButton;
    private Runnable continueGameAction;
    private Runnable newGameAction;
    private TextButton newGameButton;
    private TextButton loadGameButton;
    private NoAdsButton noAdsButton;
    private Runnable replayTutorialAction;
    private SingleSubscriber<? super Integer> numberOfPlayersSingleSubscriber;
    private Runnable removeOnRewardedAdLoadedListener = () -> {};
    private Runnable removeOnRewardedAdFailedToLoadListener = () -> {};
    private Image englishLocaleFlag;
    private Image slovakLocaleFlag;

    private static final String FLAGS_TEXTURE = "flags/flags.png";
    private static final int FLAGS_COLUMNS = 15;
    private static final int FLAGS_ROWS = 13;
    private static final String LANGUAGE_ENGLISH = "en";
    private static final String LANGUAGE_SLOVAK = "sk";
    private static final float MENU_VERTICAL_OFFSET = -45f;
    private static final float SPLASH_TITLE_SCALE = 0.6f;

    public void setDefaultSkin(Skin skin) {
        this.skin = skin;
    }

    @Override
    public Single<Integer> getNumberOfPlayers() {
        return Single.<Integer>create(sub -> {
            numberOfPlayersSingleSubscriber = sub;
            loadTextures1();
            nrPlayersDialog.getColor().a = 0f;
            nrPlayersDialog.show(stage);
            nrPlayersDialog.setY(305);
            nrPlayersDialog.setSubscriber(sub);
        }).doOnSuccess(x -> {
            collectionButton.remove();
            hallOfFameButton.remove();
            replayTutorialButton.remove();
            noAdsButton.remove();
        });
    }

    private SelectMultipleInvestigators selectMultipleInvestigators;

    @Override
    public Single<InvestigatorInfo[]> selectInvestigators(Integer input, List<InvestigatorInfo> availableInvestigators, Supplier<List<InvestigatorInfo>> initInvestigatorsAction) {
        return Single.<InvestigatorInfo[]>create(sub -> {

            Runnable updateAvailableInvestigatorsAction = () -> {
                selectMultipleInvestigators.remove();
                selectMultipleInvestigators = new SelectMultipleInvestigators(initInvestigatorsAction.get(), sub, input, stage);
                selectMultipleInvestigators.setBackground(background);
                selectMultipleInvestigators.show();
            };

            selectMultipleInvestigators = new SelectMultipleInvestigators(availableInvestigators, sub, input, stage);
            selectMultipleInvestigators.setUpdateAvailableInvestigatorsAction(updateAvailableInvestigatorsAction);
            selectMultipleInvestigators.setBackground(background);
            selectMultipleInvestigators.show();


            CustomAssetManager.getTextureAsync("ancient_one/button_shub_niggurath.jpg").toCompletable()
                    .andThen(CustomAssetManager.getTextureAsync("ancient_one/button_yog_sothoth.jpg").toCompletable())
                    .andThen(CustomAssetManager.getTextureAsync("ancient_one/button_cthulhu.jpg").toCompletable())
                    .andThen(CustomAssetManager.getTextureAsync("ancient_one/button_azathoth.jpg").toCompletable()).subscribe();

        });
    }

    @Override
    public Single<AncientOneInfo> selectAncientOne(List<AncientOneInfo> availableAncientOnes) {
        return Single.create(sub -> {
            Preferences preferences = Gdx.app.getPreferences("AncientTerror.xml");
            boolean isCthulhuPurchased = preferences.getBoolean("cthulhu", false);
            boolean isShubNiggurathPurchased = preferences.getBoolean("shub_niggurath", false);
            boolean isYogSothothPurchased = preferences.getBoolean("yog_sothoth", false);
            SelectAncientOneTable selectAncientOneTable = new SelectAncientOneTable(availableAncientOnes, isCthulhuPurchased, isShubNiggurathPurchased, isYogSothothPurchased, sub);


            ScrollPane scrollPane = new ScrollPane(selectAncientOneTable);

            scrollPane.setPosition(
                    stage.getWidth() * 0.05f,
                    stage.getHeight()/2f - selectAncientOneTable.getHeight()/2f);
            scrollPane.setWidth(stage.getWidth() * 0.9f);
            scrollPane.setHeight(selectAncientOneTable.getHeight());
            scrollPane.setOverscroll(false, false);
            ActorFrame frame = new ActorFrame(scrollPane, 5);
            selectAncientOneTable.setFrame(frame);
            stage.addActor(frame);
            stage.addActor(scrollPane);
            selectAncientOneTable.animate();
//            sub.onSuccess(availableAncientOnes.get(0));
            /*
            selectAncientOneDialog.initImproveSkill(availableAncientOnes);
            selectAncientOneDialog.setSubscriber(sub);
            selectAncientOneDialog.show(stage);
            */
        });
    }

    @Override
    public Single<DifficultyId> selectDifficulty() {
        return Single.create(sub -> {

            Dialog dialog = new Dialog(get("init.selectDifficulty"), skin);

            TextButton difficultyEasyButton = createNiceButton(get("init.difficulty.easy"));
            TextButton difficultyNormalButton = createNiceButton(get("init.difficulty.normal"));

            dialog.getContentTable().add(difficultyEasyButton).size(280,50);
            dialog.getContentTable().row();
            dialog.getContentTable().add(difficultyNormalButton).size(280,50);

            dialog.pack();

            addClickListener(difficultyEasyButton, () -> {
                dialog.hide();
                sub.onSuccess(DifficultyId.EASY);
            });
            addClickListener(difficultyNormalButton, () -> {
                dialog.hide();
                sub.onSuccess(DifficultyId.NORMAL);
            });

            dialog.setPosition(VIEWPORT_WIDTH/2f - dialog.getWidth()/2,
                    VIEWPORT_HEIGHT/2f - dialog.getHeight()/2f);
            stage.addActor(dialog);
        });
    }

    @Override
    public void show() {
        if (screenInitialized) {
            initLocaleFromPreferences();
            refreshLocalizedTexts();
            refreshLocaleFlagSelection();
            Gdx.input.setInputProcessor(new InputMultiplexer(InfoStage.getStageSafe(), stage));
            return;
        }
        initLocaleFromPreferences();
        new InAppPurchaseManager().isProductPurchased("cthulhu").subscribe();
        new InAppPurchaseManager().isProductPurchased("shub_niggurath").subscribe();
        new InAppPurchaseManager().isProductPurchased("yog_sothoth").subscribe();
        screenInitialized = true;
        stage = new Stage(new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
        CustomAssetManager.getTextureAsync(SPLASH).subscribe(texture -> {
            background = new Image(CustomAssetManager.getTexture(SPLASH));
            background.setSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
            background.setOrigin(Align.center);

            background.setScale(1.25f);
            float width = ViewProperties.VIEWPORT_WIDTH * background.getScaleX() - ViewProperties.VIEWPORT_WIDTH;
            float height = ViewProperties.VIEWPORT_HEIGHT * background.getScaleY() - ViewProperties.VIEWPORT_HEIGHT;
            MyMoveToAction moveToAction = new MyMoveToAction(new Vector2(0, 0),
                    () -> randomPointInRectangle(new Vector2(0, 0), width, height), 30f, 1.25f);
            background.addAction(Actions.repeat(RepeatAction.FOREVER,
                    moveToAction));
            stage.getRoot().addActorAt(0, background);
            new ThunderEffect(background).execute();
        });
        CustomAssetManager.getTextureAsync(SPLASH_TITLE).subscribe(texture -> {
            Image splashTitle = new Image(texture);
            splashTitle.setSize(texture.getWidth() * SPLASH_TITLE_SCALE, texture.getHeight() * SPLASH_TITLE_SCALE);
            splashTitle.setPosition(VIEWPORT_WIDTH / 2f, VIEWPORT_HEIGHT - 20f, Align.top);
            stage.addActor(splashTitle);
        });


        rebuildLocalizedDialogs();


        Gdx.input.setInputProcessor(new InputMultiplexer(InfoStage.getStageSafe(), stage));
        NewMusicBox.getInstance().changeMusic(NewMusicBox.TRACK_HORROR_GAME_INTRO);


        replayTutorialButton = createNiceButton(get("init.tutorial"));
        replayTutorialButton.setPosition(ViewProperties.VIEWPORT_WIDTH/2f - replayTutorialButton.getWidth()/2f, 245 + MENU_VERTICAL_OFFSET);
        addClickListener(replayTutorialButton, () -> {
            GoogleServicesHolder.setTutorialPassed(false);
            replayTutorialButton.setTouchable(Touchable.disabled);
            replayTutorial();
            replayTutorialButton.setTouchable(Touchable.enabled);
            if (numberOfPlayersSingleSubscriber != null) {
                numberOfPlayersSingleSubscriber.onSuccess(1);
                nrPlayersDialog.hide();
            }

        });

        collectionButton = createNiceButton(get("init.cardsCollection"));
        collectionButton.setPosition(ViewProperties.VIEWPORT_WIDTH/2f - collectionButton.getWidth()/2f, 185 + MENU_VERTICAL_OFFSET);
        addClickListener(collectionButton, () -> {
            changeScreenHandler.changeScreen(ScreenType.CARDS);
        });

        hallOfFameButton = createNiceButton(get("init.hallOfFame"));
        hallOfFameButton.setPosition(ViewProperties.VIEWPORT_WIDTH/2f - hallOfFameButton.getWidth()/2f, 125 + MENU_VERTICAL_OFFSET);
        addClickListener(hallOfFameButton, () -> {
            changeScreenHandler.changeScreen(ScreenType.HALL_OF_FAME);
        });



        noAdsButton = new NoAdsButton();
        noAdsButton.setPosition(VIEWPORT_WIDTH - noAdsButton.getWidth() - 5, 5);
        initLocaleSelector();
        stage.addActor(englishLocaleFlag);
        stage.addActor(slovakLocaleFlag);

        if (GoogleServicesHolder.isTutorialPassed()) {
            stage.addActor(collectionButton);
            stage.addActor(hallOfFameButton);
            stage.addActor(replayTutorialButton);
            stage.addActor(noAdsButton);
        }

        if (Gdx.files.local("save.json").exists()) {

            loadGameButton = createNiceButton(get("init.continue"));
            loadGameButton.setPosition(ViewProperties.VIEWPORT_WIDTH/2f - loadGameButton.getWidth()/2f, 365 + MENU_VERTICAL_OFFSET);
            stage.addActor(loadGameButton);
            ButtonUtils.addClickListener(loadGameButton, continueGameAction);

            newGameButton = createNiceButton(get("init.newGame"));
            newGameButton.setPosition(ViewProperties.VIEWPORT_WIDTH/2f - newGameButton.getWidth()/2f, 305 + MENU_VERTICAL_OFFSET);
            stage.addActor(newGameButton);

            if (!Gdx.app.getPreferences("AncientTerror.xml").getBoolean("no_ads", false)) {
                removeOnRewardedAdLoadedListener.run();
                removeOnRewardedAdFailedToLoadListener.run();
                removeOnRewardedAdLoadedListener = GoogleServicesHolder.getAdHandler().addOnRewardedAdLoadedAction(() -> {
                    GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AD_MOB, "rewarded_ad", "loaded");
                });
                removeOnRewardedAdFailedToLoadListener = GoogleServicesHolder.getAdHandler().addOnRewardedAdFailedToLoadAction(errorCode -> {
                    GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AD_MOB, "rewarded_ad", "load_failed_" + errorCode);
                });
            }


            ButtonUtils.addClickListener(newGameButton, () -> {
                Runnable adRewardedAction = () -> {
                    Gdx.files.local("save.json").delete();
                    newGameButton.remove();
                    loadGameButton.remove();
                    newGameAction.run();
                };

                if (Gdx.app.getPreferences("AncientTerror.xml").getBoolean("no_ads", false)) {
                    adRewardedAction.run();
                    return;
                }
                GoogleServicesHolder.getAdHandler().isRewardedVideoAdLoaded().subscribe(isLoaded -> {
                    Gdx.app.postRunnable(() -> {
                        if (isLoaded) {
                            displayChalkboardWithNoYesButtons(get("init.restartPrompt"),
                                    VIEWPORT_WIDTH/2, VIEWPORT_HEIGHT/2).subscribe(answerIsTrue -> {
                                if (!answerIsTrue) {
                                    return;
                                }
                                newGameButton.setDisabled(true);
                                newGameButton.setTouchable(Touchable.disabled);
                                loadGameButton.setDisabled(true);
                                loadGameButton.setTouchable(Touchable.disabled);
                                final boolean[] rewarded = {false};
                                GoogleServicesHolder.getAdHandler().showRewardedAd(new AdHandler.AdCallbacks()
                                        .setOnAdStartedAction(() -> {
                                            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AD_MOB, "rewarded_ad", "show_started");
                                        })
                                        .setOnAdRewardedAction(() -> {
                                            rewarded[0] = true;
                                            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AD_MOB, "rewarded_ad", "reward_granted");
                                            adRewardedAction.run();
                                        })
                                        .setOnAdClosedAction(() -> {
                                            if (!rewarded[0]) {
                                                newGameButton.setDisabled(false);
                                                newGameButton.setTouchable(Touchable.enabled);
                                                loadGameButton.setDisabled(false);
                                                loadGameButton.setTouchable(Touchable.enabled);
                                            }
                                        })
                                        .setOnAdFailedToLoadAction(errorCode -> {
                                            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AD_MOB, "rewarded_ad", "show_failed_" + errorCode);
                                            newGameButton.setDisabled(false);
                                            newGameButton.setTouchable(Touchable.enabled);
                                            loadGameButton.setDisabled(false);
                                            loadGameButton.setTouchable(Touchable.enabled);
                                        }));
                            });
                        } else {
                            adRewardedAction.run();
                        }
                    });
                });
            });
        }

    }

    private void rebuildLocalizedDialogs() {
        nrPlayersDialog = new NrPlayersDialog(get("init.numberOfInvestigators"), skin);
        nrPlayersDialog.setModal(false);
        selectAncientOneDialog = new SelectAncientOneDialog(get("init.selectAncientOne"), skin);
    }

    private void initLocaleFromPreferences() {
        Preferences preferences = Gdx.app.getPreferences("AncientTerror.xml");
        String preferredLanguage = preferences.getString("ui.language", LANGUAGE_ENGLISH);
        UiText.setLanguage(preferredLanguage);
    }

    private void initLocaleSelector() {
        englishLocaleFlag = createLocaleFlag(13, 5, LANGUAGE_ENGLISH);
        slovakLocaleFlag = createLocaleFlag(11, 8, LANGUAGE_SLOVAK);

        float margin = 8f;
        englishLocaleFlag.setPosition(margin, margin);
        slovakLocaleFlag.setPosition(margin + englishLocaleFlag.getWidth() + margin, margin);
        refreshLocaleFlagSelection();
    }

    private Image createLocaleFlag(int row, int column, String languageTag) {
        Texture flagsTexture = CustomAssetManager.getTexture(FLAGS_TEXTURE);
        int regionWidth = flagsTexture.getWidth() / FLAGS_COLUMNS;
        int regionHeight = flagsTexture.getHeight() / FLAGS_ROWS;
        TextureRegion region = new TextureRegion(flagsTexture, (column - 1) * regionWidth, (row - 1) * regionHeight, regionWidth, regionHeight);
        Image image = new Image(new TextureRegionDrawable(region));
        image.setSize(regionWidth * 0.55f, regionHeight * 0.55f);
        ButtonUtils.addClickListener(image, () -> applyLanguage(languageTag));
        return image;
    }

    private void applyLanguage(String languageTag) {
        if (languageTag.equals(UiText.getLanguage())) {
            return;
        }
        UiText.setLanguage(languageTag);
        rebuildLocalizedDialogs();
        refreshLocalizedTexts();
        refreshLocaleFlagSelection();
    }

    private void refreshLocaleFlagSelection() {
        if (englishLocaleFlag == null || slovakLocaleFlag == null) {
            return;
        }
        boolean englishSelected = LANGUAGE_ENGLISH.equals(UiText.getLanguage());
        englishLocaleFlag.setColor(1f, 1f, 1f, englishSelected ? 1f : 0.45f);
        slovakLocaleFlag.setColor(1f, 1f, 1f, englishSelected ? 0.45f : 1f);
    }

    private void refreshLocalizedTexts() {
        if (nrPlayersDialog != null) {
            nrPlayersDialog.getTitleLabel().setText(get("init.numberOfInvestigators"));
        }
        if (selectAncientOneDialog != null) {
            selectAncientOneDialog.getTitleLabel().setText(get("init.selectAncientOne"));
        }
        if (collectionButton != null) {
            collectionButton.setText(get("init.cardsCollection"));
        }
        if (hallOfFameButton != null) {
            hallOfFameButton.setText(get("init.hallOfFame"));
        }
        if (replayTutorialButton != null) {
            replayTutorialButton.setText(get("init.tutorial"));
        }
        if (loadGameButton != null) {
            loadGameButton.setText(get("init.continue"));
        }
        if (newGameButton != null) {
            newGameButton.setText(get("init.newGame"));
        }
    }

    private void replayTutorial() {
        if (replayTutorialAction != null) {
            replayTutorialAction.run();
        }
    }

    public void setReplayTutorialAction(Runnable replayTutorialAction) {
        this.replayTutorialAction = replayTutorialAction;
    }

    private TextButton createNiceButton(String text) {
        TextButton niceButton = new TextButton(text, skin);
        niceButton.getLabel().setFontScale(0.5f);
        niceButton.setSize(280, 50);
        niceButton.getLabel().setStyle(new Label.LabelStyle(CustomAssetManager.getBitmapFont(FONT_ADLER), Color.WHITE));
        return niceButton;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        InfoStage.getStageSafe().act();
        stage.act();
        CustomAssetManager.loadAssets();
        stage.draw();
        InfoStage.getStageSafe().draw();

    }

    public void setChangeScreenHandler(ChangeScreenHandler changeScreenHandler) {
        this.changeScreenHandler = changeScreenHandler;
    }

    @Override
    public void toGameScreen() {
        if (changeScreenHandler == null) {
            return;
        }
        changeScreenHandler.changeScreen(ScreenType.GAME);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, false);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        removeOnRewardedAdLoadedListener.run();
        removeOnRewardedAdLoadedListener = () -> {};
        removeOnRewardedAdFailedToLoadListener.run();
        removeOnRewardedAdFailedToLoadListener = () -> {};
    }

    @Override
    public void dispose() {
        removeOnRewardedAdLoadedListener.run();
        removeOnRewardedAdLoadedListener = () -> {};
        removeOnRewardedAdFailedToLoadListener.run();
        removeOnRewardedAdFailedToLoadListener = () -> {};
    }

    public void setController(InitGameController controller) {
        this.controller = controller;
    }

    @Override
    public Completable displayChalkboard(String text, int positionX, int positionY) {
        if (chalkboard == null) {
            chalkboard = new Chalkboard(skin);
        }
        chalkboard.setPosition(positionX - chalkboard.getWidth()/2f, positionY - chalkboard.getHeight()/2f);
        InfoStage.getChalkboardLayer().addActor(chalkboard);
        return chalkboard.display(resolveLocalizedText(text));
    }

    public Single<Boolean> displayChalkboardWithNoYesButtons(String text, int positionX, int positionY) {
        if (chalkboard == null) {
            chalkboard = new Chalkboard(skin);
        }
        chalkboard.setPosition(positionX - chalkboard.getWidth()/2f, positionY - chalkboard.getHeight()/2f);
        InfoStage.getChalkboardLayer().addActor(chalkboard);
        return chalkboard.displayWithNoYesButtons(resolveLocalizedText(text));
    }

    private String resolveLocalizedText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        String localized = UiText.get(text);
        String missingKey = "!" + text + "!";
        if (missingKey.equals(localized)) {
            return text;
        }
        return localized;
    }

    @Override
    public void hideChalkboard() {
        chalkboard.hide();
    }

    @Override
    public void clearTouchBlockers(TouchBlockerData.BlockerTarget blockerTarget) {
        if (blockerTarget == TouchBlockerData.BlockerTarget.INFO_STAGE) {
            InfoStage.getTouchBlockerLayer().clear();
        } else if (blockerTarget == TouchBlockerData.BlockerTarget.MAP) {
            MapStage.getTouchBlockerLayer().clear();
        }
    }
    @Override
    public void displayTouchBlocker(TouchBlockerData data) {
        TouchBlocker touchBlocker = new TouchBlocker(data.getBlockerArea().getWidth(), data.getBlockerArea().getHeight());
        touchBlocker.setPosition(data.getBlockerArea().getX(), data.getBlockerArea().getY());
        if (data.getWindowArea() != null) {
            touchBlocker.enableWindow(new Rectangle(data.getWindowArea().getX(), data.getWindowArea().getY(),
                            data.getWindowArea().getWidth(), data.getWindowArea().getHeight()));
        }
        if (data.isSemiTransparent()) {
            touchBlocker.setSemiTransparent();
        }
        if (data.isClickthrough()) {
            touchBlocker.setClickable();
        }

        if (data.getBlockerTarget() == TouchBlockerData.BlockerTarget.INFO_STAGE) {
            InfoStage.getTouchBlockerLayer().addActor(touchBlocker);
        } else if (data.getBlockerTarget() == TouchBlockerData.BlockerTarget.MAP) {
            MapStage.getTouchBlockerLayer().addActor(touchBlocker);
        }
    }

    @Override
    public Completable showHudButtons() {
        return HudButtons.getInstance().show();
    }

    @Override
    public Completable waitForAncientOneClick() {
        return Completable.create(onSub -> {
            BigActorsManager.createAncientOneCard(AncientOneId.AZATHOTH, false);
            BigActorsManager.getAncientOneCard().setBeforeDisplayAction(() -> {
                BigActorsManager.getAncientOneCard().setBeforeDisplayAction(null);
                onSub.onCompleted();
            });
        });
    }

    @Override
    public Completable waitForReserveClick() {
        return Completable.create(onSub -> {
            BigActorsManager.getReserveSheet().setBeforeDisplayAction(() -> {
                BigActorsManager.getReserveSheet().setBeforeDisplayAction(null);
                onSub.onCompleted();
            });
        });
    }

    @Override
    public Completable waitForMysteryClick() {
        return Completable.create(onSub -> {
            MoveCameraToLocationHelper.setEnabled(true);
            BigActorsManager.getMysteryCard().setBeforeDisplayAction(() -> {
                BigActorsManager.getAncientOneCard().setBeforeDisplayAction(null);
                onSub.onCompleted();
            });
        });
    }

    @Override
    public Completable waitForInvestigatorClick() {
        return Completable.create(onSub -> {
            BigActorsManager.getPassportTable().setBeforeDisplayAction(() -> {
                BigActorsManager.getPassportTable().setBeforeDisplayAction(null);
                onSub.onCompleted();
            });
        });
    }

    @Override
    public Completable waitForAncientOneHide() {
        return Completable.create(onSub -> {
            BigActorsManager.getAncientOneCard().setAfterHideAction(() -> {
                BigActorsManager.getAncientOneCard().setAfterHideAction(null);
                onSub.onCompleted();
            });
        });
    }

    @Override
    public Completable waitForMysteryHide() {
        return Completable.create(onSub -> {
            BigActorsManager.getMysteryCard().setAfterHideAction(() -> {
                BigActorsManager.getMysteryCard().setAfterHideAction(() -> {});
                onSub.onCompleted();
            });
        });
    }

    @Override
    public Completable waitForInvestigatorHide() {
        return Completable.create(onSub -> {
            BigActorsManager.getPassportTable().setBeforeHideAction(() -> {
                BigActorsManager.getPassportTable().setBeforeHideAction(null);
                onSub.onCompleted();
            });
        });
    }

    @Override
    public Completable waitForCharterFlightInspected() {
        return Completable.create(onSub -> {
            BigActorsManager.getReserveSheet().setScaledDownConsumer(in -> {
                BigActorsManager.displayOrHideReserve();
                onSub.onCompleted();
            });
        });
    }

    @Override
    public void save(Object saveData) {
        new SaveLoadGameHelper().save(saveData);
    }

    @Override
    public <T> T load(Class<T> saveDataClazz) {
        return new SaveLoadGameHelper().load(saveDataClazz);
    }

    public void reset() {
        stage.addActor(collectionButton);
        stage.addActor(hallOfFameButton);
        stage.addActor(replayTutorialButton);
        stage.addActor(noAdsButton);
        if (newGameButton != null) {
            newGameButton.remove();
        }
        if (loadGameButton != null) {
            loadGameButton.remove();
        }
    }

    public void setContinueGameAction(Runnable continueGameAction) {
        this.continueGameAction = continueGameAction;
    }

    public void setNewGameAction(Runnable newGameAction) {
        this.newGameAction = newGameAction;
    }

    @Override
    public String getUnlockedAssets() {
        return Gdx.app.getPreferences("AncientTerror.xml").getString("unlockedAssets");
    }

    @Override
    public String getUnlockedArtifacts() {
        return Gdx.app.getPreferences("AncientTerror.xml").getString("unlockedArtifacts");
    }

    @Override
    public void rewriteUnlockedAssets(String unlockedAssets) {
        Gdx.app.getPreferences("AncientTerror.xml")
                .putString("unlockedAssets", unlockedAssets);
        Gdx.app.getPreferences("AncientTerror.xml").flush();
    }

    @Override
    public void rewriteUnlockedArtifacts(String unlockedArtifacts) {
        Gdx.app.getPreferences("AncientTerror.xml")
                .putString("unlockedArtifacts", unlockedArtifacts);
        Gdx.app.getPreferences("AncientTerror.xml").flush();
    }

    @Override
    public boolean hasPurchasedNoAds() {
        Preferences preferences = Gdx.app.getPreferences("AncientTerror.xml");
        if (preferences.getBoolean("TUTORIAL_PASSED", false)) {
            return preferences.getBoolean("no_ads", false);
        } else {
            return true;
        }
    }

    @Override
    public boolean hasPurchasedBonusInvestigators() {
        return Gdx.app.getPreferences("AncientTerror.xml").getBoolean("investigators_1", false);
    }
}
