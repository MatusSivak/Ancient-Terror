package sk.sivak.eldritchhorror.core.view.components.halloffame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.firebase.HallOfFameData;
import sk.sivak.eldritchhorror.core.view.ScreenType;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.combat.ThunderEffect;
import sk.sivak.eldritchhorror.core.view.components.table.ActorFrame;
import sk.sivak.eldritchhorror.core.view.firebase.FirebaseHallOfFame;
import sk.sivak.eldritchhorror.core.view.handler.ChangeScreenHandler;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;
import sk.sivak.eldritchhorror.core.view.utils.MyMoveToAction;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_ADLER;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_MINYA;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.GRAY_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.SPLASH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTextureRegionDrawable;
import static sk.sivak.eldritchhorror.core.view.utils.RectangleUtils.randomPointInRectangle;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class HallOfFameScreen implements Screen {
    private Stage stage;
    private ScrollPane scrollPane;
    private Skin skin;
    private ChangeScreenHandler changeScreenHandler;
    private boolean screenInitialized;
    private List<HallOfFameData> hallOfFameDataList;
    private VisTable detailsTable;
    private ActorFrame detailsTableFrame;
    private Label selectedNameLabel;
    private Label selectedAncientOneLabel;
    private Label selectedDifficultyLabel;

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
        background.addAction(Actions.repeat(RepeatAction.FOREVER, moveToAction));
        stage.addActor(background);
        new ThunderEffect(background).execute();

        Table buttonsTable = new Table();
        buttonsTable.pad(15);
        buttonsTable.setHeight(ViewProperties.VIEWPORT_HEIGHT);

        TextButton backButton = buildButton(get("hallOfFame.back"));
        backButton.setColor(Color.GRAY);
        ButtonUtils.addClickListener(backButton, () -> {
            changeScreenHandler.changeScreen(ScreenType.INIT_GAME);
        });
        buttonsTable.add(backButton).width(175).pad(0).align(Align.bottomLeft).expandY().height(backButton.getHeight());
        buttonsTable.align(Align.topLeft);

        Gdx.input.setInputProcessor(stage);

        buttonsTable.validate();
        stage.addActor(buttonsTable);

        initTable();

    }

    private void initTable() {
        VisTable headerTable = new VisTable();
        headerTable.add(createHeaderLabel(get("hallOfFame.header.player"), Color.YELLOW)).pad(0).width(220);
        headerTable.addSeparator(true).padTop(0).padBottom(0);
        headerTable.add(createHeaderLabel(get("hallOfFame.header.ancientOne"), Color.YELLOW)).pad(0).width(180);
        headerTable.addSeparator(true).padTop(0).padBottom(0);
        headerTable.add(createHeaderLabel(get("hallOfFame.header.difficulty"), Color.YELLOW)).pad(0).width(120);
        headerTable.pack();
        headerTable.setPosition(417, VIEWPORT_HEIGHT - 40);
        stage.addActor(new ActorFrame(headerTable,5));
        stage.addActor(headerTable);

        createDetailsTable(null);

        new FirebaseHallOfFame().fetchHallOfFameData().subscribe(list -> {
            Gdx.app.postRunnable(() -> {
                this.hallOfFameDataList = list;
                VisTable table = new VisTable();
                for (int i = 0; i < list.size(); i++) {
                    HallOfFameData hallOfFameData = list.get(i);
                    float alpha = i%2 == 0 ? 0.33f : 0.66f;
                    Label nameLabel = createNameLabel(" " + hallOfFameData.getName().trim(), Color.WHITE, alpha);
                    table.add(nameLabel).pad(0).width(220);
                    table.addSeparator(true).padTop(0).padBottom(0);
                    Label ancientOneLabel = createNameLabel(" " + hallOfFameData.getAncientOneId().toPrettyString(), Color.LIGHT_GRAY, alpha);
                    table.add(ancientOneLabel).pad(0).width(180);
                    table.addSeparator(true).padTop(0).padBottom(0);
                    String difficulty = hallOfFameData.getDifficulty().equals("EASY") ? get("hallOfFame.difficulty.easy") : get("hallOfFame.difficulty.normal");
                    Label difficultyLabel = createNameLabel(" " + difficulty, Color.LIGHT_GRAY, alpha);
                    table.add(difficultyLabel).pad(0).width(120);
                    table.row();
                    table.addSeparator(false).pad(0).colspan(5).row();

                    Runnable onRowClick = () -> {
                        createDetailsTable(hallOfFameData);
                        if (selectedNameLabel != null) {
                            selectedNameLabel.setColor(Color.WHITE);
                        }
                        if (selectedAncientOneLabel != null) {
                            selectedAncientOneLabel.setColor(Color.LIGHT_GRAY);
                        }
                        if (selectedDifficultyLabel != null) {
                            selectedDifficultyLabel.setColor(Color.LIGHT_GRAY);
                        }
                        selectedNameLabel = nameLabel;
                        selectedAncientOneLabel = ancientOneLabel;
                        selectedDifficultyLabel = difficultyLabel;

                        selectedNameLabel.setColor(Color.YELLOW);
                        selectedAncientOneLabel.setColor(Color.YELLOW);
                        selectedDifficultyLabel.setColor(Color.YELLOW);
                    };
                    ButtonUtils.addClickListener(nameLabel, onRowClick);
                    ButtonUtils.addClickListener(ancientOneLabel, onRowClick);
                    ButtonUtils.addClickListener(difficultyLabel, onRowClick);
                }

                table.pack();
                scrollPane = new ScrollPane(table);
                scrollPane.setSize(table.getWidth(),ViewProperties.VIEWPORT_HEIGHT - 60);
                scrollPane.setPosition(VIEWPORT_WIDTH - scrollPane.getWidth() - 15, 15);
                scrollPane.setOverscroll(false, false);

                stage.addActor(new ActorFrame(scrollPane, 5));
                stage.addActor(scrollPane);

            });
        });
    }

    private void createDetailsTable(HallOfFameData hallOfFameData) {
        if (detailsTable != null) {
            detailsTable.remove();
            detailsTableFrame.remove();
        }
        detailsTable = new VisTable();
        detailsTable.pad(5);
        if (hallOfFameData == null) {
            detailsTable.add(createDetailsLabel(get("hallOfFame.details.unknown.datetime"), Color.WHITE)).width(375).row();
            detailsTable.add(createDetailsLabel(get("hallOfFame.details.unknown.investigators"), Color.WHITE)).width(375).row();
            detailsTable.add(createDetailsLabel(get("hallOfFame.details.unknown.doom"), Color.WHITE)).width(375).row();
            detailsTable.add(createDetailsLabel(get("hallOfFame.details.unknown.rounds"), Color.WHITE)).width(375).row();
            detailsTable.add(createDetailsLabel(get("hallOfFame.details.unknown.mysteries"), Color.WHITE)).width(375).row();
        } else {
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
            DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, Locale.getDefault());
            String dateAsString = dateFormat.format(new Date(hallOfFameData.getTimestamp()));
            String timeAsString = timeFormat.format(new Date(hallOfFameData.getTimestamp()));
            detailsTable.add(createDetailsLabel(get("hallOfFame.details.datetime", dateAsString, timeAsString), Color.WHITE)).width(375).row();

            String investigatorsString = "";
            for (String investigator : hallOfFameData.getInvestigators()) {
                investigatorsString += "\n - " + investigator;
            }
            detailsTable.add(createDetailsLabel(get("hallOfFame.details.investigators", hallOfFameData.getInvestigatorsCount(), investigatorsString), Color.WHITE)).width(375).row();

            int minutes;
            int hours;
            if (hallOfFameData.getDoom() == 0) {
                minutes = 0;
                hours = 0;
            } else {
                minutes = 60 - hallOfFameData.getDoom() * 5;
                hours = 23;
                if (minutes < 0) {
                    hours = 22;
                    minutes += 60;
                }
            }


            detailsTable.add(createDetailsLabel(get("hallOfFame.details.doom", String.format(Locale.getDefault(), "%02d",hours), String.format(Locale.getDefault(), "%02d",minutes)), Color.WHITE)).width(375).row();
            detailsTable.add(createDetailsLabel(get("hallOfFame.details.rounds", hallOfFameData.getRounds()), Color.WHITE)).width(375).row();
            String mysteriesString = "";
            for (String mystery : hallOfFameData.getSolvedMysteries()) {
                mysteriesString += "\n - " + mystery;
            }
            detailsTable.add(createDetailsLabel(get("hallOfFame.details.mysteries", hallOfFameData.getSolvedMysteries().size(), mysteriesString), Color.WHITE)).width(375).row();
        }

        detailsTable.pack();
        detailsTable.setPosition(15, VIEWPORT_HEIGHT - detailsTable.getHeight() - 18);
        detailsTable.setBackground(getTextureRegionDrawable(GRAY_BACKGROUND));
        detailsTableFrame = new ActorFrame(detailsTable, 5);
        stage.addActor(detailsTableFrame);
        stage.addActor(detailsTable);
    }

    private TextButton buildButton(String text) {
        TextButton button = new TextButton(text, skin);
        button.getLabel().setFontScale(0.5f);
        button.getLabel().setStyle(new Label.LabelStyle(CustomAssetManager.getBitmapFont(FONT_ADLER), Color.WHITE));
        button.setSize(280, button.getHeight()*1.5f);
        return button;
    }

    @Override
    public void render(float delta) {
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

    private Label createNameLabel(String text, Color fontColor, float alpha) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_ADLER), fontColor);
        Color color = new Color(1f, 1f, 1f, alpha);
        labelStyle.background = new TextureRegionDrawable(CustomAssetManager.getTextureRegionDrawable(GRAY_BACKGROUND)) {
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                batch.setColor(color);
                super.draw(batch, x, y, width, height);
            }
        };
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.left);
        label.setFontScale(0.35f);
        return label;
    }

    private Label createHeaderLabel(String text, Color color) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_MINYA), color);
        Color bgColor = new Color(1f, 1f, 1f, 1f);
        labelStyle.background = new TextureRegionDrawable(CustomAssetManager.getTextureRegionDrawable(GRAY_BACKGROUND)) {
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                batch.setColor(bgColor);
                super.draw(batch, x, y, width, height);
            }
        };
        Label label = new Label(text, labelStyle);
        label.setFontScale(0.5f);
        label.setAlignment(Align.left, Align.left);
        return label;
    }

    private Label createDetailsLabel(String text, Color color) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_MINYA), color);
        Label label = new Label(text, labelStyle);
        labelStyle.font.getData().markupEnabled = true;
        label.setFontScale(0.5f);
        label.setWrap(true);
        label.setAlignment(Align.left, Align.left);
        return label;
    }
}
