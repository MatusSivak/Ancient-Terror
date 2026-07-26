package sk.sivak.eldritchhorror.core.eventlistener.asset;

import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.GruesomeTalismanAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.AddOneToDieResultData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class GruesomeTalismanListener extends AbstractAssetListener<GruesomeTalismanAsset> {

    private static final Logger logger = LogManager.getLogger(GruesomeTalismanListener.class);
    private AddOneToDieResultListener addOneToDieResultListener;

    @Override
    protected void register() {
        addOneToDieResultListener = new AddOneToDieResultListener();
        getEventQueue().addDirectEventListener(addOneToDieResultListener, DirectEvent.ADD_ONE_TO_DIE_RESULT);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(addOneToDieResultListener);
    }

    private class AddOneToDieResultListener extends AbstractAssetEventListener<AddOneToDieResultData> {

        public AddOneToDieResultListener() {
            super(GruesomeTalismanListener.this);
        }

        @Override
        protected Runnable getEventAction(AddOneToDieResultData input) {
            return () -> {
                TestData testData = input.getTestData();
                if (testData.hasReachedMinScoreToEndTest()) {
                    return;
                }
                if (testData.getStat() != Stat.WILL) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.COMBAT)) {
                    return;
                }
                if (!Stream.anyMatch(testData.getDiceRolls(), dr -> dr.getDiceValue() < 6)) {
                    return;
                }

                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setTitle("Add 1 to 1 die?");
                showCardRequest.setHideType(HideType.RETURN);
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
            };
        }

        private void onAnswer(ShowCardResponse response, AddOneToDieResultData input) {
            if (response == ShowCardResponse.NO) {
                ServicePlatform.get().getService().convertTo(AddOneToDieResultData.class, () -> input);
            } else {
                ServicePlatform.get().getTestService().hold();
                ServicePlatform.get().getService().convertTo(AddOneToDieResultData.class, () -> input);
                ServicePlatform.get().getTestService().addOneToDieResult();
                ServicePlatform.get().getTestService().release();
            }
        }

        @Override
        public Class<AddOneToDieResultData> getDataClass() {
            return AddOneToDieResultData.class;
        }
    }
}
