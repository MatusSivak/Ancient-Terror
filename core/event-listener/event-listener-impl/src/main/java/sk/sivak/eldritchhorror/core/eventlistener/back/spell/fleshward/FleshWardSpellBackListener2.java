package sk.sivak.eldritchhorror.core.eventlistener.back.spell.fleshward;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.token.LoseTokenData;

import java.util.List;

public class FleshWardSpellBackListener2 extends AbstractSpellBackListener {

    public FleshWardSpellBackListener2(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0, this::showTargetInvestigator));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2, true));
    }

    private void showTargetInvestigator() {
        InvestigatorId targetInvestigatorId = getData("targetInvestigatorId");
        InvestigatorId spellOwnerId = getData("spellOwnerId");
        if (spellOwnerId != targetInvestigatorId) {
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(targetInvestigatorId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
        }
    }

    public void on0() {
        InvestigatorId targetInvestigatorId = getData("targetInvestigatorId");
        if (ServicePlatform.get().getConditionsDeck().hasCondition(targetInvestigatorId, ConditionId.INTERNAL_INJURY)) {
            TypewriterUtils.confirmInfos("[#BAD]Lose one Health[]").subscribe(this::loseOneHealth);
        } else {
            ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.CHOOSE_ONE);
            String[] buttonTexts = new String[] {
                    "[#BAD]Lose one Health[]",
                    "[#BAD]Gain Internal Injury Condition[]"
            };
            ServicePlatform.get().getEncounterService().displayButtons(buttonTexts).subscribe(choice -> {
                if (choice == 0) {
                    loseOneHealth();
                } else {
                    onGainInternalInjury();
                }
            });
        }
    }

    private void loseOneHealth() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        ServicePlatform.get().getTokenService().loseHealth(1);
        ServicePlatform.get().getService().release();
    }

    private void onGainInternalInjury() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        ServicePlatform.get().getGameService().gainCondition(ConditionId.INTERNAL_INJURY);
        ServicePlatform.get().getService().release();
    }


    private void on1() {
        TypewriterUtils.confirmInfos("No additional effect.").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getService().release();
        });
    }

    private void on2() {
        TypewriterUtils.confirmInfos("[#GOOD]Prevent that investigator from losing Health up to your test result instead[]").subscribe(() -> {
            LoseTokenData loseTokenData = getData("loseTokenData");
            int score = getTestData().getScore();
            loseTokenData.setAmount(Math.max(0, loseTokenData.getAmount() - score));
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getService().release();
        });
    }
}
