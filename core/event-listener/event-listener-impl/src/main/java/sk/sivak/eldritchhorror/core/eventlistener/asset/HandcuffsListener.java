package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.functions.Action1;
import sk.sivak.eldritchhorror.core.constants.asset.HandcuffsAsset;
import sk.sivak.eldritchhorror.core.constants.asset.PersonalAssistantAsset;
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
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class HandcuffsListener extends AbstractAssetListener<HandcuffsAsset> {

    private static final Logger logger = LogManager.getLogger(HandcuffsListener.class);
    private DefeatWeakMonsterListener defeatWeakMonsterListener;

    @Override
    protected void register() {
        defeatWeakMonsterListener = new DefeatWeakMonsterListener();
        getEventQueue().addDirectEventListener(defeatWeakMonsterListener, DirectEvent.BEFORE_DAMAGE_TEST);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(defeatWeakMonsterListener);
    }

    private class DefeatWeakMonsterListener extends AbstractAssetEventListener<CombatData> {

        DefeatWeakMonsterListener() {
            super(HandcuffsListener.this);
        }

        @Override
        protected Runnable getEventAction(CombatData input) {
            return () -> {
                if (input.getMonsterInfo().getToughness() > 2) {
                    return;
                }
                if (input.getMonsterInfo().isEpic()) {
                    return;
                }
                if (input.getDamageTestType() != Stat.STRENGTH) {
                    return;
                }
                ServicePlatform.get().getTokenService().canSpend(0,1,0,0)
                        .subscribe(spendData -> onCanSpend(spendData, input));
            };
        }

        private void onCanSpend(SpendData spendData, CombatData input) {
            if (spendData.hasEnough()) {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setTitle("Spend 1 Focus to defeat Monster?");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);

                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
            } else {
                ServicePlatform.get().getService().convertTo(CombatData.class, () -> input);
            }
        }

        private void onAnswer(ShowCardResponse showCardResponse, CombatData input) {
            if (showCardResponse == ShowCardResponse.YES) {
                ServicePlatform.get().getTokenService().spend(0,1,0,0)
                        .subscribe(spendData -> onSpend(spendData, input));
            } else {
                ServicePlatform.get().getService().convertTo(CombatData.class, () -> input);
            }
        }

        private void onSpend(SpendData spendData, CombatData input) {
            if (spendData.hasEnough()) {
                ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.AFTER_DAMAGE_CHECK, input);

                ServicePlatform.get().getService().startInsertingAfterCommand(BeforeAfterEvent.HIDE_COMBAT_TABLE);
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().hideBackground();
                spendData.pay();
                ServicePlatform.get().getMonsterService().defeatMonster(input.getMonsterInfo(), true, false);
                ServicePlatform.get().getService().convertTo(Void.class, () -> null);
                ServicePlatform.get().getService().skipBeforeEvent(BeforeAfterEvent.END_OF_COMBAT_EVENT, null);
                ServicePlatform.get().getService().release();
                ServicePlatform.get().getService().endInsertingAtCommand();
            } else {
                ServicePlatform.get().getService().convertTo(CombatData.class, () -> input);
            }
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }
}
