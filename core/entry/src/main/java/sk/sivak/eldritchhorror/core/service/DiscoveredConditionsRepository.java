package sk.sivak.eldritchhorror.core.service;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;

import java.util.List;

public class DiscoveredConditionsRepository implements DiscoveredRepository<ConditionId>{

    private final DiscoveredRepositoryTemplate<ConditionId, ConditionIdHasManyValues> template;

    private static class ConditionIdHasManyValues implements HasManyValues<ConditionId> {

        @Override
        public ConditionId[] values() {
            return ConditionId.values();
        }
    }

    public DiscoveredConditionsRepository() {
        template = new DiscoveredRepositoryTemplate<>(new ConditionIdHasManyValues());
        template.setKeyName("discoveredConditions");
    }


    @Override
    public void saveDiscovered(List<ConditionId> discoveredConditions) {
        template.saveData(discoveredConditions);
    }

    @Override
    public List<ConditionId> getDiscovered() {
        return template.getDiscovered();
    }
}
