package sk.sivak.eldritchhorror.core.model;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.MysteryCardId;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.model.save.MysteryDeckSaveData;
import sk.sivak.eldritchhorror.core.model.save.MysteryDeckSaveDataRead;
import sk.sivak.eldritchhorror.core.model.util.MysteryDeckHelper;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class MysteryDeck implements MysteryDeckWrite {

    private List<MysteryCardInfo> mysteryCards;

    private MysteryCardInfo currentMysteryCard;

    private int solvedMysteries;

    @Override
    public void initMysteryDeck(AncientOneId ancientOneId, Integer nrOfInvestigators) {
        solvedMysteries = 0;
        currentMysteryCard = null;
        mysteryCards = new MysteryDeckHelper().initMysteryDeck(ancientOneId, nrOfInvestigators);
    }

    @Override
    public MysteryCardInfo getCurrentMysteryCard() {
        return currentMysteryCard;
    }

    @Override
    public MysteryCardInfo drawNewMystery() {
        if (mysteryCards.isEmpty()) {
            this.currentMysteryCard = null;
        } else {
            this.currentMysteryCard = mysteryCards.remove(0);
        }
        return this.currentMysteryCard;
    }

    @Override
    public void solveCurrentMystery() {
        solvedMysteries++;
    }

    @Override
    public int getSolvedMysteriesCount() {
        return solvedMysteries;
    }

    @Override
    public List<String> getSolvedMysteries(AncientOneId ancientOneId) {
        List<MysteryCardInfo> allMysteries = new MysteryDeckHelper().initMysteryDeck(ancientOneId, 1);
        List<String> solvedMysteries = new LinkedList<>();
        for (MysteryCardInfo mystery : allMysteries) {
            boolean anyMatch = Stream.anyMatch(mysteryCards, mc -> mc.getMysteryCardId() == mystery.getMysteryCardId());
            if (!anyMatch) {
                solvedMysteries.add(mystery.getName());
            }
        }
        return solvedMysteries;
    }

    @Override
    public MysteryDeckSaveData save() {
        MysteryDeckSaveData mysteryDeckSaveData = new MysteryDeckSaveData();
        mysteryDeckSaveData.setSolvedMysteries(solvedMysteries);
        mysteryDeckSaveData.setProgress(currentMysteryCard.getProgress());
        mysteryDeckSaveData.setPinLocations(new LinkedList<>(currentMysteryCard.getPinLocations()));
        List<MysteryCardId> mysteryCards = Stream.collectToList(Stream.map(this.mysteryCards, MysteryCardInfo::getMysteryCardId));
        mysteryCards.add(0, currentMysteryCard.getMysteryCardId());
        mysteryDeckSaveData.setMysteryCards(mysteryCards);
        return mysteryDeckSaveData;
    }

    @Override
    public void load(MysteryDeckSaveDataRead saveData) {
        List<MysteryCardInfo> newMysteryCards = new LinkedList<>();
        for (MysteryCardId mysteryCardId : saveData.getMysteryCards()) {
            MysteryCardInfo foundMysteryCard = Stream.findFirstOrException(mysteryCards,
                    mysteryCardInfo -> mysteryCardInfo.getMysteryCardId().equals(mysteryCardId));
            newMysteryCards.add(foundMysteryCard);
        }
        mysteryCards = newMysteryCards;
        solvedMysteries = saveData.getSolvedMysteries();
        if (mysteryCards.get(0).getPinLocations() != null && !mysteryCards.get(0).getPinLocations().isEmpty()) {
            mysteryCards.get(0).setPinLocationsSupplier(saveData::getPinLocations);
        }
    }
}
