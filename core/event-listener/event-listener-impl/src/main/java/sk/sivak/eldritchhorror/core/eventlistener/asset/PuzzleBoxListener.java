package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.PuzzleBoxAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.action.RestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Collections;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse.YES;
import static sk.sivak.eldritchhorror.core.eventtype.data.test.TestData.JUST_ONE;

/**
 * @author msivak
 */
public class PuzzleBoxListener extends AbstractAssetListener<PuzzleBoxAsset> {

    private static final Logger logger = LogManager.getLogger(PuzzleBoxListener.class);
    private PuzzleBoxAfterRestListener puzzleBoxAfterRestListener;

    @Override
    protected void register() {
        puzzleBoxAfterRestListener = new PuzzleBoxAfterRestListener();
        getEventQueue().addAfterEventListener(puzzleBoxAfterRestListener, BeforeAfterEvent.REST);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(puzzleBoxAfterRestListener);
    }

    private class PuzzleBoxAfterRestListener extends AbstractAssetEventListener<RestData> {

        private PuzzleBoxAfterRestListener() {
            super(PuzzleBoxListener.this);
        }

        @Override
        protected Runnable getEventAction(RestData input) {
            return () -> {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                showCardRequest.setTitle("Open the Puzzle Box?");
                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
            };
        }


        void onAnswer(ShowCardResponse showCardResponse, RestData input) {
            if (showCardResponse != YES) {
                ServicePlatform.get().getService().convertTo(RestData.class, () -> input);
                return;
            }

            Single<TestData> test = ServicePlatform.get().getTestService().test(Stat.OBSERVATION, -2, JUST_ONE);
            test.subscribe(testData -> {
                if (testData.isSuccessful()) {
                    ShowCardRequest request = new ShowCardRequest();
                    request.setHideType(HideType.DISCARD_ON_YES);
                    request.setAssetInfo(getAssetInfo());
                    request.setTitle("Discard Puzzle Box to gain 1 Artifact?");
                    request.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                    ServicePlatform.get().getGameService().showCard(request)
                            .subscribe(x -> onDiscardAssetAnswer(x, input));
                } else {
                    ServicePlatform.get().getService().convertTo(RestData.class, () -> input);
                }
            });
        }

        void onDiscardAssetAnswer(ShowCardResponse showCardResponse, RestData input) {
            ServicePlatform.get().getService().hold();
            if (YES == showCardResponse) {
                ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
                ServicePlatform.get().getGameService().gainArtifact();
            }
            ServicePlatform.get().getService().convertTo(RestData.class, () -> input);
            ServicePlatform.get().getService().release();
        }


        @Override
        public Class<RestData> getDataClass() {
            return RestData.class;
        }
    }
}
