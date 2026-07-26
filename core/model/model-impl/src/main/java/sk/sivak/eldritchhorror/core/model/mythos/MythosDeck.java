package sk.sivak.eldritchhorror.core.model.mythos;

import java8.features.function.Supplier;
import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import sk.sivak.eldritchhorror.core.constants.mythos.GreenMythosCard;
import sk.sivak.eldritchhorror.core.constants.mythos.GreenMythosCard10;
import sk.sivak.eldritchhorror.core.constants.mythos.GreenMythosCard2;
import sk.sivak.eldritchhorror.core.constants.mythos.GreenMythosCard9;
import sk.sivak.eldritchhorror.core.constants.mythos.MythosCard;
import sk.sivak.eldritchhorror.core.constants.mythos.YellowMythosCard;
import sk.sivak.eldritchhorror.core.constants.mythos.YellowMythosCard1;
import sk.sivak.eldritchhorror.core.constants.mythos.YellowMythosCard10;
import sk.sivak.eldritchhorror.core.constants.mythos.YellowMythosCard11;
import sk.sivak.eldritchhorror.core.constants.mythos.YellowMythosCard3;
import sk.sivak.eldritchhorror.core.constants.mythos.YellowMythosCard7;
import sk.sivak.eldritchhorror.core.constants.mythos.YellowMythosCard9;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.model.MythosDeckWrite;
import sk.sivak.eldritchhorror.core.model.save.MythosDeckSaveData;
import sk.sivak.eldritchhorror.core.model.save.MythosDeckSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveData;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MythosDeck implements MythosDeckWrite {

    private List<MythosCard> allAvailableMythosCards;
    private List<MythosCard> selectedMythosCards;
    private int drawnCardsCount;

    @Override
    public void initSelectedMythosCards(MythosStage... mythosStages) {
        initAllAvailableMythosCards();
        for (MythosStage mythosStage : mythosStages) {
            List<MythosCard> stageCards = new LinkedList<>();
            for (MythosCard.MythosColor mythosColor : MythosCard.MythosColor.values()) {
                addCardToStageCards(stageCards, () -> mythosStage.getCount(mythosColor), mythosColor);
            }
            Collections.shuffle(stageCards);
            selectedMythosCards.addAll(stageCards);
        }
        if (!GoogleServicesHolder.isTutorialPassed()) {
            selectedMythosCards.add(0, new YellowMythosCard1());
            selectedMythosCards.add(1, new GreenMythosCard2());
            selectedMythosCards.add(2, new YellowMythosCard7());
        }
    }

    private void initAllAvailableMythosCards() {
        allAvailableMythosCards = new LinkedList<>();
        selectedMythosCards = new LinkedList<>();
        drawnCardsCount = 0;
        for (MythosCard.MythosColor mythosColor : MythosCard.MythosColor.values()) {
            int i=1;
            while (true) {
                try {
                    Class<?> mythosCard = Class.forName("sk.sivak.eldritchhorror.core.constants.mythos."+mythosColor.getValue()+"MythosCard" + i++);
                    allAvailableMythosCards.add((MythosCard) mythosCard.newInstance());
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    break;
                }
            }
        }
        Collections.shuffle(allAvailableMythosCards);
    }

    @Override
    public MythosCard drawMythosCard() {
        drawnCardsCount++;
        return selectedMythosCards.remove(0);
    }

    private void addCardToStageCards(List<MythosCard> stageCards,
                                     Supplier<Integer> cardsCountSupplier,
                                     MythosCard.MythosColor mythosColor) {
        for (int i = 0; i < cardsCountSupplier.get(); i++) {
            MythosCard colorCard = Stream.findFirstOrException(allAvailableMythosCards,
                    mythosCard -> mythosCard.getMythosColor() == mythosColor);
            allAvailableMythosCards.remove(colorCard);
            stageCards.add(colorCard);
        }
    }

    @Override
    public int getDrawnCardsCount() {
        return drawnCardsCount;
    }


    @Override
    public MythosDeckSaveData save() {
        MythosDeckSaveData mythosDeckSaveData = new MythosDeckSaveData();
        mythosDeckSaveData.setDrawnCardsCount(drawnCardsCount);
        List<String> selectedMythosCardNames = new LinkedList<>();
        for (MythosCard selectedMythosCard : selectedMythosCards) {
            selectedMythosCardNames.add(selectedMythosCard.getClass().getSimpleName());
        }
        mythosDeckSaveData.setSelectedMythosCards(selectedMythosCardNames);
        return mythosDeckSaveData;
    }

    @Override
    public void load(MythosDeckSaveDataRead mythosDeckSaveData) {
        initAllAvailableMythosCards();
        drawnCardsCount = mythosDeckSaveData.getDrawnCardsCount();
        for (String savedSelectedMythosCard : mythosDeckSaveData.getSelectedMythosCards()) {
            MythosCard selectedMythosCard = Stream.findFirstOrException(allAvailableMythosCards,
                    mythosCard -> mythosCard.getClass().getSimpleName().equals(savedSelectedMythosCard));
            selectedMythosCards.add(selectedMythosCard);
            allAvailableMythosCards.remove(selectedMythosCard);
        }
    }
}
