package sk.sivak.eldritchhorror.core.view.components.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import sk.sivak.eldritchhorror.core.view.utils.AncientTerrorMenuStyles;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;
import com.badlogic.gdx.utils.Align;
import java8.features.function.Predicate;
import java8.features.stream.Stream;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.initgame.InAppPurchaseManager;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class SelectMultipleInvestigators {
    private List<InvestigatorInfo> availableInvestigators;
    private SelectInvestigatorComponent selectInvestigatorComponent;
    private SingleSubscriber<? super InvestigatorInfo[]> subscriber;
    private List<InvestigatorInfo> selectedInvestigators;
    private int totalToSelect;
    private Stage stage;

    private QuoteLabel quoteLabel;
    private LabeledStatChart labeledStatChart;
    private Table detailsPanel;
    private Label investigatorName;
    private TextButton confirmButton;
    private ScrollPane quotePane;
    private InvestigatorId previewedInvestigator;
    private Label abilityLabel;

    private Image background;

    public SelectMultipleInvestigators(List<InvestigatorInfo> availableInvestigators,
                                       SingleSubscriber<? super InvestigatorInfo[]> subscriber,
                                       int totalToSelect,
                                       Stage stage) {
        this.availableInvestigators = availableInvestigators;
        this.selectedInvestigators = new LinkedList<>();
        this.subscriber = subscriber;
        this.totalToSelect = totalToSelect;
        this.stage = stage;
        this.labeledStatChart = new LabeledStatChart();
        quoteLabel = new QuoteLabel();
        quoteLabel.setFontScale(0.24f);
        quoteLabel.setAlignment(Align.topLeft);
        abilityLabel = new Label("", new Label.LabelStyle(
                CustomAssetManager.getBitmapFontNew(CustomAssetManager.NEW_FONT_SOURCE_SERIF_4),
                new Color(0.93f, 0.9f, 0.81f, 1f)));
        abilityLabel.setFontScale(0.27f);
        abilityLabel.setAlignment(Align.topLeft);
        abilityLabel.setWrap(true);

        investigatorName = new Label(get("investigator.previewTitle"), new Label.LabelStyle(
                CustomAssetManager.getBitmapFontNew(CustomAssetManager.NEW_FONT_SOURCE_SERIF_4),
                new Color(0.91f, 0.85f, 0.69f, 1f)));
        investigatorName.setFontScale(0.36f);
        investigatorName.setAlignment(Align.center);
        investigatorName.setWrap(true);
        confirmButton = new TextButton(get("investigator.confirm"), AncientTerrorMenuStyles.button());
        confirmButton.getLabel().setFontScale(0.34f);
        confirmButton.getLabel().setWrap(true);
        AncientTerrorMenuStyles.makeMomentary(confirmButton);
        AncientTerrorMenuStyles.addFocusHighlight(confirmButton);
        confirmButton.setDisabled(true);
        ButtonUtils.addClickListener(confirmButton, () -> {
            if (!confirmButton.isDisabled() && previewedInvestigator != null) {
                onSelect(previewedInvestigator);
            }
        });
        selectInvestigatorComponent = new SelectInvestigatorComponent(0.66f) {
            @Override
            public void hideOkButton() {
                clearPreview();
            }

            @Override
            public void showOkButton() {
                InvestigatorId id = getSelectedInvestigatorId();
                if (id == previewedInvestigator) return;
                InvestigatorInfo info = investigatorIdToInfo(id);
                previewedInvestigator = id;
                investigatorName.setText(info.getInvestigatorName());
                String ability = info.getAbilityText();
                if (ability != null && ability.startsWith("investigator.")) ability = get(ability);
                abilityLabel.setText(ability == null || ability.isEmpty() ? "" :
                        "[#E8D9B0]" + get("investigator.label.ability") + "[]\n" + ability);
                String firstName = info.getInvestigatorName().trim().split("\\s+")[0];
                confirmButton.setText(get("investigator.confirmNamed", firstName));
                quoteLabel.setText("\"" + info.getQuote() + "\"");
                if (quotePane != null) quotePane.setScrollY(0);
                labeledStatChart.init(new StatChartData(
                        info.getMaxHealth(), info.getMaxSanity(),
                        info.getBaseStat(Stat.WILL), info.getBaseStat(Stat.LORE),
                        info.getBaseStat(Stat.INFLUENCE), info.getBaseStat(Stat.OBSERVATION),
                        info.getBaseStat(Stat.STRENGTH)));
                labeledStatChart.setVisible(true);
                confirmButton.setDisabled(false);
            }
        };
        selectInvestigatorComponent.setTitle(get("investigator.selectLead"));
        clearPreview();
        new InAppPurchaseManager().isProductPurchased("investigators_1").subscribe(isPurchased -> {
            if (!isPurchased) {
                selectInvestigatorComponent.showUnlockNewImage();
            }
        });

    }

    public void show() {
        Collection<InvestigatorId> investigatorIds = Stream.map(availableInvestigators, InvestigatorInfo::getInvestigatorId);

        selectInvestigatorComponent.init(investigatorIds.toArray(new InvestigatorId[investigatorIds.size()]));
        selectInvestigatorComponent.updateTeam(selectedInvestigators, totalToSelect);
        selectInvestigatorComponent.setPosition(
                5,
                VIEWPORT_HEIGHT/2f - selectInvestigatorComponent.getHeight()/2f);

        float panelWidth = VIEWPORT_WIDTH - selectInvestigatorComponent.getWidth() - 25f;
        detailsPanel = new Table();
        detailsPanel.setBackground(CustomAssetManager.getTextureRegionDrawable(
                CustomAssetManager.PURE_WHITE_BACKGROUND).tint(new Color(0.025f, 0.04f, 0.035f, 0.88f)));
        detailsPanel.setBounds(VIEWPORT_WIDTH - panelWidth - 10f, 15f, panelWidth, VIEWPORT_HEIGHT - 30f);
        detailsPanel.pad(12f);
        detailsPanel.add(investigatorName).growX().height(44f).padBottom(4f).row();
        float chartSize = Math.min(panelWidth - 24f, 220f);
        detailsPanel.add(labeledStatChart).size(chartSize).row();
        Table description = new Table();
        description.top();
        description.add(abilityLabel).growX().padBottom(14f).row();
        description.add(quoteLabel).growX();
        quotePane = new ScrollPane(description);
        quotePane.setScrollingDisabled(true, false);
        quotePane.setOverscroll(false, false);
        detailsPanel.add(quotePane).grow().minHeight(40f).pad(8f, 2f, 12f, 2f).row();
        detailsPanel.add(confirmButton).growX().height(50f);
        stage.addActor(selectInvestigatorComponent);
        stage.addActor(detailsPanel);
        CustomAssetManager.loadTextures2();
    }

    public void remove() {
        if (detailsPanel != null) detailsPanel.remove();
        selectInvestigatorComponent.remove();
    }

    private void clearPreview() {
        previewedInvestigator = null;
        labeledStatChart.init(null);
        labeledStatChart.setVisible(false);
        investigatorName.setText(get("investigator.previewTitle"));
        abilityLabel.setText("");
        confirmButton.setText(get("investigator.confirm"));
        quoteLabel.setText(get("investigator.previewHint"));
        confirmButton.setDisabled(true);
        if (quotePane != null) quotePane.setScrollY(0);
    }

    private void onSelect(InvestigatorId investigatorId) {
        InvestigatorInfo info = investigatorIdToInfo(investigatorId);
        if (selectedInvestigators.contains(info)) return;
        confirmButton.setDisabled(true);
        selectInvestigatorComponent.disable(investigatorId);
        selectedInvestigators.add(info);
        selectInvestigatorComponent.updateTeam(selectedInvestigators, totalToSelect);
        if (selectedInvestigators.size() == totalToSelect) {
            remove();
            onSuccess();
        } else {
            selectInvestigatorComponent.setTitle(get("investigator.selectProgress", selectedInvestigators.size() + 1, totalToSelect));
            clearPreview();
        }
    }
    private void onSuccess() {
        subscriber.onSuccess(selectedInvestigators.toArray(new InvestigatorInfo[selectedInvestigators.size()]));
    }

    private InvestigatorInfo investigatorIdToInfo(InvestigatorId investigatorId) {
        Predicate<InvestigatorInfo> predicate = investigatorInfo -> investigatorInfo.getInvestigatorId().equals(investigatorId);
        return Stream.findFirstOrException(availableInvestigators, predicate);
    }

    public void setBackground(Image background) {
        this.background = background;
    }

    public Image getBackground() {
        return background;
    }

    public void setTitle(String title) {
        selectInvestigatorComponent.setTitle(title);
    }

    public void setUpdateAvailableInvestigatorsAction(Runnable updateAvailableInvestigatorsAction) {
        selectInvestigatorComponent.setUpdateAvailableInvestigatorsAction(updateAvailableInvestigatorsAction);
    }
}
