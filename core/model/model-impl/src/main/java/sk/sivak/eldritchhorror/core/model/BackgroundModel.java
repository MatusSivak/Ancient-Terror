package sk.sivak.eldritchhorror.core.model;

import java8.features.util.MapUtils;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class BackgroundModel implements BackgroundModelWrite{
    private Map<InvestigatorId, Stack<BackgroundDataWrite>> backgroundDataMap;
    private BackgroundDataRead currentBackground;


    @Override
    public void init() {
        backgroundDataMap = new HashMap<>();
        currentBackground = null;
    }

    @Override
    public void pushBackground(InvestigatorId investigatorId, BackgroundDataWrite backgroundData) {
        MapUtils.computeIfAbsent(backgroundDataMap, investigatorId, x -> new Stack<>());
        backgroundDataMap.get(investigatorId).push(backgroundData);
        currentBackground = backgroundData;
    }

    @Override
    public void popBackground(InvestigatorId investigatorId) {
        MapUtils.computeIfAbsent(backgroundDataMap, investigatorId, x -> new Stack<>());
        if (backgroundDataMap.get(investigatorId).isEmpty()) {
            return;
        }
        backgroundDataMap.get(investigatorId).pop();
        currentBackground = peekBackground(investigatorId);
    }

    @Override
    public void removeBackgrounds(InvestigatorId investigatorId, BackgroundType backgroundType) {
        Stack<BackgroundDataWrite> backgrounds = backgroundDataMap.get(investigatorId);
        if (backgrounds == null) {
            return;
        }
        boolean removingCurrent = false;
        for (int i = backgrounds.size() - 1; i >= 0; i--) {
            BackgroundDataWrite background = backgrounds.get(i);
            if (background.getBackgroundType() == backgroundType) {
                removingCurrent |= currentBackground == background;
                backgrounds.remove(i);
            }
        }
        if (removingCurrent) {
            currentBackground = peekBackground(investigatorId);
        }
    }

    @Override
    public BackgroundDataWrite peekBackground(InvestigatorId investigatorId) {
        MapUtils.computeIfAbsent(backgroundDataMap, investigatorId, x -> new Stack<>());
        if (backgroundDataMap.get(investigatorId).isEmpty()) {
            return null;
        }
        return backgroundDataMap.get(investigatorId).peek();
    }

    @Override
    public BackgroundDataRead getCurrentBackground() {
        return currentBackground;
    }

    @Override
    public void setCurrentBackground(BackgroundDataRead currentBackground) {
        this.currentBackground = currentBackground;
    }
}
