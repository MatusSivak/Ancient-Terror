package sk.sivak.eldritchhorror.core.eventlistener.spell;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.fleshward.FleshWardSpell;
import sk.sivak.eldritchhorror.core.constants.spell.wither.WitherSpell;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.constants.test.UsableAsset;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.asset.BandagesListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;
import sk.sivak.eldritchhorror.core.eventtype.data.token.LoseTokenData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FleshWardListener extends AbstractSpellListener<FleshWardSpell> {

    private EnableCardListener enableCardListener;
    private FleshWardListener.BeforeLoseHealthListener beforeLoseHealthListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(beforeLoseHealthListener, enableCardListener);
    }

    @Override
    protected void register() {
        beforeLoseHealthListener = new BeforeLoseHealthListener();
        getEventQueue().addBeforeEventListener(beforeLoseHealthListener, BeforeAfterEvent.LOSE_HEALTH);

        enableCardListener = new EnableCardListener(spellInfo);
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    private class BeforeLoseHealthListener extends EventListenerImpl<LoseTokenData> {

        private AbstractSpellBackListener spellBackListener;

        @Override
        public void onNotify(LoseTokenData eventData) {
            ServicePlatform.get().getService().<LoseTokenData>addEventCommand((input) -> {
                getEventAction(input).run();
            });
        }

        private Runnable getEventAction(LoseTokenData input) {
            return () -> {
                if (!enabled) {
                    return;
                }
                if (input.getAmount() <= 0) {
                    return;
                }

                InvestigatorId targetInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();

                ServicePlatform.get().getService().hold();
                if (spellOwnerId != targetInvestigatorId) {
                    ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(spellOwnerId);
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                }
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setSpellInfo(spellInfo);
                showCardRequest.setTitle("Cast Flesh Ward?");
                showCardRequest.setHideType(HideType.DISABLE_ON_YES);
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);

                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input, targetInvestigatorId));
                ServicePlatform.get().getService().release();

            };
        }

        private void onAnswer(ShowCardResponse showCardResponse, LoseTokenData input, InvestigatorId targetInvestigatorId) {
            if (ShowCardResponse.YES != showCardResponse) {
                ServicePlatform.get().getService().hold();
                if (spellOwnerId != targetInvestigatorId) {
                    ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(targetInvestigatorId);
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                }
                ServicePlatform.get().getService().convertTo(LoseTokenData.class, () -> input);
                ServicePlatform.get().getService().release();
                return;
            }

            ServicePlatform.get().getTestService().test(Stat.LORE, 0, 16,
                    new TestFlavorRequest(TestFlavorType.SPELL)).subscribe(
                            testData -> disableAndFlipCard(testData, input, targetInvestigatorId));
        }

        private void disableAndFlipCard(TestData testData,
                                        LoseTokenData input,
                                        InvestigatorId targetInvestigatorId) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().disableCard(spellInfo);

            spellBackListener = findSpellBackListener();
            spellBackListener.setTestData(testData);
            spellBackListener.setMainSpellAction(createMainSpellAction(input));
            spellBackListener.setData("targetInvestigatorId", targetInvestigatorId);
            spellBackListener.setData("spellOwnerId", spellOwnerId);
            spellBackListener.setData("loseTokenData", input);
            spellBackListener.executeWhole();

            ServicePlatform.get().getService().addEventCommand(in -> {
                if (ServicePlatform.get().getInvestigators().getActiveInvestigatorId() != targetInvestigatorId) {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(targetInvestigatorId);
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                    ServicePlatform.get().getService().release();
                }
            });

            ServicePlatform.get().getService().convertTo(LoseTokenData.class, () -> input);
            ServicePlatform.get().getService().release();
        }

        private Runnable createMainSpellAction(LoseTokenData input) {
            return () -> {
                input.setAmount(Math.max(0, input.getAmount() - 2));
            };
        }

        @Override
        public Class<LoseTokenData> getDataClass() {
            return LoseTokenData.class;
        }
    }

}
