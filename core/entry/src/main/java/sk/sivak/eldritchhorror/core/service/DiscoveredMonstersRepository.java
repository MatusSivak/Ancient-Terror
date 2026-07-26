package sk.sivak.eldritchhorror.core.service;

import sk.sivak.eldritchhorror.core.constants.monster.MonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.epic.EpicMonsterId;

import java.util.LinkedList;
import java.util.List;

public class DiscoveredMonstersRepository implements DiscoveredRepository<MonsterId>{

    private final DiscoveredRepositoryTemplate<NonEpicMonsterId, NonEpicMonsterIdHasManyValues> regularMonstersTemplate;
    private final DiscoveredRepositoryTemplate<EpicMonsterId, EpicMonsterIdHasManyValues> epicMonstersTemplate;

    private static class NonEpicMonsterIdHasManyValues implements HasManyValues<NonEpicMonsterId> {

        @Override
        public NonEpicMonsterId[] values() {
            return NonEpicMonsterId.values();
        }
    }

    private static class EpicMonsterIdHasManyValues implements HasManyValues<EpicMonsterId> {

        @Override
        public EpicMonsterId[] values() {
            return EpicMonsterId.values();
        }
    }

    public DiscoveredMonstersRepository() {
        regularMonstersTemplate = new DiscoveredRepositoryTemplate<>(new NonEpicMonsterIdHasManyValues());
        regularMonstersTemplate.setKeyName("discoveredRegularMonsters");
        epicMonstersTemplate = new DiscoveredRepositoryTemplate<>(new EpicMonsterIdHasManyValues());
        epicMonstersTemplate.setKeyName("discoveredEpicMonsters");
    }


    @Override
    public void saveDiscovered(List<MonsterId> discoveredMonsters) {
    }

    @Override
    public List<MonsterId> getDiscovered() {
        List<NonEpicMonsterId> discoveredRegular = regularMonstersTemplate.getDiscovered();
        List<EpicMonsterId> discoveredEpic = epicMonstersTemplate.getDiscovered();
        List<MonsterId> result = new LinkedList<>();
        result.addAll(discoveredEpic);
        result.addAll(discoveredRegular);
        return result;
    }
}
