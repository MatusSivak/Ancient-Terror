package sk.sivak.eldritchhorror.core.eventlistener.back.spell.wither;

import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.util.DiscardThisSpellUnless;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.util.List;

public class WitherSpellBackListener1 extends AbstractSpellBackListener {

    public WitherSpellBackListener1(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1)
                .withBeforeActionIsExecutedAction(() -> setData("statBonus", 5)));
    }

    private void on0() {
        new DiscardThisSpellUnless(getSpellInfo(), getActiveInvestigatorId()).lose2Sanity();
    }


    private void on1() {
        TypewriterUtils.confirmInfos("[#BAD]Lose one Sanity[]", "[#GOOD]Gain plus 5 Strength instead[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseSanity(1);
            ServicePlatform.get().getService().release();
        });
    }
}
