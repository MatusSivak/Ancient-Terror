package sk.sivak.eldritchhorror.core.action.impl.encounter;

import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.Encounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.SkipEncounter;

import java.util.Arrays;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class SelectEncounterActionTest {

    @Test public void startsTheOnlyEnabledEncounter() {
        Encounter only = encounter(true);
        assertSame(only, SelectEncounterAction.automaticChoice(Arrays.asList(encounter(false), only, encounter(false))));
    }

    @Test public void asksWhenSeveralEncountersAreEnabled() {
        assertNull(SelectEncounterAction.automaticChoice(Arrays.asList(encounter(true), encounter(true))));
    }

    @Test public void leavesFullyDisabledListsToTheExistingFallback() {
        assertNull(SelectEncounterAction.automaticChoice(Arrays.asList(encounter(false), encounter(false))));
    }

    private static Encounter encounter(boolean enabled) {
        Encounter encounter = new SkipEncounter();
        EncounterButtonData data = new EncounterButtonData(encounter.getUuid());
        if (!enabled) data.disable("encounter.disabled.alreadyEncountered");
        encounter.setEncounterButtonData(data);
        return encounter;
    }
}
