package sk.sivak.eldritchhorror.core.model.rumor;

import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;
import sk.sivak.eldritchhorror.core.model.RumorsWrite;
import sk.sivak.eldritchhorror.core.model.save.RumorsSaveData;
import sk.sivak.eldritchhorror.core.model.save.RumorsSaveDataRead;

import java.util.LinkedList;
import java.util.List;

public class Rumors implements RumorsWrite {
    private List<AbstractRumorCardInfo> activeRumors;
    private List<AbstractRumorCardInfo> availableRumors;

    @Override
    public void init() {
        activeRumors = new LinkedList<>();
        availableRumors = new LinkedList<>();
        availableRumors.add(new TheWindWalkerRumorCard()); // 1
        availableRumors.add(new StarsAlignedRumorCard()); // 2
        availableRumors.add(new FracturedRealityRumorCard());
        availableRumors.add(new GrowingMadnessRumorCard());
        availableRumors.add(new LostKnowledgeRumorCard());
    }

    @Override
    public void initActiveRumor(String rumorId, Integer cluesRequired) {
        AbstractRumorCardInfo newActiveRumor = Stream.findFirstOrException(availableRumors, ar -> ar.getId().equals(rumorId));
        availableRumors.remove(newActiveRumor);
        newActiveRumor.setCluesRequired(cluesRequired);
        activeRumors.add(newActiveRumor);
    }

    @Override
    public void countdown(String rumorId, int amount) {
        AbstractRumorCardInfo activeRumor = getActiveRumor(rumorId);
        activeRumor.setTimeRemaining(activeRumor.getTimeRemaining()-amount);
    }

    @Override
    public AbstractRumorCardInfo getActiveRumor(String rumorId) {
        return Stream.findFirstOrException(activeRumors, ar -> ar.getId().equals(rumorId));
    }

    @Override
    public List<RumorCardInfo> getActiveRumors() {
        return new LinkedList<>(activeRumors);
    }

    @Override
    public void removeActiveRumor(String rumorId) {
        IterableUtils.removeIf(activeRumors, ar -> ar.getId().equals(rumorId));
    }

    public RumorsSaveData save() {
        RumorsSaveData rumorsSaveData = new RumorsSaveData();
        List<RumorsSaveData.ActiveRumorSaveData> activeRumorSaveDataList = new LinkedList<>();
        for (AbstractRumorCardInfo activeRumor : this.activeRumors) {
            RumorsSaveData.ActiveRumorSaveData activeRumorSaveData = new RumorsSaveData.ActiveRumorSaveData();
            activeRumorSaveData.setRumorId(activeRumor.getId());
            activeRumorSaveData.setTimeRemaining(activeRumor.getTimeRemaining());
            activeRumorSaveData.setCluesRequired(activeRumor.getCluesRequired());
            activeRumorSaveDataList.add(activeRumorSaveData);
        }
        rumorsSaveData.setActiveRumors(new LinkedList<>(activeRumorSaveDataList));
        return rumorsSaveData;
    }

    @Override
    public void load(RumorsSaveDataRead rumorsSaveData) {
        init();
        if (rumorsSaveData == null) {
            return;
        }
        if (rumorsSaveData.getActiveRumors() == null) {
            return;
        }
        for (RumorsSaveDataRead.ActiveRumorSaveDataRead savedActiveRumor : rumorsSaveData.getActiveRumors()) {
            initActiveRumor(savedActiveRumor.getRumorId(), savedActiveRumor.getCluesRequired());
            getActiveRumor(savedActiveRumor.getRumorId()).setTimeRemaining(savedActiveRumor.getTimeRemaining());
        }
    }
}
