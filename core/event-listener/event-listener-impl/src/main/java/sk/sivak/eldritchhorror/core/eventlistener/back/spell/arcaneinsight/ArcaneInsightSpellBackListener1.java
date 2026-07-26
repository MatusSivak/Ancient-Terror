package sk.sivak.eldritchhorror.core.eventlistener.back.spell.arcaneinsight;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.util.List;

public class ArcaneInsightSpellBackListener1 extends AbstractSpellBackListener{

    public ArcaneInsightSpellBackListener1(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2));
    }

    private void on0() {
        if (EncounterUtils.getPossession(getActiveInvestigatorId(), AssetTrait.TOME).isEmpty()) {
            TypewriterUtils.confirmInfos("[#BAD]Discard this card[]").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                justDiscard();
                ServicePlatform.get().getService().release();
            });
        } else {
            TypewriterUtils.confirmInfos("You have at least one Tome,\nso no additional effect.").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getService().release();
            });
        }
    }

    private void on1() {
        TypewriterUtils.confirmInfos("[#BAD]Lose one Sanity.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseSanity(1);
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
            TypewriterUtils.confirmInfos("[#GOOD]That investigator gains one Spell[]").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                changeInvestigatorsAction(() -> ServicePlatform.get().getGameService().gainSpell());
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

    private void changeInvestigatorsAction(Runnable runnable) {
        InvestigatorId selectedInvestigatorId = getData("investigatorId");
        InvestigatorId activeInvestigatorId = getActiveInvestigatorId();
        if (selectedInvestigatorId == activeInvestigatorId) {
            runnable.run();
        } else {
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(selectedInvestigatorId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            runnable.run();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(activeInvestigatorId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
        }
    }


}
