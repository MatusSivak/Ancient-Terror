package sk.sivak.eldritchhorror.core.action.impl.tutorial;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.CollectAvailableActionsData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.DRAW_MYTHOS_CARD;

public class AdjustRolledDicesInCombatAction implements Action<Object, Object> {

    private AfterRollDicesListener afterRollDicesListener;

    public AdjustRolledDicesInCombatAction() {

    }

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            afterRollDicesListener = new AfterRollDicesListener();
            ServicePlatform.get().getEventQueue().addAfterEventListener(afterRollDicesListener, BeforeAfterEvent.ROLL_DICES);
            ServicePlatform.get().getEventQueue().addBeforeEventListener(new BeforeDrawMythosCardListener(), DRAW_MYTHOS_CARD);
            onSub.onSuccess(null);
        });
    }

    private class BeforeDrawMythosCardListener extends EventListenerImpl<Object> {

        @Override
        public void onNotify(Object eventData) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getEventQueue().unregisterListener(afterRollDicesListener);
                ServicePlatform.get().getEventQueue().unregisterListener(this);
            });
        }

        @Override
        public Class<Object> getDataClass() {
            return Object.class;
        }
    }

    private class AfterRollDicesListener extends EventListenerImpl<TestData> {

        private int executedTimes = 0;

        @Override
        public void onNotify(TestData eventData) {
            List<EncounterType> performedEncounters = ServicePlatform.get().getPerformedEncounters().getPerformedEncounters(InvestigatorId.THE_SPY);
            Random rng = new Random();
            if (executedTimes == 0) {
                eventData.setMinScoreToEndTest(0);
                for (int i = 0; i < eventData.getDiceRolls().size(); i++) {
                    eventData.getDiceRolls().get(i).setDiceValue(rng.nextInt(4) + 1);
                }
            } else if (executedTimes == 1) {
                eventData.setMinScoreToEndTest(1);
                for (int i = 0; i < eventData.getDiceRolls().size(); i++) {
                    eventData.getDiceRolls().get(i).setDiceValue(rng.nextInt(4) + 1);
                }
                eventData.getDiceRolls().get(rng.nextInt(eventData.getDiceRolls().size())).setDiceValue(rng.nextInt(2)+5);
            } else if (performedEncounters.contains(EncounterType.OTHER_WORLD) && executedTimes == 2) {
                eventData.setMinScoreToEndTest(1);
                for (int i = 0; i < eventData.getDiceRolls().size(); i++) {
                    eventData.getDiceRolls().get(i).setDiceValue(rng.nextInt(2)+5);
                }
                eventData.getDiceRolls().get(rng.nextInt(eventData.getDiceRolls().size())).setDiceValue(rng.nextInt(4)+1);
            } else if (performedEncounters.contains(EncounterType.OTHER_WORLD) && executedTimes == 3) {
                eventData.setMinScoreToEndTest(0);
                for (int i = 0; i < eventData.getDiceRolls().size(); i++) {
                    eventData.getDiceRolls().get(i).setDiceValue(rng.nextInt(4) + 1);
                }
            } else if (performedEncounters.contains(EncounterType.LOCATION) && executedTimes == 2) {
                eventData.setMinScoreToEndTest(1);
                for (int i = 0; i < eventData.getDiceRolls().size(); i++) {
                    eventData.getDiceRolls().get(i).setDiceValue(rng.nextInt(4) + 1);
                }
                eventData.getDiceRolls().get(rng.nextInt(eventData.getDiceRolls().size())).setDiceValue(rng.nextInt(2)+5);
            } else if (performedEncounters.contains(EncounterType.GENERAL) && executedTimes == 2) {
                eventData.setMinScoreToEndTest(0);
                for (int i = 0; i < eventData.getDiceRolls().size(); i++) {
                    eventData.getDiceRolls().get(i).setDiceValue(rng.nextInt(4) + 1);
                }
            }
            executedTimes++;
        }

        private void asd() {
            System.out.println("ASD");
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    @Override
    public void setInput(Object input) {

    }
}
