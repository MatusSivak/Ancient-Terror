package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.TravelData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ByakheeMonsterListener extends AbstractMonsterListener {

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
            if (!eventData.isInCombat()) {
                return;
            }
            if (eventData.getMonsterInfo() != monsterInfo) {
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            Question<Boolean> question = new Question<>();
            question.setTitle("Lose 1 Sanity and Move 3 spaces?");
            question.setOptions(Question.Option.noYesOptions);
            question.setPortraitBeforeTitle(monsterInfo);
            ServicePlatform.get().getGameService().ask(question).subscribe(answer -> onAnswer(answer, eventData));
            ServicePlatform.get().getService().release();
        }

        private void onAnswer(Answer<Boolean, Object> answer, DefeatMonsterData eventData) {
            if (!answer.getResponseData()) {
                ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> eventData);
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTokenService().loseSanity(1);
            ServicePlatform.get().getBasicActionService().moveSpaces(new TravelData(true), 3);
            ServicePlatform.get().getEncounterService().disableAnotherEncounter(getActiveInvestigatorId());
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
