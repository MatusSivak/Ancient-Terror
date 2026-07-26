package sk.sivak.eldritchhorror.core.view.components.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import java8.features.function.Predicate;
import java8.features.function.Supplier;
import java8.features.stream.Stream;
import rx.Single;
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

public class SelectMultipleInvestigators {
    private List<InvestigatorInfo> availableInvestigators;
    private SelectInvestigatorComponent selectInvestigatorComponent;
    private SingleSubscriber<? super InvestigatorInfo[]> subscriber;
    private List<InvestigatorInfo> selectedInvestigators;
    private int totalToSelect;
    private Stage stage;

    private QuoteLabel quoteLabel;
    private LabeledStatChart labeledStatChart;
    private Image darkBackground1;
    private Image darkBackground2;
    private Image background;
//    private Image loadingImage;

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
//        loadingImage = new Image(CustomAssetManager.getTexture(CustomAssetManager.LOADING));

        selectInvestigatorComponent = new SelectInvestigatorComponent(0.66f) {
            private InvestigatorInfo lastSelectedInvestigator;

            @Override
            public void hideOkButton() {
            }

            @Override
            public void showOkButton() {
                InvestigatorInfo selectedInvestigator = investigatorIdToInfo(selectInvestigatorComponent.getSelectedInvestigatorId());
                if (selectedInvestigator == lastSelectedInvestigator) {
                    labeledStatChart.init(null);
                    quoteLabel.hide();
                    onSelect(selectInvestigatorComponent.getSelectedInvestigatorId());
                } else {
                    quoteLabel.setText("\""+ selectedInvestigator.getQuote()+"\"\n[#808080]- " + selectedInvestigator.getInvestigatorName()+" -[]");
                    labeledStatChart.init(new StatChartData(
                            selectedInvestigator.getMaxHealth(), selectedInvestigator.getMaxSanity(),
                            selectedInvestigator.getBaseStat(Stat.WILL), selectedInvestigator.getBaseStat(Stat.LORE),
                            selectedInvestigator.getBaseStat(Stat.INFLUENCE), selectedInvestigator.getBaseStat(Stat.OBSERVATION),
                            selectedInvestigator.getBaseStat(Stat.STRENGTH)));
                }
                this.lastSelectedInvestigator = selectedInvestigator;
            }
        };
        selectInvestigatorComponent.setTitle("Select Lead Investigator");

        new InAppPurchaseManager().isProductPurchased("investigators_1").subscribe(isPurchased -> {
            if (!isPurchased) {
                selectInvestigatorComponent.showUnlockNewImage();
            }
        });

        /*
        selectInvestigatorComponent.add(quoteLabel).pad(5).padBottom(7).width(quoteLabel.getWidth()).height(30);
        selectInvestigatorComponent.row();
        selectInvestigatorComponent.add(okButton).pad(5).width(okButton.getWidth()).height(okButton.getHeight());
        */
    }

    public void show() {
        Collection<InvestigatorId> investigatorIds = Stream.map(availableInvestigators, InvestigatorInfo::getInvestigatorId);

        selectInvestigatorComponent.init(investigatorIds.toArray(new InvestigatorId[investigatorIds.size()]));
        selectInvestigatorComponent.setPosition(
                5,
                VIEWPORT_HEIGHT/2f - selectInvestigatorComponent.getHeight()/2f);
        float spaceTop = (VIEWPORT_HEIGHT - selectInvestigatorComponent.getHeight()) / 2f;

        labeledStatChart.setSize(
                VIEWPORT_WIDTH - 15 - selectInvestigatorComponent.getWidth(),
                VIEWPORT_WIDTH - 15 - selectInvestigatorComponent.getWidth());
        labeledStatChart.setPosition(
                VIEWPORT_WIDTH - labeledStatChart.getWidth() - 5,
                VIEWPORT_HEIGHT - labeledStatChart.getHeight() - spaceTop);

        quoteLabel.setSize(
                VIEWPORT_WIDTH - 15 - selectInvestigatorComponent.getWidth(),
                VIEWPORT_HEIGHT - 5 - spaceTop*2 - labeledStatChart.getHeight());
        quoteLabel.setPosition(
                VIEWPORT_WIDTH - labeledStatChart.getWidth() - 5,
                VIEWPORT_HEIGHT - 5 - spaceTop - labeledStatChart.getHeight() - quoteLabel.getHeight());
        darkBackground1 = new Image(CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND));
        darkBackground1.setColor(new Color(0,0,0,0.5f));
        darkBackground1.setPosition(labeledStatChart.getX(), labeledStatChart.getY());
        darkBackground1.setSize(labeledStatChart.getWidth(), labeledStatChart.getHeight());

        darkBackground2 = new Image(CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND));
        darkBackground2.setColor(new Color(0,0,0,0.5f));
        darkBackground2.setPosition(quoteLabel.getX(), quoteLabel.getY());
        darkBackground2.setSize(quoteLabel.getWidth(), quoteLabel.getHeight());

        stage.addActor(darkBackground1);
        stage.addActor(darkBackground2);
        stage.addActor(labeledStatChart);
        stage.addActor(selectInvestigatorComponent);
        stage.addActor(quoteLabel);

        CustomAssetManager.loadTextures2();
    }

    public void remove() {
        darkBackground1.remove();
        darkBackground2.remove();
        labeledStatChart.remove();
        selectInvestigatorComponent.remove();
        quoteLabel.remove();
    }

    private void onSelect(InvestigatorId investigatorId) {
        InvestigatorInfo selectedInvestigatorInfo = investigatorIdToInfo(investigatorId);
        selectInvestigatorComponent.disable(investigatorId);
        selectedInvestigators.add(selectedInvestigatorInfo);
        selectInvestigatorComponent.setTitle("Select investigator " + (selectedInvestigators.size() + 1) + "/" + totalToSelect);
        if (selectedInvestigators.size() == totalToSelect) {
            darkBackground1.remove();
            darkBackground2.remove();
            labeledStatChart.remove();
            selectInvestigatorComponent.remove();
            quoteLabel.remove();
            onSuccess();
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
