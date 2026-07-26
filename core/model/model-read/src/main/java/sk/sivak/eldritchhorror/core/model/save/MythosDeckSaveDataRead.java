package sk.sivak.eldritchhorror.core.model.save;

import java.util.List;

public interface MythosDeckSaveDataRead {
    int getDrawnCardsCount();
    List<String> getSelectedMythosCards();
}
