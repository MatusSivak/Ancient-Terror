package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import java8.features.function.Predicate;
import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class TheExConvictActionListener extends AbstractActionPhaseListener<TheExConvictActionListener.TheExConvictAction> {

    private static final Logger logger = LogManager.getLogger(TheExConvictActionListener.class);

    public TheExConvictActionListener() {
        name = "Trade\nAsset";
    }

    @Override
    protected String getAdditionalInfo() {
        if (isDisabled()) {
            return null;
        }
        List<AssetInfo> reserve = ServicePlatform.get().getAssetDeck().getReserve();

        List<AssetInfo> itemsOrTrinkets = findItemsOrTrinkets(reserve);
        StringBuilder sb = new StringBuilder();
        for (AssetInfo itemsOrTrinket : itemsOrTrinkets) {
            sb.append(itemsOrTrinket.getName()).append(" (").append(itemsOrTrinket.getCost()).append(")").append("\n");
        }
        return sb.toString();
    }

    private List<AssetInfo> findItemsOrTrinkets(List<AssetInfo> assets) {
        Predicate<AssetInfo> predicate = assetInfo ->
                assetInfo.getTraits().contains(AssetTrait.ITEM) ||
                        assetInfo.getTraits().contains(AssetTrait.TRINKET);

        return Stream.collectToList(assets, predicate);
    }

    @Override
    protected String getGeneralDescription() {
        return "You may discard " +
                "1 Item or Trinket Asset " +
                "to gain 1 Item or Trinket " +
                "of your choice " +
                "from the reserve " +
                "with value up to +1 " +
                "of the discarded Asset.";
    }

    @Override
    protected TheExConvictActionListener.TheExConvictAction createAction() {
        return new TheExConvictActionListener.TheExConvictAction();
    }

    @Override
    protected boolean isVisible() {
        return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_EX_CONVICT;
    }

    @Override
    protected boolean isDisabled() {
        if (isItemOrTrinketNotPresent(ServicePlatform.get().getAssetDeck().getAssets(InvestigatorId.THE_EX_CONVICT))) {
            disabledReason = "You don't own any Items or Trinkets";
            return true;
        }

        if (isItemOrTrinketNotPresent(ServicePlatform.get().getAssetDeck().getReserve())) {
            disabledReason = "No Items or Trinkets in Reserve.";
            return true;
        }
        return false;
    }

    private boolean isItemOrTrinketNotPresent(List<AssetInfo> assets) {
        return !Stream.anyMatch(assets, assetInfo ->
                assetInfo.getTraits().contains(AssetTrait.ITEM) ||
                        assetInfo.getTraits().contains(AssetTrait.TRINKET));
    }

    @Override
    protected ActionButtonData.ActionButtonId getActionButtonId() {
        return ActionButtonData.ActionButtonId.INVESTIGATOR;
    }

    @Override
    protected String getTexturePath() {
        return "investigator/ICON_THE_EX_CONVICT.png";
    }

    @Override
    protected boolean getNeedsScaleDown() {
        return true;
    }

    @Override
    protected boolean isNotRecommended() {
        return false;
    }

    protected class TheExConvictAction extends AbstractActionPhaseAction {

        private AssetInfo assetToDiscard;
        private List<AssetInfo> itemsOrTrinkets;

        @Override
        public void execute() {
            logger.info("Executing the ex convict action...");

            List<AssetInfo> assets = ServicePlatform.get().getAssetDeck().getAssets(InvestigatorId.THE_EX_CONVICT);
            SelectCardData selectCardData = new SelectCardData();
            selectCardData.setHideText("Display assets?");
            selectCardData.setTitleText("Select Item or Trinket to discard");
            selectCardData.setAvailableCards(findItemsOrTrinkets(assets));

            ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(this::onSelectedCardToDiscard);
        }

        private void onSelectedCardToDiscard(CardInfo cardToDiscard) {
            this.assetToDiscard = ((AssetInfo) cardToDiscard);
            itemsOrTrinkets = findItemsOrTrinkets(ServicePlatform.get().getAssetDeck().getReserve());
            itemsOrTrinkets = Stream.collectToList(itemsOrTrinkets, itemOrTrinket -> itemOrTrinket.getCost() <= assetToDiscard.getCost() + 1);

            if (itemsOrTrinkets.size() == 0) {
                showNothingWithinValue();
                return;
            }

            askToPickAssetFromReserve();
        }

        private void askToPickAssetFromReserve() {
            Question<Boolean> question = new Question<>();
            question.addOption(new Question.Option<>("No", false, 0x800000ff))
                    .addOption(new Question.Option<>("Yes", true, 0x008000ff));

            question.setTitle("Trade " + assetToDiscard.getName() + " for other asset?");
            Question.SelectComponentsTableData<AssetInfo> tableData = new Question.SelectComponentsTableData<>();
            tableData.setType(Question.SelectComponentsTableType.CARDS);
            tableData.setAvailableKeys(itemsOrTrinkets);
            question.setSelectComponentsTableData(tableData);
            Single<Answer<Boolean, CardInfo>> ask = ServicePlatform.get().getGameService().ask(question);
            ask.subscribe(this::pickAssetFromReserveAnswer);
        }

        private void pickAssetFromReserveAnswer(Answer<Boolean, CardInfo> answer) {
            if (answer.getResponseData()) {
                onPickAssetFromReserve(answer.getAdditionalData());
            } else {
                onNothingToTradeFor();
            }
        }

        private void onPickAssetFromReserve(CardInfo selectedAsset) {
            if (selectedAsset == null) {
                SelectCardData selectCardData = new SelectCardData();
                selectCardData.setAvailableCards(itemsOrTrinkets);
                selectCardData.setHideText("Display Reserve?");
                selectCardData.setTitleText("Select Trinket or Item");
                ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(this::onSelectedCardToGain);
            } else {
                onSelectedCardToGain(selectedAsset);
            }
        }

        private void showNothingWithinValue() {
            Question<Boolean> question = new Question<>();
            question.setOptions(Collections.singletonList(new Question.Option<>("OK", Boolean.TRUE)));
            question.setTitle("Nothing to trade for.");
            ServicePlatform.get().getGameService().ask(question).subscribe((response) -> onNothingToTradeFor());
        }

        private void onNothingToTradeFor() {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getBasicActionService().addFreeAction();
            ServicePlatform.get().getBasicActionService().removePerformedAction(TheExConvictAction.this);
            ServicePlatform.get().getService().convertToNull();
            ServicePlatform.get().getService().release();
        }

        private void onSelectedCardToGain(CardInfo cardToGain) {
            ServicePlatform.get().getService().hold();
            showDiscardInfo();
            ServicePlatform.get().getGameService().gainAsset((AssetInfo) cardToGain);
            ServicePlatform.get().getCardService().refillReserve();
            ServicePlatform.get().getGameService().convertToNull();
            ServicePlatform.get().getService().release();
        }

        private void showDiscardInfo() {
            ShowCardRequest request = new ShowCardRequest();
            request.setHideType(HideType.DISCARD_ALWAYS);
            request.setAssetInfo(assetToDiscard);
            request.setTitle("Discard " + assetToDiscard.getName());
            request.setDisplayAssetResponseType(DisplayAssetResponseType.NOTHING);

            ServicePlatform.get().getGameService().showCard(request).subscribe(response -> onDiscardAssetConfirm());
        }

        private void onDiscardAssetConfirm() {
            ServicePlatform.get().getService().discardAssetFromInvestigator(InvestigatorId.THE_EX_CONVICT, assetToDiscard, false);
        }
    }
}
