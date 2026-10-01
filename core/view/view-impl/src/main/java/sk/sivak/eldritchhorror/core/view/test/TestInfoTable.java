package sk.sivak.eldritchhorror.core.view.test;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.UsableAsset;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.TargetActorChangedListener;
import sk.sivak.eldritchhorror.core.view.utils.SelectionPanelStyle;

import java.util.Collections;
import java.util.List;

import static java8.features.stream.Stream.*;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SOURCE_SERIF_4;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFontNew;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/**
 * @author msivak
 */
public class TestInfoTable extends VisTable implements TargetActorChangedListener {

    private final List<UsableAsset> allUsableAssets;
    private final int modifier;
    private final int baseStatValue;
    private final int bonusStatValue;
    private int additionalDicesCount;
    private final Label dicePoolLabel;
    private List<UsableAsset> selectedUsableAssets;
    private int statBonus;
    private int diceBonus;

    private Cell statBonusCellTitle;
    private Cell statBonusCellValue;
    private Cell diceBonusCellTitle;
    private Cell diceBonusCellValue;
    private static final Color TEXT = Color.valueOf("E6E1D3");
    private static final Color MUTED = Color.valueOf("B5BDB4");
    private static final Color POSITIVE = Color.valueOf("8FD694");
    private static final Color NEGATIVE = Color.valueOf("E57373");
    private static final Color ACCENT = Color.valueOf("E9C46A");
    private static final Drawable BACKGROUND = SelectionPanelStyle.panel("121B1DEE", "87734E", 10, 16);

    public TestInfoTable(Stat stat, int modifier, int baseStatValue,
                         int bonusStatValue, List<UsableAsset> allUsableAssets,
                         int additionalDicesCount) {
        super(true);
        this.allUsableAssets = allUsableAssets;
        this.modifier = modifier;
        this.baseStatValue = baseStatValue;
        this.bonusStatValue = bonusStatValue;
        this.additionalDicesCount = additionalDicesCount;

        setBackground(BACKGROUND);
        defaults().spaceBottom(4);

        defaults().spaceRight(24);
        add(createTitle(stat.prettyString())).growX();

        if (bonusStatValue > 0) {
            Table statValue = new Table();
            statValue.add(createLabel("" + baseStatValue, TEXT)).padRight(2);
            statValue.add(createLabel("+" + bonusStatValue, POSITIVE));
            add(statValue).align(Align.right).row();
        } else {
            add(createLabel("" + baseStatValue, TEXT)).align(Align.right).row();
        }

        statBonusCellTitle = add(createTitle("")).spaceBottom(0).height(0).growX();
        statBonusCellValue = add(createLabel("", POSITIVE)).spaceBottom(0).height(0).align(Align.right);
        statBonusCellValue.row();

        diceBonusCellTitle = add(createTitle("")).spaceBottom(0).height(0).growX();
        diceBonusCellValue = add(createLabel("", POSITIVE)).spaceBottom(0).height(0).align(Align.right);
        diceBonusCellValue.row();

        if (modifier != 0) {
            add(createTitle(get("test.modifier"))).growX();
        }
        if (modifier < 0) {
            add(createLabel("" + modifier, NEGATIVE)).align(Align.right).row();
        } else if (modifier > 0) {
            add(createLabel("+" + modifier, POSITIVE)).align(Align.right).row();
        }

        Image separator = new Image(CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.PURE_WHITE_BACKGROUND)
                .tint(Color.valueOf("87734E99")));
        add(separator).colspan(2).growX().height(1).padTop(4).padBottom(6).row();
        Label dicePoolTitle = createTitle(get("test.dicePool"));
        dicePoolTitle.getStyle().fontColor = ACCENT;
        add(dicePoolTitle).growX();

        int dicePool = calculateDicePool();
        dicePoolLabel = createLabel("" + dicePool, ACCENT);
        dicePoolLabel.setFontScale(0.55f);
        add(dicePoolLabel).align(Align.right).row();

        pack();
        if (additionalDicesCount != 0) {
            updateBonuses(Collections.emptyList());
        }
    }

    public void updateBonuses(List<AssetId> selectedAssets) {

        float widthBefore = getWidth();
        selectedUsableAssets = collectToList(this.allUsableAssets, ua -> selectedAssets.contains(ua.getCardInfo().getId()));

        calculateStatBonus(selectedUsableAssets);
        displayStatBonus();

        calculateDiceBonus(selectedUsableAssets);
        displayDiceBonus();

        dicePoolLabel.setText("" + calculateDicePool());
        invalidate();

        pack();

        setX(getX() + (widthBefore - getWidth()) / 2);
    }

    public List<UsableAsset> getSelectedUsableAssets() {
        return selectedUsableAssets;
    }

    private void displayStatBonus() {
        if (statBonus > 0) {
            statBonusCellTitle.spaceBottom(4);
            statBonusCellTitle.height(22f);
            ((Label) statBonusCellTitle.getActor()).setText(get("test.statBonus").replace(":", "").trim());

            statBonusCellValue.spaceBottom(4);
            statBonusCellValue.height(22f);
            ((Label) statBonusCellValue.getActor()).setText("+" + statBonus);
        }
    }

    private void displayDiceBonus() {
        Color fontColor = null;
        String cellValueText = null;
        if (diceBonus + additionalDicesCount > 0) {
            fontColor = POSITIVE;
            cellValueText = "+" + (diceBonus + additionalDicesCount);
        } else if (diceBonus + additionalDicesCount < 0) {
            fontColor = NEGATIVE;
            cellValueText = ""+(diceBonus + additionalDicesCount);
        }
        if (diceBonus + additionalDicesCount != 0) {
            diceBonusCellTitle.spaceBottom(4);
            diceBonusCellTitle.height(22f);
            ((Label) diceBonusCellTitle.getActor()).setText(get("test.diceBonus").replace(":", "").trim());

            diceBonusCellValue.spaceBottom(4);
            diceBonusCellValue.height(22f);
            ((Label) diceBonusCellValue.getActor()).setText(cellValueText);
            ((Label) diceBonusCellValue.getActor()).getStyle().fontColor = fontColor;
        }
    }

    private void calculateStatBonus(List<UsableAsset> usableAssets) {
        boolean hasBonus = anyMatch(usableAssets, usableAsset -> usableAsset.getStatBonus() > 0);
        statBonus = 0;
        if (!hasBonus) {
            statBonusCellTitle.spaceBottom(0).height(0);
            statBonusCellValue.spaceBottom(0).height(0);
            ((Label) statBonusCellTitle.getActor()).setText("");
            ((Label) statBonusCellValue.getActor()).setText("");
        }
        for (UsableAsset usableAsset : usableAssets) {
            if (usableAsset.getStatBonus() < statBonus) {
                continue;
            }
            statBonus = usableAsset.getStatBonus();
        }
    }

    private void calculateDiceBonus(List<UsableAsset> usableAssets) {
        boolean hasBonus = anyMatch(usableAssets, usableAsset -> usableAsset.getDicePoolBonus() > 0);
        diceBonus = 0;
        if (!hasBonus) {
            diceBonusCellTitle.spaceBottom(0).height(0);
            diceBonusCellValue.spaceBottom(0).height(0);
            ((Label) diceBonusCellTitle.getActor()).setText("");
            ((Label) diceBonusCellValue.getActor()).setText("");
        }
        for (UsableAsset usableAsset : usableAssets) {
            diceBonus += usableAsset.getDicePoolBonus();
        }
    }

    private int calculateDicePool() {
        return Math.max(1, baseStatValue + bonusStatValue + modifier + statBonus + diceBonus + additionalDicesCount);
    }

    private Label createLabel(String text, Color color) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40), color);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.right);
        label.setFontScale(0.45f);
        return label;
    }

    private Label createTitle(String text) {
        Label label = createLabel(text.replace(":", "").trim(), MUTED);
        label.setAlignment(Align.left);
        label.setFontScale(0.42f);
        return label;
    }
    @Override
    protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
        super.drawBackground(batch, parentAlpha, x, y);
    }

    @Override
    public void onTargetChange(List<CardTemplate> cards) {
        updateBonuses(collectToList(map(cards, it -> it.getCardInfo().getId())));
    }
}
