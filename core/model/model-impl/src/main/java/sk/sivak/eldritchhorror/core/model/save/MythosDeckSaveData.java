package sk.sivak.eldritchhorror.core.model.save;

import java.util.LinkedList;
import java.util.List;

public class MythosDeckSaveData implements SaveDataWrite.MythosDeckSaveDataWrite {
    private int drawnCardsCount;
    private List<String> selectedMythosCards = new LinkedList<>();

    public void setDrawnCardsCount(int drawnCardsCount) {
        this.drawnCardsCount = drawnCardsCount;
    }

    public void setSelectedMythosCards(List<String> selectedMythosCards) {
        this.selectedMythosCards = selectedMythosCards;
    }

    @Override
    public int getDrawnCardsCount() {
        return drawnCardsCount;
    }

    @Override
    public List<String> getSelectedMythosCards() {
        return selectedMythosCards;
    }
}
