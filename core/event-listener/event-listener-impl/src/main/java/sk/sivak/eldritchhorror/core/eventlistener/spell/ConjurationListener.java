package sk.sivak.eldritchhorror.core.eventlistener.spell;

import java8.features.function.Predicate;
import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.action.CardInfoAware;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.spell.conjuration.ConjurationSpell;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;

import java.util.Arrays;
import java.util.List;

public class ConjurationListener extends AbstractSpellListener<ConjurationSpell> {


    private ConjurationActionListener conjurationActionListener;
    private EnableCardListener enableCardListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(conjurationActionListener, enableCardListener);
    }

    @Override
    protected void register() {
        conjurationActionListener = new ConjurationActionListener();
        getEventQueue().addBeforeEventListener(conjurationActionListener, BeforeAfterEvent.FIND_ACTIONS);
        enableCardListener = new EnableCardListener(spellInfo);
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    private class ConjurationActionListener extends AbstractActionPhaseListener {

        public ConjurationActionListener() {
            name = "Conjuration";
        }

        @Override
        protected String getAdditionalInfo() {
            if (isDisabled()) {
                return null;
            }
            List<AssetInfo> reserve = ServicePlatform.get().getAssetDeck().getReserve();

            List<AssetInfo> itemsOrTrinkets = findItemsOrTrinkets(reserve);
            StringBuilder sb = new StringBuilder();
            for (AssetInfo itemOrTrinket : itemsOrTrinkets) {
                sb.append(itemOrTrinket.getName()).append(" (").append(itemOrTrinket.getCost()).append(")").append("\n");
            }
            return sb.toString();
        }

        @Override
        protected String getGeneralDescription() {
            return "Test Lore+1. If you pass,\n" +
                    "you may gain\n" +
                    "1 Item or Trinket\n" +
                    "from the reserve with value\n" +
                    "equal to or less than\n" +
                    "your test result.\n" +
                    "Then flip this card.";
        }

        @Override
        protected String getTexturePath() {
            return "card/spell/CONJURATION.jpg";
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
        protected ConjurationAction createAction() {
            return new ConjurationAction();
        }

        @Override
        protected boolean isVisible() {
            return getInvestigators().getActiveInvestigatorId() == spellOwnerId;
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
    }

    private class ConjurationAction extends AbstractActionPhaseAction implements CardInfoAware<ConjurationSpell> {

        private AbstractSpellBackListener spellBackListener;

        @Override
        public void execute() {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setSpellInfo(spellInfo);
            showCardRequest.setHideType(HideType.DISABLE_ALWAYS);
            showCardRequest.setTitle("Cast Conjuration");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
            Single<ShowCardResponse> displaySpell = ServicePlatform.get().getGameService().showCard(showCardRequest);
            displaySpell.subscribe(response -> {
                Single<TestData> test = ServicePlatform.get().getTestService().test(Stat.LORE, 1, 16,
                        new TestFlavorRequest(TestFlavorType.SPELL));
                test.subscribe(this::disableAndFlipCard);
            });
        }

        private void disableAndFlipCard(TestData testData) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().disableCard(spellInfo);

            AbstractSpellBackListener spellBackListener = findSpellBackListener();
            spellBackListener.setTestData(testData);
            List<AssetInfo> itemsOrTrinkets = findItemsOrTrinkets(ServicePlatform.get().getAssetDeck().getReserve());
            spellBackListener.setData("itemsOrTrinkets", itemsOrTrinkets);
            spellBackListener.setMainSpellAction(createMainSpellAction(testData, itemsOrTrinkets));
            spellBackListener.executeWhole();

            ServicePlatform.get().getInvestigatorService().convertToNull();
            ServicePlatform.get().getService().release();
        }

        private Runnable createMainSpellAction(TestData testData, List<AssetInfo> itemsOrTrinkets) {
            return () -> {
                ServicePlatform.get().getService().hold();

                IterableUtils.removeIf(itemsOrTrinkets, itemOrTrinket -> itemOrTrinket.getCost() > testData.getScore());

                SelectCardData selectCardData = new SelectCardData();
                selectCardData.setAvailableCards(itemsOrTrinkets);
                selectCardData.setHideText("Display Reserve?");
                selectCardData.setTitleText("Select Trinket or Item");
                ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(selectedCard -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getGameService().gainAsset((AssetInfo) selectedCard);
                    itemsOrTrinkets.remove(selectedCard);
                    ServicePlatform.get().getCardService().refillReserve();
                    ServicePlatform.get().getService().release();
                });

                ServicePlatform.get().getService().release();
            };
        }

        @Override
        public ConjurationSpell getCardInfo() {
            return spellInfo;
        }
    }

    private List<AssetInfo> findItemsOrTrinkets(List<AssetInfo> reserve) {
        Predicate<AssetInfo> predicate = assetInfo ->
                assetInfo.getTraits().contains(AssetTrait.ITEM) ||
                        assetInfo.getTraits().contains(AssetTrait.TRINKET);

        return Stream.collectToList(reserve, predicate);
    }


}
