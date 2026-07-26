package sk.sivak.eldritchhorror.core.eventtype;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventtype.data.CollectAvailableActionsData;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.EncounterResult;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.MysteryEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.RumorEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.spell.ResolvedSpellData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.AddOneToDieResultData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

/**
 * @author msivak
 */
public enum DirectEvent {


    TEST_START(TestData.class),
    INIT_INVESTIGATORS(InvestigatorId.class),
    REGISTER_USABLE_ASSETS(TestData.class),
    REGISTER_BONUS_DICE(TestData.class),
    REROLL_USING_ASSETS_1(TestData.class),
    REROLL_USING_ASSETS_2(TestData.class),
    REROLL_USING_ASSETS_3(TestData.class),
    UPDATE_SCORE_USING_ASSETS(TestData.class),
    ADD_ONE_TO_DIE_RESULT(AddOneToDieResultData.class),
    COLLECT_COMMON_ENCOUNTERS(AvailableEncounters.class),
    DISABLE_OR_REMOVE_ACTIONS(CollectAvailableActionsData.class),
    DISABLE_ENCOUNTERS(AvailableEncounters.class),
    INIT_ACTION_BUTTON(Void.class),
    REENABLE_DISABLED_ABILITIES(Void.class),
    ENCOUNTER_ACTIVE_MYSTERY(MysteryEncounter.class),
    ENCOUNTER_ONGOING_RUMOR(RumorEncounter.class),
    INVESTIGATOR_FINISHED_ENCOUNTER(EncounterResult.class),
    PERFORM_ACTION_START(Object.class),
    BEFORE_DAMAGE_TEST(CombatData.class),
    SANITY_LOST_IN_HORROR_CHECK(CombatData.class),
    JUST_AFTER_DAMAGE_TEST(CombatData.class),
    BEFORE_HORROR_TEST(CombatData.class),
    OMEN_CHANGED(OmenInfo.class),
    SPELL_RESOLVED(ResolvedSpellData.class),


    RECKONING_MONSTER(Object.class),
    RECKONING_ANCIENT_ONE(Object.class),
    RECKONING_RUMORS(Object.class),
    RECKONING_CARDS_1(Object.class),
    RECKONING_CARDS_2(Object.class),
    RECKONING_CARDS_3(Object.class);

    private Class<?> dataClass;

    DirectEvent(Class<?> dataClass) {
        this.dataClass = dataClass;
    }

    public Class<?> getDataClass() {
        return dataClass;
    }
}
