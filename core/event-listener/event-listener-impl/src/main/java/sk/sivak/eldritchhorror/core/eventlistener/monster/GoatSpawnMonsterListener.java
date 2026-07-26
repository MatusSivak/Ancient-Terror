package sk.sivak.eldritchhorror.core.eventlistener.monster;

import java8.features.util.IterableUtils;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.TravelData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;

import java.util.List;

public class GoatSpawnMonsterListener extends AbstractMonsterListener {

    private BeforeDefeatMonsterListener beforeDefeatMonsterListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        beforeDefeatMonsterListener = new BeforeDefeatMonsterListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeDefeatMonsterListener, BeforeAfterEvent.DEFEAT_MONSTER);
    }

    private class BeforeDefeatMonsterListener extends EventListenerImpl<DefeatMonsterData> {

        public BeforeDefeatMonsterListener() {
            super();
        }


        @Override
        public void onNotify(DefeatMonsterData eventData) {
            if (eventData.getMonsterInfo() != monsterInfo) {
                return;
            }
            if (!eventData.isInCombat()) {
                return;
            }
            InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
            boolean hasDarkPact = ServicePlatform.get().getConditionsDeck().hasCondition(activeInvestigatorId, ConditionId.DARK_PACT);
            if (hasDarkPact) {
                return;
            }
            List<MonsterInfo> availableMonsters = ServicePlatform.get().getMonsterCup().getMonsters();
            IterableUtils.removeIf(availableMonsters, m -> m == monsterInfo || m.isEpic());
            if (availableMonsters.size() == 0) {
                return;
            }

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            Question<Boolean> question = new Question<>();
            question.setTitle("Gain a Dark Pact to discard 1 Monster?");
            question.setOptions(Question.Option.noYesOptions);
            question.setPortraitBeforeTitle(monsterInfo);
            ServicePlatform.get().getGameService().ask(question).subscribe(answer -> onAnswer(answer, eventData, availableMonsters));
            ServicePlatform.get().getService().release();
        }

        private void onAnswer(Answer<Boolean, Object> answer, DefeatMonsterData eventData, List<MonsterInfo> availableMonsters) {
            if (!answer.getResponseData()) {
                ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> eventData);
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getGameService().gainCondition(ConditionId.DARK_PACT);
            SelectMonsterData selectMonsterData = new SelectMonsterData();
            selectMonsterData.setAvailableMonsters(availableMonsters);
            ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData).subscribe(selectedMonster -> {
                ServicePlatform.get().getMonsterService().discardMonster(selectedMonster);
            });
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
    }
}
