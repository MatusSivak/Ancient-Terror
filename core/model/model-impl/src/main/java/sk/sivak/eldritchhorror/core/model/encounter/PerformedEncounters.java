package sk.sivak.eldritchhorror.core.model.encounter;

import java8.features.stream.Stream;
import java8.features.util.MapUtils;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.model.PerformedEncountersWrite;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PerformedEncounters implements PerformedEncountersWrite {
    private Map<InvestigatorId, List<EncounterData>> map;
    private Map<InvestigatorId, Boolean> disabledByForceMap;

    @Override
    public List<EncounterType> getPerformedEncounters(InvestigatorId investigatorId) {
        return Stream.collectToList(Stream.map(map.get(investigatorId), encounterData -> encounterData.encounterType));
    }

    @Override
    public boolean isPerformed(InvestigatorId investigatorId, EncounterType encounterType, String encounterId) {
        List<EncounterData> encounterDataList = MapUtils.getOrDefault(map, investigatorId, new LinkedList<>());
        return encounterDataList.contains(new EncounterData(encounterType, encounterId));
    }

    @Override
    public void perform(InvestigatorId investigatorId, EncounterType encounterType, String encounterId) {
        MapUtils.computeIfAbsent(map, investigatorId, key -> new LinkedList<>());
        map.get(investigatorId).add(new EncounterData(encounterType, encounterId));
    }

    @Override
    public boolean canHaveAnotherEncounter(InvestigatorId investigatorId) {
        if (disabledByForceMap.get(investigatorId) != null && disabledByForceMap.get(investigatorId)) {
            return false;
        }
        List<EncounterData> encounterData = map.get(investigatorId);
        for (EncounterData encounterDatum : encounterData) {
            if (encounterDatum.encounterType != EncounterType.COMBAT) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canSkip(InvestigatorId investigatorId, boolean isAnyMonsterOnThisSpace) {
        if (isAnyMonsterOnThisSpace) {
            return false;
        }
        List<EncounterData> encounterData = MapUtils.computeIfAbsent(map, investigatorId, key -> new LinkedList<>());
        for (EncounterData encounterDatum : encounterData) {
            if (encounterDatum.encounterType == EncounterType.COMBAT) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void reset() {
        map = new HashMap<>();
        disabledByForceMap = new HashMap<>();
    }

    @Override
    public void disableAnotherEncounter(InvestigatorId investigatorId) {
        disabledByForceMap.put(investigatorId, true);
    }

    @Override
    public boolean hasEncounteredAnything(InvestigatorId investigatorId) {
        return !MapUtils.getOrDefault(map, investigatorId, new LinkedList<>()).isEmpty();
    }

    private class EncounterData {
        private EncounterType encounterType;
        private String encounterId;

        private EncounterData(EncounterType encounterType, String encounterId) {
            this.encounterType = encounterType;
            this.encounterId = encounterId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EncounterData that = (EncounterData) o;

            if (encounterType != that.encounterType) return false;
            return encounterId != null ? encounterId.equals(that.encounterId) : that.encounterId == null;
        }

        @Override
        public int hashCode() {
            int result = encounterType.hashCode();
            result = 31 * result + (encounterId != null ? encounterId.hashCode() : 0);
            return result;
        }
    }
}
