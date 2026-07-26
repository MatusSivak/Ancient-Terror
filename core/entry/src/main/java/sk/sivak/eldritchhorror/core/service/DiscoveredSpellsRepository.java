package sk.sivak.eldritchhorror.core.service;

import sk.sivak.eldritchhorror.core.constants.spell.SpellId;

import java.util.List;

public class DiscoveredSpellsRepository implements DiscoveredRepository<SpellId>{

    private final DiscoveredRepositoryTemplate<SpellId, SpellIdHasManyValues> template;

    private static class SpellIdHasManyValues implements HasManyValues<SpellId> {

        @Override
        public SpellId[] values() {
            return SpellId.values();
        }
    }

    public DiscoveredSpellsRepository() {
        template = new DiscoveredRepositoryTemplate<>(new SpellIdHasManyValues());
        template.setKeyName("discoveredSpells");
    }


    @Override
    public void saveDiscovered(List<SpellId> discoveredSpells) {
        template.saveData(discoveredSpells);
    }

    @Override
    public List<SpellId> getDiscovered() {
        return template.getDiscovered();
    }
}
