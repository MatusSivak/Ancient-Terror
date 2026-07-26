package sk.sivak.eldritchhorror.core.eventlistener.spell;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.wither.WitherSpell;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.constants.test.UsableAsset;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.wither.WitherSpellBackListener4;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;

import java.util.Collections;
import java.util.List;

public class WitherListener extends AbstractSpellListener<WitherSpell> {

    private CastWitherListener castWitherListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(castWitherListener);
    }

    @Override
    protected void register() {
        castWitherListener = new CastWitherListener();
        getEventQueue().addDirectEventListener(castWitherListener, DirectEvent.REGISTER_USABLE_ASSETS);
    }


    private class CastWitherListener extends EventListenerImpl<TestData> {

        private TestData input;
        private AbstractSpellBackListener spellBackListener;

        @Override
        public void onNotify(TestData eventData) {
            if (!isOwner()) {
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
                askCastWither();
            };
        }

        private void askCastWither() {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setSpellInfo(spellInfo);
            showCardRequest.setTitle("Cast Wither?");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
            showCardRequest.setHideType(HideType.RETURN);
            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(this::answerCastWither);
        }

        private void answerCastWither(ShowCardResponse showCardResponse) {
            if (ShowCardResponse.YES != showCardResponse) {
                ServicePlatform.get().getService().convertTo(TestData.class, () -> input);
                return;
            }
            testLore();
        }

        private void testLore() {
            Single<TestData> test = ServicePlatform.get().getTestService().test(Stat.LORE, 0, 2,
                    new TestFlavorRequest(TestFlavorType.SPELL));
            test.subscribe(this::testLoreResult);
        }

        private void testLoreResult(TestData testResult) {
            ServicePlatform.get().getService().hold();

            spellBackListener = findSpellBackListener();
            spellBackListener.setTestData(testResult);
            spellBackListener.setMainSpellAction(createMainSpellAction());
            spellBackListener.setData("statBonus", 3);
            spellBackListener.executeWhole();
            ServicePlatform.get().getEncounterService().checkDefeatedAfterEncounter();
            ServicePlatform.get().getInvestigatorService().convertTo(TestData.class, () -> {
                System.out.println("HERE");
                return input;
            });
            ServicePlatform.get().getService().release();
        }

        private Runnable createMainSpellAction() {
            return () -> {
                addUsableAsset(input, spellInfo, spellBackListener.getData("statBonus"));
            };
        }

        protected UsableAsset addUsableAsset(TestData testData, SpellInfo spellInfo, int statBonus) {
            UsableAsset usableAsset = new UsableAsset();
            usableAsset.setCardInfo(spellInfo);
            usableAsset.setStatBonus(statBonus);
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
