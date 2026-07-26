package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.FishingNetAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class FishingNetListener extends AbstractAssetListener<FishingNetAsset> {

    private static final Logger logger = LogManager.getLogger(FishingNetListener.class);
    private RerollUsingAssetsListener rerollUsingAssetsListener;
    private ReduceDamageListener reduceDamageListener;

    @Override
    protected void register() {
        rerollUsingAssetsListener = new RerollUsingAssetsListener();
        getEventQueue().addDirectEventListener(rerollUsingAssetsListener, DirectEvent.REROLL_USING_ASSETS_1);
        reduceDamageListener = new ReduceDamageListener();
        getEventQueue().addBeforeEventListener(reduceDamageListener, BeforeAfterEvent.UPDATE_MONSTER_DAMAGE);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(rerollUsingAssetsListener, reduceDamageListener);
    }

    private class RerollUsingAssetsListener extends AbstractAssetEventListener<TestData> {

        public RerollUsingAssetsListener() {
            super(FishingNetListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.hasReachedMinScoreToEndTest()) {
                    return;
                }
                if (input.getStat() != Stat.STRENGTH) {
                    return;
                }
                if (input.getCalculatedDicePool() <= input.getScore()) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.COMBAT)) {
                    return;
                }
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setTitle("Reroll 1 die?");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);

                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
            };
        }

        private void onAnswer(ShowCardResponse showCardResponse, TestData input) {
            if (ShowCardResponse.YES == showCardResponse) {
                ServicePlatform.get().getTestService().rerollDie(input);
            } else {
                ServicePlatform.get().getService().convertFromTo(ShowCardResponse.class, TestData.class, response -> input);
            }

        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    private class ReduceDamageListener extends AbstractAssetEventListener<CombatData> {

        public ReduceDamageListener() {
            super(FishingNetListener.this);
        }

        @Override
        protected Runnable getEventAction(CombatData input) {
            return () -> {
                if (input.getActualDamage() == null) {
                    return;
                }
                if (input.getActualDamage() <= 1) {
                    return;
                }
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setTitle("Reduce Damage by 1.");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);

                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(showCardResponse -> onAnswer(input));
            };
        }

        private void onAnswer(CombatData input) {
            input.setActualDamage(Math.max(1, input.getActualDamage()-1));
            ServicePlatform.get().getService().convertTo(CombatData.class, () -> input);
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }
}
