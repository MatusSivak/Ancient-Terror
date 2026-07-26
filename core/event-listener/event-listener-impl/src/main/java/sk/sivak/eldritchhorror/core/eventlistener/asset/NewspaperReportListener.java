package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.NewspaperReportAsset;
import sk.sivak.eldritchhorror.core.constants.asset.PocketWatchAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.GainClueData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.GAIN_CLUE;

/**
 * @author msivak
 */
public class NewspaperReportListener extends AbstractAssetListener<NewspaperReportAsset> {

    private static final Logger logger = LogManager.getLogger(NewspaperReportListener.class);
    private AfterGainClueListener afterGainClueListener;

    @Override
    protected void register() {
        afterGainClueListener = new AfterGainClueListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterGainClueListener, GAIN_CLUE);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(afterGainClueListener);
    }

    private class AfterGainClueListener extends EventListenerImpl<GainClueData> {

        @Override
        public void onNotify(GainClueData eventData) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                if (!eventData.isEncounter()) {
                    return;
                }
                if (eventData.isClueGained()) {
                    return;
                }
                ServicePlatform.get().getTokenService().canSpend(1,0,0,0).subscribe(spendData -> {
                    if (spendData.hasEnough()) {
                        ShowCardRequest showCardRequest = new ShowCardRequest();
                        showCardRequest.setHideType(HideType.DISCARD_ON_YES);
                        showCardRequest.setAssetInfo(getAssetInfo());
                        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                        showCardRequest.setTitle("Spend one Clue to retreat Doom?");
                        ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(answer -> onAnswer(answer, eventData));
                    } else {
                        ServicePlatform.get().getService().convertTo(GainClueData.class, () -> eventData);
                    }
                });
            });
        }

        private void onAnswer(ShowCardResponse response, GainClueData eventData) {
            if (response == ShowCardResponse.NO) {
                ServicePlatform.get().getService().convertTo(GainClueData.class, () -> eventData);
                return;
            }
            ServicePlatform.get().getTokenService().spend(1,0,0,0).subscribe(spendData -> {
                if (spendData.hasEnough()) {
                    ServicePlatform.get().getService().hold();
                    spendData.pay();
                    ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
                    ServicePlatform.get().getDoomOmenService().retreatDoom();
                    eventData.setClueGained(true);
                    ServicePlatform.get().getService().convertTo(GainClueData.class, () -> eventData);
                    ServicePlatform.get().getService().release();
                } else {
                    ServicePlatform.get().getService().convertTo(getDataClass(), () -> eventData);
                }
            });
        }

        @Override
        public Class<GainClueData> getDataClass() {
            return GainClueData.class;
        }
    }
}
