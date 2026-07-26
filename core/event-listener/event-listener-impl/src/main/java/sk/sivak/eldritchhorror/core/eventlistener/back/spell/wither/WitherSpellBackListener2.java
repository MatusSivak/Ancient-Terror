package sk.sivak.eldritchhorror.core.eventlistener.back.spell.wither;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.util.DiscardThisSpellUnless;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.List;

public class WitherSpellBackListener2 extends AbstractSpellBackListener {

    public WitherSpellBackListener2(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2));
    }

    private void on0() {
        new DiscardThisSpellUnless(getSpellInfo(), getActiveInvestigatorId()).gainCondition(ConditionId.INTERNAL_INJURY);
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
        TypewriterUtils.confirmInfos("[#GOOD]You may reroll one die\nduring the Strength test[]").subscribe(() -> {
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getService().startInsertingAfterCommand("REROLL_USING_ASSETS_1");
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().<TestData>addEventCommand(input -> {
                if (input.hasReachedMinScoreToEndTest()) {
                    return;
                }
                if (input.getCalculatedDicePool() <= input.getScore()) {
                    return;
                }
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setSpellInfo(getSpellInfo());
                showCardRequest.setTitle("Reroll 1 die?");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);

                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
            });
            ServicePlatform.get().getService().release();
            ServicePlatform.get().getService().endInsertingAtCommand();
        });
    }

    private void onAnswer(ShowCardResponse showCardResponse, TestData input) {
        if (ShowCardResponse.YES == showCardResponse) {
            ServicePlatform.get().getTestService().rerollDie(input);
        } else {
            ServicePlatform.get().getService().convertFromTo(ShowCardResponse.class, TestData.class, response -> input);
        }

    }
}
