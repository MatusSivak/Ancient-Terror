package sk.sivak.eldritchhorror.core.eventlistener.back.spell.clairvoyance;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.util.List;

public class ClairvoyanceSpellBackListener4 extends AbstractSpellBackListener {

    public ClairvoyanceSpellBackListener4(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0, this::showActiveInvestigator));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1, this::showActiveInvestigator));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2));
    }

    private void showActiveInvestigator() {
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
    }

    private void on0() {
        if (getTestData().hasRolledAny1()) {
            TypewriterUtils.confirmInfos("[#BAD]Discard this card[]").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                justDiscard();
                ServicePlatform.get().getService().release();
            });
        } else {
            TypewriterUtils.confirmInfos("No additional effect.").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getService().release();
            });
        }
    }

    private void justDiscard() {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setSpellInfo(getSpellInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setTitle("Discard " +getSpellInfo().getName()+ " Spell");
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        Single<ShowCardResponse> displaySpell = ServicePlatform.get().getGameService().showCard(showCardRequest);

        displaySpell.subscribe(response ->
                ServicePlatform.get().getService().discardSpellFromInvestigator(getActiveInvestigatorId(), getSpellInfo()));
    }


    private void on1() {
        if (ServicePlatform.get().getConditionsDeck().hasCondition(getActiveInvestigatorId(), ConditionId.PARANOIA)) {
            TypewriterUtils.confirmInfos("[#BAD]Lose one Sanity.[]").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getTokenService().loseSanity(1);
                ServicePlatform.get().getService().release();
            });
        } else {
            ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.CHOOSE_ONE);
            ServicePlatform.get().getEncounterService().displayButtons(
                    "[#BAD]Lose one Sanity[]",
                    "[#BAD]Gain a Paranoia Condition[]").subscribe(choice -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                if (choice == 0) {
                    ServicePlatform.get().getTokenService().loseSanity(1);
                } else {
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.PARANOIA);
                }
                ServicePlatform.get().getService().release();
            });
        }
    }

    private void on2() {
        TypewriterUtils.confirmInfos("[#GOOD]Spawn one Clue.").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().spawnClues(1).subscribe();
            ServicePlatform.get().getService().release();
        });
    }
}
