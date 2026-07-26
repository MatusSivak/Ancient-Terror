package sk.sivak.eldritchhorror.core.eventlistener.back.spell.arcaneinsight;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.util.List;

public class ArcaneInsightSpellBackListener3 extends AbstractSpellBackListener{

    public ArcaneInsightSpellBackListener3(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2));
    }

    private void on0() {
        if (EncounterUtils.getPossession(getData("investigatorId"), AssetTrait.TOME).isEmpty()) {
            TypewriterUtils.confirmInfos("[#BAD]That investigator discards all of his Clues[]").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                Runnable action = () -> {
                    int clueCount = ServicePlatform.get().getCluePool().getClueCount(getActiveInvestigatorId());
                    for (int i = 0; i < clueCount; i++) {
                        ServicePlatform.get().getTokenService().loseClue();
                    }
                };
                changeInvestigatorsAction(action);
                ServicePlatform.get().getService().release();
            });
        } else {
            TypewriterUtils.confirmInfos("That investigator has at least one Tome,\nso no additional effect.").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getService().release();
            });
        }
    }

    private void on1() {
        TypewriterUtils.confirmInfos("[#BAD]Lose one Health[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseHealth(1);
            ServicePlatform.get().getService().release();
        });
    }

    private void on2() {
        if (EncounterUtils.getPossession(getData("investigatorId"), AssetTrait.TOME).isEmpty()) {
            TypewriterUtils.confirmInfos("That investigator doesn't have a Tome,\nso no additional effect.").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getService().release();
            });
        } else {
            TypewriterUtils.confirmInfos("[#GOOD]That investigator gains one Clue[]").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                changeInvestigatorsAction(() -> ServicePlatform.get().getTokenService().gainClueFromPool());
                ServicePlatform.get().getService().release();
            });
        }
    }

    private void changeInvestigatorsAction(Runnable runnable) {
        InvestigatorId selectedInvestigatorId = getData("investigatorId");
        InvestigatorId activeInvestigatorId = getActiveInvestigatorId();
        if (selectedInvestigatorId == activeInvestigatorId) {
            runnable.run();
        } else {
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(selectedInvestigatorId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            ServicePlatform.get().getService().addEventCommand(in -> {
                runnable.run();
            });
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(activeInvestigatorId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
        }
    }

}
