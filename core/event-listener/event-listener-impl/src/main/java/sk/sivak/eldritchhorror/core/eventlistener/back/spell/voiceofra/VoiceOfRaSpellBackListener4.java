package sk.sivak.eldritchhorror.core.eventlistener.back.spell.voiceofra;

import rx.Single;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.service.data.ClueFocusHealthSanity;

import java.util.List;

public class VoiceOfRaSpellBackListener4 extends AbstractVoiceOfRaSpellBackListener {

    public VoiceOfRaSpellBackListener4(SpellInfo spellInfo) {
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
        TypewriterUtils.confirmInfos("[#BAD]Lose one Sanity[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseSanity(1);
            ServicePlatform.get().getService().release();
        });
    }

    private void on2() {
        Single<SpendData> canSpendHealthSingle = ServicePlatform.get().getTokenService().canSpend(0, 0, 1, 0);
        Single<SpendData> canSpendSanitySingle = ServicePlatform.get().getTokenService().canSpend(0, 0, 0, 1);

        canSpendHealthSingle.zipWith(canSpendSanitySingle, (canSpendHealth, canSpendSanity) ->
                new Boolean[]{canSpendHealth.hasEnough(), canSpendSanity.hasEnough()}).subscribe(canSpendHealthOrSanity -> {
            if (canSpendHealthOrSanity[0] && canSpendHealthOrSanity[1]) {
                TypewriterUtils.noYesQuestion("[#BAD]Spend one Health and Sanity[] to\n[#GOOD]immediately perform one Action[]?", () -> {
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                }, () -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    Single<SpendData> spendHealthSingle = ServicePlatform.get().getTokenService().spend(0, 0, 1, 0);
                    Single<SpendData> spendSanitySingle = ServicePlatform.get().getTokenService().spend(0, 0, 0, 1);

                    spendHealthSingle.zipWith(spendSanitySingle, (spendHealth, spendSanity) -> {
                        if (spendHealth.hasEnough() && spendSanity.hasEnough()) {
                            return (Action0) () -> {
                                spendHealth.pay();
                                spendSanity.pay();
                            };
                        } else {
                            return null;
                        }
                    }).subscribe(action -> {
                        if (action == null) {
                            return;
                        }
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getBasicActionService().addFreeAction();
                        action.call();
                        ServicePlatform.get().getGameService().justPerformAction();
                        ServicePlatform.get().getService().release();
                    });
                    ServicePlatform.get().getService().release();
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
}
