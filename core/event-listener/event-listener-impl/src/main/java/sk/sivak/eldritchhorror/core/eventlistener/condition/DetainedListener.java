package sk.sivak.eldritchhorror.core.eventlistener.condition;

import java8.features.stream.Stream;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.condition.detained.DetainedCondition;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventlistener.action.basic.SkipActionListener;
import sk.sivak.eldritchhorror.core.eventlistener.artifact.AbstractArtifactEventListener;
import sk.sivak.eldritchhorror.core.eventlistener.artifact.PallidMaskListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.CollectAvailableActionsData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.ConditionEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.Encounter;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.data.test.TestData.JUST_ONE;

public class DetainedListener extends AbstractConditionListener<DetainedCondition> {

    private DetainedActionListener detainedActionListener;
    private DisableOrRemoveActionsListener disableOrRemoveActionsListener;
    private DisableEncountersListener disableEncountersListener;
    private AddEncounterListener addEncounterListener;
    private Encounter detainedEncounter;
    private HideCombatEncountersListener hideCombatEncountersListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(detainedActionListener, disableOrRemoveActionsListener, addEncounterListener, disableEncountersListener, hideCombatEncountersListener);
    }

    @Override
    protected void register() {
        detainedActionListener = new DetainedActionListener();
        disableOrRemoveActionsListener = new DisableOrRemoveActionsListener();
        addEncounterListener = new AddEncounterListener();
        disableEncountersListener = new DisableEncountersListener();
        hideCombatEncountersListener = new HideCombatEncountersListener();
        getEventQueue().addBeforeEventListener(detainedActionListener, BeforeAfterEvent.FIND_ACTIONS);
        getEventQueue().addDirectEventListener(disableOrRemoveActionsListener, DirectEvent.DISABLE_OR_REMOVE_ACTIONS);
        getEventQueue().addDirectEventListener(addEncounterListener, DirectEvent.COLLECT_COMMON_ENCOUNTERS);
        getEventQueue().addDirectEventListener(disableEncountersListener, DirectEvent.DISABLE_ENCOUNTERS);
        getEventQueue().addBeforeEventListener(hideCombatEncountersListener, BeforeAfterEvent.DISABLE_EPIC_COMBAT_ENCOUNTERS);
    }

    private class AddEncounterListener extends EventListenerImpl<AvailableEncounters> {

        @Override
        public void onNotify(AvailableEncounters eventData) {
            if (!isOwner()) {
                return;
            }

            detainedEncounter = new ConditionEncounter(conditionInfo);
            eventData.addEncounter(detainedEncounter);
        }

        @Override
        public Class<AvailableEncounters> getDataClass() {
            return AvailableEncounters.class;
        }
    }

    private class DisableEncountersListener extends EventListenerImpl<AvailableEncounters> {

        @Override
        public void onNotify(AvailableEncounters eventData) {
            if (!isOwner()) {
                return;
            }
            for (Encounter encounter : eventData.getEncounters()) {
                if (encounter == detainedEncounter) {
                    continue;
                }
                encounter.getEncounterButtonData().disable("You are Detained");
            }
        }

        @Override
        public Class<AvailableEncounters> getDataClass() {
            return AvailableEncounters.class;
        }
    }
    private class DetainedActionListener extends AbstractActionPhaseListener {

        public DetainedActionListener() {
            name = "Leave\nJail";
        }

        @Override
        protected String getAdditionalInfo() {
            return "Detained: " + investigatorId;
        }

        @Override
        protected String getGeneralDescription() {
            return "Test Influence.\nIf you pass,\ndiscard this card.";
        }

        @Override
        protected String getTexturePath() {
            return "card/condition/DETAINED.jpg";
        }

        @Override
        protected float getScaleDownPercentage() {
        return 1.0f;
    }

        @Override
        protected boolean getNeedsMask() {
            return true;
        }

        @Override
        protected DetainedAction createAction() {
            return new DetainedAction();
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

        private class DetainedAction extends AbstractActionPhaseAction {

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
                    showCardRequest.setTitle("Discard Detained");
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

    private class DisableOrRemoveActionsListener extends EventListenerImpl<CollectAvailableActionsData> {

        @Override
        public void onNotify(CollectAvailableActionsData eventData) {
            if (!isOwner()) {
                return;
            }
            Iterator<ActionPhaseAction> iterator = eventData.getActionPhaseActions().iterator();
            while (iterator.hasNext()) {
                ActionPhaseAction next = iterator.next();
                if (next instanceof DetainedActionListener.DetainedAction) {
                    continue;
                }
                if (next instanceof SkipActionListener.SkipAction) {
                    continue;
                }
                iterator.remove();
            }
        }

        private boolean isOwner() {
            return investigatorId.equals(ServicePlatform.get().getInvestigators().getActiveInvestigatorId());
        }

        @Override
        public Class<CollectAvailableActionsData> getDataClass() {
            return CollectAvailableActionsData.class;
        }
    }

    private class HideCombatEncountersListener extends EventListenerImpl<AvailableEncounters> {


        @Override
        public void onNotify(AvailableEncounters eventData) {
            if (!isOwner()) {
                return;
            }
            eventData.removeEncounters(encounter -> encounter.getEncounterType() == EncounterType.COMBAT);
        }

        @Override
        public Class<AvailableEncounters> getDataClass() {
            return AvailableEncounters.class;
        }
    }
}
