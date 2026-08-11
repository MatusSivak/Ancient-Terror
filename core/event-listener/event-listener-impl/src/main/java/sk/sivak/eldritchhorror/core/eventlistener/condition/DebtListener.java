package sk.sivak.eldritchhorror.core.eventlistener.condition;

import java8.features.function.Supplier;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.debt.DebtCondition;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.AbstractCardReckoningListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.data.test.TestData.JUST_ONE;

public class DebtListener extends AbstractConditionListener<DebtCondition> {

    private DebtActionListener debtActionListener;
    private ConditionReckoningListener reckoningListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(debtActionListener, reckoningListener);
    }

    @Override
    protected void register() {
        debtActionListener = new DebtActionListener();
        getEventQueue().addBeforeEventListener(debtActionListener, BeforeAfterEvent.FIND_ACTIONS);

        reckoningListener = new ConditionReckoningListener(DebtListener.this);
        getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_CARDS_2);
    }

    private class DebtActionListener extends AbstractActionPhaseListener {

        public DebtActionListener() {
            name = "Pay\nDebt";
        }

        @Override
        protected String getAdditionalInfo() {
            return "Debt: " + investigatorId;
        }

        @Override
        protected String getGeneralDescription() {
            return "Test Influence.\nIf you pass,\ndiscard this card.";
        }

        @Override
        protected String getTexturePath() {
            return "card/condition/DEBT.jpg";
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
        protected DebtAction createAction() {
            return new DebtAction();
        }

        @Override
        protected boolean isVisible() {
            LocationId activeLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
            LocationId ownerLocationId = ServicePlatform.get().getInvestigators().getInvestigator(investigatorId).getCurrentLocationId();
            return activeLocationId == ownerLocationId;
        }

        @Override
        protected boolean isDisabled() {
            return false;
        }

        @Override
        protected boolean isNotRecommended() {
            return false;
        }
    }

    private class DebtAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            Single<TestData> test = ServicePlatform.get().getTestService().test(Stat.INFLUENCE, 0, JUST_ONE,
                    new TestFlavorRequest(TestFlavorType.CONDITION));
            test.subscribe(this::withTestResult);
        }

        private void withTestResult(TestData testResult) {
            if (testResult.isSuccessful()) {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setConditionInfo(conditionInfo);
                showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.NOTHING);
                showCardRequest.setTitle("Debt paid");
                ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(this::onAnswer);
            } else {
                ServicePlatform.get().getInvestigatorService().convertToNull();
            }
        }

        private void onAnswer(ShowCardResponse showCardResponse) {
            ServicePlatform.get().getGameService().hold();
            ServicePlatform.get().getService().discardConditionFromInvestigator(investigatorId, conditionInfo);
            ServicePlatform.get().getInvestigatorService().convertToNull();
            ServicePlatform.get().getGameService().release();
        }
    }

}
