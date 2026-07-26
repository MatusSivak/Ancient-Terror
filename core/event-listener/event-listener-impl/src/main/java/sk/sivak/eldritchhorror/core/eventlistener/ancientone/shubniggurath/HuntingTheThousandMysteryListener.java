package sk.sivak.eldritchhorror.core.eventlistener.ancientone.shubniggurath;

import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AbstractMysteryListener;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.DEFEAT_MONSTER;

public class HuntingTheThousandMysteryListener extends AbstractMysteryListener {

    private int progress;
    private AfterDefeatMonsterListener afterDefeatMonsterListener;

    public HuntingTheThousandMysteryListener(MysteryCardInfo mysteryCardInfo) {
        super(mysteryCardInfo);
    }

    @Override
    public void advanceActiveMystery() {
        if (progress >= mysteryCardInfo.getMysteryComplexity()) {
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Can't advance active mystery.");
            ServicePlatform.get().getGameService().ask(question).subscribe();
        } else {
            progress+=2;
            ServicePlatform.get().getGameService().advanceCurrentMysteryCard(2);
        }
    }

    @Override
    public void register() {
        super.register();
        afterDefeatMonsterListener = new AfterDefeatMonsterListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(afterDefeatMonsterListener, DEFEAT_MONSTER);

    }

    private class AfterDefeatMonsterListener extends EventListenerImpl<DefeatMonsterData> {

        @Override
        public void onNotify(DefeatMonsterData eventData) {
            if (eventData == null || eventData.getMonsterInfo() == null || eventData.getMonsterInfo().isEpic()) {
                return;
            }
            if (progress >= mysteryCardInfo.getMysteryComplexity()) {
                return;
            }
            ServicePlatform.get().getTokenService().canSpend(2,0,0,0).subscribe(canSpend -> {
                if (!canSpend.hasEnough()) {
                    ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> eventData);
                    return;
                }
                Question<Boolean> question = new Question<>();
                question.setOptions(Question.Option.noYesOptions);
                question.setTitle("Spend two Clues to advance the Active Mystery" +
                        (eventData.getMonsterInfo().getToughness() == 1 ? "?" : " "+ eventData.getMonsterInfo().getToughness() +" times?"));
                question.displayCurrentMysteryCard();
                ServicePlatform.get().getGameService().ask(question).subscribe(response -> {
                    if (!response.getResponseData()) {
                        ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> eventData);
                        return;
                    }
                    ServicePlatform.get().getTokenService().spend(2,0,0,0).subscribe(spendData -> {
                        if (!spendData.hasEnough()) {
                            ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> eventData);
                            return;
                        }
                        ServicePlatform.get().getService().hold();
                        spendData.pay();
                        progress+=eventData.getMonsterInfo().getToughness();
                        ServicePlatform.get().getGameService().advanceCurrentMysteryCard(eventData.getMonsterInfo().getToughness());
                        ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> eventData);
                        ServicePlatform.get().getService().release();
                    });
                });
            });
        }

        @Override
        public Class<DefeatMonsterData> getDataClass() {
            return DefeatMonsterData.class;
        }
    }

    @Override
    protected List<LocationId> getPinLocations() {
        List<MonsterInfo> monsters = ServicePlatform.get().getMonsterCup().getMonsters();
        Set<LocationId> monsterLocations = new HashSet<>();

        for (MonsterInfo monster : monsters) {
            if (monster.isEpic()) {
                continue;
            }
            monsterLocations.add(monster.getCurrentLocation());
        }

        return new LinkedList<>(monsterLocations);
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(afterDefeatMonsterListener);
    }

    @Override
    public void justRegisterListeners(int progress) {
        afterDefeatMonsterListener = new AfterDefeatMonsterListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(afterDefeatMonsterListener, DEFEAT_MONSTER);
        this.progress = progress;
    }

    @Override
    public void justAddRedPins() {

    }

    @Override
    protected int getProgress() {
        return progress;
    }
}
