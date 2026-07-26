package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.SpecializedTrainingAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.action.FocusData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Collections;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse.YES;
import static sk.sivak.eldritchhorror.core.eventtype.data.test.TestData.JUST_ONE;

/**
 * @author msivak
 */
public class SpecializedTrainingListener extends AbstractAssetListener<SpecializedTrainingAsset> {

    private static final Logger logger = LogManager.getLogger(SpecializedTrainingListener.class);
    private AfterFocusListener afterFocusListener;

    @Override
    protected void register() {
        afterFocusListener = new AfterFocusListener();
        getEventQueue().addAfterEventListener(afterFocusListener, BeforeAfterEvent.FOCUS);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(afterFocusListener);
    }

    private class AfterFocusListener extends AbstractAssetEventListener<FocusData> {

        private AfterFocusListener() {
            super(SpecializedTrainingListener.this);
        }

        @Override
        protected Runnable getEventAction(FocusData input) {
            return () -> {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                showCardRequest.setTitle("Consult experts to train you?");
                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
            };
        }

        void onAnswer(ShowCardResponse showCardResponse, FocusData input) {
            if (showCardResponse != YES) {
                ServicePlatform.get().getService().convertTo(FocusData.class, () -> input);
                return;
            }

            Single<TestData> test = ServicePlatform.get().getTestService().test(Stat.WILL, 0, JUST_ONE);
            test.subscribe(testData -> {
                if (testData.isSuccessful()) {
                    onSuccessfulTest(input);
                } else {
                    ServicePlatform.get().getService().convertTo(FocusData.class, () -> input);
                }
            });
        }

        private void onSuccessfulTest(FocusData input) {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setAssetInfo(getAssetInfo());
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
            showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
            showCardRequest.setTitle("Discard " + getAssetInfo().getName());
            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(res -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
                ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
                ServicePlatform.get().getInvestigatorService().improveSkill(null);
                ServicePlatform.get().getService().convertTo(FocusData.class, () -> input);
                ServicePlatform.get().getService().release();
            });
        }

        @Override
        public Class<FocusData> getDataClass() {
            return FocusData.class;
        }
    }
}
