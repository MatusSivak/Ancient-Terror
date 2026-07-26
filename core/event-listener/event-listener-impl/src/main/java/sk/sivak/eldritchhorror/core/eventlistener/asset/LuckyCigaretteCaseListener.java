package sk.sivak.eldritchhorror.core.eventlistener.asset;

import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.LuckyCigaretteCaseAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.AddOneToDieResultData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Arrays;
import java.util.List;

/**
 * @author msivak
 */
public class LuckyCigaretteCaseListener extends AbstractAssetListener<LuckyCigaretteCaseAsset> {

    private static final Logger logger = LogManager.getLogger(LuckyCigaretteCaseListener.class);
    private AddOneToDieResultListener addOneToDieResultListener;
    private EnableCardListener enableCardListener;

    @Override
    protected void register() {
        addOneToDieResultListener = new AddOneToDieResultListener();
        getEventQueue().addDirectEventListener(addOneToDieResultListener, DirectEvent.ADD_ONE_TO_DIE_RESULT);
        enableCardListener = new EnableCardListener(getAssetInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(addOneToDieResultListener, enableCardListener);
    }

    private class AddOneToDieResultListener extends AbstractAssetEventListener<AddOneToDieResultData> {

        public AddOneToDieResultListener() {
            super(LuckyCigaretteCaseListener.this);
        }

        @Override
        protected Runnable getEventAction(AddOneToDieResultData input) {
            return () -> {
                if (!enabled) {
                    return;
                }
                TestData testData = input.getTestData();
                if (testData.hasReachedMinScoreToEndTest()) {
                    ServicePlatform.get().getService().convertTo(AddOneToDieResultData.class, () -> input);
                    return;
                }
                if (!Stream.anyMatch(testData.getDiceRolls(), dr -> dr.getDiceValue() < 6)) {
                    ServicePlatform.get().getService().convertTo(AddOneToDieResultData.class, () -> input);
                    return;
                }

                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setTitle("Add 1 to 1 die?");
                showCardRequest.setHideType(HideType.DISABLE_ON_YES);
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
                ServicePlatform.get().getService().disableCard(getAssetInfo());
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
