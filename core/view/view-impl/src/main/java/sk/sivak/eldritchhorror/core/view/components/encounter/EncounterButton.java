package sk.sivak.eldritchhorror.core.view.components.encounter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.CombatEncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.action.ActionButton;
import sk.sivak.eldritchhorror.core.view.components.sheet.DisplayHide;
import sk.sivak.eldritchhorror.core.view.components.sheet.monster.ToughnessBar;
import sk.sivak.eldritchhorror.core.view.utils.UiText;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SOURCE_SERIF_4;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFontNew;

public class EncounterButton extends Table {

    public static final int LABEL_WIDTH = 230;
    public static final int BUTTON_SIZE = 44;
    public static final float OPACITY_3 = 1f;
    public static final float OPACITY_1 = 0.75f;
    public static final float OPACITY_2 = 0.875f;
    private float backgroundAlpha = 0.75f;
    private final ActionButtonData actionButtonData;
    private EncounterButtonData encounterButtonData;
    private ActionButton actionButton;
    private SelectEncounterTable selectEncounterListener;
    private Image hitImage;
    private ClickListener choiceListener;

    public EncounterButton(EncounterButtonData encounterButtonData) {
        this.encounterButtonData = encounterButtonData;
        actionButtonData = new EncounterButtonDataAdapter(encounterButtonData).asActionButtonData();
        init();
    }

    private void init() {
        if (encounterButtonData.isEnabled()) {
            initEnabled(encounterButtonData);
        } else {
            initDisabled();
        }

        setBackground(EncounterChoiceDrawable.NORMAL);
        left();
        pad(12);
        addHitImage();
        pack();
    }

    public void setSelectEncounterListener(SelectEncounterTable selectEncounterListener) {
        this.selectEncounterListener = selectEncounterListener;
    }

    private void initDisabled() {
        actionButton = addActionButton();
        actionButton.setChecked(true);
        Label disabledLabel = createDisabledLabel(encounterButtonData.getDisabledReason());
        disabledLabel.setWrap(true);
        disabledLabel.setAlignment(Align.left, Align.center);
        add(disabledLabel).width(LABEL_WIDTH).padLeft(5).padRight(5);
    }

    private void initEnabled(EncounterButtonData encounterButtonData) {
        if (encounterButtonData.getButtonIcon().contains("monster/")) {
            initMonsterButton();
        } else if (encounterButtonData.getSecondLine() == null) {
            initOneLiner();
        } else {
            initTwoLiner();
        }
    }

    private void initOneLiner() {
        addActionButton();
        add(createEnabledLabel(resolveLocalizedText(encounterButtonData.getFirstLine()))).width(LABEL_WIDTH).padLeft(5).padRight(5);
    }

    private void initTwoLiner() {
        addActionButton();
        Table lines = new Table();
        lines.add(createEnabledLabel(resolveLocalizedText(encounterButtonData.getFirstLine()))).left().width(LABEL_WIDTH).row();
        Label detail = createLabel(resolveLocalizedText(encounterButtonData.getSecondLine()), new Color(0xcbb990ff));
        detail.setStyle(new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 18), new Color(0xcbb990ff)));
        lines.add(detail).left().width(LABEL_WIDTH).padTop(3).row();
        add(lines).padLeft(5).padRight(5);
    }

    private void initMonsterButton() {
        addActionButton();
        Table lines = new Table();
        lines.add(createEnabledLabel(resolveLocalizedText(encounterButtonData.getFirstLine()))).left().width(LABEL_WIDTH).row();

        CombatEncounterButtonData combatEncounterButtonData = ((CombatEncounterButtonData) encounterButtonData);
        Integer toughness = combatEncounterButtonData.getMonsterInfo().getToughness();
        Integer currentHealth = combatEncounterButtonData.getMonsterInfo().getCurrentHealth();

        ToughnessBar toughnessBar = new ToughnessBar();
        float scale = Math.min(0.49f, LABEL_WIDTH / (Math.max(1, toughness == null ? 0 : toughness) * 42.3f));
        toughnessBar.init(toughness == null ? 0 : toughness, currentHealth == null ? 0 : currentHealth, scale);

        lines.add(toughnessBar).height(53 * scale).left().row();
        add(lines).padLeft(5).padRight(5);
    }

    private ActionButton addActionButton() {
        if (encounterButtonData.getButtonIcon().contains("GATE")) {
            actionButton = GateEncounterButton.build(actionButtonData, encounterButtonData.getButtonIcon().split("_")[0]);
        } else if ("STORM".equals(encounterButtonData.getButtonIcon())) {
            actionButton = StormEncounterButton.build(actionButtonData);
        } else if ("VORTEX".equals(encounterButtonData.getButtonIcon())) {
            actionButton = VortexEncounterButton.build(actionButtonData);
        } else if ("token/compass.png".equals(encounterButtonData.getButtonIcon())) {
            actionButton = ActionButton.build(actionButtonData);
            int direction = MathUtils.random(360);
            actionButton.getIcon().addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                    Actions.rotateTo(direction - 15, 1f, Interpolation.sine),
                    Actions.rotateTo(direction + 15, 1f, Interpolation.sine)
            )));
        } else {
            actionButton = ActionButton.build(actionButtonData);
        }

        ImageButton.ImageButtonStyle iconStyle = new ImageButton.ImageButtonStyle(actionButton.getStyle());
        iconStyle.up = iconStyle.down = iconStyle.checked = iconStyle.over = iconStyle.disabled = null;
        actionButton.setStyle(iconStyle);
        actionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                actionButton.setChecked(true);
                if (!actionButtonData.isEnabled()) {
                    return;
                }
                backgroundAlpha = OPACITY_3;
                onClick();
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean result = super.touchDown(event, x, y, pointer, button);
                if (!actionButtonData.isEnabled()) {
                    return result;
                }
                backgroundAlpha = OPACITY_2;
                return result;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (!actionButtonData.isEnabled()) {
                    return;
                }
                if (!isPressed()) {
                    backgroundAlpha = OPACITY_1;
                }
                super.touchDragged(event, x, y, pointer);
            }
        });
        add(actionButton).width(BUTTON_SIZE).height(BUTTON_SIZE);
        return actionButton;
    }

    private Label createEnabledLabel(String text) {
        return createLabel(text, new Color(0xeee1c5ff));
    }

    private Label createDisabledLabel(String text) {
        return createLabel(resolveLocalizedText(text), new Color(0xe4a39aff));
    }

    private Label createLabel(String text, Color color) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 20), color);
        Label label = new Label(text, labelStyle);
        label.setWrap(true);
        label.setAlignment(Align.left, Align.left);
        return label;
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
    protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
        EncounterChoiceDrawable background = !encounterButtonData.isEnabled() ? EncounterChoiceDrawable.DISABLED
                : choiceListener != null && choiceListener.isPressed() ? EncounterChoiceDrawable.PRESSED
                : choiceListener != null && choiceListener.isOver() ? EncounterChoiceDrawable.HOVER
                : EncounterChoiceDrawable.NORMAL;
        Color before = batch.getColor();
        float r = before.r, g = before.g, b = before.b, a = before.a;
        Color tint = getColor();
        batch.setColor(tint.r, tint.g, tint.b, tint.a * parentAlpha);
        background.draw(batch, x, y, getWidth(), getHeight());
        batch.setColor(r, g, b, a);
    }

    private void addHitImage() {
        Image image = new Image(CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND));
        image.setColor(new Color(1f,1f,1f,0f));
        hitImage = image;
        choiceListener = new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                actionButton.setPressedOverride(false);
            }
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean result = super.touchDown(event, x, y, pointer, button);
                if (!actionButtonData.isEnabled()) {
                    return result;
                }
                actionButton.setPressedOverride(true);
                backgroundAlpha = OPACITY_2;
                return result;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (!actionButtonData.isEnabled()) {
                    return;
                }
                actionButton.setPressedOverride(false);
                actionButton.setChecked(true);
                backgroundAlpha = OPACITY_3;
                onClick();
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (!actionButtonData.isEnabled()) {
                    return;
                }
                if (!isPressed()) {
                    backgroundAlpha = OPACITY_1;
                    actionButton.setPressedOverride(false);
                }
                super.touchDragged(event, x, y, pointer);
            }

        };
        image.addListener(choiceListener);
        addActor(image);
    }

    @Override
    public void layout() {
        super.layout();
        if (hitImage != null) {
            hitImage.setBounds(0, 0, getWidth(), getHeight());
        }
    }

    private void onClick() {
        if (selectEncounterListener == null) {
            return;
        }
        if (!encounterButtonData.isEnabled()) {
            return;
        }
        selectEncounterListener.onSelect(encounterButtonData.getUuid());
        if (encounterButtonData.getButtonIcon().startsWith("card/condition/")) {
            BigActorsManager.displayOrHideEncounterTable();
        }
        selectEncounterListener = null;
    }
}
