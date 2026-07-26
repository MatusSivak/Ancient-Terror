package sk.sivak.eldritchhorror.core.eventlistener.back.spell.intervene;

import java8.features.function.Consumer;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.List;

public class InterveneSpellBackListener2 extends AbstractSpellBackListener {

    public InterveneSpellBackListener2(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));

    }

    private void on0() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.CHOOSE_ONE);
        String combatantName = ((InvestigatorId) getData("combatantId")).toString();
        String[] buttonTexts = new String[]{
                "[#BAD]Discard this card[]",
                "[#BAD]"+combatantName+" loses two Sanity[]"
        };
        ServicePlatform.get().getEncounterService().displayButtons(buttonTexts).subscribe(choice -> {
            if (choice == 0) {
                finishAndDiscard();
            } else {
                onLoseSanityChoice();
            }
        });
        ServicePlatform.get().getService().release();
    }


    private void on1() {
        String combatantName = ((InvestigatorId) getData("combatantId")).toString();
        TypewriterUtils.confirmInfos(
                "[#BAD]"+combatantName +" loses one Sanity[]",
                "[#GOOD]"+combatantName +" may reroll two dice[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(getData("combatantId"));
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            ServicePlatform.get().getTokenService().loseSanity(1);
            ServicePlatform.get().getService().addEventCommand(in -> {
                addListener();
            });
            ServicePlatform.get().getService().release();
        });
    }

    private void addListener() {
        ServicePlatform.get().getService().startInsertingAfterCommand("REROLL_USING_ASSETS_1");
        ServicePlatform.get().getService().hold();
        Consumer<TestData> eventCommand = input -> {
            if (input.hasReachedMinScoreToEndTest()) {
                return;
            }
            if (input.getCalculatedDicePool() <= input.getScore()) {
                return;
            }
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setSpellInfo(getSpellInfo());
            showCardRequest.setTitle("Reroll one die?");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);

            ServicePlatform.get().getGameService().showCard(showCardRequest)
                    .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
        };
        for (int i = 0; i < 2; i++) {
            ServicePlatform.get().getService().addEventCommand(eventCommand);
        }

        ServicePlatform.get().getService().release();
        ServicePlatform.get().getService().endInsertingAtCommand();
    }

    private void onAnswer(ShowCardResponse showCardResponse, TestData input) {
        if (ShowCardResponse.YES == showCardResponse) {
            ServicePlatform.get().getTestService().rerollDie(input);
        } else {
            ServicePlatform.get().getService().convertFromTo(ShowCardResponse.class, TestData.class, response -> input);
        }

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

    private void onLoseSanityChoice() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(getData("combatantId"));
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
        ServicePlatform.get().getTokenService().loseSanity(2);
        ServicePlatform.get().getService().release();
    }
}
