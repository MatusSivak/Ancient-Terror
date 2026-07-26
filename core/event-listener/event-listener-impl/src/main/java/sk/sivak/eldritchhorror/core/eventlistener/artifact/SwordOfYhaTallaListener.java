package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.artifact.SwordOfYhaTallaArtifact;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Arrays;
import java.util.List;

public class SwordOfYhaTallaListener extends AbstractArtifactListener<SwordOfYhaTallaArtifact> {

    private static final Logger logger = LogManager.getLogger(SwordOfYhaTallaListener.class);
    private RegisterUsableAssetListener registerUsableAssetListener;

    private AfterDefeatMonsterListener afterDefeatMonsterListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(registerUsableAssetListener, afterDefeatMonsterListener);
    }

    @Override
    protected void register() {
        registerUsableAssetListener = new RegisterUsableAssetListener();
        getEventQueue().addDirectEventListener(registerUsableAssetListener, DirectEvent.REGISTER_USABLE_ASSETS);

        afterDefeatMonsterListener = new AfterDefeatMonsterListener();
        getEventQueue().addAfterEventListener(afterDefeatMonsterListener, BeforeAfterEvent.DEFEAT_MONSTER);
    }

    private class RegisterUsableAssetListener extends AbstractArtifactEventListener<TestData> {

        public RegisterUsableAssetListener() {
            super(SwordOfYhaTallaListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.getStat() != Stat.STRENGTH && input.getStat() != Stat.WILL) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.COMBAT)) {
                    return;
                }
                if (input.getStat() == Stat.STRENGTH) {
                    addUsableArtifact(input, getArtifactInfo(), 3);
                } else if (input.getStat() == Stat.WILL) {
                    addUsableArtifact(input, getArtifactInfo(), 2);
                }
            };
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    private class AfterDefeatMonsterListener extends AbstractArtifactEventListener<DefeatMonsterData> {

        AfterDefeatMonsterListener() {
            super(SwordOfYhaTallaListener.this);
        }

        @Override
        protected Runnable getEventAction(DefeatMonsterData input) {
            return () -> {
                if (!input.isInCombat()) {
                    return;
                }
                ServicePlatform.get().getGameService().showCard(new ShowCardRequestBuilder(getArtifactInfo())
                        .withTitle("Gain 1 Clue.")
                        .withAssetHideType(HideType.RETURN)
                        .withDisplayAssetResponseType(DisplayAssetResponseType.OK)
                        .build())
                        .subscribe(showCardResponse -> onAnswer(input));
            };
        }

        private void onAnswer(DefeatMonsterData input) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTokenService().gainClueFromPool();
            ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> input);
            ServicePlatform.get().getService().release();
        }


        @Override
        public Class<DefeatMonsterData> getDataClass() {
            return DefeatMonsterData.class;
        }
    }
}
