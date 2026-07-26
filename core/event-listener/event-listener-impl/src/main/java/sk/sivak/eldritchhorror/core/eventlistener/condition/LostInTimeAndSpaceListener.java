package sk.sivak.eldritchhorror.core.eventlistener.condition;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.lostintimeandspace.LostInTimeAndSpaceCondition;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.AfterActionPerformedData;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainedCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.ConditionEncounter;

import java.util.Arrays;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;
import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.AFTER_ACTION_PERFORMED;

public class LostInTimeAndSpaceListener extends AbstractConditionListener<LostInTimeAndSpaceCondition> {

    private BeforeShowActiveInvestigatorListener beforeShowActiveInvestigatorListener;
    private AddEncounterListener addEncounterListener;
    private EventListener<Object> performActionStartListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(beforeShowActiveInvestigatorListener, addEncounterListener, performActionStartListener);
    }

    @Override
    protected void register() {
        beforeShowActiveInvestigatorListener = new BeforeShowActiveInvestigatorListener();
        addEncounterListener = new AddEncounterListener();
        performActionStartListener = new PerformActionStartListener();

        getEventQueue().addBeforeEventListener(beforeShowActiveInvestigatorListener, BeforeAfterEvent.SHOW_ACTIVE_INVESTIGATOR);
        getEventQueue().addDirectEventListener(addEncounterListener, DirectEvent.COLLECT_COMMON_ENCOUNTERS);
        getEventQueue().addDirectEventListener(performActionStartListener, DirectEvent.PERFORM_ACTION_START);


        ServicePlatform.get().getService().addEventCommand(in -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getInvestigatorService().lostInTimeAndSpace(investigatorId);
            ServicePlatform.get().getInvestigatorService().convertTo(Object.class, () -> in);
            ServicePlatform.get().getService().release();
        });


    }

    private class PerformActionStartListener extends EventListenerImpl<Object> {

        @Override
        public void onNotify(Object eventData) {
            if (investigatorId != getActiveInvestigatorId()) {
                return;
            }
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setConditionInfo(conditionInfo);
            showCardRequest.setHideType(HideType.RETURN);
            showCardRequest.setTitle("Cannot perform actions.");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(showCardResponse -> {
                ServicePlatform.get().getPerformedActions().clearFreeActions(investigatorId);

                ServicePlatform.get().getService().hold();
                if (ServicePlatform.get().getInvestigators().getInvestigator(investigatorId).isDelayed()) {
                    ServicePlatform.get().getInvestigatorService().endDelayed(investigatorId);
                }
                ServicePlatform.get().getInvestigatorService().hideLostInTimeAndSpace(investigatorId);
                AfterActionPerformedData afterActionPerformedData = new AfterActionPerformedData();
                afterActionPerformedData.setCanPerformAction(false);
                afterActionPerformedData.setLast(ServicePlatform.get().getInvestigators().isActiveLast());
                ServicePlatform.get().getService().skipAfterEvent(AFTER_ACTION_PERFORMED, afterActionPerformedData);

                ServicePlatform.get().getService().release();
            });
        }

        @Override
        public Class<Object> getDataClass() {
            return Object.class;
        }
    }

    private class BeforeShowActiveInvestigatorListener extends EventListenerImpl<InvestigatorId> {

        @Override
        public void onNotify(InvestigatorId eventData) {
            if (eventData != investigatorId) {
                return;
            }
            ServicePlatform.get().getInvestigatorService().showLostInTimeAndSpace(eventData);
        }

        @Override
        public Class<InvestigatorId> getDataClass() {
            return InvestigatorId.class;
        }
    }

    private class AddEncounterListener extends EventListenerImpl<AvailableEncounters> {

        @Override
        public void onNotify(AvailableEncounters eventData) {
            if (!isOwner()) {
                return;
            }

            eventData.addEncounter(new ConditionEncounter(conditionInfo));
        }

        @Override
        public Class<AvailableEncounters> getDataClass() {
            return AvailableEncounters.class;
        }
    }

}
