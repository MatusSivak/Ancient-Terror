package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.view.ScreenType;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.table.ActorFrame;
import sk.sivak.eldritchhorror.core.view.components.table.ArtifactsCollectionTable;
import sk.sivak.eldritchhorror.core.view.components.table.AssetsCollectionTable;
import sk.sivak.eldritchhorror.core.view.components.table.CardsCollectionTable;
import sk.sivak.eldritchhorror.core.view.components.table.ConditionsCollectionTable;
import sk.sivak.eldritchhorror.core.view.components.table.SpellsCollectionTable;
import sk.sivak.eldritchhorror.core.view.handler.ChangeScreenHandler;
import sk.sivak.eldritchhorror.core.view.utils.ButtonBuilder;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;
import sk.sivak.eldritchhorror.core.view.utils.MyMoveToAction;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_ADLER;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.SPLASH;
import static sk.sivak.eldritchhorror.core.view.utils.RectangleUtils.randomPointInRectangle;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class CombatScreen implements Screen {
    private Stage stage;
    private Label fpsLabel;

    private List<AssetId> unlockedAssets = new LinkedList<>();
    private List<AssetId> discoveredAssets = new LinkedList<>();

    private List<ArtifactId> unlockedArtifacts = new LinkedList<>();
    private List<ArtifactId> discoveredArtifacts = new LinkedList<>();

    private List<SpellId> discoveredSpells = new LinkedList<>();

    private List<ConditionId> discoveredConditions = new LinkedList<>();

    private ScrollPane scrollPane;
    private Skin skin;
    private ChangeScreenHandler changeScreenHandler;
    private boolean screenInitialized;

    @Override
    public void show() {
        if (screenInitialized) {
            Gdx.input.setInputProcessor(stage);
            return;
        }
        screenInitialized = true;
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }
        stage = new Stage(new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
        Image background = new Image(CustomAssetManager.getTexture(SPLASH));
        background.setSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        background.setOrigin(Align.center);

        background.setScale(1.25f);
        float width = ViewProperties.VIEWPORT_WIDTH * background.getScaleX() - ViewProperties.VIEWPORT_WIDTH;
        float height = ViewProperties.VIEWPORT_HEIGHT * background.getScaleY() - ViewProperties.VIEWPORT_HEIGHT;
        MyMoveToAction moveToAction = new MyMoveToAction(new Vector2(0, 0),
                () -> randomPointInRectangle(new Vector2(0, 0), width, height), 30f, 1.25f);
        background.addAction(Actions.repeat(RepeatAction.FOREVER,
                moveToAction));
        stage.addActor(background);
        new ThunderEffect(background).execute();

        TextButton buttonAssets = buildButton(get("combat.assets"));
        TextButton buttonArtifacts = buildButton(get("combat.artifacts"));
        TextButton buttonSpells = buildButton(get("combat.spells"));
        TextButton buttonConditions = buildButton(get("combat.conditions"));

        Table buttonsTable = new Table();
        buttonsTable.pad(15);
        buttonsTable.setHeight(ViewProperties.VIEWPORT_HEIGHT);

        buttonAssets.clearActions();
        buttonArtifacts.clearActions();
        buttonSpells.clearActions();
        buttonConditions.clearActions();

        ButtonUtils.addClickListener(buttonAssets, () -> {
            AssetsCollectionTable table = new AssetsCollectionTable();
            table.setUnlocked(unlockedAssets);
            table.setDiscovered(discoveredAssets);
            initNewTable(table);
            buttonsTable.getCell(buttonAssets).width(175);
            buttonAssets.getLabel().setFontScale(0.5f);
            buttonAssets.setChecked(true);
            buttonAssets.setColor(Color.WHITE);
            buttonAssets.getLabel().setColor(Color.WHITE);


            buttonArtifacts.getLabel().setColor(Color.GRAY);
            buttonSpells.getLabel().setColor(Color.GRAY);
            buttonConditions.getLabel().setColor(Color.GRAY);
            buttonsTable.getCell(buttonArtifacts).width(175 * 0.75f);
            buttonsTable.getCell(buttonSpells).width(175 * 0.75f);
            buttonsTable.getCell(buttonConditions).width(175 * 0.75f);
            buttonArtifacts.getLabel().setFontScale(0.35f);
            buttonSpells.getLabel().setFontScale(0.35f);
            buttonConditions.getLabel().setFontScale(0.35f);
            buttonArtifacts.setChecked(false);
            buttonSpells.setChecked(false);
            buttonConditions.setChecked(false);
            buttonArtifacts.setColor(Color.GRAY);
            buttonSpells.setColor(Color.GRAY);
            buttonConditions.setColor(Color.GRAY);
//            scrollDown(1, table.getRows());
        });

        ButtonUtils.addClickListener(buttonArtifacts, () -> {
            ArtifactsCollectionTable table = new ArtifactsCollectionTable();
            table.setUnlocked(unlockedArtifacts);
            table.setDiscovered(discoveredArtifacts);
            initNewTable(table);
            buttonsTable.getCell(buttonArtifacts).width(175);
            buttonArtifacts.getLabel().setFontScale(0.5f);
            buttonArtifacts.setChecked(true);
            buttonArtifacts.getStyle().checked = buttonArtifacts.getStyle().down;
            buttonArtifacts.setColor(Color.WHITE);
            buttonArtifacts.getLabel().setColor(Color.WHITE);

            buttonAssets.getLabel().setColor(Color.GRAY);
            buttonSpells.getLabel().setColor(Color.GRAY);
            buttonConditions.getLabel().setColor(Color.GRAY);
            buttonsTable.getCell(buttonAssets).width(175 * 0.75f);
            buttonsTable.getCell(buttonSpells).width(175 * 0.75f);
            buttonsTable.getCell(buttonConditions).width(175 * 0.75f);
            buttonAssets.getLabel().setFontScale(0.35f);
            buttonSpells.getLabel().setFontScale(0.35f);
            buttonConditions.getLabel().setFontScale(0.35f);
            buttonAssets.setChecked(false);
            buttonSpells.setChecked(false);
            buttonConditions.setChecked(false);
            buttonAssets.setColor(Color.GRAY);
            buttonSpells.setColor(Color.GRAY);
            buttonConditions.setColor(Color.GRAY);
//            scrollDown(1, table.getRows());
        });

        ButtonUtils.addClickListener(buttonSpells, () -> {
            SpellsCollectionTable table = new SpellsCollectionTable();
            table.setUnlocked(Arrays.asList(SpellId.values()));
            table.setDiscovered(discoveredSpells);
            initNewTable(table);
            buttonsTable.getCell(buttonSpells).width(175);
            buttonSpells.getLabel().setFontScale(0.5f);
            buttonSpells.setChecked(true);
            buttonSpells.setColor(Color.WHITE);
            buttonSpells.getStyle().checked = buttonSpells.getStyle().down;
            buttonSpells.getLabel().setColor(Color.WHITE);


            buttonAssets.getLabel().setColor(Color.GRAY);
            buttonArtifacts.getLabel().setColor(Color.GRAY);
            buttonConditions.getLabel().setColor(Color.GRAY);
            buttonsTable.getCell(buttonAssets).width(175 * 0.75f);
            buttonsTable.getCell(buttonArtifacts).width(175 * 0.75f);
            buttonsTable.getCell(buttonConditions).width(175 * 0.75f);
            buttonAssets.getLabel().setFontScale(0.35f);
            buttonArtifacts.getLabel().setFontScale(0.35f);
            buttonConditions.getLabel().setFontScale(0.35f);
            buttonAssets.setChecked(false);
            buttonArtifacts.setChecked(false);
            buttonConditions.setChecked(false);
            buttonAssets.setColor(Color.GRAY);
            buttonArtifacts.setColor(Color.GRAY);
            buttonConditions.setColor(Color.GRAY);
//            scrollDown(1, table.getRows());

        });

        ButtonUtils.addClickListener(buttonConditions, () -> {
            ConditionsCollectionTable table = new ConditionsCollectionTable();
            table.setUnlocked(Arrays.asList(ConditionId.values()));
            table.setDiscovered(discoveredConditions);
            initNewTable(table);
            buttonsTable.getCell(buttonConditions).width(175);
            buttonConditions.getLabel().setFontScale(0.5f);
            buttonConditions.setChecked(true);
            buttonConditions.getStyle().checked = buttonConditions.getStyle().down;
            buttonConditions.setColor(Color.WHITE);
            buttonConditions.getLabel().setColor(Color.WHITE);

            buttonAssets.getLabel().setColor(Color.GRAY);
            buttonArtifacts.getLabel().setColor(Color.GRAY);
            buttonSpells.getLabel().setColor(Color.GRAY);
            buttonsTable.getCell(buttonAssets).width(175 * 0.75f);
            buttonsTable.getCell(buttonArtifacts).width(175 * 0.75f);
            buttonsTable.getCell(buttonSpells).width(175 * 0.75f);
            buttonAssets.getLabel().setFontScale(0.35f);
            buttonArtifacts.getLabel().setFontScale(0.35f);
            buttonSpells.getLabel().setFontScale(0.35f);
            buttonAssets.setChecked(false);
            buttonArtifacts.setChecked(false);
            buttonSpells.setChecked(false);
            buttonAssets.setColor(Color.GRAY);
            buttonArtifacts.setColor(Color.GRAY);
            buttonSpells.setColor(Color.GRAY);
//            scrollDown(1, table.getRows());
        });

        buttonsTable.add(buttonAssets).align(Align.center).width(175).pad(0).height(buttonAssets.getHeight()).row();
        buttonsTable.add(buttonArtifacts).align(Align.center).width(175  * 0.75f).pad(0).height(buttonArtifacts.getHeight()).row();
        buttonsTable.add(buttonSpells).align(Align.center).width(175  * 0.75f).pad(0).height(buttonSpells.getHeight()).row();
        buttonsTable.add(buttonConditions).align(Align.center).width(175 * 0.75f).pad(0).height(buttonConditions.getHeight()).row();

        buttonAssets.getLabel().setColor(Color.WHITE);
        buttonArtifacts.getLabel().setColor(Color.GRAY);
        buttonSpells.getLabel().setColor(Color.GRAY);
        buttonConditions.getLabel().setColor(Color.GRAY);

        buttonAssets.getLabel().setFontScale(0.5f);
        buttonArtifacts.getLabel().setFontScale(0.35f);
        buttonSpells.getLabel().setFontScale(0.35f);
        buttonConditions.getLabel().setFontScale(0.35f);

        buttonAssets.setColor(Color.WHITE);
        buttonArtifacts.setColor(Color.GRAY);
        buttonSpells.setColor(Color.GRAY);
        buttonConditions.setColor(Color.GRAY);

        TextButton backButton = buildButton(get("combat.back"));
        backButton.setColor(Color.GRAY);
        ButtonUtils.addClickListener(backButton, () -> {
            changeScreenHandler.changeScreen(ScreenType.INIT_GAME);
        });
        buttonsTable.add(backButton).width(175).pad(0).align(Align.bottomLeft).expandY().height(backButton.getHeight());
        buttonsTable.align(Align.topLeft);

        AssetsCollectionTable table = new AssetsCollectionTable();
        table.setUnlocked(unlockedAssets);
        table.setDiscovered(discoveredAssets);
        table.init();

        buttonAssets.setChecked(true);
        buttonAssets.getStyle().checked = buttonAssets.getStyle().over;
        buttonAssets.setScale(0.8f);


        scrollPane = new ScrollPane(table);
        scrollPane.setSize(table.getWidth(),ViewProperties.VIEWPORT_HEIGHT - 30);
        scrollPane.setPosition(VIEWPORT_WIDTH - scrollPane.getWidth() - 15, 15);
        scrollPane.setOverscroll(false, false);

        stage.addActor(new ActorFrame(scrollPane, 5));
        stage.addActor(scrollPane);


        fpsLabel = new Label("XX", new Label.LabelStyle(CustomAssetManager.getBitmapFont(CustomAssetManager.FONT_ADLER), Color.YELLOW));
        fpsLabel.setPosition(0, ViewProperties.VIEWPORT_HEIGHT - fpsLabel.getHeight());
//        stage.addActor(fpsLabel);

        Gdx.input.setInputProcessor(stage);

        buttonsTable.validate();
        stage.addActor(buttonsTable);


    }


    private TextButton buildButton(String title) {
        TextButton button = new TextButton(title, skin);
        button.getLabel().setFontScale(0.5f);
        button.getLabel().setStyle(new Label.LabelStyle(CustomAssetManager.getBitmapFont(FONT_ADLER), Color.WHITE));
        button.setSize(280, button.getHeight()*1.5f);
        return button;
    }

    private void initNewTable(CardsCollectionTable table) {
        table.init();

        scrollPane.remove();
        scrollPane = new ScrollPane(table);
        scrollPane.setSize(table.getWidth(), ViewProperties.VIEWPORT_HEIGHT - 30);
        scrollPane.setPosition(VIEWPORT_WIDTH - scrollPane.getWidth() - 15, 15);
        scrollPane.setOverscroll(false, false);
        stage.addActor(scrollPane);
    }

    public void setUnlockedAssets(List<AssetId> unlockedAssets) {
        this.unlockedAssets = unlockedAssets;
    }

    public void setDiscoveredAssets(List<AssetId> discoveredAssets) {
        this.discoveredAssets = discoveredAssets;
    }

    public void setUnlockedArtifacts(List<ArtifactId> unlockedArtifacts) {
        this.unlockedArtifacts = unlockedArtifacts;
    }

    public void setDiscoveredArtifacts(List<ArtifactId> discoveredArtifacts) {
        this.discoveredArtifacts = discoveredArtifacts;
    }

    public void setDiscoveredSpells(List<SpellId> discoveredSpells) {
        this.discoveredSpells = discoveredSpells;
    }

    public void setDiscoveredConditions(List<ConditionId> discoveredConditions) {
        this.discoveredConditions = discoveredConditions;
    }

    private float totalTime = 0f;

    @Override
    public void render(float delta) {
        if (totalTime > 0.125f) {
            fpsLabel.setText("" + Math.round(1 / delta));
            totalTime = 0;
        }
        totalTime += delta;
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
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

    }

    @Override
    public void dispose() {

    }


    public void setDefaultSkin(Skin skin) {
        this.skin = skin;
    }

    public void setChangeScreenHandler(ChangeScreenHandler changeScreenHandler) {
        this.changeScreenHandler = changeScreenHandler;
    }
}
