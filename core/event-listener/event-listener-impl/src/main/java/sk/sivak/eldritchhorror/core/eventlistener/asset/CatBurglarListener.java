package sk.sivak.eldritchhorror.core.eventlistener.asset;

import java8.features.function.Predicate;
import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.action.CardInfoAware;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.asset.CatBurglarAsset;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;

import java.util.Arrays;
import java.util.List;

/**
 * @author msivak
 */
public class CatBurglarListener extends AbstractAssetListener<CatBurglarAsset> {

    private static final Logger logger = LogManager.getLogger(CatBurglarListener.class);
    private ActionListener actionListener;
    private EnableCardListener enableCardListener;

    @Override
    protected void register() {
        actionListener = new ActionListener();
        getEventQueue().addBeforeEventListener(actionListener, BeforeAfterEvent.FIND_ACTIONS);
        enableCardListener = new EnableCardListener(getAssetInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(actionListener, enableCardListener);
    }

    private class ActionListener extends AbstractActionPhaseListener<ActionListener.CatBurglarAction> {

        public ActionListener() {
            name = "Cat\nBurglar";
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
                sb.append(itemsOrTrinket.getName()).append("\n");
            }
            return sb.toString();
        }

        private List<AssetInfo> findItemsOrTrinkets(List<AssetInfo> reserve) {
            Predicate<AssetInfo> predicate = assetInfo ->
                    assetInfo.getTraits().contains(AssetTrait.ITEM) ||
                            assetInfo.getTraits().contains(AssetTrait.TRINKET);

            return Stream.collectToList(reserve, predicate);
        }

        @Override
        protected String getGeneralDescription() {
            return "Roll 1 die.\n" +
                    "On a 5 or 6, gain 1 Item or Trinket Asset\n" +
                    "from the reserve.\n" +
                    "On a 1, discard this card.";
        }

        @Override
        protected String getTexturePath() {
            return "card/asset/CAT_BURGLAR.jpg";
        }

        @Override
        protected float getScaleDownPercentage() {
        return 0.5f;
    }

        @Override
        protected boolean getNeedsMask() {
            return true;
        }

        @Override
        protected CatBurglarAction createAction() {
            return new CatBurglarAction();
        }

        @Override
        protected boolean isVisible() {
            return getInvestigators().getActiveInvestigatorId() == investigatorId;
        }

        @Override
        protected boolean isDisabled() {
            List<AssetInfo> reserve = ServicePlatform.get().getAssetDeck().getReserve();
            boolean isItemOrTrinketPresent = Stream.anyMatch(reserve, assetInfo ->
                    assetInfo.getTraits().contains(AssetTrait.ITEM) ||
                            assetInfo.getTraits().contains(AssetTrait.TRINKET));
            if (!isItemOrTrinketPresent) {
                disabledReason = "No Items or Trinkets in Reserve.";
                return true;
            }
            if (!enabled) {
                disabledReason = "Action already performed this round"; // THIS MUST BE LAST
                return true;
            }
            return false;
        }

        @Override
        protected boolean isNotRecommended() {
            return false;
        }

        protected class CatBurglarAction extends AbstractActionPhaseAction implements CardInfoAware<CatBurglarAsset> {

            private static final int MIN_SUCCESSFUL = 5;
            private static final int MAX_FAILED = 1;

            @Override
            public void execute() {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setHideType(HideType.DISABLE_ALWAYS);
                showCardRequest.setTitle("Roll 1 die.");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
                displayAsset.subscribe(response -> {
                    rollDie();
                });
            }

            private void rollDie() {
                RollData rollData = new RollData();
                rollData.setMinSuccessful(MIN_SUCCESSFUL);
                rollData.setMaxFailed(MAX_FAILED);
                Single<Integer> singleDieResult = ServicePlatform.get().getTestService().rollDie(rollData);
                singleDieResult.subscribe(this::resolveDiceRoll);
            }

            private void resolveDiceRoll(Integer result) {
                ServicePlatform.get().getService().hold();
                if (result >= MIN_SUCCESSFUL) {
                    onSuccess();
                } else if (result <= MAX_FAILED) {
                    showDiscardInfo();
                }
                ServicePlatform.get().getService().disableCard(getAssetInfo());
                ServicePlatform.get().getInvestigatorService().convertToNull();
                ServicePlatform.get().getService().release();
            }

            private void onSuccess() {
                List<AssetInfo> itemsOrTrinkets = findItemsOrTrinkets(ServicePlatform.get().getAssetDeck().getReserve());
                SelectCardData selectCardData = new SelectCardData();
                selectCardData.setAvailableCards(itemsOrTrinkets);
                selectCardData.setHideText("Display Reserve?");
                selectCardData.setTitleText("Select Trinket or Item");
                ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(this::onSelectCard);
            }

            private void onSelectCard(CardInfo cardInfo) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getGameService().gainAsset((AssetInfo) cardInfo);
                ServicePlatform.get().getCardService().refillReserve();
                ServicePlatform.get().getGameService().convertToNull();
                ServicePlatform.get().getService().release();
            }

            private void showDiscardInfo() {
                ShowCardRequest request = new ShowCardRequest();
                request.setHideType(HideType.DISCARD_ALWAYS);
                request.setAssetInfo(getAssetInfo());
                request.setTitle("Discard Cat Burglar");
                request.setDisplayAssetResponseType(DisplayAssetResponseType.NOTHING);
                ServicePlatform.get().getGameService().showCard(request).subscribe(this::onDiscardAssetConfirm);
            }

            void onDiscardAssetConfirm(ShowCardResponse showCardResponse) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), true);
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
            }

            @Override
            public CatBurglarAsset getCardInfo() {
                return getAssetInfo();
            }
        }
    }
}
