package sk.sivak.eldritchhorror.core.eventlistener.back.spell.stormofspirits;

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

import java.util.List;

public class StormOfSpiritsSpellBackListener2 extends AbstractSpellBackListener{

    private AfterDefeatMonsterListener afterDefeatMonsterListener;

    public StormOfSpiritsSpellBackListener2(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
    }


    private void on0() {
        TypewriterUtils.confirmInfos("[#BAD]Lose one Sanity.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseSanity(1);
            ServicePlatform.get().getService().release();
        });
    }

    private void on1() {
        TypewriterUtils.confirmInfos("[#GOOD]If you defeat the Monster,\n" +
                "you may spend one Sanity to gain one Clue.[]").subscribe(() -> {
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

        @Override
        public void onNotify(DefeatMonsterData eventData) {
            ServicePlatform.get().getService().addEventCommand(in -> {

                ServicePlatform.get().getTokenService().canSpend(0,0, 0, 1).subscribe(canSpend -> {
                    if (!canSpend.hasEnough()) {
                        ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> eventData);
                        ServicePlatform.get().getEventQueue().unregisterListener(AfterDefeatMonsterListener.this);
                        return;
                    }
                    ShowCardRequest showCardRequest = new ShowCardRequest();
                    showCardRequest.setSpellInfo(getSpellInfo());
                    showCardRequest.setTitle("Spend one Sanity to gain one Clue?");
                    showCardRequest.setAssetOriginType(AssetOriginType.INVENTORY);
                    showCardRequest.setHideType(HideType.RETURN);
                    showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                    ServicePlatform.get().getGameService().showCard(showCardRequest);

                    ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(answer -> {
                        if (ShowCardResponse.NO == answer) {
                            ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> eventData);
                            ServicePlatform.get().getEventQueue().unregisterListener(AfterDefeatMonsterListener.this);
                            return;
                        }
                        ServicePlatform.get().getTokenService().spend(0,0,0,1).subscribe(spendData -> {
                            if (!spendData.hasEnough()) {
                                ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> eventData);
                                ServicePlatform.get().getEventQueue().unregisterListener(AfterDefeatMonsterListener.this);
                                return;
                            }
                            ServicePlatform.get().getService().hold();
                            spendData.pay();
                            ServicePlatform.get().getTokenService().gainClueFromPool();
                            ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> eventData);
                            ServicePlatform.get().getEventQueue().unregisterListener(AfterDefeatMonsterListener.this);
                            ServicePlatform.get().getService().release();
                        });
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
