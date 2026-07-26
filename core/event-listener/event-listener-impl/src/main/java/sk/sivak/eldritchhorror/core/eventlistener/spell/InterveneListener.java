package sk.sivak.eldritchhorror.core.eventlistener.spell;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.intervene.InterveneSpell;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.constants.test.UsableAsset;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;

import java.util.Collections;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class InterveneListener extends AbstractSpellListener<InterveneSpell> {

    private CastInterveneListener castInterveneListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(castInterveneListener);
    }

    @Override
    protected void register() {
        castInterveneListener = new CastInterveneListener();
        getEventQueue().addDirectEventListener(castInterveneListener, DirectEvent.REGISTER_USABLE_ASSETS);
    }


    private class CastInterveneListener extends EventListenerImpl<TestData> {

        private TestData input;
        private AbstractSpellBackListener spellBackListener;

        @Override
        public void onNotify(TestData eventData) {
            if (isOwner()) {
                return;
            }
            ServicePlatform.get().getService().<TestData>addEventCommand((input) -> {
                getEventAction(input).run();
            });
        }

        private boolean isOwner() {
            return spellOwnerId.equals(ServicePlatform.get().getInvestigators().getActiveInvestigatorId());
        }

        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.getStat() != Stat.STRENGTH) {
                    return;
                }
                if (ServicePlatform.get().getTestFlavor().getFlavorType() != TestFlavorType.COMBAT) {
                    return;
                }
                this.input = input;
                askCastIntervene();
            };
        }

        private void askCastIntervene() {
            ServicePlatform.get().getService().hold();
            InvestigatorId combatantId = getActiveInvestigatorId();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(spellOwnerId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setSpellInfo(spellInfo);
            showCardRequest.setTitle("Cast Intervene?");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
            showCardRequest.setHideType(HideType.RETURN);
            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(showCardResponse -> answerCastIntervene(showCardResponse, combatantId));
            ServicePlatform.get().getService().release();
        }

        private void answerCastIntervene(ShowCardResponse showCardResponse, InvestigatorId combatantId) {
            if (ShowCardResponse.YES != showCardResponse) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(combatantId);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                ServicePlatform.get().getService().convertTo(TestData.class, () -> input);
                ServicePlatform.get().getService().release();
                return;
            }
            testLore(combatantId);
        }

        private void testLore(InvestigatorId combatantId) {
            Single<TestData> test = ServicePlatform.get().getTestService().test(Stat.LORE, 0, 3,
                    new TestFlavorRequest(TestFlavorType.SPELL));
            test.subscribe(testResult -> testLoreResult(testResult, combatantId));
        }

        private void testLoreResult(TestData testResult, InvestigatorId combatantId) {
            ServicePlatform.get().getService().hold();
            spellBackListener = findSpellBackListener();
            spellBackListener.setTestData(testResult);
            spellBackListener.setMainSpellAction(createMainSpellAction());
            spellBackListener.setData("statBonus", 3);
            spellBackListener.setData("dicePoolBonus", 0);
            spellBackListener.setData("combatantId", combatantId);
            spellBackListener.executeWhole();
            ServicePlatform.get().getEncounterService().checkDefeatedAfterEncounter();
            ServicePlatform.get().getInvestigatorService().convertTo(TestData.class, () -> input);
            ServicePlatform.get().getService().release();
        }

        private Runnable createMainSpellAction() {
            return () -> {
                addUsableAsset(input, spellInfo,
                        spellBackListener.getData("statBonus"),
                        spellBackListener.getData("dicePoolBonus"));
            };
        }

        protected UsableAsset addUsableAsset(TestData testData, SpellInfo spellInfo, int statBonus, int dicePoolBonus) {
            UsableAsset usableAsset = new UsableAsset();
            usableAsset.setCardInfo(spellInfo);
            usableAsset.setStatBonus(statBonus);
            usableAsset.setDicePoolBonus(dicePoolBonus);
            usableAsset.setUpByDefault(true);
            testData.addUsableAsset(usableAsset);
            return usableAsset;
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

}
