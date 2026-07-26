package sk.sivak.eldritchhorror.core.model;

import java8.features.util.MapUtils;
import sk.sivak.eldritchhorror.core.constants.mythos.MythosCard;

import java.util.HashMap;
import java.util.Map;

public interface MythosDeckRead {
    void initSelectedMythosCards(MythosStage... stages);

    int getDrawnCardsCount();

    interface MythosStage {
        int getCount(MythosCard.MythosColor mythosColor);
    }

    class MythosStageImpl implements MythosStage {
        private final Map<MythosCard.MythosColor, Integer> mythosColorMap = new HashMap<>();

        public MythosStageImpl(int greenCount, int yellowCount, int blueCount) {
            mythosColorMap.put(MythosCard.MythosColor.GREEN, greenCount);
            mythosColorMap.put(MythosCard.MythosColor.YELLOW, yellowCount);
            mythosColorMap.put(MythosCard.MythosColor.BLUE, blueCount);
        }

        @Override
        public int getCount(MythosCard.MythosColor mythosColor) {
            return MapUtils.getOrDefault(mythosColorMap, mythosColor, 0);
        }
    }

}
