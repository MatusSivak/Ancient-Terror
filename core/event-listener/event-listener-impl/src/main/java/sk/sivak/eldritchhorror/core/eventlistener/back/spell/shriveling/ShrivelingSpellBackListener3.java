package sk.sivak.eldritchhorror.core.eventlistener.back.spell.shriveling;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.util.DiscardThisSpellUnless;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.util.List;

public class ShrivelingSpellBackListener3 extends AbstractSpellBackListener{

    public ShrivelingSpellBackListener3(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2));
    }

    private void on0() {
        new DiscardThisSpellUnless(getSpellInfo(), getActiveInvestigatorId()).gainCondition(ConditionId.POISONED);
    }

    private void on1() {
        TypewriterUtils.confirmInfos("[#BAD]Lose one Health.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseHealth(1);
            ServicePlatform.get().getService().release();
        });
    }

    private void on2() {
        MonsterInfo monsterInfo = getData("monsterInfo");
        if (monsterInfo.isAlive()) {
            TypewriterUtils.confirmInfos("[#GOOD]The chosen Monster loses four Health instead.[]").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getMonsterService().dealDamageToMonster(monsterInfo, 2);
                ServicePlatform.get().getService().release();
            });
        } else {
            TypewriterUtils.confirmInfos("No additional effect.").subscribe(() -> {
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            });
        }
    }
}
