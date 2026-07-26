package sk.sivak.eldritchhorror.core.model;

import java8.features.function.Predicate;
import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionBack;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.condition.debt.DebtCondition;
import sk.sivak.eldritchhorror.core.constants.condition.debt.DebtConditionBack6;
import sk.sivak.eldritchhorror.core.constants.condition.leginjury.LegInjuryCondition;
import sk.sivak.eldritchhorror.core.constants.condition.leginjury.LegInjuryConditionBack1;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.model.save.AssetsDeckSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.ConditionsDeckSaveData;
import sk.sivak.eldritchhorror.core.model.save.ConditionsDeckSaveDataRead;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java8.features.stream.Stream.anyMatch;
import static java8.features.stream.Stream.findFirstOrException;
import static java8.features.util.MapUtils.getOrDefault;

public class ConditionsDeck implements ConditionsDeckWrite {

    private static final Logger logger = LogManager.getLogger(ConditionsDeck.class);

    private Map<InvestigatorId, List<ConditionInfo>> investigatorConditionsMap;

    private List<ConditionInfo> deck;

    @Override
    public void createDeck() {
        investigatorConditionsMap = new HashMap<>();
        deck = new LinkedList<>();
        for (ConditionId conditionId : ConditionId.values()) {
            int suffix = 1;
            while (true) {
                try {
                    Class<?> backConditionClassName = Class.forName(conditionId.getBackConditionClassName(suffix++));
                    AbstractConditionInfo abstractConditionInfo = (AbstractConditionInfo) Class.forName(conditionId.getConditionClassName()).newInstance();
                    abstractConditionInfo.setConditionBack((ConditionBack) backConditionClassName.newInstance());
                    deck.add(abstractConditionInfo);
                } catch (ClassNotFoundException e) {
                    break;
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        Collections.shuffle(deck);

        logger.info("Conditions deck created with " + deck.size() + " conditions.");

        if (!GoogleServicesHolder.isTutorialPassed()) {
            LegInjuryCondition legInjuryCondition = new LegInjuryCondition();
            legInjuryCondition.setConditionBack(new LegInjuryConditionBack1());
            deck.add(0, legInjuryCondition);

            DebtCondition debtCondition = new DebtCondition();
            debtCondition.setConditionBack(new DebtConditionBack6());
            deck.add(0, debtCondition);
        }
    }

    @Override
    public boolean hasCondition(InvestigatorId investigatorId, ConditionId conditionId) {
        List<ConditionInfo> conditions = getConditions(investigatorId);
        return anyMatch(conditions, condition -> conditionId == condition.getId());
    }

    @Override
    public ConditionInfo getCondition(InvestigatorId investigatorId, ConditionId conditionId) {
        List<ConditionInfo> conditions = getConditions(investigatorId);
        return findFirstOrException(conditions, condition -> conditionId == condition.getId());
    }

    @Override
    public List<ConditionInfo> getCondition(InvestigatorId investigatorId, ConditionTrait conditionTrait) {
        List<ConditionInfo> conditions = getConditions(investigatorId);
        return Stream.collectToList(conditions, conditionInfo -> conditionInfo.getTraits().contains(conditionTrait));
    }

    @Override
    public boolean hasAnyCondition(InvestigatorId investigatorId) {
        List<ConditionInfo> conditions = getConditions(investigatorId);
        return anyMatch(conditions, condition -> true);
    }

    @Override
    public boolean hasTrait(InvestigatorId investigatorId, ConditionTrait trait) {
        List<ConditionInfo> conditions = getConditions(investigatorId);
        return anyMatch(conditions, condition -> condition.getTraits().contains(trait));
    }

    @Override
    public List<ConditionInfo> getConditions(InvestigatorId investigatorId) {
        return getOrDefault(investigatorConditionsMap, investigatorId, new LinkedList<>());
    }

    @Override
    public boolean canGetTrait(InvestigatorId investigatorId, ConditionTrait trait) {
        return canGainCondition(ci -> ci.getTraits().contains(trait), investigatorId);
    }

    @Override
    public boolean canGetConditionId(InvestigatorId investigatorId, ConditionId conditionId) {
        return canGainCondition(ci -> ci.getId() == conditionId, investigatorId);
    }

    @Override
    public ConditionInfo gainCondition(ConditionId conditionId, InvestigatorId investigatorId) {
        return gainCondition(ci -> conditionId == ci.getId(), investigatorId);
    }

    @Override
    public ConditionInfo gainCondition(ConditionTrait conditionTrait, InvestigatorId investigatorId) {
        return gainCondition(ci -> ci.getTraits().contains(conditionTrait), investigatorId);
    }

    private ConditionInfo gainCondition(Predicate<ConditionInfo> predicate, InvestigatorId investigatorId) {
        List<ConditionInfo> conditionsList = getOrDefault(investigatorConditionsMap, investigatorId, new LinkedList<>());
        Predicate<ConditionInfo> notOwningPredicate = conditionInfo -> !Stream.map(conditionsList, ConditionInfo::getId).contains(conditionInfo.getId());
        Predicate<ConditionInfo> compositePredicate = conditionInfo -> notOwningPredicate.test(conditionInfo) && predicate.test(conditionInfo);
        ConditionInfo conditionInfo = findFirstOrException(deck, compositePredicate);
        deck.remove(conditionInfo);

        conditionsList.add(conditionInfo);
        investigatorConditionsMap.put(investigatorId, conditionsList);
        logger.info("Investigator '" + investigatorId + "' gained condition '" + conditionInfo.getId() + "'");
        return conditionInfo;
    }

    private boolean canGainCondition(Predicate<ConditionInfo> predicate, InvestigatorId investigatorId) {
        List<ConditionInfo> conditionsList = getOrDefault(investigatorConditionsMap, investigatorId, new LinkedList<>());
        Predicate<ConditionInfo> notOwningPredicate = conditionInfo -> !Stream.map(conditionsList, ConditionInfo::getId).contains(conditionInfo.getId());
        Predicate<ConditionInfo> compositePredicate = conditionInfo -> notOwningPredicate.test(conditionInfo) && predicate.test(conditionInfo);
        return anyMatch(deck, compositePredicate);
    }

    @Override
    public void discard(InvestigatorId investigatorId, ConditionInfo conditionInfo) {
        investigatorConditionsMap.get(investigatorId).remove(conditionInfo);
        deck.add(conditionInfo);
        Collections.shuffle(deck);
        logger.info("Investigator '" + investigatorId + "' discarded condition '" + conditionInfo.getName() + "'.");
    }

    @Override
    public ConditionsDeckSaveData save() {
        ConditionsDeckSaveData conditionsDeckSaveData = new ConditionsDeckSaveData();
        HashMap<String, List<? extends ConditionsDeckSaveDataRead.ConditionInfoSaveDataRead>> saveDataMap = new HashMap<>();
        for (Map.Entry<InvestigatorId, List<ConditionInfo>> entry : investigatorConditionsMap.entrySet()) {
            List<ConditionsDeckSaveData.ConditionInfoSaveData> conditionInfoSaveData = Stream.collectToList(Stream.map(entry.getValue(),
                    conditionInfo -> new ConditionsDeckSaveData.ConditionInfoSaveData(
                            conditionInfo.getId(), conditionInfo.getConditionBack().getConditionBackId())));
            saveDataMap.put(entry.getKey().name(), conditionInfoSaveData);
        }
        conditionsDeckSaveData.setInvestigatorConditionInfoMap(saveDataMap);
        return conditionsDeckSaveData;
    }

    @Override
    public void load(ConditionsDeckSaveDataRead saveData) {
        createDeck();

        for (Map.Entry<String, List<? extends ConditionsDeckSaveDataRead.ConditionInfoSaveDataRead>> entry : saveData.getInvestigatorConditionInfoMap().entrySet()) {
            for (ConditionsDeckSaveDataRead.ConditionInfoSaveDataRead ownedCondition : entry.getValue()) {
                ConditionInfo conditionInfo = Stream.findFirstOrException(deck,
                        ai -> ai.getId().equals(ownedCondition.getConditionId()) && ai.getConditionBack().getConditionBackId() == ownedCondition.getConditionBackId());
                InvestigatorId investigatorId = InvestigatorId.valueOf(entry.getKey());
                List<ConditionInfo> existingConditions = getOrDefault(investigatorConditionsMap, investigatorId, new LinkedList<>());
                existingConditions.add(conditionInfo);
                deck.remove(conditionInfo);
                investigatorConditionsMap.put(investigatorId, existingConditions);
            }
        }
    }
}
