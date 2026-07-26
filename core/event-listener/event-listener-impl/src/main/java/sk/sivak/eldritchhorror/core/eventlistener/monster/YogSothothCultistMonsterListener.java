package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;
import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.DEFEAT_MONSTER;

public class YogSothothCultistMonsterListener extends AbstractMonsterListener{

    private AfterAdvanceDoomListener afterAdvanceDoomListener;
    private BeforeDefeatMonsterListener beforeDefeatMonsterListener;
    private BeforeDamageTestListener beforeDamageTestListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        AbstractMonsterInfo editableMonsterInfo = (AbstractMonsterInfo) monsterInfo;
        editableMonsterInfo.setDamage(1);
        editableMonsterInfo.setDamageTestType(Stat.STRENGTH);
        editableMonsterInfo.setHorror(null);
        editableMonsterInfo.setToughness(1);
        editableMonsterInfo.setCurrentHealth(monsterInfo.getToughness());
        editableMonsterInfo.setSpecialText("If you defeat this Monster during a Combat Encounter,\nlose one Sanity and gain one Spell.");



        afterAdvanceDoomListener = new AfterAdvanceDoomListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterAdvanceDoomListener, BeforeAfterEvent.ADVANCE_DOOM);

        beforeDefeatMonsterListener = new BeforeDefeatMonsterListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeDefeatMonsterListener, DEFEAT_MONSTER);
    }

    private class AfterAdvanceDoomListener extends EventListenerImpl<Void> {

        @Override
        public void onNotify(Void eventData) {
            if (ServicePlatform.get().getDoomTrack().getCurrentDoom() > 0) {
                return;
            }
            ((AbstractMonsterInfo) monsterInfo).setToughness(1);
            ((AbstractMonsterInfo) monsterInfo).setDamageTestModifier(-1);
            ((AbstractMonsterInfo) monsterInfo).setCurrentHealth(1);
            ((AbstractMonsterInfo) monsterInfo).setSpecialText("Before resolving the Strength test,\n" +
                    "lose one Sanity for each Spell you have.");

            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getEventQueue().unregisterListener(afterAdvanceDoomListener);
                ServicePlatform.get().getEventQueue().unregisterListener(beforeDefeatMonsterListener);
            });

            beforeDamageTestListener = new BeforeDamageTestListener();
            ServicePlatform.get().getEventQueue().addDirectEventListener(beforeDamageTestListener, DirectEvent.BEFORE_DAMAGE_TEST);
        }

        @Override
        public Class<Void> getDataClass() {
            return Void.class;
        }
    }


    private class BeforeDamageTestListener extends EventListenerImpl<CombatData> {

        @Override
        public void onNotify(CombatData eventData) {
            if (eventData.getMonsterInfo() != monsterInfo) {
                return;
            }
            List<SpellInfo> spells = ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId());
            if (spells.isEmpty()) {
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            Question<Object> question = new Question<>();
            question.setTitle("You will lose "+spells.size()+" Sanity.");
            Question.SelectComponentsTableData<SpellInfo> selectComponentsTableData = new Question.SelectComponentsTableData<>();
            selectComponentsTableData.setAvailableKeys(spells);
            selectComponentsTableData.setType(Question.SelectComponentsTableType.CARDS);
            question.setSelectComponentsTableData(selectComponentsTableData);
            question.setOptions(Question.Option.okOption);
            ServicePlatform.get().getGameService().ask(question).subscribe(whatever -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getTokenService().loseSanity(spells.size());
                ServicePlatform.get().getService().convertTo(CombatData.class, () -> eventData);
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();

        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(afterAdvanceDoomListener);
        ServicePlatform.get().getEventQueue().unregisterListener(beforeDefeatMonsterListener);
        if (beforeDamageTestListener != null) {
            ServicePlatform.get().getEventQueue().unregisterListener(beforeDamageTestListener);
        }
    }

    private class BeforeDefeatMonsterListener extends EventListenerImpl<DefeatMonsterData> {

        @Override
        public void onNotify(DefeatMonsterData eventData) {
            if (!eventData.isInCombat()) {
                return;
            }
            if (eventData.getMonsterInfo() != monsterInfo) {
                return;
            }

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            ServicePlatform.get().getTokenService().loseSanity(1);
            ServicePlatform.get().getGameService().gainSpell();
            ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<DefeatMonsterData> getDataClass() {
            return DefeatMonsterData.class;
        }
    }
}
