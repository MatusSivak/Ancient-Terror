package sk.sivak.eldritchhorror.core.eventlistener.ancientone.shubniggurath;

import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AbstractMysteryListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.MysteryEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Collections;

public class HourOfTheMoonLensMysteryListener extends AbstractMysteryListener implements EventListener<AvailableEncounters> {

    private AvailableEncounters availableEncounters;
    private EncounterActiveMysteryListener encounterActiveMysteryListener;

    public HourOfTheMoonLensMysteryListener(MysteryCardInfo mysteryCardInfo) {
        super(mysteryCardInfo);
    }

    private int progress;

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public void register() {
        ServicePlatform.get().getGameService().hold();
        ServicePlatform.get().getGameService().showCurrentMysteryCard(true, false);
        ServicePlatform.get().getGameService().spawnRedPins();
        ServicePlatform.get().getEventQueue().addDirectEventListener(this, DirectEvent.COLLECT_COMMON_ENCOUNTERS);
        encounterActiveMysteryListener = new EncounterActiveMysteryListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(encounterActiveMysteryListener, DirectEvent.ENCOUNTER_ACTIVE_MYSTERY);
        ServicePlatform.get().getGameService().release();
    }

    @Override
    public void justRegisterListeners(int progress) {
        encounterActiveMysteryListener = new EncounterActiveMysteryListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(this, DirectEvent.COLLECT_COMMON_ENCOUNTERS);
        ServicePlatform.get().getEventQueue().addDirectEventListener(encounterActiveMysteryListener, DirectEvent.ENCOUNTER_ACTIVE_MYSTERY);
        this.progress = progress;
    }

    @Override
    public void justAddRedPins() {
        ServicePlatform.get().getGameService().justAddRedPins();
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(this);
        ServicePlatform.get().getEventQueue().unregisterListener(encounterActiveMysteryListener);
        ServicePlatform.get().getGameService().clearRedPins();
    }

    private class EncounterActiveMysteryListener extends EventListenerImpl<MysteryEncounter> {

        private MysteryEncounter eventData;

        @Override
        public void onNotify(MysteryEncounter eventData) {
            if (progress >= mysteryCardInfo.getMysteryComplexity()) {
                return;
            }
            this.eventData = eventData;
            Question<Boolean> question = new Question<>();
            question.setOptions(Collections.singletonList(new Question.Option<>("OK", Boolean.TRUE)));
            question.setTitle("Watch a ritual that transforms a worshipers?");
            question.displayCurrentMysteryCard();
            ServicePlatform.get().getGameService().ask(question).subscribe(this::onAnswer);
        }

        private void onAnswer(Answer<Boolean, Object> answer) {
            ServicePlatform.get().getMonsterService().ambush(NonEpicMonsterId.GOAT_SPAWN).subscribe(combatData -> {
                if (combatData.getMonsterInfo().isAlive()) {
                    ServicePlatform.get().getService().convertTo(MysteryEncounter.class, () -> eventData);
                    return;
                }
                onSuccessfulCombat();
            });
        }

        private void onSuccessfulCombat() {
            Question<Object> question = new Question<>();
            question.setOptions(Collections.singletonList(new Question.Option<>("OK", Boolean.TRUE)));
            question.setTitle("You witnessed a transformation to a Goat Spawn.");
            ServicePlatform.get().getGameService().ask(question).subscribe(we -> {
                ServicePlatform.get().getTokenService().spend(2, 0, 0, 0).subscribe(spendData -> {
                    if (!spendData.hasEnough()) {
                        Question<Object> question2 = new Question<>();
                        question2.setOptions(Collections.singletonList(new Question.Option<>("OK", Boolean.TRUE)));
                        question2.setTitle("Not enough Clues.");
                        ServicePlatform.get().getGameService().ask(question2).subscribe(answer2 -> {
                            ServicePlatform.get().getService().convertTo(MysteryEncounter.class, () -> eventData);
                        });
                    } else {
                        ServicePlatform.get().getService().hold();
                        spendData.pay();
                        progress++;
                        ServicePlatform.get().getGameService().advanceCurrentMysteryCard(1);
                        ServicePlatform.get().getService().convertTo(MysteryEncounter.class, () -> eventData);
                        ServicePlatform.get().getService().release();
                    }
                });
            });

        }

        @Override
        public Class<MysteryEncounter> getDataClass() {
            return MysteryEncounter.class;
        }
    }

    @Override
    public void onNotify(AvailableEncounters eventData) {
        this.availableEncounters = eventData;
        InvestigatorRead activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        if (!mysteryCardInfo.getPinLocations().contains(activeInvestigator.getCurrentLocationId())) {
            return;
        }
        if (progress >= mysteryCardInfo.getMysteryComplexity()) {
            return;
        }
        ServicePlatform.get().getTokenService().canSpend(2, 0, 0, 0).subscribe(this::onCanSpend);
    }

    private void onCanSpend(SpendData spendData) {
        MysteryEncounter encounter = new MysteryEncounter(mysteryCardInfo.getName());
        availableEncounters.addEncounter(encounter);
        if (!spendData.hasEnough()) {
            encounter.disable("Not enough Clues");
        }
        ServicePlatform.get().getService().convertTo(AvailableEncounters.class, () -> availableEncounters);
    }

    @Override
    public Class<AvailableEncounters> getDataClass() {
        return AvailableEncounters.class;
    }

    @Override
    public void advanceActiveMystery() {
        if (progress >= mysteryCardInfo.getMysteryComplexity()) {
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Can't advance active mystery.");
            ServicePlatform.get().getGameService().ask(question).subscribe();
        } else {
            progress++;
            ServicePlatform.get().getGameService().advanceCurrentMysteryCard(1);
        }
    }
}
