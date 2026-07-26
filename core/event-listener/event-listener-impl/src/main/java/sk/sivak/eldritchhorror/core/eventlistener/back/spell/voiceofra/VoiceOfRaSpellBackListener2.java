package sk.sivak.eldritchhorror.core.eventlistener.back.spell.voiceofra;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.util.List;

public class VoiceOfRaSpellBackListener2 extends AbstractVoiceOfRaSpellBackListener {

    public VoiceOfRaSpellBackListener2(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2));
    }

    private void on0() {
        ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.CHOOSE_ONE);
        ServicePlatform.get().getEncounterService().displayButtons(
                "[#BAD]Discard this card[]",
                "[#BAD]Lose one Health[]\n[#BAD]Gain an Injury Condition[]").subscribe(choice -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            if (choice == 0) {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setSpellInfo(getSpellInfo());
                showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
                showCardRequest.setTitle("Discard " + getSpellInfo().getName() + " Spell");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                Single<ShowCardResponse> displaySpell = ServicePlatform.get().getGameService().showCard(showCardRequest);

                displaySpell.subscribe(response ->
                        ServicePlatform.get().getService().discardSpellFromInvestigator(getActiveInvestigatorId(), getSpellInfo()));
            } else {
                ServicePlatform.get().getTokenService().loseHealth(1);
                ServicePlatform.get().getGameService().gainCondition(ConditionTrait.INJURY);

            }
            ServicePlatform.get().getService().release();
        });
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
        ServicePlatform.get().getTokenService().canSpend(0,0,1,0).subscribe(canSpend -> {
            if (!canSpend.hasEnough()) {
                TypewriterUtils.confirmInfos("No additional effect.").subscribe(() -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    ServicePlatform.get().getService().release();
                });
            } else {
                TypewriterUtils.noYesQuestion("[#BAD]Spend one Health[] to\n[#GOOD]Recover one Sanity[]?", () -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    ServicePlatform.get().getService().release();
                }, () -> {
                    ServicePlatform.get().getTokenService().spend(0,0,1,0).subscribe(spend -> {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        if (spend.hasEnough()) {
                            spend.pay();
                            ServicePlatform.get().getTokenService().gainSanity(1);
                        }
                        ServicePlatform.get().getService().release();
                    });
                });
            }
        });

    }


}
