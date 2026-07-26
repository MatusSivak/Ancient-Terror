package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.ElderSignAsset;
import sk.sivak.eldritchhorror.core.constants.asset.TomeOfHorrorsAsset;
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
import java.util.List;

/**
 * @author msivak
 */
public class TomeOfHorrorsListener extends AbstractAssetListener<TomeOfHorrorsAsset> {

    private static final Logger logger = LogManager.getLogger(TomeOfHorrorsListener.class);
    private ReduceHorrorListener reduceHorrorListener;
    private RegisterUsableAssetListener registerUsableAssetListener;

    @Override
    protected void register() {

        registerUsableAssetListener = new RegisterUsableAssetListener();
        getEventQueue().addDirectEventListener(registerUsableAssetListener, DirectEvent.REGISTER_USABLE_ASSETS);

        reduceHorrorListener = new ReduceHorrorListener();
        getEventQueue().addBeforeEventListener(reduceHorrorListener, BeforeAfterEvent.UPDATE_MONSTER_HORROR);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(registerUsableAssetListener, reduceHorrorListener);
    }

    private class RegisterUsableAssetListener extends AbstractAssetEventListener<TestData> {

        public RegisterUsableAssetListener() {
            super(TomeOfHorrorsListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.getStat() != Stat.WILL) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.COMBAT)) {
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

    private class ReduceHorrorListener extends AbstractAssetEventListener<CombatData> {

        public ReduceHorrorListener() {
            super(TomeOfHorrorsListener.this);
        }

        @Override
        protected Runnable getEventAction(CombatData input) {
            return () -> {
                if (input.getActualHorror() == null) {
                    return;
                }
                if (input.getActualHorror() <= 1) {
                    return;
                }
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setTitle("Reduce Horror by 1.");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);

                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
            };
        }

        private void onAnswer(ShowCardResponse showCardResponse, CombatData input) {
            input.setActualHorror(Math.max(1, input.getActualHorror()-1));
            ServicePlatform.get().getService().convertTo(CombatData.class, () -> input);
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }
}
