package sk.sivak.eldritchhorror.core.eventlistener.spell;

import java8.features.stream.Stream;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.mistsofreleh.MistsOfRelehSpell;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.mistsofreleh.MistsOfRelehSpellBackListener1;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.mistsofreleh.MistsOfRelehSpellBackListener3;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.mistsofreleh.MistsOfRelehSpellBackListener4;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;

import java.util.Collections;
import java.util.List;

public class MistsOfRelehListener extends AbstractSpellListener<MistsOfRelehSpell> {

    private HideCombatEncountersListener hideCombatEncountersListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(hideCombatEncountersListener);
    }

    @Override
    protected void register() {
        hideCombatEncountersListener = new HideCombatEncountersListener();
        getEventQueue().addBeforeEventListener(hideCombatEncountersListener, BeforeAfterEvent.DISABLE_EPIC_COMBAT_ENCOUNTERS);
    }


    private class HideCombatEncountersListener extends EventListenerImpl<AvailableEncounters> {

        private AvailableEncounters input;

        @Override
        public void onNotify(AvailableEncounters eventData) {
            if (!isOwner()) {
                return;
            }
            ServicePlatform.get().getService().<AvailableEncounters>addEventCommand((input) -> {
                this.input = input;
                getEventAction().run();
            });
        }

        private boolean isOwner() {
            return spellOwnerId.equals(ServicePlatform.get().getInvestigators().getActiveInvestigatorId());
        }

        protected Runnable getEventAction() {
            return () -> {
                if (!Stream.anyMatch(input.getEncounters(), encounter -> encounter.getEncounterType() == EncounterType.COMBAT)) {
                    return;
                }
                if (ServicePlatform.get().getPerformedEncounters().hasEncounteredAnything(spellOwnerId)) {
                    return;
                }
                questionIgnoreMonsters();
            };
        }

        private void testLore() {
            Single<TestData> test = ServicePlatform.get().getTestService().test(Stat.LORE, 0, 3,
                    new TestFlavorRequest(TestFlavorType.SPELL));
            test.subscribe(this::testLoreResult);
        }

        private void testLoreResult(TestData testResult) {
            if (testResult.isSuccessful()) {
                input.removeEncounters(encounter -> encounter.getEncounterType() == EncounterType.COMBAT);
            }
            flipThisCard(testResult);
        }

        private void flipThisCard(TestData testResult) {
            ServicePlatform.get().getService().hold();
            AbstractSpellBackListener spellBackListener = findSpellBackListener();
            spellBackListener.setTestData(testResult);
            spellBackListener.executeWhole();

            ServicePlatform.get().getEncounterService().checkDefeatedAfterEncounter();
            ServicePlatform.get().getService().convertTo(AvailableEncounters.class, () -> input);
            ServicePlatform.get().getService().release();
        }

        private void questionIgnoreMonsters() {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setSpellInfo(spellInfo);
            showCardRequest.setTitle("Ignore Monsters?");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
            showCardRequest.setHideType(HideType.RETURN);
            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(this::answerIgnoreMonsters);
        }

        private void answerIgnoreMonsters(ShowCardResponse response) {
            if (ShowCardResponse.YES != response) {
                ServicePlatform.get().getService().convertTo(AvailableEncounters.class, () -> input);
                return;
            }
            testLore();
        }

        @Override
        public Class<AvailableEncounters> getDataClass() {
            return AvailableEncounters.class;
        }
    }

}
