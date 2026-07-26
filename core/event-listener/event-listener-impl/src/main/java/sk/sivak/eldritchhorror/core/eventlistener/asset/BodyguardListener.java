package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.BodyguardAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
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
public class BodyguardListener extends AbstractAssetListener<BodyguardAsset> {

    private static final Logger logger = LogManager.getLogger(BodyguardListener.class);
    private RegisterUsableAssetListener registerUsableAssetListener;
    private ReduceDamageListener reduceDamageListener;

    @Override
    protected void register() {
        registerUsableAssetListener = new RegisterUsableAssetListener();
        getEventQueue().addDirectEventListener(registerUsableAssetListener, DirectEvent.REGISTER_USABLE_ASSETS);
        reduceDamageListener = new ReduceDamageListener();
        getEventQueue().addBeforeEventListener(reduceDamageListener, BeforeAfterEvent.UPDATE_MONSTER_DAMAGE);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(registerUsableAssetListener, reduceDamageListener);
    }

    private class RegisterUsableAssetListener extends AbstractAssetEventListener<TestData> {

        public RegisterUsableAssetListener() {
            super(BodyguardListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.getStat() != Stat.STRENGTH) {
                    return;
                }
                addUsableAsset(input, getAssetInfo(), 1);
            };
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    private class ReduceDamageListener extends AbstractAssetEventListener<CombatData> {

        public ReduceDamageListener() {
            super(BodyguardListener.this);
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
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
            };
        }

        private void onAnswer(ShowCardResponse showCardResponse, CombatData input) {
            input.setActualDamage(Math.max(1, input.getActualDamage()-1));
            ServicePlatform.get().getService().convertTo(CombatData.class, () -> input);
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }
}
