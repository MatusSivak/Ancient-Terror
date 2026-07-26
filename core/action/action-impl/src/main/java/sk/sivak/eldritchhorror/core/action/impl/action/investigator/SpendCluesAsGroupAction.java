package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import java8.features.util.IterableUtils;
import java8.features.util.MapUtils;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SpendCluesAsGroupAction extends AbstractHookableAction<Integer, Boolean> {

    public SpendCluesAsGroupAction() {
        super(BeforeAfterEvent.SPEND_CLUES_AS_GROUP);
    }

    private InvestigatorId highlightedInvestigatorId;

    @Override
    protected void onExecute(SingleSubscriber<? super Boolean> ss) {
        List<? extends InvestigatorRead> availableInvestigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        AtomicInteger agreedToPay = new AtomicInteger(0);
        highlightedInvestigatorId = activeInvestigatorId;
        Map<InvestigatorId, List<SpendData>> alreadyPayedMap = new HashMap<>();

        ServicePlatform.get().getService().hold();

        for (int i = 0; i < input; i++) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                if (agreedToPay.get() < input) {
                    ServicePlatform.get().getService().hold();
                    payWithClues(createPayActionMap(availableInvestigators, agreedToPay, activeInvestigatorId), alreadyPayedMap);
                    ServicePlatform.get().getService().release();
                }
            });
        }

        // Revert payment if it was not successful
        revertPayment(agreedToPay, alreadyPayedMap);

        ServicePlatform.get().getService().convertTo(Boolean.class, () -> agreedToPay.get() == input);
        ServicePlatform.get().getService().release();
        ss.onSuccess(null);
    }

    private void revertPayment(AtomicInteger agreedToPay, Map<InvestigatorId, List<SpendData>> alreadyPayedMap) {
        ServicePlatform.get().getService().addEventCommand(in -> {
            if (agreedToPay.get() == input) { // OK
                Question<Object> question = new Question<>();
                question.setOptions(Question.Option.okOption);
                question.setTitle("Clues successfully spent.");
                ServicePlatform.get().getGameService().ask(question).subscribe();
                return;
            }
            if (alreadyPayedMap.isEmpty()) { //OK
                Question<Object> question = new Question<>();
                question.setOptions(Question.Option.okOption);
                question.setTitle("Not enough Clues.");
                ServicePlatform.get().getGameService().ask(question).subscribe();
                return;
            }
            // OK
            ServicePlatform.get().getService().hold();
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Not enough Clues. Spending will be reverted.");
            ServicePlatform.get().getGameService().ask(question).subscribe(answer -> {
                ServicePlatform.get().getService().hold();
                for (Map.Entry<InvestigatorId, List<SpendData>> entry : alreadyPayedMap.entrySet()) {
                    changeAndShowActiveInvestigator(entry.getKey());
                    for (SpendData spendData : entry.getValue()) {
                        spendData.revert();
                    }
                }
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
        });
    }

    private Map<InvestigatorId, SpendData> createPayActionMap(List<? extends InvestigatorRead> availableInvestigators,
                                                              AtomicInteger agreedToPay,
                                                              InvestigatorId activeInvestigatorId) {
        Map<InvestigatorId, SpendData> payActionsMap = new HashMap<>();
        List<InvestigatorId> investigatorsThatCanSpendClue = findInvestigatorsThatCanSpendClue(availableInvestigators, activeInvestigatorId);

        ServicePlatform.get().getService().addEventCommand(whatever -> {
            for (InvestigatorId investigatorId : investigatorsThatCanSpendClue) {
                askToSpendClue(payActionsMap, investigatorId, agreedToPay, availableInvestigators);
            }
        });
        return payActionsMap;
    }

    private void payWithClues(Map<InvestigatorId, SpendData> payActionsMap, Map<InvestigatorId, List<SpendData>> alreadyPayedMap) {
        ServicePlatform.get().getService().addEventCommand(whatever -> {
            for (Map.Entry<InvestigatorId, SpendData> entry : payActionsMap.entrySet()) {
                ServicePlatform.get().getService().addEventCommand(aaa -> {
                    ServicePlatform.get().getService().hold();
                    changeAndShowActiveInvestigator(entry.getKey());
                    entry.getValue().pay();
                    MapUtils.computeIfAbsent(alreadyPayedMap, entry.getKey(), key -> new LinkedList<>());
                    alreadyPayedMap.get(entry.getKey()).add(entry.getValue());
                    ServicePlatform.get().getService().release();
                });
            }
        });
    }

    private List<InvestigatorId> findInvestigatorsThatCanSpendClue(List<? extends InvestigatorRead> availableInvestigators,
                                                                   InvestigatorId activeInvestigatorId) {
        List<InvestigatorId> canSpendClueInvestigators = new LinkedList<>();
        Iterator<? extends InvestigatorRead> iterator = availableInvestigators.iterator();
        for (InvestigatorRead availableInvestigator : availableInvestigators) {
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(availableInvestigator.getInfo().getInvestigatorId());
            ServicePlatform.get().getTokenService().canSpend(1,0,0,0).subscribe(canSpendData -> {
                if (canSpendData.hasEnough()) {
                    canSpendClueInvestigators.add(availableInvestigator.getInfo().getInvestigatorId());
                }
            });
        }
        ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(activeInvestigatorId);
        return canSpendClueInvestigators;
    }

    private void askToSpendClue(Map<InvestigatorId, SpendData> payActionsMap,
                                InvestigatorId investigatorId,
                                AtomicInteger agreedToPay,
                                List<? extends InvestigatorRead> availableInvestigators) {
        ServicePlatform.get().getService().addEventCommand(aaa -> {
            if (agreedToPay.get() == input) {
                return;
            }
            ServicePlatform.get().getInvestigatorService().hold();
            changeAndShowActiveInvestigator(investigatorId);
            Question<Boolean> question = new Question<>();
            question.setOptions(Question.Option.noYesOptions);
            question.setTitle("Spend 1 Clue? Clues remaining: "+(input-agreedToPay.get()));
            ServicePlatform.get().getGameService().ask(question).subscribe(answer -> {
                if (answer.getResponseData()) {
                    ServicePlatform.get().getTokenService().spend(1, 0,0,0).subscribe(spendData -> {
                        if (spendData.hasEnough()) {
                            payActionsMap.put(investigatorId, spendData);
                            agreedToPay.incrementAndGet();
                        }
                    });
                } else {
                    IterableUtils.removeIf(availableInvestigators,
                            investigatorRead -> investigatorRead.getInfo().getInvestigatorId() == investigatorId);
                }
            });
            ServicePlatform.get().getInvestigatorService().release();
        });
    }

    private void changeAndShowActiveInvestigator(InvestigatorId investigatorId) {
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();

        if (activeInvestigatorId != investigatorId) {
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
        }
        if (highlightedInvestigatorId != investigatorId) {
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            highlightedInvestigatorId = investigatorId;
        }
    }
}
