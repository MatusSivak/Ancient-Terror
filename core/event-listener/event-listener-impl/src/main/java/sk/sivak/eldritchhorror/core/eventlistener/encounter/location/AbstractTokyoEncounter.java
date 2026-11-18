package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

/** Keeps an unsuccessful targeting attempt distinct from failing the encounter's test. */
public abstract class AbstractTokyoEncounter extends AbstractLocationEncounter {
    protected AbstractTokyoEncounter(int page) {
        super(page, LocationEncounter.LocationEncounterType.TOKYO);
    }

    protected boolean hasMonsterTargets(boolean includeEpic, int maximumToughness) {
        for (MonsterInfo monster : ServicePlatform.get().getMonsterCup().getMonsters()) {
            if ((includeEpic || !monster.isEpic()) && monster.getToughness() <= maximumToughness) return true;
        }
        return false;
    }

    protected TestPassFlavorInfoFailFlavorInfoTemplate withMonsterTargets(
            TestPassFlavorInfoFailFlavorInfoTemplate template, boolean includeEpic, int maximumToughness) {
        return hasMonsterTargets(includeEpic, maximumToughness) ? template : template.withPassWithoutEffect(2);
    }
}
