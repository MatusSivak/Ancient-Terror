package sk.sivak.eldritchhorror.core.view.test;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.UsableAsset;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.TargetActorChangedListener;

import java.util.Collections;
import java.util.List;

import static java8.features.stream.Stream.*;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_MINYA;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;
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
    private Color WHITE = Color.WHITE;

    public TestInfoTable(Stat stat, int modifier, int baseStatValue,
                         int bonusStatValue, List<UsableAsset> allUsableAssets,
                         int additionalDicesCount) {
        super(true);
        this.allUsableAssets = allUsableAssets;
        this.modifier = modifier;
        this.baseStatValue = baseStatValue;
        this.bonusStatValue = bonusStatValue;
        this.additionalDicesCount = additionalDicesCount;

        pad(VIEWPORT_HEIGHT * 0.02f);

        add(createLabel("" + stat.prettyString() + ": ", Color.WHITE)).align(Align.right);

        if (bonusStatValue > 0) {
            add(createLabel("" + baseStatValue, Color.WHITE)).spaceRight(0).align(Align.right);
            add(createLabel("+" + bonusStatValue, Color.GREEN)).align(Align.right).row();
        } else {
            add(createLabel("" + baseStatValue, Color.WHITE)).align(Align.right).colspan(2).row();
        }

        statBonusCellTitle = add(createLabel("", Color.WHITE)).spaceBottom(0).height(0).align(Align.right);
        statBonusCellValue = add(createLabel("", Color.GREEN)).colspan(2).spaceBottom(0).height(0).align(Align.right);
        statBonusCellValue.row();

        diceBonusCellTitle = add(createLabel("", Color.WHITE)).spaceBottom(0).height(0).align(Align.right);
        diceBonusCellValue = add(createLabel("", Color.GREEN)).colspan(2).spaceBottom(0).height(0).align(Align.right);
        diceBonusCellValue.row();

        if (modifier != 0) {
            add(createLabel(get("test.modifier"), Color.WHITE)).align(Align.right);
        }
        if (modifier < 0) {
            add(createLabel("" + modifier, Color.RED)).align(Align.right).colspan(2).row();
        } else if (modifier > 0) {
            add(createLabel("+" + modifier, Color.GREEN)).align(Align.right).colspan(2).row();
        }

        addSeparator().colspan(3);
        add(createLabel(get("test.dicePool"), Color.YELLOW)).align(Align.right);

        int dicePool = calculateDicePool();
        dicePoolLabel = createLabel("" + dicePool, Color.YELLOW);
        add(dicePoolLabel).align(Align.right).colspan(2).row();

        pack();
        setBackground(CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.GRAY_BACKGROUND));

        if (additionalDicesCount != 0) {
            updateBonuses(Collections.emptyList());
        }
    }

    public void updateBonuses(List<AssetId> selectedAssets) {

        float widthBefore = getWidth();
        setBackground((Drawable) null);
        selectedUsableAssets = collectToList(this.allUsableAssets, ua -> selectedAssets.contains(ua.getCardInfo().getId()));

        calculateStatBonus(selectedUsableAssets);
        displayStatBonus();

        calculateDiceBonus(selectedUsableAssets);
        displayDiceBonus();

        dicePoolLabel.setText("" + calculateDicePool());
        invalidate();

        pack();

        setBackground(CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.GRAY_BACKGROUND));

        setX(getX() + (widthBefore - getWidth()) / 2);
    }

    public List<UsableAsset> getSelectedUsableAssets() {
        return selectedUsableAssets;
    }

    private void displayStatBonus() {
        if (statBonus > 0) {
            statBonusCellTitle.spaceBottom(8);
            statBonusCellTitle.height(24.5f);
            ((Label) statBonusCellTitle.getActor()).setText(get("test.statBonus"));

            statBonusCellValue.spaceBottom(8);
            statBonusCellValue.height(24.5f);
            ((Label) statBonusCellValue.getActor()).setText("+" + statBonus);
        }
    }

    private void displayDiceBonus() {
        Color fontColor = null;
        String cellValueText = null;
        if (diceBonus + additionalDicesCount > 0) {
            fontColor = Color.GREEN;
            cellValueText = "+" + (diceBonus + additionalDicesCount);
        } else if (diceBonus + additionalDicesCount < 0) {
            fontColor = Color.RED;
            cellValueText = ""+(diceBonus + additionalDicesCount);
        }
        if (diceBonus + additionalDicesCount != 0) {
            diceBonusCellTitle.spaceBottom(8);
            diceBonusCellTitle.height(24.5f);
            ((Label) diceBonusCellTitle.getActor()).setText(get("test.diceBonus"));

            diceBonusCellValue.spaceBottom(8);
            diceBonusCellValue.height(24.5f);
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
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_MINYA), color);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.right);
        label.setFontScale(0.5f);
        return label;
    }

    @Override
    protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
        super.drawBackground(batch, parentAlpha * 0.8f, x, y);
    }

    @Override
    public void onTargetChange(List<CardTemplate> cards) {
        updateBonuses(collectToList(map(cards, it -> it.getCardInfo().getId())));
    }
}
