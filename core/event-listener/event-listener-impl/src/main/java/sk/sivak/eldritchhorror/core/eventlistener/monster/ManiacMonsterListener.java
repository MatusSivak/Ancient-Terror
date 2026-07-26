package sk.sivak.eldritchhorror.core.eventlistener.monster;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
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
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.DEFEAT_MONSTER;
import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.DESTROY_INVESTIGATOR_HEALTH;

public class ManiacMonsterListener extends AbstractMonsterListener {

    private BeforeDefeatMonsterListener beforeDefeatMonsterListener;
    private BeforeDestroyHealthListener beforeDestroyHealthListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        beforeDefeatMonsterListener = new BeforeDefeatMonsterListener();
        beforeDestroyHealthListener = new BeforeDestroyHealthListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeDestroyHealthListener, DESTROY_INVESTIGATOR_HEALTH);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeDefeatMonsterListener, DEFEAT_MONSTER);

    }

    private class BeforeDestroyHealthListener extends EventListenerImpl<CombatData> {

        @Override
        public void onNotify(CombatData eventData) {
            if (eventData.getDamageTestResult().getScore() != 0) {
                return;
            }
            if (eventData.getMonsterInfo() != monsterInfo) {
                return;
            }
            InvestigatorId investigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
            List<? extends CardInfo> allies = EncounterUtils.getPossession(investigatorId, AssetTrait.ALLY);
            if (allies.isEmpty()) {
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);

            Question<Boolean> question = new Question<>();
            question.setTitle("Discard Ally instead of losing Health?");
            question.setOptions(Question.Option.noYesOptions);
            question.setPortraitBeforeTitle(monsterInfo);
            Question.SelectComponentsTableData<CardInfo> selectComponentsTableData = new Question.SelectComponentsTableData<>();
            selectComponentsTableData.setType(Question.SelectComponentsTableType.CARDS);
            selectComponentsTableData.setAvailableKeys(allies);
            question.setSelectComponentsTableData(selectComponentsTableData);
            ServicePlatform.get().getGameService().ask(question).subscribe(answer -> onAnswer(answer, eventData, allies));
            ServicePlatform.get().getService().release();
        }

        private void onAnswer(Answer<Boolean, Object> answer, CombatData eventData, List<? extends CardInfo> allies) {
            if (!answer.getResponseData()) {
                ServicePlatform.get().getService().convertTo(CombatData.class, () -> eventData);
                return;
            }
            AssetInfo additionalData = (AssetInfo)answer.getAdditionalData();
            if (additionalData == null) {
                SelectCardData selectCardData = new SelectCardData();
                selectCardData.setTitleText("Select Ally to discard");
                selectCardData.setHideText("Show Allies?");
                selectCardData.setAvailableCards(allies);
                ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(cardInfo -> {
                    discardAlly((AssetInfo)cardInfo, eventData);
                });
            } else {
                discardAlly(additionalData, eventData);
            }
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
                ServicePlatform.get().getService().discardAssetFromInvestigator(activeInvestigatorId, ally, false);
                eventData.setHealthLost(0);
                ServicePlatform.get().getService().convertTo(CombatData.class, () -> eventData);
                ServicePlatform.get().getService().release();
            });
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }

    private class BeforeDefeatMonsterListener extends EventListenerImpl<DefeatMonsterData> {

        public BeforeDefeatMonsterListener() {
            super();
        }


        @Override
        public void onNotify(DefeatMonsterData eventData) {
            if (!eventData.isInCombat()) {
                return;
            }
            if (eventData.getMonsterInfo() != monsterInfo) {
                return;
            }
            AssetInfo axe = ServicePlatform.get().getAssetDeck().find(AssetId.AXE);
            if (axe == null) {
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            Question<Boolean> question = new Question<>();
            question.setTitle("Gain Axe?");
            question.setOptions(Question.Option.noYesOptions);
            question.setPortraitBeforeTitle(monsterInfo);
            ServicePlatform.get().getGameService().ask(question).subscribe(answer -> onAnswer(answer, eventData, axe));
            ServicePlatform.get().getService().release();
        }

        private void onAnswer(Answer<Boolean, Object> answer, DefeatMonsterData eventData, AssetInfo axe) {
            if (!answer.getResponseData()) {
                ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> eventData);
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getGameService().gainAsset(axe);
            ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<DefeatMonsterData> getDataClass() {
            return DefeatMonsterData.class;
        }
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(beforeDefeatMonsterListener);
        ServicePlatform.get().getEventQueue().unregisterListener(beforeDestroyHealthListener);
    }
}
