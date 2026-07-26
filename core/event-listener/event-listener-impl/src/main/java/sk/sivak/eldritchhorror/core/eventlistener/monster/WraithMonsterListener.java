package sk.sivak.eldritchhorror.core.eventlistener.monster;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.CONFIRM_TEST;

public class WraithMonsterListener extends AbstractMonsterListener {


    private BeforeDefeatMonsterListener beforeDefeatMonsterListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        if (ServicePlatform.get().getInvestigators().getLeadInvestigator() == null) {
            return;
        }
        ServicePlatform.get().getService().addEventCommand( in -> {
            InvestigatorRead leadInvestigator = ServicePlatform.get().getInvestigators().getLeadInvestigator();
            InvestigatorRead activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpawnText(monsterInfo);
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(leadInvestigator.getInfo().getInvestigatorId());
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            ServicePlatform.get().getGameService().gainCondition(ConditionId.CURSED);
            if (activeInvestigator == null) {
                ServicePlatform.get().getInvestigatorService().unsetActiveInvestigator();
            } else {
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(activeInvestigator.getInfo().getInvestigatorId());
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            }
            ServicePlatform.get().getService().convertTo(Object.class, () -> in);
            ServicePlatform.get().getService().release();
        });


        beforeDefeatMonsterListener = new BeforeDefeatMonsterListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeDefeatMonsterListener, BeforeAfterEvent.DEFEAT_MONSTER);
    }

    @Override
    public void justRegisterListeners(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
        beforeDefeatMonsterListener = new BeforeDefeatMonsterListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeDefeatMonsterListener, BeforeAfterEvent.DEFEAT_MONSTER);
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(beforeDefeatMonsterListener);
    }

    private class BeforeDefeatMonsterListener extends EventListenerImpl<DefeatMonsterData> {

        @Override
        public void onNotify(DefeatMonsterData eventData) {
            if (eventData.getMonsterInfo() != monsterInfo) {
                return;
            }

            List<InvestigatorId> cursedInvestigators = new LinkedList<>();
            for (InvestigatorRead onBoardInvestigator : ServicePlatform.get().getInvestigators().getOnBoardInvestigators()) {
                InvestigatorId investigatorId = onBoardInvestigator.getInfo().getInvestigatorId();
                boolean isCursed = ServicePlatform.get().getConditionsDeck().hasCondition(investigatorId, ConditionId.CURSED);
                if (isCursed) {
                    cursedInvestigators.add(investigatorId);
                }
            }

            if (cursedInvestigators.isEmpty()) {
                return;
            }

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            Question<Boolean> question = new Question<>();
            question.setTitle("Discard a Cursed Condition?");
            Question.SelectComponentsTableData<InvestigatorId> selectComponentsTableData = new Question.SelectComponentsTableData<>();
            selectComponentsTableData.setType(Question.SelectComponentsTableType.INVESTIGATORS);
            selectComponentsTableData.setAvailableKeys(cursedInvestigators);
            question.setSelectComponentsTableData(selectComponentsTableData);
            question.setPortraitBeforeTitle(monsterInfo);
            question.setOptions(Question.Option.noYesOptions);
            ServicePlatform.get().getGameService().ask(question).subscribe(answer -> onAnswer(answer, eventData, cursedInvestigators));
            ServicePlatform.get().getService().release();
        }

        private void onAnswer(Answer<Boolean, Object> answer, DefeatMonsterData eventData, List<InvestigatorId> cursedInvestigators) {
            if (!answer.getResponseData())  {
                ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> eventData);
                return;
            }
            if (answer.getAdditionalData() != null) {
                InvestigatorId selectedInvestigatorId = (InvestigatorId) answer.getAdditionalData();
                ConditionInfo cursedCondition = ServicePlatform.get().getConditionsDeck().getCondition(selectedInvestigatorId, ConditionId.CURSED);
                discardCursedConditionShow(cursedCondition, selectedInvestigatorId, eventData);
            } else {
                InvestigatorRestriction investigatorRestriction = new InvestigatorRestriction(true);
                investigatorRestriction.addAllowedInvestigators(cursedInvestigators);
                ServicePlatform.get().getInvestigatorService().selectInvestigator(investigatorRestriction).subscribe(cursedInvestigator -> {
                    ServicePlatform.get().getService().hold();
                    ConditionInfo cursedCondition = ServicePlatform.get().getConditionsDeck().getCondition(cursedInvestigator, ConditionId.CURSED);
                    discardCursedConditionShow(cursedCondition, cursedInvestigator, eventData);
                    ServicePlatform.get().getService().release();
                });
            }
        }

        private void discardCursedConditionShow(ConditionInfo cursedCondition, InvestigatorId selectedInvestigator, DefeatMonsterData eventData) {
            InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
            if (activeInvestigatorId == selectedInvestigator) {
                ShowCardRequest showCardRequest = createShowCardRequest(cursedCondition);
                Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
                displayAsset.subscribe(response -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getService().discardConditionFromInvestigator(activeInvestigatorId, cursedCondition);
                    ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> eventData);
                    ServicePlatform.get().getService().release();
                });
            } else {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(selectedInvestigator);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                ShowCardRequest showCardRequest = createShowCardRequest(cursedCondition);
                Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
                displayAsset.subscribe(response -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getService().discardConditionFromInvestigator(selectedInvestigator, cursedCondition);
                    ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(activeInvestigatorId);
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                    ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> eventData);
                    ServicePlatform.get().getService().release();
                });
                ServicePlatform.get().getService().release();
            }
        }

        private ShowCardRequest createShowCardRequest(ConditionInfo cursedCondition) {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setConditionInfo(cursedCondition);
            showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
            showCardRequest.setTitle("Discard Cursed Condition");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
            return showCardRequest;
        }


        @Override
        public Class<DefeatMonsterData> getDataClass() {
            return DefeatMonsterData.class;
        }
    }

}
