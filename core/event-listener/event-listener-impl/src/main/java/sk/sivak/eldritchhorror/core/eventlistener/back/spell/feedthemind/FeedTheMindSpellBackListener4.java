package sk.sivak.eldritchhorror.core.eventlistener.back.spell.feedthemind;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.util.DiscardThisSpellUnless;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.LoseImprovementData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.LinkedList;
import java.util.List;

public class FeedTheMindSpellBackListener4 extends AbstractSpellBackListener {

    public FeedTheMindSpellBackListener4(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2));
    }

    private void on0() {
        ServicePlatform.get().getEncounterService().displayButtons(
                "[#BAD]Discard this card[]",
                "[#BAD]Lose all Skill Improvement[]").subscribe(choice -> {
            if (choice == 0) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setSpellInfo(getSpellInfo());
                showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
                showCardRequest.setTitle("Discard " +getSpellInfo().getName()+ " Spell");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                Single<ShowCardResponse> displaySpell = ServicePlatform.get().getGameService().showCard(showCardRequest);

                displaySpell.subscribe(response ->
                        ServicePlatform.get().getService().discardSpellFromInvestigator(getActiveInvestigatorId(), getSpellInfo()));
                ServicePlatform.get().getService().release();
            } else {
                InvestigatorRead investigator = ServicePlatform.get().getInvestigators().getInvestigator(getActiveInvestigatorId());
                List<Stat> improvedSkills = new LinkedList<>();

                for (Stat stat : Stat.values()) {
                    if (investigator.getStatBonus(stat) == 0) {
                        continue;
                    }
                    improvedSkills.add(stat);
                }
                if (improvedSkills.isEmpty()) {
                    TypewriterUtils.confirmInfos("No Skill improved. Nothing to lose.").subscribe(() -> {
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    });
                } else {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    for (Stat improvedSkill : improvedSkills) {
                        ServicePlatform.get().getInvestigatorService().loseImprovement(new LoseImprovementData(improvedSkill, 2));
                    }
                    ServicePlatform.get().getService().release();
                }
            }
        });
    }

    private void on1() {
        TypewriterUtils.confirmInfos("[#BAD]The chosen investigator loses one Sanity.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            if (getData("selectedInvestigator").equals(getActiveInvestigatorId())) {
                ServicePlatform.get().getTokenService().loseSanity(1);
                ServicePlatform.get().getService().release();
            } else {
                InvestigatorId spellOwnerId = getActiveInvestigatorId();
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(getData("selectedInvestigator"));
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                ServicePlatform.get().getTokenService().loseSanity(1);
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(spellOwnerId);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                ServicePlatform.get().getService().release();
            }
        });

    }

    private void on2() {
        TypewriterUtils.confirmInfos("[#GOOD]The chosen investigator gains one Clue.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            if (getData("selectedInvestigator").equals(getActiveInvestigatorId())) {
                ServicePlatform.get().getTokenService().gainClueFromPool();
                ServicePlatform.get().getService().release();
            } else {
                InvestigatorId spellOwnerId = getActiveInvestigatorId();
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(getData("selectedInvestigator"));
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                ServicePlatform.get().getTokenService().gainClueFromPool();
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(spellOwnerId);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                ServicePlatform.get().getService().release();
            }
        });
    }


}
