package sk.sivak.eldritchhorror.core.eventlistener.back.spell.plumbthevoid;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.util.DiscardThisSpellUnless;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.util.List;

public class PlumbTheVoidSpellBackListener2 extends AbstractSpellBackListener {

    public PlumbTheVoidSpellBackListener2(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0, this::showSpellOwnerAction));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
    }

    private void on0() {
        new DiscardThisSpellUnless(getSpellInfo(), getActiveInvestigatorId()).gainCondition(ConditionId.AMNESIA);
    }

    private void showSpellOwnerAction() {
        InvestigatorId spellOwnerId = getActiveInvestigatorId();
        InvestigatorId selectedInvestigator = getData("selectedInvestigator");
        if (spellOwnerId != selectedInvestigator) {
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
        }
    }

    private void on1() {
        TypewriterUtils.confirmInfos("No additional effect.").subscribe(() -> {
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        });

    }

}
