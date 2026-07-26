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
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.constants.spell.plumbthevoid.PlumbTheVoidSpell;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PlumbTheVoidListener extends AbstractSpellListener<PlumbTheVoidSpell> {


    private PlumbTheVoidActionListener plumbTheVoidActionListener;
    private EnableCardListener enableCardListener;

    public PlumbTheVoidListener() {

    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(plumbTheVoidActionListener, enableCardListener);
    }

    @Override
    protected void register() {
        plumbTheVoidActionListener = new PlumbTheVoidActionListener();
        getEventQueue().addBeforeEventListener(plumbTheVoidActionListener, BeforeAfterEvent.FIND_ACTIONS);

        enableCardListener = new EnableCardListener(spellInfo);
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    private class PlumbTheVoidActionListener extends AbstractActionPhaseListener {

        public PlumbTheVoidActionListener() {
            name = "Plumb\n" +
                    "the Void";
        }

        @Override
        protected String getAdditionalInfo() {
            return null;
        }

        @Override
        protected String getGeneralDescription() {
            return "Test Lore-1. If you pass,\n" +
                    "an investigator\n" +
                    "of your choice\n" +
                    "may move to any space.\n" +
                    "Then flip this card.";
        }

        @Override
        protected String getTexturePath() {
            return "card/spell/PLUMB_THE_VOID.jpg";
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
        protected PlumbTheVoidAction createAction() {
            return new PlumbTheVoidAction();
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

    private class PlumbTheVoidAction extends AbstractActionPhaseAction implements CardInfoAware<PlumbTheVoidSpell> {


        private InvestigatorId selectedInvestigator;

        @Override
        public void execute() {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setSpellInfo(spellInfo);
            showCardRequest.setHideType(HideType.DISABLE_ALWAYS);
            showCardRequest.setTitle("Cast Plumb the Void.");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
            Single<ShowCardResponse> displaySpell = ServicePlatform.get().getGameService().showCard(showCardRequest);
            displaySpell.subscribe(response -> {
                selectSingleInvestigator();
            });
        }

        private void selectSingleInvestigator() {
            List<? extends InvestigatorRead> allInvestigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
            Collection<InvestigatorId> allowedInvestigators = Stream.map(allInvestigators, in -> in.getInfo().getInvestigatorId());

            InvestigatorRestriction investigatorRestriction = new InvestigatorRestriction(false);
            investigatorRestriction.addAllowedInvestigators(allowedInvestigators);
            ServicePlatform.get().getInvestigatorService().selectInvestigator(investigatorRestriction)
                    .subscribe(this::onInvestigatorSelect);
        }

        private void onInvestigatorSelect(InvestigatorId investigatorId) {
            this.selectedInvestigator = investigatorId;
            Single<TestData> test = ServicePlatform.get().getTestService().test(Stat.LORE, -1, 3,
                    new TestFlavorRequest(TestFlavorType.SPELL));
            test.subscribe(this::disableAndFlipCard);
        }

        private Runnable createMainSpellAction() {
            return () -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(selectedInvestigator);

                ServicePlatform.get().getGameService().selectLocation(new SelectLocationData(null)).subscribe(locationId -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getBasicActionService().travelToLocation(new LocationInfo.Connection() {
                        @Override
                        public LocationId getLocationId() {
                            return locationId;
                        }

                        @Override
                        public PathType getPathType() {
                            return PathType.WALK;
                        }
                    });
                    ServicePlatform.get().getService().release();
                });

                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(spellOwnerId);
                ServicePlatform.get().getService().release();
            };
        }

        private void disableAndFlipCard(TestData testResult) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().disableCard(spellInfo);

            AbstractSpellBackListener spellBackListener = findSpellBackListener();
            spellBackListener.setTestData(testResult);
            spellBackListener.setMainSpellAction(createMainSpellAction());
            spellBackListener.setData("selectedInvestigator", selectedInvestigator);
            spellBackListener.executeWhole();

            ServicePlatform.get().getInvestigatorService().convertToNull();
            ServicePlatform.get().getService().release();
        }


        @Override
        public PlumbTheVoidSpell getCardInfo() {
            return spellInfo;
        }
    }


}
