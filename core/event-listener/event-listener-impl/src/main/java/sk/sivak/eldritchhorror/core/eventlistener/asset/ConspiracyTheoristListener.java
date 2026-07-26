package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.ConspiracyTheoristAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.card.AbstractCardReckoningListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class ConspiracyTheoristListener extends AbstractAssetListener<ConspiracyTheoristAsset> {

    private static final Logger logger = LogManager.getLogger(ConspiracyTheoristListener.class);
    private RegisterUsableAssetListener registerUsableAssetListener;
    private ReckoningListener reckoningListener;

    @Override
    protected void register() {
        registerUsableAssetListener = new RegisterUsableAssetListener();
        getEventQueue().addDirectEventListener(registerUsableAssetListener, DirectEvent.REGISTER_USABLE_ASSETS);

        reckoningListener = new ReckoningListener();
        getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_CARDS_2);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(registerUsableAssetListener, reckoningListener);
    }

    private class RegisterUsableAssetListener extends AbstractAssetEventListener<TestData> {

        public RegisterUsableAssetListener() {
            super(ConspiracyTheoristListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.getStat() != Stat.OBSERVATION) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.RESEARCH)) {
                    return;
                }
                addUsableAsset(input, getAssetInfo(), 2);
            };
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    private class ReckoningListener extends AbstractCardReckoningListener {

        @Override
        protected InvestigatorId getCardOwnerId() {
            return investigatorId;
        }

        @Override
        protected void onNotify() {
            ShowCardRequest showCardRequest = new ShowCardRequestBuilder(getAssetInfo())
                    .withTitle("Roll 1 die. On a 4, 5 or 6, gain 1 Clue.")
                    .withDisplayAssetResponseType(DisplayAssetResponseType.OK)
                    .build();
            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(ok -> onConfirm());
        }

        private void onConfirm() {
            RollData rollData = new RollData();
            rollData.setMinSuccessful(4);
            rollData.setMaxFailed(0);
            Single<Integer> singleDieResult = ServicePlatform.get().getTestService().rollDie(rollData);
            singleDieResult.subscribe(this::resolveDiceRoll);
        }

        private void resolveDiceRoll(Integer rolledValue) {
            if (rolledValue >= 4) {
                ServicePlatform.get().getTokenService().gainClueFromPool();
            }
        }
    }
}
