package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.TearGasAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Collections;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.data.test.CountRerollDiceData.CountRerollDiceType.OTHER;

/**
 * @author msivak
 */
public class TearGasListener extends AbstractAssetListener<TearGasAsset> {

    private static final Logger logger = LogManager.getLogger(TearGasListener.class);
    private RerollUsingAssetsListener rerollUsingAssetsListener;

    @Override
    protected void register() {
        rerollUsingAssetsListener = new RerollUsingAssetsListener();
        getEventQueue().addDirectEventListener(rerollUsingAssetsListener, DirectEvent.REROLL_USING_ASSETS_3);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(rerollUsingAssetsListener);
    }

    private class RerollUsingAssetsListener extends AbstractAssetEventListener<TestData> {

        public RerollUsingAssetsListener() {
            super(TearGasListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.getStat() != Stat.STRENGTH) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.COMBAT)) {
                    return;
                }
                if (input.getCalculatedDicePool() <= input.getScore()) {
                    ShowCardRequest showCardRequest = new ShowCardRequest();
                    showCardRequest.setAssetInfo(getAssetInfo());
                    showCardRequest.setHideType(HideType.DISCARD_ON_YES);
                    showCardRequest.setTitle("Reduce Monster's damage?");
                    showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);

                    ServicePlatform.get().getGameService().showCard(showCardRequest)
                            .subscribe(showCardResponse -> onAnswerJustReduce(showCardResponse, input));
                    return;
                }
                if (input.getCalculatedDicePool() - input.getSuccessRolls() == 1) {
                    ShowCardRequest showCardRequest = new ShowCardRequest();
                    showCardRequest.setAssetInfo(getAssetInfo());
                    showCardRequest.setHideType(HideType.DISCARD_ON_YES);
                    showCardRequest.setTitle("Reroll die and reduce Monster's damage?");
                    showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);

                    ServicePlatform.get().getGameService().showCard(showCardRequest)
                            .subscribe(showCardResponse -> onAnswerRerollOne(showCardResponse, input));
                    return;
                }

                if (input.getCalculatedDicePool() - input.getSuccessRolls() >= 2) {
                    ShowCardRequest showCardRequest = new ShowCardRequest();
                    showCardRequest.setAssetInfo(getAssetInfo());
                    showCardRequest.setHideType(HideType.DISCARD_ON_YES);
                    showCardRequest.setTitle("Reroll dice and reduce Monster's damage?");
                    showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                    ServicePlatform.get().getGameService().showCard(showCardRequest)
                            .subscribe(showCardResponse -> onAnswerRerollTwo(showCardResponse, input));
                }
            };
        }

        private void onAnswerJustReduce(ShowCardResponse showCardResponse, TestData input) {
            if (ShowCardResponse.YES == showCardResponse) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
                reduceMonsterDamage();
                ServicePlatform.get().getService().convertTo(TestData.class, () -> input);
                ServicePlatform.get().getService().release();
            } else {
                ServicePlatform.get().getService().convertFromTo(ShowCardResponse.class, TestData.class, response -> input);
            }
        }

        private void onAnswerRerollOne(ShowCardResponse showCardResponse, TestData input) {
            if (ShowCardResponse.YES == showCardResponse) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
                reduceMonsterDamage();
                ServicePlatform.get().getTestService().rerollDie(input);
                ServicePlatform.get().getService().release();
            } else {
                ServicePlatform.get().getService().convertFromTo(ShowCardResponse.class, TestData.class, response -> input);
            }
        }

        private void onAnswerRerollTwo(ShowCardResponse showCardResponse, TestData input) {
            if (ShowCardResponse.YES == showCardResponse) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
                reduceMonsterDamage();
                ServicePlatform.get().getTestService().findCountRerollDice(2, OTHER, input);
                ServicePlatform.get().getTestService().rerollDice();
                ServicePlatform.get().getService().release();
            } else {
                ServicePlatform.get().getService().convertFromTo(ShowCardResponse.class, TestData.class, response -> input);
            }
        }

        private void reduceMonsterDamage() {
            CombatData combatData = ServicePlatform.get().getTestFlavor().getFlavorData();
            combatData.setActualDamage(Math.max(1, combatData.getActualDamage()-1));
            ServicePlatform.get().getTestService().updateMonsterDamage(combatData);
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }
}
