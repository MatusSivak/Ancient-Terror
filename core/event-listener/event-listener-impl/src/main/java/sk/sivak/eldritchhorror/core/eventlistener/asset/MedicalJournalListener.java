package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.MedicalJournalAsset;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.action.RestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author msivak
 */
public class MedicalJournalListener extends AbstractAssetListener<MedicalJournalAsset> {

    private static final Logger logger = LogManager.getLogger(MedicalJournalListener.class);
    private MedicalJournalBeforeRest medicalJournalBeforeRest;
    private RerollUsingAssetsListener rerollUsingAssetsListener;

    @Override
    protected void register() {
        medicalJournalBeforeRest = new MedicalJournalBeforeRest();
        getEventQueue().addBeforeEventListener(medicalJournalBeforeRest, BeforeAfterEvent.REST);
        rerollUsingAssetsListener = new RerollUsingAssetsListener();
        getEventQueue().addDirectEventListener(rerollUsingAssetsListener, DirectEvent.REROLL_USING_ASSETS_1);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(medicalJournalBeforeRest, rerollUsingAssetsListener);
    }

    private class RerollUsingAssetsListener extends AbstractAssetEventListener<TestData> {

        public RerollUsingAssetsListener() {
            super(MedicalJournalListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.hasReachedMinScoreToEndTest()) {
                    return;
                }
                if (input.getStat() != Stat.STRENGTH) {
                    return;
                }
                if (input.getCalculatedDicePool() <= input.getScore()) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.CONDITION)) {
                    return;
                }
                Set<ConditionTrait> conditionTraits = ((ConditionInfo) ServicePlatform.get().getTestFlavor().getFlavorData()).getTraits();
                if (!conditionTraits.contains(ConditionTrait.ILLNESS) && !conditionTraits.contains(ConditionTrait.INJURY)) {
                    return;
                }
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setTitle("Reroll 1 die?");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);

                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
            };
        }

        private void onAnswer(ShowCardResponse showCardResponse, TestData input) {
            if (ShowCardResponse.YES == showCardResponse) {
                ServicePlatform.get().getTestService().rerollDie(input);
            } else {
                ServicePlatform.get().getService().convertFromTo(ShowCardResponse.class, TestData.class, response -> input);
            }

        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    private class MedicalJournalBeforeRest extends AbstractAssetEventListener<RestData> {

        private MedicalJournalBeforeRest() {
            super(MedicalJournalListener.this);
        }

        @Override
        protected Runnable getEventAction(RestData input) {
            return () -> {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                showCardRequest.setTitle("Recover additional Health?");
                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
            };
        }

        private void onAnswer(ShowCardResponse showCardResponse, RestData input) {
            if (showCardResponse != ShowCardResponse.YES) {
                ServicePlatform.get().getService().convertFromTo(Object.class, RestData.class, out -> input);
                return;
            }

            ServicePlatform.get().getService().hold();
            input.setHealthGained(input.getHealthGained() + 1);
            ServicePlatform.get().getService().convertFromTo(Object.class, RestData.class, out -> input);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<RestData> getDataClass() {
            return RestData.class;
        }
    }
}
