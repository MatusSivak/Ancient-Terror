package sk.sivak.eldritchhorror.core.eventlistener.back.spell.blessingofisis;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
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

public class BlessingOfIsisSpellBackListener4 extends AbstractSpellBackListener {

    public BlessingOfIsisSpellBackListener4(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2));
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
        TypewriterUtils.confirmInfos("[#BAD]The chosen investigator loses one Sanity[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            boolean wasChanged = changeActiveInvestigator(getData("selectedInvestigator"));
            ServicePlatform.get().getTokenService().loseSanity(1);
            if (wasChanged) {
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(getActiveInvestigatorId());
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            }
            ServicePlatform.get().getService().release();
        });
    }

    private void on2() {
        TypewriterUtils.confirmInfos("[#GOOD]The chosen investigator\n" +
                "recovers one Health and one Sanity[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            boolean wasChanged = changeActiveInvestigator(getData("selectedInvestigator"));
            ServicePlatform.get().getTokenService().gainHealth(1);
            ServicePlatform.get().getTokenService().gainSanity(1);
            if (wasChanged) {
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(getActiveInvestigatorId());
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            }
            ServicePlatform.get().getService().release();
        });
    }

    private boolean changeActiveInvestigator(InvestigatorId newActiveInvestigator) {
        if (getActiveInvestigatorId() != newActiveInvestigator) {
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(newActiveInvestigator);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            return true;
        }
        return false;
    }

}
