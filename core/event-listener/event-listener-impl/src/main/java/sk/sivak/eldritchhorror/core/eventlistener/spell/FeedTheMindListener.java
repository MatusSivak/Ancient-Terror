package sk.sivak.eldritchhorror.core.eventlistener.spell;

import java8.features.stream.Stream;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.action.CardInfoAware;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.spell.feedthemind.FeedTheMindSpell;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FeedTheMindListener extends AbstractSpellListener<FeedTheMindSpell> {


    private FeedTheMindActionListener feedTheMindActionListener;
    private EnableCardListener enableCardListener;

    public FeedTheMindListener() {

    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(feedTheMindActionListener, enableCardListener);
    }

    @Override
    protected void register() {
        feedTheMindActionListener = new FeedTheMindActionListener();
        getEventQueue().addBeforeEventListener(feedTheMindActionListener, BeforeAfterEvent.FIND_ACTIONS);

        enableCardListener = new EnableCardListener(spellInfo);
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    private class FeedTheMindActionListener extends AbstractActionPhaseListener {

        public FeedTheMindActionListener() {
            name = "Feed\n" +
                    "the Mind";
        }

        @Override
        protected String getAdditionalInfo() {
            return null;
        }

        @Override
        protected String getGeneralDescription() {
            return "Test Lore-1. If you pass,\n" +
                    "choose an investigator\n" +
                    "on your space\n" +
                    "to improve 1 skill\n" +
                    "of his choice.\n" +
                    "Then flip this card.";
        }

        @Override
        protected String getTexturePath() {
            return "card/spell/FEED_THE_MIND.jpg";
        }

        @Override
        protected float getScaleDownPercentage() {
        return 1.00f;
    }

        @Override
        protected boolean getNeedsMask() {
            return true;
        }

        @Override
        protected FeedTheMindAction createAction() {
            return new FeedTheMindAction();
        }

        @Override
        protected boolean isVisible() {
            return getInvestigators().getActiveInvestigatorId() == spellOwnerId;
        }

        @Override
        protected boolean isDisabled() {
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
    }

    private class FeedTheMindAction extends AbstractActionPhaseAction implements CardInfoAware<FeedTheMindSpell> {


        @Override
        public void execute() {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setSpellInfo(spellInfo);
            showCardRequest.setHideType(HideType.DISABLE_ALWAYS);
            showCardRequest.setTitle("Cast Feed the Mind.");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
            Single<ShowCardResponse> displaySpell = ServicePlatform.get().getGameService().showCard(showCardRequest);
            displaySpell.subscribe(response -> {
                Single<TestData> test = ServicePlatform.get().getTestService().test(Stat.LORE, -1, 3,
                        new TestFlavorRequest(TestFlavorType.SPELL));
                test.subscribe(this::withTestResult);
            });
        }

        private void withTestResult(TestData testResult) {
            if (testResult.isSuccessful()) {
                selectSingleInvestigator(testResult);
            } else {
                disableAndFlipCard(testResult, null);
            }
        }

        private void disableAndFlipCard(TestData testResult, InvestigatorId selectedInvestigator) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().disableCard(spellInfo);

            AbstractSpellBackListener spellBackListener = findSpellBackListener();
            spellBackListener.setTestData(testResult);
            spellBackListener.setData("selectedInvestigator", selectedInvestigator);
            spellBackListener.executeWhole();

            ServicePlatform.get().getInvestigatorService().convertToNull();
            ServicePlatform.get().getService().release();
        }

        private void selectSingleInvestigator(TestData testResult) {
            LocationId activeLocationId = ServicePlatform.get().getInvestigators().getInvestigator(spellOwnerId).getCurrentLocationId();

            List<? extends InvestigatorRead> selectedInvestigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
            List<? extends InvestigatorRead> investigatorsOnSameSpace = Stream.collectToList(selectedInvestigators,
                    selected -> selected.getCurrentLocationId() == activeLocationId);

            Collection<InvestigatorId> allowedInvestigators = Stream.map(investigatorsOnSameSpace, in -> in.getInfo().getInvestigatorId());

            InvestigatorRestriction investigatorRestriction = new InvestigatorRestriction(true);
            investigatorRestriction.addAllowedInvestigators(allowedInvestigators);
            ServicePlatform.get().getInvestigatorService().selectInvestigator(investigatorRestriction)
                    .subscribe(selectedInvestigator -> onInvestigatorSelect(selectedInvestigator, testResult));
        }

        private void onInvestigatorSelect(InvestigatorId investigatorId, TestData testResult) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
            ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
            ServicePlatform.get().getInvestigatorService().improveSkill(null);
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(spellOwnerId);
            disableAndFlipCard(testResult, investigatorId);
            ServicePlatform.get().getService().release();
        }

        @Override
        public FeedTheMindSpell getCardInfo() {
            return spellInfo;
        }
    }


}
