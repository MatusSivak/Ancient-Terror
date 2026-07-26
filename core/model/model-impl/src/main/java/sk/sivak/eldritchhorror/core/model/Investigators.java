package sk.sivak.eldritchhorror.core.model;

import java8.features.function.Predicate;
import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.model.save.InvestigatorsSaveData;
import sk.sivak.eldritchhorror.core.model.save.InvestigatorsSaveDataRead;
import sk.sivak.eldritchhorror.core.model.util.InvestigatorsHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java8.features.stream.Stream.findFirstOrException;

/**
 * @author msivak
 */
public class Investigators implements InvestigatorsWrite {

    private InvestigatorWrite activeInvestigator;

    private List<InvestigatorWrite> selectedInvestigators;
    private List<InvestigatorInfo> availableInvestigators;
    private List<InvestigatorWrite> defeatedInvestigators;
    private List<InvestigatorId> investigatorOrder;
    private int defeatedOrDevouredCount = 0;

    public List<InvestigatorWrite> getOnBoardInvestigators() {
        return Stream.collectToList(selectedInvestigators,
                investigatorWrite -> !investigatorWrite.isLostInTimeAndSpace());
    }

    @Override
    public List<InvestigatorInfo> getAvailableInvestigators() {
        return new LinkedList<>(availableInvestigators);
    }

    @Override
    public void initAvailableInvestigators(boolean bonusInvestigatorsPurchased) {
        activeInvestigator = null;
        selectedInvestigators = new ArrayList<>();
        availableInvestigators = null;
        defeatedInvestigators = new LinkedList<>();
        investigatorOrder = new LinkedList<>();

        availableInvestigators = InvestigatorsHelper.initAvailableInvestigators(bonusInvestigatorsPurchased);
    }

    @Override
    public void addLockedToAvailable() {
        availableInvestigators.addAll(InvestigatorsHelper.initLockedInvestigators());
        Collections.shuffle(availableInvestigators);
    }

    @Override
    public void initSelectedInvestigators(InvestigatorInfo[] selected) {
        InvestigatorsHelper.initSelectedInvestigators(selectedInvestigators, selected);
        InvestigatorsHelper.removeFromAvailable(selected, availableInvestigators);
        setLeadInvestigator(selected[0].getInvestigatorId(), false);

        for (InvestigatorWrite selectedInvestigator : selectedInvestigators) {
            investigatorOrder.add(selectedInvestigator.getInfo().getInvestigatorId());
        }
    }

    @Override
    public void initReplacingInvestigator(InvestigatorId removedInvestigator, InvestigatorInfo replacing) {
        int indexOfRemovedInvestigator = investigatorOrder.indexOf(removedInvestigator);
        investigatorOrder.remove(removedInvestigator);
        investigatorOrder.add(indexOfRemovedInvestigator, replacing.getInvestigatorId());
        InvestigatorWrite newInvestigator = new Investigator();
        selectedInvestigators.add(indexOfRemovedInvestigator, newInvestigator);
        InvestigatorsHelper.initSelectedInvestigators(
                Collections.singletonList(newInvestigator), new InvestigatorInfo[]{replacing});

        IterableUtils.removeIf(availableInvestigators,
                investigator -> investigator == replacing);
        if (activeInvestigator == null) {
            activeInvestigator = selectedInvestigators.get(indexOfRemovedInvestigator);
        }
    }

    @Override
    public void setLeadInvestigator(InvestigatorId investigatorId, boolean reorder) {
        if (selectedInvestigators.isEmpty()) {
            return;
        }
        InvestigatorWrite newLeadInvestigator = Stream.findFirstOrException(selectedInvestigators,
                investigator -> investigator.getInfo().getInvestigatorId() == investigatorId);

        newLeadInvestigator.setLeadInvestigator(true);

        if (reorder) {
            selectedInvestigators.remove(newLeadInvestigator);
            selectedInvestigators.add(0, newLeadInvestigator);
            investigatorOrder.remove(newLeadInvestigator.getInfo().getInvestigatorId());
            investigatorOrder.add(0, newLeadInvestigator.getInfo().getInvestigatorId());
        }
    }

    @Override
    public int getPlayers() {
        return selectedInvestigators.size();
    }

    @Override
    public void initWithPlayers(int players) {
        selectedInvestigators = InvestigatorsHelper.initWithPlayers(players);
    }

    public InvestigatorWrite getActiveInvestigator() {
        return activeInvestigator;
    }

    @Override
    public InvestigatorWrite getInvestigator(InvestigatorId investigatorId) {
        return findFirstOrException(selectedInvestigators, investigator -> investigatorId == investigator.getInfo().getInvestigatorId());
    }

    @Override
    public InvestigatorId getActiveInvestigatorId() {
        if (getActiveInvestigator() == null) {
            return null;
        }
        return getActiveInvestigator().getInfo().getInvestigatorId();
    }

    @Override
    public boolean isActiveLast() {
        if (activeInvestigator == null) {
            return true;
        }
        Map<InvestigatorId, InvestigatorWrite> map = new HashMap<>();
        for (InvestigatorWrite selectedInvestigator : selectedInvestigators) {
            map.put(selectedInvestigator.getInfo().getInvestigatorId(), selectedInvestigator);
        }
        int indexOfActiveInvestigator = investigatorOrder.indexOf(activeInvestigator.getInfo().getInvestigatorId());
        for (int i = indexOfActiveInvestigator + 1; i < investigatorOrder.size(); i++) {
            if (map.containsKey(investigatorOrder.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public InvestigatorWrite getLeadInvestigator() {
        if (Stream.anyMatch(selectedInvestigators, InvestigatorRead::isLeadInvestigator)) {
            return findFirstOrException(selectedInvestigators, InvestigatorRead::isLeadInvestigator);
        }
        return null;
    }

    @Override
    public void initActiveInvestigator() {
        if (Stream.anyMatch(selectedInvestigators, InvestigatorRead::isLeadInvestigator)) {
            activeInvestigator = findFirstOrException(selectedInvestigators, InvestigatorRead::isLeadInvestigator);
        }
    }

    @Override
    public void changeActiveInvestigator() {
        if (activeInvestigator == null) {
            return;
        }

        int indexOfActiveInvestigator = investigatorOrder.indexOf(activeInvestigator.getInfo().getInvestigatorId());
        Map<InvestigatorId, InvestigatorWrite> map = new HashMap<>();
        for (InvestigatorWrite selectedInvestigator : selectedInvestigators) {
            map.put(selectedInvestigator.getInfo().getInvestigatorId(), selectedInvestigator);
        }
        for (int i = indexOfActiveInvestigator+1; i < investigatorOrder.size(); i++) {
            if (map.containsKey(investigatorOrder.get(i))) {
                activeInvestigator = map.get(investigatorOrder.get(i));
                return;
            }
        }
        for (int i = 0; i <= indexOfActiveInvestigator; i++) {
            if (map.containsKey(investigatorOrder.get(i))) {
                activeInvestigator = map.get(investigatorOrder.get(i));
                return;
            }
        }
        activeInvestigator = null;
    }

    @Override
    public void changeActiveInvestigator(InvestigatorId targetInvestigatorId) {
        Predicate<InvestigatorWrite> predicate = investigatorWrite -> investigatorWrite.getInfo().getInvestigatorId() == targetInvestigatorId;
        if (Stream.anyMatch(selectedInvestigators, predicate)) {
            activeInvestigator = Stream.findFirstOrException(selectedInvestigators, predicate);
        }

    }

    @Override
    public void lostInTimeAndSpace(InvestigatorId investigatorId) {
        getInvestigator(investigatorId).setLostInTimeAndSpace(true);
    }

    @Override
    public void unsetActiveInvestigator() {
        activeInvestigator = null;
    }

    @Override
    public void defeatInvestigator(InvestigatorId defeatedInvestigatorId) {
        InvestigatorWrite defeatedInvestigator = Stream.findFirstOrException(selectedInvestigators,
                investigatorWrite -> investigatorWrite.getInfo().getInvestigatorId() == defeatedInvestigatorId);
        defeatedInvestigators.add(defeatedInvestigator);

        devourInvestigator(defeatedInvestigatorId);
    }

    @Override
    public void removeDefeatedInvestigator(InvestigatorId investigatorId) {
        InvestigatorWrite defeatedInvestigator = Stream.findFirstOrException(defeatedInvestigators,
                investigatorWrite -> investigatorWrite.getInfo().getInvestigatorId() == investigatorId);
        defeatedInvestigators.remove(defeatedInvestigator);
    }

    @Override
    public void devourInvestigator(InvestigatorId devouredInvestigatorId) {
        IterableUtils.removeIf(availableInvestigators,
                investigator -> investigator.getInvestigatorId() == devouredInvestigatorId);
        IterableUtils.removeIf(selectedInvestigators,
                investigatorWrite -> investigatorWrite.getInfo().getInvestigatorId() == devouredInvestigatorId);

        defeatedOrDevouredCount++;
    }

    @Override
    public int getDefeatedOrDevouredCount() {
        return defeatedOrDevouredCount;
    }

    @Override
    public boolean isOnBoard(InvestigatorId investigatorId) {
        return Stream.anyMatch(getOnBoardInvestigators(),
                investigatorWrite -> investigatorWrite.getInfo().getInvestigatorId() == investigatorId);
    }

    @Override
    public List<InvestigatorId> getToBeReplacedInvestigators() {
        List<InvestigatorId> toBeReplacedInvestigators = new LinkedList<>();
        Map<InvestigatorId, InvestigatorWrite> map = new HashMap<>();
        for (InvestigatorWrite selectedInvestigator : selectedInvestigators) {
            map.put(selectedInvestigator.getInfo().getInvestigatorId(), selectedInvestigator);
        }
        for (InvestigatorId investigatorId : investigatorOrder) {
            if (map.containsKey(investigatorId)) {
                continue;
            }
            toBeReplacedInvestigators.add(investigatorId);
        }
        return toBeReplacedInvestigators;
    }

    @Override
    public List<InvestigatorWrite> getDefeatedInvestigators() {
        return defeatedInvestigators;
    }

    @Override
    public InvestigatorsSaveData save() {
        InvestigatorsSaveData investigatorsSaveData = new InvestigatorsSaveData();
        investigatorsSaveData.setAvailableInvestigators(Stream.collectToList(Stream.map(availableInvestigators, InvestigatorInfo::getInvestigatorId)));
        investigatorsSaveData.setInvestigatorOrder(investigatorOrder);
        investigatorsSaveData.setDefeatedOrDevouredCount(defeatedOrDevouredCount);

        LinkedList<InvestigatorsSaveData.InvestigatorSaveData> savedSelectedInvestigators = new LinkedList<>();
        for (InvestigatorWrite selectedInvestigator : selectedInvestigators) {
            InvestigatorsSaveData.InvestigatorSaveData savedSelectedInvestigator = new InvestigatorsSaveData.InvestigatorSaveData();

            savedSelectedInvestigator.setInvestigatorId(selectedInvestigator.getInfo().getInvestigatorId());
            savedSelectedInvestigator.setHealth(selectedInvestigator.getCurrentHealth());
            savedSelectedInvestigator.setSanity(selectedInvestigator.getCurrentSanity());
            savedSelectedInvestigator.setLocationId(selectedInvestigator.getCurrentLocationId());
            savedSelectedInvestigator.setLeadInvestigator(selectedInvestigator.isLeadInvestigator());
            savedSelectedInvestigator.setTrainTickets(selectedInvestigator.getTrainTickets());
            savedSelectedInvestigator.setShipTickets(selectedInvestigator.getShipTickets());
            savedSelectedInvestigator.setFocusTokens(selectedInvestigator.getFocusTokens());
            savedSelectedInvestigator.setDelayed(selectedInvestigator.isDelayed());
            savedSelectedInvestigator.setLostInTimeAndSpace(selectedInvestigator.isLostInTimeAndSpace());
            savedSelectedInvestigator.setPassiveAbilityDisabled(selectedInvestigator.isPassiveAbilityDisabled());

            HashMap<String, Integer> saveDataBonusMap = new HashMap<>();
            for (Map.Entry<Stat, Integer> statMap : selectedInvestigator.getStatBonusMap().entrySet()) {
                saveDataBonusMap.put(statMap.getKey().name(), statMap.getValue());
            }
            savedSelectedInvestigator.setStatBonusMap(saveDataBonusMap);

            savedSelectedInvestigators.add(savedSelectedInvestigator);
        }

        investigatorsSaveData.setSelectedInvestigators(savedSelectedInvestigators);

        LinkedList<InvestigatorsSaveDataRead.DefeatedInvestigatorSaveDataRead> savedDefeatedInvestigatorList = new LinkedList<>();
        for (InvestigatorWrite defeatedInvestigator : defeatedInvestigators) {
            InvestigatorsSaveData.DefeatedInvestigatorSaveData savedDefeatedInvestigator = new InvestigatorsSaveData.DefeatedInvestigatorSaveData();
            if (defeatedInvestigator.getCurrentHealth() <= 0 && defeatedInvestigator.getCurrentSanity() <= 0) {
                savedDefeatedInvestigator.setDefeatedByHealth(new Random().nextBoolean());
            } else {
                savedDefeatedInvestigator.setDefeatedByHealth(defeatedInvestigator.getCurrentHealth() <= 0);
            }
            savedDefeatedInvestigator.setInvestigatorId(defeatedInvestigator.getInfo().getInvestigatorId());
            savedDefeatedInvestigator.setLocationId(defeatedInvestigator.getCurrentLocationId());
            savedDefeatedInvestigator.setShipTickets(defeatedInvestigator.getShipTickets());
            savedDefeatedInvestigator.setTrainTickets(defeatedInvestigator.getTrainTickets());
            savedDefeatedInvestigatorList.add(savedDefeatedInvestigator);
        }
        investigatorsSaveData.setDefeatedInvestigators(savedDefeatedInvestigatorList);

        return investigatorsSaveData;
    }

    @Override
    public void load(InvestigatorsSaveDataRead saveData, boolean hasPurchasedBonusInvestigators) {
        initAvailableInvestigators(hasPurchasedBonusInvestigators);

        investigatorOrder = saveData.getInvestigatorOrder();
        defeatedOrDevouredCount = saveData.getDefeatedOrDevouredCount();
        for (InvestigatorsSaveDataRead.InvestigatorSaveDataRead savedSelectedInvestigator : saveData.getSelectedInvestigators()) {
            InvestigatorInfo savedInvestigatorInfo = Stream.findFirstOrException(availableInvestigators,
                    ai -> ai.getInvestigatorId().equals(savedSelectedInvestigator.getInvestigatorId()));
            Investigator investigator = new Investigator();
            investigator.setInfo(savedInvestigatorInfo);
            investigator.setCurrentHealth(savedSelectedInvestigator.getHealth());
            investigator.setCurrentSanity(savedSelectedInvestigator.getSanity());
            investigator.setLeadInvestigator(savedSelectedInvestigator.isLeadInvestigator());
            investigator.setCurrentLocationId(savedSelectedInvestigator.getLocationId());
            investigator.setTrainTickets(savedSelectedInvestigator.getTrainTickets());
            investigator.setShipTickets(savedSelectedInvestigator.getShipTickets());
            investigator.setFocusTokens(savedSelectedInvestigator.getFocusTokens());
            investigator.setLostInTimeAndSpace(savedSelectedInvestigator.isLostInTimeAndSpace());
            investigator.setPassiveAbilityDisabled(savedSelectedInvestigator.isPassiveAbilityDisabled());
            if (savedSelectedInvestigator.isDelayed()) {
                investigator.startDelayed();
            }
            for (Map.Entry<String, Integer> statValueEntry : savedSelectedInvestigator.getStatBonusMap().entrySet()) {
                for (Integer i = 0; i < statValueEntry.getValue(); i++) {
                    investigator.improveStat(Stat.valueOf(statValueEntry.getKey()));
                }
            }
            selectedInvestigators.add(investigator);
        }

        for (InvestigatorsSaveDataRead.DefeatedInvestigatorSaveDataRead defeatedInvestigator : saveData.getDefeatedInvestigators()) {
            Investigator investigator = new Investigator();
            investigator.setShipTickets(defeatedInvestigator.getShipTickets());
            investigator.setTrainTickets(defeatedInvestigator.getTrainTickets());
            investigator.setCurrentLocationId(defeatedInvestigator.getLocationId());
            InvestigatorInfo savedInvestigatorInfo = Stream.findFirstOrException(availableInvestigators,
                    ai -> ai.getInvestigatorId().equals(defeatedInvestigator.getInvestigatorId()));
            investigator.setInfo(savedInvestigatorInfo);
            defeatedInvestigators.add(investigator);
        }

        IterableUtils.removeIf(availableInvestigators,
                ai -> !saveData.getAvailableInvestigators().contains(ai.getInvestigatorId()));

    }
}
