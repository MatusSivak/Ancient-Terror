package sk.sivak.eldritchhorror.core.eventlistener.monster;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.DESTROY_MONSTER_HEALTH;
import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_MONSTER;

public class ZombieHordeMonsterListener extends AbstractMonsterListener {

    private ReckoningListener reckoningListener;
    private AfterDestroyInvestigatorHealth afterDestroyInvestigatorHealth;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        reckoningListener = new ReckoningListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, RECKONING_MONSTER);
        afterDestroyInvestigatorHealth = new AfterDestroyInvestigatorHealth();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(afterDestroyInvestigatorHealth, DESTROY_MONSTER_HEALTH);
    }

    private class ReckoningListener extends AbstractMonsterReckoningListener {

        @Override
        void onNotify() {
            ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
            ServicePlatform.get().getService().moveCameraToLocation(monsterInfo.getCurrentLocation());
            ServicePlatform.get().getMonsterService().highlightReckoningText(monsterInfo);
            RollData rollData = new RollData();
            rollData.setMinSuccessful(3);
            rollData.setMaxFailed(2);
            Single<Integer> singleDieResult = ServicePlatform.get().getTestService().rollDie(rollData);
            singleDieResult.subscribe(this::resolveDiceRoll);
        }

        private void resolveDiceRoll(Integer value) {
            if (value <= 2) {
                ServicePlatform.get().getDoomOmenService().advanceDoom();
            }
        }
    }

    private class AfterDestroyInvestigatorHealth extends EventListenerImpl<CombatData> {

        @Override
        public void onNotify(CombatData eventData) {
            if (eventData.getMonsterInfo() != monsterInfo) {
                return;
            }
            if (eventData.getHealthLost() == 0) {
                return;
            }
            InvestigatorId investigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
            List<? extends CardInfo> allies = EncounterUtils.getPossession(investigatorId, AssetTrait.ALLY);
            if (allies.isEmpty()) {
                return;
            }

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);

            SelectCardData selectCardData = new SelectCardData();
            selectCardData.setTitleText("Select Ally to discard");
            selectCardData.setHideText("Show Allies?");
            selectCardData.setAvailableCards(allies);
            ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(cardInfo -> {
                discardAlly((AssetInfo)cardInfo, eventData);
            });

            ServicePlatform.get().getService().release();
        }

        private void discardAlly(AssetInfo ally, CombatData eventData) {
            InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setAssetInfo(ally);
            showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
            showCardRequest.setTitle("Discard Ally");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
            Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
            displayAsset.subscribe(response -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().discardAssetFromInvestigator(activeInvestigatorId, ally, true);
                ServicePlatform.get().getService().convertTo(CombatData.class, () -> eventData);
                ServicePlatform.get().getService().release();
            });
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }

    @Override
    public void unregister() {

        ServicePlatform.get().getEventQueue().unregisterListener(reckoningListener);
        ServicePlatform.get().getEventQueue().unregisterListener(afterDestroyInvestigatorHealth);
    }
}
