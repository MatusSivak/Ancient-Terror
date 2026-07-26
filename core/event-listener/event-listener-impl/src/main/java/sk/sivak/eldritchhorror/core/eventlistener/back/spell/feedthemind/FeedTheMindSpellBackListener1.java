package sk.sivak.eldritchhorror.core.eventlistener.back.spell.feedthemind;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.util.DiscardThisSpellUnless;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.util.List;

public class FeedTheMindSpellBackListener1 extends AbstractSpellBackListener {

    public FeedTheMindSpellBackListener1(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
    }

    private void on0() {
        new DiscardThisSpellUnless(getSpellInfo(), getActiveInvestigatorId()).lose2Sanity();
    }


    private void on1() {
        TypewriterUtils.confirmInfos("[#GOOD]Improve one additional Skill.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            InvestigatorId spellOwnerId = getActiveInvestigatorId();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(getData("selectedInvestigator"));
            ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
            ServicePlatform.get().getInvestigatorService().improveSkill(null);
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(spellOwnerId);
            ServicePlatform.get().getService().release();
        });
    }
}
