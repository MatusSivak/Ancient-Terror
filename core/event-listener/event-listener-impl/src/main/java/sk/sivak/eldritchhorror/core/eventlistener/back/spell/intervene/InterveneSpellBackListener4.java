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
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.CONFIRM_TEST;

public class InterveneSpellBackListener4 extends AbstractSpellBackListener {

    public InterveneSpellBackListener4(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2));

    }

    private void on0() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.CHOOSE_ONE);
        String combatantName = ((InvestigatorId) getData("combatantId")).toString();
        String[] buttonTexts = new String[]{
                "[#BAD]Discard this card[]",
                "[#BAD]"+combatantName+" gains\nMadness Condition[]"
        };
        ServicePlatform.get().getEncounterService().displayButtons(buttonTexts).subscribe(choice -> {
            if (choice == 0) {
                finishAndDiscard();
            } else {
                onGainMadnessCondition();
            }
        });
        ServicePlatform.get().getService().release();
    }


    private void on1() {
        TypewriterUtils.confirmInfos("[#BAD]Lose one Health[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseHealth(1);
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(getData("combatantId"));
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            ServicePlatform.get().getService().release();
        });
    }

    private void on2() {
        TypewriterUtils.confirmInfos("[#GOOD]Reduce the Monster's damage by one.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(getData("combatantId"));
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);

            ServicePlatform.get().getService().addEventCommand(in -> {
                addListener();
            });
            ServicePlatform.get().getService().release();
        });
    }

    private void addListener() {
        ServicePlatform.get().getService().startInsertingBeforeCommand(CONFIRM_TEST);
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().<TestData>addEventCommand(input -> {
            CombatData combatData = ServicePlatform.get().getTestFlavor().getFlavorData();
            combatData.setActualDamage(Math.max(1, combatData.getActualDamage()-1));
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTestService().updateMonsterDamage(combatData);
            ServicePlatform.get().getTestService().convertTo(TestData.class, () -> input);
            ServicePlatform.get().getService().release();
        });
        ServicePlatform.get().getService().release();
        ServicePlatform.get().getService().endInsertingAtCommand();
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

    private void onGainMadnessCondition() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(getData("combatantId"));
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
        ServicePlatform.get().getGameService().gainCondition(ConditionTrait.MADNESS);
        ServicePlatform.get().getService().release();
    }
}
