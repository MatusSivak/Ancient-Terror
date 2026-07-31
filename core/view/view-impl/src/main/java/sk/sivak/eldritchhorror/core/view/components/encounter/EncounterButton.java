package sk.sivak.eldritchhorror.core.view.components.encounter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_MINYA;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;

public class EncounterButton extends Table {

    public static final int LABEL_WIDTH = 230;
    public static final int BUTTON_SIZE = 50;
    public static final float OPACITY_3 = 1f;
    public static final float OPACITY_1 = 0.75f;
    public static final float OPACITY_2 = 0.875f;
    private float backgroundAlpha = 0.75f;
    private final ActionButtonData actionButtonData;
    private EncounterButtonData encounterButtonData;
    private ActionButton actionButton;
    private SelectEncounterTable selectEncounterListener;

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

        addHitImage();
        pack();
        setBackground(CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.GRAY_BACKGROUND));
    }

    public void setSelectEncounterListener(SelectEncounterTable selectEncounterListener) {
        this.selectEncounterListener = selectEncounterListener;
    }

    private void initDisabled() {
        ActionButton actionButton = addActionButton();
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
        add(createEnabledLabel(encounterButtonData.getFirstLine())).width(LABEL_WIDTH).padLeft(5).padRight(5);
    }

    private void initTwoLiner() {
        addActionButton();
        Table lines = new Table();
        lines.add(createEnabledLabel(encounterButtonData.getFirstLine())).left().width(LABEL_WIDTH).row();
        lines.add(createEnabledLabel(encounterButtonData.getSecondLine())).left().width(LABEL_WIDTH).row();
        add(lines).padLeft(5).padRight(5);
    }

    private void initMonsterButton() {
        addActionButton();
        Table lines = new Table();
        lines.add(createEnabledLabel(encounterButtonData.getFirstLine())).left().width(LABEL_WIDTH).row();

        CombatEncounterButtonData combatEncounterButtonData = ((CombatEncounterButtonData) encounterButtonData);
        Integer toughness = combatEncounterButtonData.getMonsterInfo().getToughness();
        Integer currentHealth = combatEncounterButtonData.getMonsterInfo().getCurrentHealth();

        ToughnessBar toughnessBar = new ToughnessBar();
        float scale = 0.49f;
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
        return createLabel(text, Color.WHITE);
    }

    private Label createDisabledLabel(String text) {
        return createLabel(resolveLocalizedText(text), new Color(1f, 0.25f, 0.25f, 1f));
    }

    private Label createLabel(String text, Color color) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_MINYA), color);
        Label label = new Label(text, labelStyle);
        label.setFontScale(0.5f);
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
        super.drawBackground(batch, parentAlpha * backgroundAlpha, x, y);
    }

    private void addHitImage() {
        Image image = new Image(CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND));
        image.setColor(new Color(1f,1f,1f,0f));
        image.setWidth(LABEL_WIDTH + 5);
        image.setHeight(BUTTON_SIZE);
        image.setPosition(BUTTON_SIZE + 5, 5);
        image.addListener(new ClickListener() {
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

        });
        addActor(image);
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
