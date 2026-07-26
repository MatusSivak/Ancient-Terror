package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.LodgeResearcherAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;

import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class GenealogyResearchListener extends AbstractAssetListener<LodgeResearcherAsset> {

    private static final Logger logger = LogManager.getLogger(GenealogyResearchListener.class);
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
            super(GenealogyResearchListener.this);
        }

        @Override
        protected Runnable getEventAction(DefeatMonsterData input) {
            return () -> {
                if (!input.isInCombat()) {
                    return;
                }
                if (input.getMonsterInfo().getToughness() < 2) {
                    return;
                }

                ServicePlatform.get().getGameService().showCard(new ShowCardRequestBuilder(getAssetInfo())
                        .withTitle("Examine the creature's remains?")
                        .withDisplayAssetResponseType(DisplayAssetResponseType.YES_NO)
                        .build())
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
            };
        }

        private void onAnswer(ShowCardResponse response, DefeatMonsterData input) {
            if (response == ShowCardResponse.NO) {
                ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> input);
                return;
            }
            ServicePlatform.get().getTestService().test(Stat.OBSERVATION, 0, 1).subscribe(testData -> {
                if (!testData.isSuccessful()) {
                    ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> input);
                    return;
                }
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getTokenService().gainClueFromPool();
                ServicePlatform.get().getTokenService().gainClueFromPool();
                ServicePlatform.get().getGameService().showCard(new ShowCardRequestBuilder(getAssetInfo())
                        .withTitle("Discard this card")
                        .withDisplayAssetResponseType(DisplayAssetResponseType.OK)
                        .withAssetHideType(HideType.DISCARD_ALWAYS)
                        .build())
                        .subscribe(showCardResponse -> {
                            ServicePlatform.get().getService().hold();
                            ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
                            ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> input);
                            ServicePlatform.get().getService().release();
                        });
                ServicePlatform.get().getService().release();
            });
        }

        @Override
        public Class<DefeatMonsterData> getDataClass() {
            return DefeatMonsterData.class;
        }
    }
}
