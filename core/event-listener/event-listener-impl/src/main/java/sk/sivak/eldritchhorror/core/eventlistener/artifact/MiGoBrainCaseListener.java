package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.KhopeshOfTheAbyssArtifact;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.trade.TradeData;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventlistener.asset.DeliveryServiceListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MiGoBrainCaseListener extends AbstractArtifactListener<KhopeshOfTheAbyssArtifact> {

    private EnableCardListener enableCardListener;
    private ActionListener actionListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(enableCardListener, actionListener);
    }

    @Override
    protected void register() {
        actionListener = new ActionListener();
        getEventQueue().addBeforeEventListener(actionListener, BeforeAfterEvent.FIND_ACTIONS);

        enableCardListener = new EnableCardListener(getArtifactInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    private class ActionListener extends AbstractActionPhaseListener<ActionListener.MiGoBrainCaseAction> {

        public ActionListener() {
            name = "Mi-Go\n" +
                    "Brain Case";
        }


        @Override
        protected String getAdditionalInfo() {
            return null;
        }

        @Override
        protected String getGeneralDescription() {
            return "You and another investigator\n" +
                    "may trade possessions.\n" +
                    "In addition, he may move\n" +
                    "to your space.\n" +
                    "If he does, move\n" +
                    "to his previous space.";
        }

        @Override
        protected String getTexturePath() {
            return "card/artifact/MI_GO_BRAIN_CASE.jpg";
        }

        @Override
        protected boolean getNeedsScaleDown() {
            return true;
        }

        @Override
        protected boolean getNeedsMask() {
            return true;
        }

        @Override
        protected MiGoBrainCaseAction createAction() {
            return new MiGoBrainCaseAction();
        }

        @Override
        protected boolean isVisible() {
            return getInvestigators().getActiveInvestigatorId() == investigatorId;
        }

        @Override
        protected boolean isDisabled() {
            if (ServicePlatform.get().getInvestigators().getOnBoardInvestigators().size() < 2) {
                disabledReason = "No available investigator.";
                return true;
            }
            if (!enabled) {
                disabledReason = "Action already performed this round"; // THIS MUST BE LAST
                return true;
            }
            return false;
        }

        @Override
        protected boolean isNotRecommended() {
            return false;
        }

        protected class MiGoBrainCaseAction extends AbstractActionPhaseAction {

            private InvestigatorId tradedInvestigator;

            @Override
            public void execute() {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setArtifactInfo(getArtifactInfo());
                showCardRequest.setHideType(HideType.DISABLE_ALWAYS);
                showCardRequest.setTitle("Trade possessions, change positions.");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
                displayAsset.subscribe(response -> {
                    tradePossessions();
                });
            }

            private void tradePossessions() {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().disableCard(getArtifactInfo());

                AfterFindListener afterFindListener = new AfterFindListener();
                AfterSelectInvestigatorForTradeListener afterSelectInvestigatorForTradeListener = new AfterSelectInvestigatorForTradeListener();

                ServicePlatform.get().getEventQueue().addAfterEventListener(afterFindListener, BeforeAfterEvent.FIND_INVESTIGATORS_FOR_TRADE);
                ServicePlatform.get().getEventQueue().addAfterEventListener(afterSelectInvestigatorForTradeListener, BeforeAfterEvent.SELECT_INVESTIGATOR_FOR_TRADE);

                InvestigatorId ownerOfTheCase = getOwner();
                ServicePlatform.get().getBasicActionService().trade();

                ServicePlatform.get().getService().addEventCommand(in -> {
                    ServicePlatform.get().getEventQueue().unregisterListener(afterFindListener);
                    ServicePlatform.get().getEventQueue().unregisterListener(afterSelectInvestigatorForTradeListener);

                });

                Question<Boolean> question = new Question<>();
                question.setOptions(Question.Option.noYesOptions);
                question.setTitle("Exchange positions?");
                ServicePlatform.get().getGameService().ask(question).subscribe(answer -> onAnswer(answer, ownerOfTheCase));

                ServicePlatform.get().getService().release();
            }

            private void onAnswer(Answer<Boolean, Object> answer, InvestigatorId ownerOfTheCase) {
                if (answer.getResponseData()) {
                    ServicePlatform.get().getService().hold();
                    LocationId tradedLocationId = ServicePlatform.get().getInvestigators().getInvestigator(tradedInvestigator).getCurrentLocationId();
                    LocationId ownerLocationId = ServicePlatform.get().getInvestigators().getInvestigator(ownerOfTheCase).getCurrentLocationId();


                    ServicePlatform.get().getInvestigatorService().removeInvestigatorFromPlace(ownerOfTheCase);
                    ServicePlatform.get().getInvestigatorService().spawnInvestigator(ownerOfTheCase, tradedLocationId);
                    ServicePlatform.get().getInvestigatorService().removeInvestigatorFromPlace(tradedInvestigator);
                    ServicePlatform.get().getInvestigatorService().spawnInvestigator(tradedInvestigator, ownerLocationId);
                    if (ServicePlatform.get().getPerformedActions().canPerformAction(investigatorId)) {
                        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                    }
                    ServicePlatform.get().getService().convertToNull();
                    ServicePlatform.get().getService().release();
                } else {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getService().convertToNull();
                    ServicePlatform.get().getService().release();
                }

            }

            private class AfterSelectInvestigatorForTradeListener extends EventListenerImpl<InvestigatorId> {

                @Override
                public void onNotify(InvestigatorId eventData) {
                    tradedInvestigator = eventData;
                }

                @Override
                public Class<InvestigatorId> getDataClass() {
                    return InvestigatorId.class;
                }
            }

        }
    }

    private class AfterFindListener extends EventListenerImpl<List> {

        @Override
        public void onNotify(List eventData) {
            eventData.clear();
            List<? extends InvestigatorRead> selectedInvestigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
            Collection<InvestigatorId> selectedInvestigatorsIds =
                    Stream.map(selectedInvestigators, in -> in.getInfo().getInvestigatorId());
            IterableUtils.removeIf(selectedInvestigatorsIds, in -> in == investigatorId);
            eventData.addAll(selectedInvestigatorsIds);
        }

        @Override
        public Class<List> getDataClass() {
            return List.class;
        }
    }

}
