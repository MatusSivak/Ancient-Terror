package sk.sivak.eldritchhorror.core.eventlistener.back.spell.stormofspirits;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.displayasset.AssetOriginType;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;

import java.util.Arrays;
import java.util.List;

public class StormOfSpiritsSpellBackListener4 extends AbstractSpellBackListener{

    private AfterDefeatMonsterListener afterDefeatMonsterListener;

    public StormOfSpiritsSpellBackListener4(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
    }


    private void on0() {
        ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.CHOOSE_ONE);
        ServicePlatform.get().getEncounterService().displayButtons(
                "[#BAD]Discard this card[]",
                "[#BAD]Lose one Health[]\n[#BAD]Lose one Sanity[]").subscribe(choice -> {
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
                ServicePlatform.get().getTokenService().loseSanity(1);

            }
            ServicePlatform.get().getService().release();
        });
    }

    private void on1() {
        TypewriterUtils.confirmInfos("[#GOOD]If you defeat the Monster,\n" +
                "recover one Health or one Sanity[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            afterDefeatMonsterListener = new AfterDefeatMonsterListener();
            ServicePlatform.get().getEventQueue().addAfterEventListener(afterDefeatMonsterListener, BeforeAfterEvent.DEFEAT_MONSTER);
            ServicePlatform.get().getEventQueue().addAfterEventListener(new AfterEndOfCombatListener(), BeforeAfterEvent.END_OF_COMBAT_EVENT);
            ServicePlatform.get().getService().release();
        });
    }

    private class AfterEndOfCombatListener extends EventListenerImpl<CombatData> {

        @Override
        public void onNotify(CombatData eventData) {
            ServicePlatform.get().getEventQueue().unregisterListener(afterDefeatMonsterListener);
            ServicePlatform.get().getEventQueue().unregisterListener(this);
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }

    private class AfterDefeatMonsterListener extends EventListenerImpl<DefeatMonsterData> {

        private static final String SANITY = "Sanity";
        private static final String HEALTH = "Health";

        @Override
        public void onNotify(DefeatMonsterData eventData) {
            ServicePlatform.get().getService().addEventCommand(in -> {

                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setSpellInfo(getSpellInfo());
                showCardRequest.setTitle("Recover one Health or one Sanity.");
                showCardRequest.setAssetOriginType(AssetOriginType.INVENTORY);
                showCardRequest.setHideType(HideType.RETURN);
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);

                ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(ok -> {
                    Question<String> question = new Question<>();
                    question.setOptions(Arrays.asList(
                            new Question.Option<>(HEALTH, HEALTH, 0x800000ff),
                            new Question.Option<>(SANITY, SANITY, 0x000080ff)));
                    question.setTitle("What do you want to recover?");

                    ServicePlatform.get().getGameService().ask(question).subscribe(answer -> {
                        ServicePlatform.get().getService().hold();
                        if (answer.getResponseData().equals(SANITY)) {
                            ServicePlatform.get().getTokenService().gainSanity(1);
                        } else {
                            ServicePlatform.get().getTokenService().gainHealth(1);
                        }
                        ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> eventData);
                        ServicePlatform.get().getEventQueue().unregisterListener(AfterDefeatMonsterListener.this);
                        ServicePlatform.get().getService().release();
                    });
                });
            });
        }

        @Override
        public Class<DefeatMonsterData> getDataClass() {
            return DefeatMonsterData.class;
        }
    }

}
