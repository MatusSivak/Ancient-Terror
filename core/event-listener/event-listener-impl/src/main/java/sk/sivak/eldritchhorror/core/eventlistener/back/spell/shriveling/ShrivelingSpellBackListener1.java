package sk.sivak.eldritchhorror.core.eventlistener.back.spell.shriveling;

import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.util.DiscardThisSpellUnless;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.util.List;

public class ShrivelingSpellBackListener1 extends AbstractSpellBackListener{

    public ShrivelingSpellBackListener1(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2));
    }

    private void on0() {
        new DiscardThisSpellUnless(getSpellInfo(), getActiveInvestigatorId()).lose2Sanity();
    }

    private void on1() {
        TypewriterUtils.confirmInfos("No additional effect.").subscribe(() -> {
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        });
    }

    private void on2() {
        MonsterInfo monsterInfo = getData("monsterInfo");
        if (monsterInfo.isAlive()) {
            int amount = getTestData().getScore() - 2;
            TypewriterUtils.confirmInfos("[#GOOD]The chosen Monster loses Health equal to your test result instead.[]").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getMonsterService().dealDamageToMonster(monsterInfo, amount);
                ServicePlatform.get().getService().release();
            });
        } else {
            on1();
        }
    }
}
