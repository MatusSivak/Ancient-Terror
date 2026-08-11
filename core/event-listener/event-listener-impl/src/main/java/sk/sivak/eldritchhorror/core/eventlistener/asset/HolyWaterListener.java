package sk.sivak.eldritchhorror.core.eventlistener.asset;

import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.HolyWaterAsset;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Arrays;
import java.util.List;

/**
 * @author msivak
 */
public class HolyWaterListener extends AbstractAssetListener<HolyWaterAsset> {

    private static final Logger logger = LogManager.getLogger(HolyWaterListener.class);
    private HolyWaterActionListener holyWaterActionListener;
    private RegisterUsableAssetListener registerUsableAssetListener;

    private boolean usedInCombat = false;
    private AfterCombatListener afterCombatListener;
    private AfterShowCombatTableListener afterShowCombatTableListener;

    @Override
    protected void register() {
        holyWaterActionListener = new HolyWaterActionListener();
        getEventQueue().addBeforeEventListener(holyWaterActionListener, BeforeAfterEvent.FIND_ACTIONS);
        afterShowCombatTableListener = new AfterShowCombatTableListener();
        getEventQueue().addAfterEventListener(afterShowCombatTableListener, BeforeAfterEvent.SHOW_COMBAT_TABLE);
        registerUsableAssetListener = new RegisterUsableAssetListener();
        getEventQueue().addDirectEventListener(registerUsableAssetListener, DirectEvent.REGISTER_USABLE_ASSETS);
        afterCombatListener = new AfterCombatListener();
        getEventQueue().addAfterEventListener(afterCombatListener, BeforeAfterEvent.HIDE_COMBAT_TABLE);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(holyWaterActionListener, registerUsableAssetListener, afterCombatListener, afterShowCombatTableListener);
    }

    private class AfterShowCombatTableListener extends AbstractAssetEventListener<CombatData> {

        public AfterShowCombatTableListener() {
            super(HolyWaterListener.this);
        }

        @Override
        protected Runnable getEventAction(CombatData input) {
            return () -> {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setHideType(HideType.DISCARD_ON_YES);
                showCardRequest.setTitle("Discard to gain +5 Will and +5 Strength?");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
                displayAsset.subscribe(response -> {
                    if (response == ShowCardResponse.YES) {
                        usedInCombat = true;
                    }
                    ServicePlatform.get().getService().convertTo(CombatData.class, () -> input);
                });
            };
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }

    private class RegisterUsableAssetListener extends AbstractAssetEventListener<TestData> {

        public RegisterUsableAssetListener() {
            super(HolyWaterListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if ( (input.getStat() != Stat.WILL) && (input.getStat() != Stat.STRENGTH) ) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.COMBAT)) {
                    return;
                }
                if (!usedInCombat) {
                    return;
                }
                addUsableAsset(input, getAssetInfo(), 5);
            };
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    private class AfterCombatListener extends AbstractAssetEventListener<Void> {

        public AfterCombatListener() {
            super(HolyWaterListener.this);
        }

        @Override
        protected Runnable getEventAction(Void input) {
            return () -> {
                if (!usedInCombat) {
                    return;
                }
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
                ServicePlatform.get().getService().convertTo(Void.class, () -> null);
                ServicePlatform.get().getService().release();
            };
        }

        @Override
        public Class<Void> getDataClass() {
            return Void.class;
        }
    }

    private class HolyWaterActionListener extends AbstractActionPhaseListener<HolyWaterActionListener.HolyWaterAction> {

        public HolyWaterActionListener() {
            name = "Holy\nWater";
        }

        @Override
        protected String getAdditionalInfo() {
            return null;
        }

        @Override
        protected String getGeneralDescription() {
            return "You may discard this card to choose an investigator on your space.\n" +
                    "That investigator gains a Blessed Condition.";
        }

        @Override
        protected String getTexturePath() {
            return "card/asset/HOLY_WATER.jpg";
        }

        @Override
        protected boolean getNeedsMask() {
            return true;
        }

        @Override
        protected float getScaleDownPercentage() {
        return 0.5f;
    }

        @Override
        protected HolyWaterAction createAction() {
            return new HolyWaterAction();
        }

        @Override
        protected boolean isVisible() {
            return getInvestigators().getActiveInvestigatorId() == investigatorId;
        }

        @Override
        protected boolean isDisabled() {
            return false;
        }

        @Override
        protected boolean isNotRecommended() {
            return false;
        }

        protected class HolyWaterAction extends AbstractActionPhaseAction {

            @Override
            public void execute() {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setTitle("Investigator will gain Blessed Condition.");
                showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
                displayAsset.subscribe(response -> {
                    selectInvestigator();
                });
            }

            private void selectInvestigator() {
                LocationId ownerLocationId = ServicePlatform.get().getInvestigators().getInvestigator(investigatorId).getCurrentLocationId();
                List<? extends InvestigatorRead> selectedInvestigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
                List<? extends InvestigatorRead> investigatorsOnThisSpace = Stream.collectToList(selectedInvestigators,
                        selected -> selected.getCurrentLocationId() == ownerLocationId);
                ServicePlatform.get().getInvestigatorService()
                        .selectInvestigator(new InvestigatorRestriction(true).addAllowedInvestigators(
                                Stream.map(investigatorsOnThisSpace, investigatorOnThisSpace -> investigatorOnThisSpace.getInfo().getInvestigatorId())))
                        .subscribe(this::onSelect);
            }

            private void onSelect(InvestigatorId selectedInvestigatorId) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
                if (selectedInvestigatorId != investigatorId) {
                    ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(selectedInvestigatorId);
                }
                ServicePlatform.get().getGameService().gainCondition(ConditionId.BLESSED);
                if (selectedInvestigatorId != investigatorId) {
                    ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
                }
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
            }
        }
    }
}
