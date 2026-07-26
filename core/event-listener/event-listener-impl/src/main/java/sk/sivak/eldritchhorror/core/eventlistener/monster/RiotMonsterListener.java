package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import static sk.sivak.eldritchhorror.core.eventtype.data.test.TestData.JUST_ONE;

public class RiotMonsterListener extends AbstractMonsterListener {

    private BeforeDamageTestListener beforeDamageTestListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        beforeDamageTestListener = new BeforeDamageTestListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(beforeDamageTestListener, DirectEvent.BEFORE_DAMAGE_TEST);
    }

    private class BeforeDamageTestListener extends EventListenerImpl<CombatData> {
        @Override
        public void onNotify(CombatData eventData) {
            if (eventData.getMonsterInfo() != monsterInfo) {
                return;
            }
            Question<Boolean> question = new Question<>();
            question.setTitle("Test Influence to disperse the mob?");
            question.setPortraitBeforeTitle(monsterInfo);
            question.setOptions(Question.Option.noYesOptions);
            ServicePlatform.get().getGameService().ask(question).subscribe(answer -> onAnswer(answer, eventData));
        }

        private void onAnswer(Answer<Boolean, Object> answer, CombatData eventData) {
            if (!answer.getResponseData()) {
                ServicePlatform.get().getService().convertTo(CombatData.class, () -> eventData);
                return;
            }
            ServicePlatform.get().getTestService().test(Stat.INFLUENCE, -1, JUST_ONE)
                    .subscribe(testResult -> withTestResult(testResult, eventData));
        }

        private void withTestResult(TestData testResult, CombatData eventData) {
            if (testResult.isSuccessful()) {
                ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.AFTER_DAMAGE_CHECK, eventData);

                ServicePlatform.get().getService().startInsertingAfterCommand(BeforeAfterEvent.HIDE_COMBAT_TABLE);
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().hideBackground();
                ServicePlatform.get().getMonsterService().defeatMonster(monsterInfo, true, false);
                ServicePlatform.get().getService().convertTo(Void.class, () -> null);
                ServicePlatform.get().getService().skipBeforeEvent(BeforeAfterEvent.END_OF_COMBAT_EVENT, null);
                ServicePlatform.get().getService().release();
                ServicePlatform.get().getService().endInsertingAtCommand();
            } else {
                ServicePlatform.get().getService().convertTo(CombatData.class, () -> eventData);
            }
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(beforeDamageTestListener);
    }
}
