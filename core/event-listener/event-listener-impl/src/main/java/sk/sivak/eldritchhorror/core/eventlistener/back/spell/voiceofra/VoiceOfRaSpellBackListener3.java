package sk.sivak.eldritchhorror.core.eventlistener.back.spell.voiceofra;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.util.DiscardThisSpellUnless;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.service.data.ClueFocusHealthSanity;

import java.util.List;

import static java.util.Arrays.asList;

public class VoiceOfRaSpellBackListener3 extends AbstractVoiceOfRaSpellBackListener {

    public VoiceOfRaSpellBackListener3(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2));
    }

    private void on0() {
        new DiscardThisSpellUnless(getSpellInfo(), getActiveInvestigatorId()).spendOneFocus();
    }

    private void on1() {
        TypewriterUtils.confirmInfos("[#BAD]Lose one Sanity[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseSanity(1);
            ServicePlatform.get().getService().release();
        });
    }

    private void on2() {
        if (ServicePlatform.get().getInvestigators().getActiveInvestigator().getFocusTokens() == 2) {
            TypewriterUtils.confirmInfos("No additional effect.").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getService().release();
            });
            return;
        }
        Single<SpendData> canSpendHealthSingle = ServicePlatform.get().getTokenService().canSpend(0, 0, 1, 0);
        Single<SpendData> canSpendSanitySingle = ServicePlatform.get().getTokenService().canSpend(0, 0, 0, 1);

        canSpendHealthSingle.zipWith(canSpendSanitySingle, (canSpendHealth, canSpendSanity) ->
                new Boolean[]{canSpendHealth.hasEnough(), canSpendSanity.hasEnough()}).subscribe(canSpendHealthOrSanity -> {
            if (canSpendHealthOrSanity[0] || canSpendHealthOrSanity[1]) {
                TypewriterUtils.noYesQuestion("[#BAD]Spend one Health or Sanity[]\nto [#GOOD]gain one Focus[]?", () -> {
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                }, () -> {
                    if (canSpendHealthOrSanity[0] && canSpendHealthOrSanity[1]) {
                        ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.CHOOSE_ONE);
                        String[] buttonTexts = new String[]{
                                "[#BAD]Spend one Health[]",
                                "[#BAD]Spend one Sanity[]"
                        };
                        ServicePlatform.get().getEncounterService().displayButtons(buttonTexts).subscribe(choice -> {
                            if (choice == 0) {
                                spendHealthOrSanity(new ClueFocusHealthSanity(0,0,1,0));
                            } else {
                                spendHealthOrSanity(new ClueFocusHealthSanity(0,0,0,1));
                            }
                        });
                    } else if (canSpendHealthOrSanity[0]) { // spend health
                        spendHealthOrSanity(new ClueFocusHealthSanity(0,0,1,0));
                    } else if (canSpendHealthOrSanity[1]) { // spend sanity
                        spendHealthOrSanity(new ClueFocusHealthSanity(0,0,0,1));
                    }
                });
            } else {
                TypewriterUtils.confirmInfos("No additional effect.").subscribe(() -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    ServicePlatform.get().getService().release();
                });
            }
        });
    }

    private void spendHealthOrSanity(ClueFocusHealthSanity clueFocusHealthSanity) {
        ServicePlatform.get().getTokenService().spend(clueFocusHealthSanity).subscribe(spend -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            if (spend.hasEnough()) {
                spend.pay();
                ServicePlatform.get().getTokenService().gainFocus();
            }
            ServicePlatform.get().getService().release();
        });
    }
}
