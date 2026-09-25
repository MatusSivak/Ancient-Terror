package sk.sivak.eldritchhorror.core.view.components.skill;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.VisTable;
import rx.CompletableSubscriber;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.DisplayHide;
import sk.sivak.eldritchhorror.core.view.components.sheet.investigator.StatsTable;
import sk.sivak.eldritchhorror.core.view.components.sheet.investigator.StatsTableData;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import sk.sivak.eldritchhorror.core.view.utils.SelectionPanelStyle;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Locale;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SPECIAL_ELITE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.PURE_WHITE_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFontNew;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class ImproveSkillComponent extends VisTable {

    private ImproveSkillTable improveSkillTable;
    private Stat onlyAvailableSkill = null;
    private final DisplayHide displayHide;
    private boolean readOnly;
    private final Map<Label, VisTable> statRows = new HashMap<>();
    private final Map<Label, int[]> statValues = new HashMap<>();

    private List<ImageButton> imageButtons = new LinkedList<>();

    private Map<Label, ImageButton> labelImageButtonMap = new HashMap<>();

    private SingleSubscriber<? super Stat> onSub;
    private CompletableSubscriber onSub2;

    public ImproveSkillComponent() {
        setTransform(true);
        displayHide = new DisplayHide(this, null);
    }

    public void init(StatsTableData statsTableData, Stat skill) {
        this.onlyAvailableSkill = skill;
        readOnly = false;
        init(statsTableData);

    }

    public void justShow(StatsTableData statsTableData) {
        onlyAvailableSkill = null;
        readOnly = true;
        init(statsTableData);
    }

    public void justHide() {
        hide();
    }

    private void init(StatsTableData statsTableData) {
        imageButtons.clear();
        labelImageButtonMap.clear();
        statRows.clear();
        statValues.clear();
        clear();
        pad(12);
        setBackground(SelectionPanelStyle.panel("101D20F5", "8E7953"));
        this.improveSkillTable = new ImproveSkillTable();
        improveSkillTable.pad(0);
        improveSkillTable.init(statsTableData);
        add(createLabel()).growX().height(28).padBottom(6).row();
        Image divider = new Image(CustomAssetManager.getTexture(PURE_WHITE_BACKGROUND));
        divider.setColor(Color.valueOf("8E7953"));
        add(divider).growX().height(1).padBottom(8).row();
        add(improveSkillTable).growX();
        setSize(210, 328);
        show();
    }


    private Label createLabel() {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SPECIAL_ELITE, 42), Color.valueOf("E8D6AD"));
        Label label = new Label(get("skill.improve"), labelStyle);
        label.setAlignment(Align.center);
        label.setFontScale(0.44f);
        return label;
    }

    private class ImproveSkillTable extends StatsTable {
        private int statIndex = 0;

        protected void addStatPair(Label label, Label value) {
            Stat stat = resolveStatByIndex(statIndex++);
            VisTable statRow = new VisTable();
            statRow.pad(4);
            statRows.put(value, statRow);
            Image glyph = new Image(CustomAssetManager.getTexture("glyphs/" + stat.name().toLowerCase(Locale.ROOT) + ".png"));
            glyph.setScaling(Scaling.fit);
            glyph.setColor(Color.valueOf("DCC99F"));
            statRow.add(glyph).size(28).expandX().padRight(12);
            Label.LabelStyle valueStyle = new Label.LabelStyle(value.getStyle());
            valueStyle.background = SelectionPanelStyle.panel("0D171B", "384A4B");
            value.setStyle(valueStyle);
            value.setAlignment(Align.center);
            statRow.add(value).width(64).height(36).padRight(8);
            if (!readOnly && (onlyAvailableSkill == null || onlyAvailableSkill == stat)) {
                ImageButton imageButton = createPlusButton(value, stat);
                statRow.add(imageButton).size(40);
                labelImageButtonMap.put(value, imageButton);
            } else {
                statRow.add().size(40);
            }
            add(statRow).growX().height(48).padBottom(4);
            row();
        }

        private Stat resolveStatByIndex(int index) {
            switch (index) {
                case 0:
                    return Stat.LORE;
                case 1:
                    return Stat.INFLUENCE;
                case 2:
                    return Stat.OBSERVATION;
                case 3:
                    return Stat.STRENGTH;
                case 4:
                    return Stat.WILL;
                default:
                    throw new IllegalStateException("Unexpected stat index: " + index);
            }
        }

        @Override
        protected void fillStatValue(Label value, int base, int bonus) {
            statValues.put(value, new int[]{base, bonus});
            renderValue(value);
            ImageButton button = labelImageButtonMap.get(value);
            boolean available = button != null && bonus < 2;
            statRows.get(value).setBackground(SelectionPanelStyle.panel(
                    available ? "203B38" : "18272B", available ? "709B83" : "304145"));
            if (!available && button != null) {
                button.clearActions();
                button.setVisible(false);
                button.setTouchable(Touchable.disabled);
            }

        }

        private ImageButton createPlusButton(Label value, Stat stat) {
            ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
            style.up = SelectionPanelStyle.panel("31584B", "9FBC8B");
            style.over = SelectionPanelStyle.panel("426F5C", "D6DBAD");
            style.down = SelectionPanelStyle.panel("1E3C33", "E8D6AD");
            style.imageUp = new PlusGlyph();
            ImageButton imageButton = new ImageButton(style);
            imageButton.setTransform(true);
            imageButton.setOrigin(20, 20);
            RepeatAction repeatAction = Actions.repeat(RepeatAction.FOREVER, Actions.parallel(
                    Actions.sequence(
                            Actions.scaleTo(0.9f, 0.9f, 0.5f, Interpolation.sine),
                            Actions.scaleTo(1f, 1f, 0.5f, Interpolation.sine)
                    )
            ));
            imageButton.addAction(repeatAction);
            ButtonUtils.addClickListener(imageButton, () -> {
                for (ImageButton button : imageButtons) {
                    if (button == imageButton) {
                        button.clearListeners();
                        repeatAction.finish();
                        float remainingScale = 1 - button.getScaleX();
                        Vector2 buttonPosition = value.localToAscendantCoordinates(
                                ImproveSkillComponent.this, new Vector2(value.getWidth() / 2, value.getHeight() / 2));
                        button.addAction(Actions.sequence(
                                Actions.parallel(
                                        Actions.rotateTo(90, 0.25f),
                                        Actions.scaleTo(1, 1, 0.25f* remainingScale/0.1f)
                                ),
                                Actions.run(() -> showLight(buttonPosition, value, stat)),
                                Actions.scaleTo(0,0,0.25f),
                                Actions.run(button::remove)
                        ));
                        continue;
                    }
                    button.clearListeners();
                    button.getActions().clear();
                    button.addAction(Actions.scaleTo(0,0,0.25f));
                }
            });

            imageButtons.add(imageButton);
            return imageButton;
        }
    }

    private void showLight(Vector2 position, Label value, Stat stat) {
        Image image = new Image(CustomAssetManager.getTexture("icon/light.png"));
        image.setSize(280,280);
        image.setOrigin(140,140);
        image.setColor(new Color(0.5f, 1f, 0.5f, 1f));
        image.setTouchable(Touchable.disabled);
        image.setPosition(position.x - 140, position.y - 140);
        addActor(image);
        image.setScale(0);

        float duration = 0.75f;

        image.addAction(new FastForwardAction<>(Actions.sequence(
                Actions.parallel(
                        Actions.scaleTo(0.5f,0.5f,duration, Interpolation.sine),
                        Actions.rotateBy(-360,duration)
                ),
                Actions.run(() -> updateLabelValue(value)),
                Actions.parallel(
                        Actions.scaleTo(0.0f,0.0f,duration, Interpolation.sine),
                        Actions.rotateBy(-360,duration)
                ),
                Actions.run(() -> {
                    hide();
                    onSub.onSuccess(stat);
                })
        )));
    }

    private void updateLabelValue(Label value) {
        statValues.get(value)[1]++;
        renderValue(value);
    }

    private void renderValue(Label value) {
        int[] numbers = statValues.get(value);
        value.setText("[#EEE6D5]" + numbers[0]
                + (numbers[1] > 0 ? "[#A8D6A0] +" + numbers[1] : "") + "[]");
    }

    private static class PlusGlyph extends BaseDrawable {
        PlusGlyph() {
            setMinWidth(16);
            setMinHeight(16);
        }

        @Override
        public void draw(Batch batch, float x, float y, float width, float height) {
            float previous = batch.getPackedColor();
            batch.setColor(0.94f, 0.91f, 0.76f, batch.getColor().a);
            batch.draw(CustomAssetManager.getTexture(PURE_WHITE_BACKGROUND), x, y + height / 2 - 1, width, 2);
            batch.draw(CustomAssetManager.getTexture(PURE_WHITE_BACKGROUND), x + width / 2 - 1, y, 2, height);
            batch.setColor(previous);
        }
    }

    public void setOnSub(SingleSubscriber<? super Stat> onSub) {
        this.onSub = onSub;
    }

    public void setOnSub(CompletableSubscriber onSub) {
        this.onSub2 = onSub;
    }

    private void show() {
        displayHide.setDisplayedY(VIEWPORT_HEIGHT/2 - getHeight()/2);
        displayHide.displayOrHide().subscribe();
    }

    private void hide() {
        displayHide.displayOrHide().subscribe();
    }
}
