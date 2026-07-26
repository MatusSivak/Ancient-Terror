package sk.sivak.eldritchhorror.core.eventlistener.spell;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.healingwords.HealingWordsSpell;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.action.RestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Collections;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class HealingWordsListener extends AbstractSpellListener<HealingWordsSpell> {


    private BeforRestListener beforeRestListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(beforeRestListener);
    }

    @Override
    protected void register() {
        beforeRestListener = new BeforRestListener();
        getEventQueue().addBeforeEventListener(beforeRestListener, BeforeAfterEvent.REST);
    }

    private class BeforRestListener extends EventListenerImpl<RestData> {

        private RestData eventData;

        private InvestigatorId restingInvestigator;
        @Override
        public void onNotify(RestData eventData) {
            this.eventData = eventData;
            InvestigatorRead activeInvestigator = ServicePlatform.get().getInvestigators().getInvestigator(getActiveInvestigatorId());
            InvestigatorRead spellOwner = ServicePlatform.get().getInvestigators().getInvestigator(spellOwnerId);
            if (activeInvestigator.getCurrentLocationId() != spellOwner.getCurrentLocationId()) {
                return;
            }

            restingInvestigator = getActiveInvestigatorId();

            ServicePlatform.get().getService().hold();
            changeToSpellOwner();
            showCard();
            ServicePlatform.get().getService().release();
        }

        private void showCard() {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setSpellInfo(spellInfo);
            showCardRequest.setHideType(HideType.RETURN);
            showCardRequest.setTitle("Cast Healing Words?");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
            Single<ShowCardResponse> displaySpell = ServicePlatform.get().getGameService().showCard(showCardRequest);
            displaySpell.subscribe(response -> {
                if (response != ShowCardResponse.YES) {
                    ServicePlatform.get().getService().hold();
                    changeToResting();
                    ServicePlatform.get().getService().convertTo(RestData.class, () -> eventData);
                    ServicePlatform.get().getService().release();
                    return;
                }
                Single<TestData> test = ServicePlatform.get().getTestService().test(Stat.LORE,0, 3,
                        new TestFlavorRequest(TestFlavorType.SPELL));
                test.subscribe(this::withTestResult);
            });
        }

        private void changeToResting() {
            if (spellOwnerId != restingInvestigator) {
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(restingInvestigator);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            }
        }


        private void changeToSpellOwner() {
            if (spellOwnerId != restingInvestigator) {
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(spellOwnerId);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            }
        }


        private void withTestResult(TestData testResult) {
            if (testResult.isSuccessful()) {
                eventData.setSanityGained(eventData.getSanityGained() + 1);
                eventData.setHealthGained(eventData.getHealthGained() + 1);
            }
            ServicePlatform.get().getService().hold();
            changeToResting();
            ServicePlatform.get().getService().convertTo(RestData.class, () -> eventData);
            ServicePlatform.get().getService().release();
            afterRestFlipCard(testResult);
        }

        private void afterRestFlipCard(TestData testResult) {
            ServicePlatform.get().getService().startInsertingAfterCommand(BeforeAfterEvent.REST);
            ServicePlatform.get().getService().hold();
            flipCard(testResult);
            ServicePlatform.get().getService().convertTo(RestData.class, () -> eventData);
            ServicePlatform.get().getService().release();
            ServicePlatform.get().getService().endInsertingAtCommand();
        }

        private void flipCard(TestData testResult) {
            ServicePlatform.get().getService().hold();

            changeToSpellOwner();
            AbstractSpellBackListener spellBackListener = findSpellBackListener();
            spellBackListener.setTestData(testResult);
            spellBackListener.setData("restingInvestigator", restingInvestigator);
            spellBackListener.setData("spellOwnerId", spellOwnerId);
            spellBackListener.executeWhole();

            ServicePlatform.get().getService().convertTo(RestData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<RestData> getDataClass() {
            return RestData.class;
        }
    }
}
