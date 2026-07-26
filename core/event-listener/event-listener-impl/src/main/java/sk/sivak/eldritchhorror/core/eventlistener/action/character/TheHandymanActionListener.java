package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.reserve.ShowReserveDragAndDropOutput;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.List;

/**
 * @author msivak
 */
public class TheHandymanActionListener extends AbstractActionPhaseListener<TheHandymanActionListener.TheHandymanAction> {

    private static final Logger logger = LogManager.getLogger(TheHandymanActionListener.class);

    public TheHandymanActionListener() {
        name = "Check\nReserve";
    }

    @Override
    protected String getAdditionalInfo() {
        if (isDisabled()) {
            return null;
        }
        List<AssetInfo> reserve = ServicePlatform.get().getAssetDeck().getReserve();

        StringBuilder sb = new StringBuilder();
        for (AssetInfo asset : reserve) {
            sb.append(asset.getName()).append(" (").append(asset.getCost()).append(")").append("\n");
        }
        return sb.toString();
    }

    private List<AssetInfo> findOneCostAssets(List<AssetInfo> reserve) {
        return Stream.collectToList(reserve, assetInfo -> assetInfo.getCost() == 1);
    }

    @Override
    protected String getGeneralDescription() {
        return "Gain one Asset\n" +
                "of your choice\n" +
                "with cost 1 from the reserve.\n\n" +
                "Or discard one card\n" +
                "from the reserve and\n" +
                "perform additional action.";
    }

    @Override
    protected TheHandymanAction createAction() {
        return new TheHandymanAction();
    }

    @Override
    protected boolean isVisible() {
        return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_HANDYMAN;
    }

    @Override
    protected boolean isDisabled() {
        return false;
    }

    @Override
    protected ActionButtonData.ActionButtonId getActionButtonId() {
        return ActionButtonData.ActionButtonId.INVESTIGATOR;
    }

    @Override
    protected String getTexturePath() {
        return "investigator/THE_HANDYMAN.png";
    }

    @Override
    protected boolean getNeedsScaleDown() {
        return true;
    }

    @Override
    protected boolean isNotRecommended() {
        return false;
    }

    protected class TheHandymanAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            List<AssetInfo> reserve = ServicePlatform.get().getAssetDeck().getReserve();
            List<AssetInfo> oneCostAssets = findOneCostAssets(reserve);

            if (oneCostAssets.isEmpty()) {
                discardOneAsset(reserve);
            } else {
                Question<Boolean> question = new Question<>();
                question.setPortraitBeforeTitle(InvestigatorId.THE_HANDYMAN);
                Question.SelectComponentsTableData<AssetInfo> tableData = new Question.SelectComponentsTableData<>();
                tableData.setType(Question.SelectComponentsTableType.CARDS);
                tableData.setAvailableKeys(oneCostAssets);
                question.setSelectComponentsTableData(tableData);
                question.setTitle("Gain one Asset?");
                question.setOptions(Question.Option.noYesOptions);
                ServicePlatform.get().getGameService().<Boolean, CardInfo>ask(question).subscribe(answer -> {
                    if (!answer.getResponseData()) {
                        discardOneAsset(reserve);
                        return;
                    }
                    ServicePlatform.get().getService().hold();
                    onPickAssetFromReserve(answer.getAdditionalData());
                    ServicePlatform.get().getTokenService().convertToNull();
                    ServicePlatform.get().getService().release();
                });
            }
        }

        private void onPickAssetFromReserve(CardInfo selectedAsset) {
            if (selectedAsset == null) {
                SelectCardData selectCardData = new SelectCardData();
                selectCardData.setAvailableCards(findOneCostAssets(ServicePlatform.get().getAssetDeck().getReserve()));
                selectCardData.setHideText("Display Reserve?");
                selectCardData.setTitleText("Select Asset");
                ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(this::onSelectedCardToGain);
            } else {
                onSelectedCardToGain(selectedAsset);
            }
        }

        private void onSelectedCardToGain(CardInfo cardToGain) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getGameService().gainAsset((AssetInfo) cardToGain);
            ServicePlatform.get().getCardService().refillReserve();
            ServicePlatform.get().getGameService().convertToNull();
            ServicePlatform.get().getService().release();
        }

        private void discardOneAsset(List<AssetInfo> reserve) {
            Question<Object> question = new Question<>();
            question.setPortraitBeforeTitle(InvestigatorId.THE_HANDYMAN);
            Question.SelectComponentsTableData<AssetInfo> tableData = new Question.SelectComponentsTableData<>();
            tableData.setType(Question.SelectComponentsTableType.CARDS);
            tableData.setAvailableKeys(reserve);
            question.setSelectComponentsTableData(tableData);
            question.setTitle("Discard one Asset (Free Action)");
            question.setOptions(Question.Option.okOption);
            ServicePlatform.get().getGameService().<Object, CardInfo>ask(question).subscribe(ok -> {
                onDiscardAssetFromReserve(ok.getAdditionalData());
            });
        }

        private void onDiscardAssetFromReserve(CardInfo selectedAsset) {
            if (selectedAsset != null) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getCardService().discardAssetFromReserve(selectedAsset);
                ServicePlatform.get().getCardService().refillReserve();
                ServicePlatform.get().getBasicActionService().addFreeAction();
                ServicePlatform.get().getGameService().convertToNull();
                ServicePlatform.get().getService().release();
            } else {
                SelectCardData selectCardData = new SelectCardData();
                selectCardData.setTitleText("Select Asset to replace");
                selectCardData.setHideText("Display Reserve?");
                selectCardData.setAvailableCards(ServicePlatform.get().getAssetDeck().getReserve());
                ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(this::onDiscardAssetFromReserve);
            }
        }
    }
}
