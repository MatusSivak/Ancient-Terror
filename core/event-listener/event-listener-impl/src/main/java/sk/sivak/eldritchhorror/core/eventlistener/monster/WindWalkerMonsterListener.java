package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
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

public class WindWalkerMonsterListener extends AbstractMonsterListener {


    private BeforeWillTestListener beforeWillTestListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        ((AbstractMonsterInfo) monsterInfo).setToughness(ServicePlatform.get().getModel().getReferenceCard().getPlayers() + 2);
        ((AbstractMonsterInfo) monsterInfo).setCurrentHealth(monsterInfo.getToughness());

        beforeWillTestListener = new BeforeWillTestListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(beforeWillTestListener, DirectEvent.BEFORE_HORROR_TEST);

    }

    @Override
    public void justRegisterListeners(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
        ((AbstractMonsterInfo) monsterInfo).setToughness(ServicePlatform.get().getModel().getReferenceCard().getPlayers() + 2);
        beforeWillTestListener = new BeforeWillTestListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(beforeWillTestListener, DirectEvent.BEFORE_HORROR_TEST);
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(beforeWillTestListener);
    }

    private class BeforeWillTestListener extends EventListenerImpl<CombatData> {
        @Override
        public void onNotify(CombatData eventData) {
            if (eventData.getMonsterInfo() != monsterInfo) {
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            ServicePlatform.get().getTokenService().canSpend(1,0,0,0).subscribe(canSpend -> {
                if (!canSpend.hasEnough()) {
                    onDoesNotHaveClue();
                    return;
                }
                ServicePlatform.get().getService().hold();

                Question<Boolean> question = new Question<>();
                question.setTitle("Spend one Clue?");
                question.setPortraitBeforeTitle(monsterInfo);
                question.setOptions(Question.Option.noYesOptions);
                ServicePlatform.get().getGameService().ask(question).subscribe(answer -> onAnswer(answer, eventData));
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
        }

        private void onAnswer(Answer<Boolean, Object> answer, CombatData eventData) {
            if (!answer.getResponseData()) {
                onDoesNotHaveClue();
                return;
            }
            ServicePlatform.get().getTokenService().spend(1,0,0,0).subscribe(spendData -> {

                if (!spendData.hasEnough()) {
                    onDoesNotHaveClue();
                    return;
                }
                ServicePlatform.get().getService().hold();
                spendData.pay();
                ServicePlatform.get().getService().convertTo(CombatData.class, () -> eventData);
                ServicePlatform.get().getService().release();

            });
        }

        private void onDoesNotHaveClue() {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTokenService().loseHealth(1);
            ServicePlatform.get().getTokenService().loseSanity(1);
            ServicePlatform.get().getService().release();


        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }
}
