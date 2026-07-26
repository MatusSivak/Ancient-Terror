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

public class AwakenYogSothothCultistMonsterListener extends AbstractMonsterListener{

    private BeforeDamageTestListener beforeDamageTestListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        AbstractMonsterInfo editableMonsterInfo = (AbstractMonsterInfo) monsterInfo;
        editableMonsterInfo.setDamage(1);
        editableMonsterInfo.setDamageTestModifier(-1);
        editableMonsterInfo.setDamageTestType(Stat.STRENGTH);
        editableMonsterInfo.setHorror(null);
        editableMonsterInfo.setToughness(1);
        editableMonsterInfo.setCurrentHealth(monsterInfo.getToughness());
        editableMonsterInfo.setSpecialText("Before resolving the Strength test,\n" +
                "lose one Sanity for each Spell you have.");

        beforeDamageTestListener = new BeforeDamageTestListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(beforeDamageTestListener, DirectEvent.BEFORE_DAMAGE_TEST);
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
        ServicePlatform.get().getEventQueue().unregisterListener(beforeDamageTestListener);
    }
}
