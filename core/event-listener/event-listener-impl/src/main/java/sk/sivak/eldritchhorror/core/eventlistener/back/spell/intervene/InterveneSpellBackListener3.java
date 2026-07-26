package sk.sivak.eldritchhorror.core.eventlistener.back.spell.intervene;

import rx.Single;
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

import java.util.List;

public class InterveneSpellBackListener3 extends AbstractSpellBackListener {

    public InterveneSpellBackListener3(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2)
                .withBeforeActionIsExecutedAction(() -> setData("dicePoolBonus", 1)));

    }

    private void on0() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.CHOOSE_ONE);
        String combatantName = ((InvestigatorId) getData("combatantId")).toString();
        String[] buttonTexts = new String[]{
                "[#BAD]Discard this card[]",
                "[#BAD]"+combatantName+" gains\nInjury Condition[]"
        };
        ServicePlatform.get().getEncounterService().displayButtons(buttonTexts).subscribe(choice -> {
            if (choice == 0) {
                finishAndDiscard();
            } else {
                onGainInjuryCondition();
            }
        });
        ServicePlatform.get().getService().release();
    }


    private void on1() {
        String combatantName = ((InvestigatorId) getData("combatantId")).toString();
        TypewriterUtils.confirmInfos(
                "[#BAD]"+combatantName +" loses one Sanity.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(getData("combatantId"));
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            ServicePlatform.get().getTokenService().loseSanity(1);
            ServicePlatform.get().getService().release();
        });
    }

    private void on2() {
        String combatantName = ((InvestigatorId) getData("combatantId")).toString();
        TypewriterUtils.confirmInfos(
                "[#GOOD]"+combatantName +" rolls additional die.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(getData("combatantId"));
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            ServicePlatform.get().getService().release();
        });
    }

    private void finishAndDiscard() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        justDiscard();
        ServicePlatform.get().getService().release();
    }

    private void justDiscard() {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setSpellInfo(getSpellInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setTitle("Discard " +getSpellInfo().getName()+ " Spell");
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        Single<ShowCardResponse> displaySpell = ServicePlatform.get().getGameService().showCard(showCardRequest);

        displaySpell.subscribe(response -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().discardSpellFromInvestigator(getActiveInvestigatorId(), getSpellInfo());
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(getData("combatantId"));
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            ServicePlatform.get().getService().release();

        });
    }

    private void onGainInjuryCondition() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(getData("combatantId"));
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
        ServicePlatform.get().getGameService().gainCondition(ConditionTrait.INJURY);
        ServicePlatform.get().getService().release();
    }
}
