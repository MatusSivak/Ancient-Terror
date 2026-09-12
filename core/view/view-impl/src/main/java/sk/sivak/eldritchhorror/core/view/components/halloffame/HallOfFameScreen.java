package sk.sivak.eldritchhorror.core.view.components.halloffame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import sk.sivak.eldritchhorror.core.constants.firebase.HallOfFameData;
import sk.sivak.eldritchhorror.core.view.ScreenType;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.combat.ThunderEffect;
import sk.sivak.eldritchhorror.core.view.firebase.FirebaseHallOfFame;
import sk.sivak.eldritchhorror.core.view.handler.ChangeScreenHandler;
import sk.sivak.eldritchhorror.core.view.utils.AncientTerrorMenuStyles;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;
import sk.sivak.eldritchhorror.core.view.utils.MyMoveToAction;
import sk.sivak.eldritchhorror.core.view.utils.UiText;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.*;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;
import static sk.sivak.eldritchhorror.core.view.utils.RectangleUtils.randomPointInRectangle;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class HallOfFameScreen implements Screen {
    private static final Color GOLD = Color.valueOf("E8D9B0");
    private static final Color TEXT = Color.valueOf("E5DFCC");
    private static final Color MUTED = Color.valueOf("BEB69F");
    private Stage stage;
    private ScrollPane recordsScroll;
    private ScrollPane detailsScroll;
    private Table detailsTable;
    private Button selectedRow;
    private ChangeScreenHandler changeScreenHandler;
    private String initializedLanguage;

    @Override
    public void show() {
        if (stage != null && !UiText.getLanguage().equals(initializedLanguage)) dispose();
        if (stage == null) {
            initializedLanguage = UiText.getLanguage();
            stage = new Stage(new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
            Image background = new Image(CustomAssetManager.getTexture(SPLASH));
            background.setSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
            background.setOrigin(Align.center);
            background.setScale(1.25f);
            background.addAction(Actions.repeat(RepeatAction.FOREVER, new MyMoveToAction(
                    new Vector2(0, 0), () -> randomPointInRectangle(new Vector2(0, 0),
                    VIEWPORT_WIDTH * 0.25f, VIEWPORT_HEIGHT * 0.25f), 30f, 1.25f)));
            stage.addActor(background);
            new ThunderEffect(background).execute();
            initTable();
        }
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        Gdx.input.setInputProcessor(stage);
    }

    private void initTable() {
        Table root = new Table();
        root.setFillParent(true);
        root.pad(12f);
        root.add(label(get("init.hallOfFame"), 0.5f, GOLD)).colspan(2).height(48f).row();

        Table detailsPanel = new Table();
        detailsPanel.setBackground(panel("35483EF5"));
        detailsPanel.pad(14f);
        detailsScroll = scroll(new Table());
        detailsPanel.add(detailsScroll).grow();
        createDetailsTable(null);

        Table recordsPanel = new Table();
        recordsPanel.setBackground(panel("2B3C33F5"));
        recordsPanel.pad(10f);
        Table header = new Table();
        header.add(label(get("hallOfFame.header.player").trim(), 0.29f, GOLD)).width(180f).padLeft(10f);
        header.add(label(get("hallOfFame.header.ancientOne").trim(), 0.29f, GOLD)).width(190f);
        header.add(label(get("hallOfFame.header.datetime"), 0.29f, GOLD)).width(168f);
        recordsPanel.add(header).growX().height(36f).row();
        recordsScroll = scroll(label(get("hallOfFame.loading"), 0.3f, MUTED));
        recordsPanel.add(recordsScroll).grow();
        root.add(detailsPanel).width(340f).growY().padRight(12f);
        root.add(recordsPanel).grow().row();

        TextButton back = new TextButton(get("hallOfFame.back"), AncientTerrorMenuStyles.button());
        back.getLabel().setFontScale(0.33f);
        AncientTerrorMenuStyles.makeMomentary(back);
        AncientTerrorMenuStyles.addFocusHighlight(back);
        ButtonUtils.addClickListener(back, () -> changeScreenHandler.changeScreen(ScreenType.INIT_GAME));
        root.add(back).width(175f).height(46f).padTop(10f).left();
        stage.addActor(root);

        final Stage requestingStage = stage;
        new FirebaseHallOfFame().fetchHallOfFameData().subscribe(list -> Gdx.app.postRunnable(() -> {
            if (stage == requestingStage) populateRecords(list);
        }), error -> Gdx.app.postRunnable(() -> {
            if (stage == requestingStage) recordsScroll.setWidget(label(get("hallOfFame.unavailable"), 0.3f, MUTED));
        }));
    }

    private void populateRecords(List<HallOfFameData> list) {
        if (list.isEmpty()) {
            recordsScroll.setWidget(label(get("hallOfFame.empty"), 0.3f, MUTED));
            return;
        }
        Table records = new Table();
        records.top();
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        for (int i = 0; i < list.size(); i++) {
            HallOfFameData record = list.get(i);
            Drawable normal = panel(i % 2 == 0 ? "46584A" : "394B40");
            Button.ButtonStyle style = new Button.ButtonStyle();
            style.up = normal;
            style.over = AncientTerrorMenuStyles.highlight(normal);
            style.down = style.over;
            style.checked = style.over;
            Button row = new Button(style);
            row.pad(10f);
            row.add(label(record.getName().trim(), 0.3f, TEXT)).width(180f);
            row.add(label(record.getAncientOneId().toPrettyString(), 0.29f, MUTED)).width(190f);
            Date timestamp = new Date(record.getTimestamp());
            row.add(label(dateFormat.format(timestamp) + "\n" + timeFormat.format(timestamp),
                    0.28f, MUTED)).width(158f);
            AncientTerrorMenuStyles.addFocusHighlight(row);
            ButtonUtils.addClickListener(row, () -> selectRecord(row, record));
            records.add(row).growX().minHeight(54f).padBottom(4f).row();
            if (i == 0) selectRecord(row, record);
        }
        recordsScroll.setWidget(records);
    }

    private void selectRecord(Button row, HallOfFameData record) {
        if (selectedRow != null) selectedRow.setChecked(false);
        selectedRow = row;
        row.setChecked(true);
        createDetailsTable(record);
    }

    private void createDetailsTable(HallOfFameData hallOfFameData) {
        detailsTable = new Table();
        detailsTable.top().left();
        if (hallOfFameData != null) {
            detailsTable.add(label(hallOfFameData.getName().trim(), 0.4f, GOLD))
                    .width(300f).padBottom(16f).row();
        }
        if (hallOfFameData == null) {
            detailsTable.add(createDetailsLabel(get("hallOfFame.details.unknown.datetime"), TEXT)).width(300).padBottom(12).row();
            detailsTable.add(createDetailsLabel(get("hallOfFame.details.unknown.investigators"), TEXT)).width(300).padBottom(12).row();
            detailsTable.add(createDetailsLabel(get("hallOfFame.details.unknown.doom"), TEXT)).width(300).padBottom(12).row();
            detailsTable.add(createDetailsLabel(get("hallOfFame.details.unknown.rounds"), TEXT)).width(300).padBottom(12).row();
            detailsTable.add(createDetailsLabel(get("hallOfFame.details.unknown.mysteries"), TEXT)).width(300).padBottom(12).row();
        } else {
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
            DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, Locale.getDefault());
            String dateAsString = dateFormat.format(new Date(hallOfFameData.getTimestamp()));
            String timeAsString = timeFormat.format(new Date(hallOfFameData.getTimestamp()));
            detailsTable.add(createDetailsLabel(get("hallOfFame.details.datetime", dateAsString, timeAsString), TEXT)).width(300).padBottom(12).row();

            String investigatorsString = "";
            for (String investigator : hallOfFameData.getInvestigators()) {
                investigatorsString += "\n - " + investigator;
            }
            detailsTable.add(createDetailsLabel(get("hallOfFame.details.investigators", hallOfFameData.getInvestigatorsCount(), investigatorsString), TEXT)).width(300).padBottom(12).row();

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


            detailsTable.add(createDetailsLabel(get("hallOfFame.details.doom", String.format(Locale.getDefault(), "%02d",hours), String.format(Locale.getDefault(), "%02d",minutes)), TEXT)).width(300).padBottom(12).row();
            detailsTable.add(createDetailsLabel(get("hallOfFame.details.rounds", hallOfFameData.getRounds()), TEXT)).width(300).padBottom(12).row();
            String mysteriesString = "";
            for (String mystery : hallOfFameData.getSolvedMysteries()) {
                mysteriesString += "\n - " + mystery;
            }
            detailsTable.add(createDetailsLabel(get("hallOfFame.details.mysteries", hallOfFameData.getSolvedMysteries().size(), mysteriesString), TEXT)).width(300).padBottom(12).row();
        }

        detailsScroll.setWidget(detailsTable);
        detailsScroll.setScrollY(0f);
    }

    private static ScrollPane scroll(com.badlogic.gdx.scenes.scene2d.Actor content) {
        ScrollPane pane = new ScrollPane(content);
        pane.setScrollingDisabled(true, false);
        pane.setOverscroll(false, false);
        return pane;
    }

    private static Drawable panel(String color) {
        Drawable background = getTextureRegionDrawable(PURE_WHITE_BACKGROUND).tint(Color.valueOf(color));
        background.setMinWidth(0f);
        background.setMinHeight(0f);
        return background;
    }

    private static Label label(String text, float scale, Color color) {
        // Locale date formats can emit non-breaking spaces absent from the bitmap font.
        text = text.replace('\u202F', ' ').replace('\u00A0', ' ');
        Label label = new Label(text, new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4), color));
        label.setFontScale(scale);
        label.setWrap(true);
        label.setAlignment(Align.left);
        return label;
    }

    private Label createDetailsLabel(String text, Color color) {
        Label result = label(text.replace("[#ffff00]", "[#E8D9B0]")
                .replace("[#bfbfbf]", "[#E5DFCC]"), 0.28f, color);
        result.getStyle().font.getData().markupEnabled = true;
        return result;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        if (stage != null) stage.getViewport().update(width, height, true);
    }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    @Override public void dispose() {
        if (stage != null) stage.dispose();
        stage = null;
        selectedRow = null;
    }

    public void setDefaultSkin(Skin skin) { }
    public void setChangeScreenHandler(ChangeScreenHandler handler) {
        changeScreenHandler = handler;
    }
}
