package sk.sivak.eldritchhorror.core.view.components.encounter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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

    public static final int LABEL_WIDTH = 220;
    public static final int BUTTON_SIZE = 56;
    private static final float ICON_IN_BADGE_SCALE = 1.0f;
    private static final Color DETAIL_COLOR = new Color(0xcbb990ff);
    private static final Color DISABLED_TITLE_COLOR = new Color(0x9d978aff);
    private static final Color DISABLED_REASON_COLOR = new Color(0xd98f85ff);
    private static final Color LOCK_COLOR = new Color(0xb9ac93ff);
    private static final int LOCK_SIZE = 24;
    private static final int REASON_INDENT = 8;
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
    private final boolean compact;
    private final boolean required;
    private final boolean hideReason;
    private Actor lockBadge;
    private final Color accent;
    private final EncounterChoiceDrawable[] backgrounds;

    public EncounterButton(EncounterButtonData encounterButtonData) {
        this(encounterButtonData, false, false, false);
    }

    /**
     * @param hideReason the dialog already explains why this option is locked, so the card only shows the lock
     */
    public EncounterButton(EncounterButtonData encounterButtonData, boolean compact, boolean required, boolean hideReason) {
        this.encounterButtonData = encounterButtonData;
        this.compact = compact;
        this.required = required && encounterButtonData.isEnabled();
        this.hideReason = hideReason;
        this.accent = this.required ? EncounterIconStyle.requiredAccent() : EncounterIconStyle.accentFor(encounterButtonData);
        this.backgrounds = EncounterChoiceDrawable.forAccent(accent);
        actionButtonData = new EncounterButtonDataAdapter(encounterButtonData).asActionButtonData();
        init();
    }

    private void init() {
        addActionButton();
        Table lines = new Table();
        lines.left();
        if (required) {
            lines.add(createSingleLineLabel(UiText.get("encounter.required").toUpperCase(), 14, accent)).left().width(LABEL_WIDTH).row();
        }
        if (encounterButtonData.isEnabled()) {
            initEnabled(lines);
        } else {
            initDisabled(lines);
        }
        add(lines).left().padLeft(5).padRight(5);

        setBackground(backgrounds[0]);
        left();
        if (compact) {
            pad(6, 14, 6, 12);
        } else {
            pad(12, 14, 12, 12);
        }
        addHitImage();
        pack();
    }

    public void setSelectEncounterListener(SelectEncounterTable selectEncounterListener) {
        this.selectEncounterListener = selectEncounterListener;
    }

    private void initDisabled(Table lines) {
        actionButton.setChecked(true);
        String title = resolveLocalizedText(encounterButtonData.getFirstLine());
        if (title != null && !title.trim().isEmpty()) {
            lines.add(createLabel(title, DISABLED_TITLE_COLOR)).left().width(LABEL_WIDTH).row();
        }
        String reason = hideReason ? null : resolveLocalizedText(encounterButtonData.getDisabledReason());
        if (reason != null && !reason.trim().isEmpty()) {
            lines.add(createSingleLineLabel(reason, 16, DISABLED_REASON_COLOR)).left().width(LABEL_WIDTH - REASON_INDENT)
                    .padLeft(REASON_INDENT).padTop(3).row();
        }
        lockBadge = new LockBadge();
        lockBadge.setTouchable(Touchable.disabled);
        addActor(lockBadge);
    }

    /** Small padlock pinned to the icon badge of a locked option. */
    private static final class LockBadge extends Actor {
        @Override
        public void draw(Batch batch, float parentAlpha) {
            Color before = batch.getColor();
            float r = before.r, g = before.g, b = before.b, a = before.a;
            float alpha = getColor().a * parentAlpha;
            batch.setColor(0.09f, 0.1f, 0.09f, alpha);
            batch.draw(EncounterIconStyle.disc(), getX(), getY(), getWidth(), getHeight());
            batch.setColor(LOCK_COLOR.r, LOCK_COLOR.g, LOCK_COLOR.b, alpha);
            batch.draw(EncounterIconStyle.ring(), getX(), getY(), getWidth(), getHeight());
            float inset = getWidth() * 0.2f;
            batch.draw(EncounterIconStyle.lock(), getX() + inset, getY() + inset, getWidth() - 2 * inset, getHeight() - 2 * inset);
            batch.setColor(r, g, b, a);
        }
    }

    private void initEnabled(Table lines) {
        lines.add(createEnabledLabel(resolveLocalizedText(encounterButtonData.getFirstLine()))).left().width(LABEL_WIDTH).row();
        if (encounterButtonData instanceof CombatEncounterButtonData) {
            addToughnessBar(lines, (CombatEncounterButtonData) encounterButtonData);
        } else if (encounterButtonData.getSecondLine() != null) {
            Label detail = createLabel(resolveLocalizedText(encounterButtonData.getSecondLine()), DETAIL_COLOR);
            detail.setStyle(new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 18), DETAIL_COLOR));
            lines.add(detail).left().width(LABEL_WIDTH).padTop(3).row();
        }
    }

    private void addToughnessBar(Table lines, CombatEncounterButtonData combatEncounterButtonData) {
        Integer toughness = combatEncounterButtonData.getMonsterInfo().getToughness();
        Integer currentHealth = combatEncounterButtonData.getMonsterInfo().getCurrentHealth();

        ToughnessBar toughnessBar = new ToughnessBar();
        float scale = Math.min(0.49f, LABEL_WIDTH / (Math.max(1, toughness == null ? 0 : toughness) * 42.3f));
        toughnessBar.init(toughness == null ? 0 : toughness, currentHealth == null ? 0 : currentHealth, scale);

        lines.add(toughnessBar).height(53 * scale).left().row();
    }

    public boolean isRequired() {
        return required;
    }

    private ActionButton addActionButton() {
        if (encounterButtonData.getButtonIcon().contains("GATE")) {
            actionButton = GateEncounterButton.build(actionButtonData, encounterButtonData.getButtonIcon().split("_")[0]);
        } else if ("STORM".equals(encounterButtonData.getButtonIcon())) {
            actionButton = StormEncounterButton.build(actionButtonData);
        } else if ("VORTEX".equals(encounterButtonData.getButtonIcon())) {
            actionButton = VortexEncounterButton.build(actionButtonData);
        } else if ("token/compass.png".equals(encounterButtonData.getButtonIcon())) {
            actionButton = ActionButton.build(actionButtonData, true);
            int direction = MathUtils.random(360);
            actionButton.getIcon().addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                    Actions.rotateTo(direction - 15, 1f, Interpolation.sine),
                    Actions.rotateTo(direction + 15, 1f, Interpolation.sine)
            )));
        } else {
            actionButton = ActionButton.build(actionButtonData, true);
        }
        if (EncounterIconStyle.isSkip(encounterButtonData)) {
            actionButton.getIcon().setDrawable(new TextureRegionDrawable(EncounterIconStyle.skipGlyph()));
        }
        actionButton.scaleIcon(ICON_IN_BADGE_SCALE);

        ImageButton.ImageButtonStyle iconStyle = new ImageButton.ImageButtonStyle(actionButton.getStyle());
        BaseDrawable badge = EncounterIconStyle.badge(accent);
        iconStyle.up = iconStyle.down = iconStyle.checked = iconStyle.over = iconStyle.disabled = badge;
        iconStyle.imageUp = iconStyle.imageDown = iconStyle.imageChecked = iconStyle.imageOver = null;
        iconStyle.imageCheckedOver = iconStyle.imageDisabled = null;
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

    /** Never wraps; ellipsis is a last-resort guard, translations are kept short enough to fit. */
    private Label createSingleLineLabel(String text, int fontSize, Color color) {
        Label label = new Label(text, new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, fontSize), color));
        label.setWrap(false);
        label.setEllipsis(true);
        label.setAlignment(Align.left, Align.left);
        return label;
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
                : choiceListener != null && choiceListener.isPressed() ? backgrounds[2]
                : choiceListener != null && choiceListener.isOver() ? backgrounds[1]
                : backgrounds[0];
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
        if (lockBadge != null && actionButton != null) {
            lockBadge.setBounds(actionButton.getX() + actionButton.getWidth() - LOCK_SIZE + 6,
                    actionButton.getY() - 4, LOCK_SIZE, LOCK_SIZE);
            lockBadge.toFront();
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
