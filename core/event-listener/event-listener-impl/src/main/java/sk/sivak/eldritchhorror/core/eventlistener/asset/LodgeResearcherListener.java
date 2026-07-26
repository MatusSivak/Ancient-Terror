package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.LodgeResearcherAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;

import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class LodgeResearcherListener extends AbstractAssetListener<LodgeResearcherAsset> {

    private static final Logger logger = LogManager.getLogger(LodgeResearcherListener.class);
    private AfterDefeatMonsterListener afterDefeatMonsterListener;

    @Override
    protected void register() {
        afterDefeatMonsterListener = new AfterDefeatMonsterListener();
        getEventQueue().addAfterEventListener(afterDefeatMonsterListener, BeforeAfterEvent.DEFEAT_MONSTER);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(afterDefeatMonsterListener);
    }

    private class AfterDefeatMonsterListener extends AbstractAssetEventListener<DefeatMonsterData> {

        public AfterDefeatMonsterListener() {
            super(LodgeResearcherListener.this);
        }

        @Override
        protected Runnable getEventAction(DefeatMonsterData input) {
            return () -> {
                if (!input.isInCombat()) {
                    return;
                }

                ServicePlatform.get().getGameService().showCard(new ShowCardRequestBuilder(getAssetInfo())
                        .withTitle("Recover Sanity and gain Clue")
                        .build())
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
            };
        }

        private void onAnswer(ShowCardResponse response, DefeatMonsterData input) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTokenService().gainSanity(1);
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
