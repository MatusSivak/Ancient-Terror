package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.WitchDoctorAsset;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.*;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.action.RestData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Collections;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

/**
 * @author msivak
 */
public class WitchDoctorListener extends AbstractAssetListener<WitchDoctorAsset> {

    private static final Logger logger = LogManager.getLogger(WitchDoctorListener.class);
    private AfterRestListener afterRestListener;

    @Override
    protected void register() {
        afterRestListener = new AfterRestListener();
        getEventQueue().addBeforeEventListener(afterRestListener, BeforeAfterEvent.REST);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(afterRestListener);
    }

    private InvestigatorRead getActiveInvestigator() {
        return ServicePlatform.get().getInvestigators().getActiveInvestigator();
    }

    private class AfterRestListener extends AbstractAssetEventListener<RestData> {

        private AfterRestListener() {
            super(WitchDoctorListener.this);
        }

        @Override
        public void onNotify(RestData input) {
            ServicePlatform.get().getService().<RestData>addEventCommand((in) -> {
                getEventAction(in).run();
            });
        }

        @Override
        protected Runnable getEventAction(RestData input) {
            return () -> {

                LocationId activeLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
                LocationId ownerLocationId = ServicePlatform.get().getInvestigators().getInvestigator(investigatorId).getCurrentLocationId();

                if (activeLocationId != ownerLocationId) {
                    return;
                }

                boolean isCursed = ServicePlatform.get().getConditionsDeck().hasCondition(
                        getActiveInvestigator().getInfo().getInvestigatorId(), ConditionId.CURSED);
                if (isCursed) {
                    ShowCardRequest showCardRequest = new ShowCardRequest();
                    showCardRequest.setAssetInfo(getAssetInfo());
                    showCardRequest.setAssetOriginType(AssetOriginType.INVENTORY);
                    showCardRequest.setHideType(HideType.RETURN);
                    showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                    showCardRequest.setTitle("Discard Cursed Condition?");
                    ServicePlatform.get().getGameService().showCard(showCardRequest)
                            .subscribe(showCardResponse -> onDiscardOrNo(showCardResponse, input));
                } else if (input.isHealthEnabled()){
                    gainHealth(input);
                }
            };
        }


        private void gainHealth(RestData input) {
            InvestigatorRead investigator = getActiveInvestigator();
            if (investigator.getCurrentHealth() == investigator.getInfo().getMaxHealth()) {
                ServicePlatform.get().getService().convertTo(RestData.class, () -> input);
                return;
            }
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setAssetInfo(getAssetInfo());
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
            showCardRequest.setTitle("Recover additional Health?");
            ServicePlatform.get().getGameService().showCard(showCardRequest)
                    .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
        }

        private void onDiscardOrNo(ShowCardResponse showCardResponse, RestData input) {
            if (ShowCardResponse.NO == showCardResponse) {
                gainHealth(input);
            } else {
                ConditionInfo cursedCard = ServicePlatform.get().getConditionsDeck().getCondition(
                        getActiveInvestigator().getInfo().getInvestigatorId(), ConditionId.CURSED);
                ShowCardRequest request = new ShowCardRequest();
                request.setHideType(HideType.DISCARD_ALWAYS);
                request.setConditionInfo(cursedCard);
                request.setTitle("Discard Cursed Condition");
                request.setDisplayAssetResponseType(DisplayAssetResponseType.NOTHING);
                ServicePlatform.get().getGameService().showCard(request)
                        .subscribe(out -> onDiscardConditionConfirm(cursedCard, input));
            }
        }

        private void onDiscardConditionConfirm(ConditionInfo conditionInfo, RestData input) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().discardConditionFromInvestigator(getActiveInvestigatorId(), conditionInfo);
            ServicePlatform.get().getService().convertTo(Object.class, () -> input);
            ServicePlatform.get().getService().release();
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
