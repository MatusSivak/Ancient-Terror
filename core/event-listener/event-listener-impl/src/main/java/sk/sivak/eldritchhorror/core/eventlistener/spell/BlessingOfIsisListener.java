package sk.sivak.eldritchhorror.core.eventlistener.spell;

import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.action.CardInfoAware;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.constants.spell.blessingofisis.BlessingOfIsisSpell;
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

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class BlessingOfIsisListener extends AbstractSpellListener<BlessingOfIsisSpell> {


    private BlessingOfIsisActionListener blessingOfIsisActionListener;
    private EnableCardListener enableCardListener;

    public BlessingOfIsisListener() {

    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(blessingOfIsisActionListener, enableCardListener);
    }

    @Override
    protected void register() {
        blessingOfIsisActionListener = new BlessingOfIsisActionListener();
        getEventQueue().addBeforeEventListener(blessingOfIsisActionListener, BeforeAfterEvent.FIND_ACTIONS);

        enableCardListener = new EnableCardListener(spellInfo);
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    private class BlessingOfIsisActionListener extends AbstractActionPhaseListener {

        public BlessingOfIsisActionListener() {
            name = "Blessing\n" +
                    "of Isis";
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
                    "that does not have\n" +
                    "a Blessed Condition\n" +
                    "to gain a Blessed Condition.\n" +
                    "Then flip this card.";
        }

        @Override
        protected String getTexturePath() {
            return "card/spell/BLESSING_OF_ISIS.jpg";
        }

        @Override
        protected float getScaleDownPercentage() {
        return 0.5f;
    }

        @Override
        protected boolean getNeedsMask() {
            return true;
        }

        @Override
        protected BlessingOfIsisAction createAction() {
            return new BlessingOfIsisAction();
        }

        @Override
        protected boolean isVisible() {
            return getInvestigators().getActiveInvestigatorId() == spellOwnerId;
        }

        @Override
        protected boolean isDisabled() {
            if (findAvailableInvestigators().isEmpty()) {
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
    }

    private class BlessingOfIsisAction extends AbstractActionPhaseAction implements CardInfoAware<BlessingOfIsisSpell> {


        private InvestigatorId selectedInvestigator;

        @Override
        public void execute() {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setSpellInfo(spellInfo);
            showCardRequest.setHideType(HideType.DISABLE_ALWAYS);
            showCardRequest.setTitle("Cast Blessing of Isis.");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
            Single<ShowCardResponse> displaySpell = ServicePlatform.get().getGameService().showCard(showCardRequest);
            displaySpell.subscribe(response -> {
                selectSingleInvestigator();
            });
        }

        private void selectSingleInvestigator() {
            List<? extends InvestigatorRead> allInvestigators = findAvailableInvestigators();
            Collection<InvestigatorId> allowedInvestigators = Stream.map(allInvestigators, in -> in.getInfo().getInvestigatorId());

            InvestigatorRestriction investigatorRestriction = new InvestigatorRestriction(true);
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
                if (spellOwnerId != selectedInvestigator) {
                    ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(selectedInvestigator);
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                }
                ServicePlatform.get().getGameService().gainCondition(ConditionId.BLESSED);
                if (spellOwnerId != selectedInvestigator) {
                    ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(spellOwnerId);
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                }
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
        public BlessingOfIsisSpell getCardInfo() {
            return spellInfo;
        }
    }

    private List<? extends InvestigatorRead> findAvailableInvestigators() {
        List<? extends InvestigatorRead> allInvestigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        IterableUtils.removeIf(allInvestigators, investigator -> {
            boolean isOnDifferentLocation = investigator.getCurrentLocationId() != ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
            boolean hasBlessedCondition = ServicePlatform.get().getConditionsDeck().hasCondition(investigator.getInfo().getInvestigatorId(), ConditionId.BLESSED);
            return isOnDifferentLocation || hasBlessedCondition;
        });
        return allInvestigators;
    }


}
