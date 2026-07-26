package sk.sivak.eldritchhorror.core.eventlistener.back.spell.stormofspirits;

import sk.sivak.eldritchhorror.core.constants.displayasset.AssetOriginType;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;

import java.util.List;

public class StormOfSpiritsSpellBackListener3 extends AbstractSpellBackListener{

    public StormOfSpiritsSpellBackListener3(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
    }


    private void on0() {
        TypewriterUtils.confirmInfos("[#BAD]Lose one Health.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseHealth(1);
            ServicePlatform.get().getService().release();
        });
    }

    private void on1() {
        TypewriterUtils.confirmInfos("[#GOOD]For each Health you lose from the test,\n" +
                "you may spend one Sanity.\n" +
                "That Monster loses one Health\n" +
                "for each Sanity you spend.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getEventQueue().addAfterEventListener(new AfterEndOfCombatListener(), BeforeAfterEvent.END_OF_COMBAT_EVENT);
            ServicePlatform.get().getService().release();
        });
    }

    private class AfterEndOfCombatListener extends EventListenerImpl<CombatData> {

        private boolean stopLooping = false;
        @Override
        public void onNotify(CombatData eventData) {
            if (eventData.getHealthLost() == 0) {
                ServicePlatform.get().getEventQueue().unregisterListener(AfterEndOfCombatListener.this);
                return;
            }
            for (int i = 0; i < eventData.getHealthLost(); i++) {

                ServicePlatform.get().getService().addEventCommand(in -> {
                    if (stopLooping) {
                        return;
                    }
                    if (!eventData.getMonsterInfo().isAlive()) {
                        stopLooping = true;
                        return;
                    }

                    ServicePlatform.get().getTokenService().canSpend(0,0, 0, 1).subscribe(canSpend -> {
                        if (!canSpend.hasEnough()) {
                            stopLooping = true;
                            ServicePlatform.get().getService().convertTo(CombatData.class, () -> eventData);
                            ServicePlatform.get().getEventQueue().unregisterListener(AfterEndOfCombatListener.this);
                            return;
                        }
                        ShowCardRequest showCardRequest = new ShowCardRequest();
                        showCardRequest.setSpellInfo(getSpellInfo());
                        showCardRequest.setTitle("Spend one Sanity to deal one damage?");
                        showCardRequest.setAssetOriginType(AssetOriginType.INVENTORY);
                        showCardRequest.setHideType(HideType.RETURN);
                        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                        ServicePlatform.get().getGameService().showCard(showCardRequest);

                        ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(answer -> {
                            if (ShowCardResponse.NO == answer) {
                                stopLooping = true;
                                ServicePlatform.get().getService().convertTo(CombatData.class, () -> eventData);
                                ServicePlatform.get().getEventQueue().unregisterListener(AfterEndOfCombatListener.this);
                                return;
                            }
                            ServicePlatform.get().getTokenService().spend(0,0,0,1).subscribe(spendData -> {
                                if (!spendData.hasEnough()) {
                                    stopLooping = true;
                                    ServicePlatform.get().getService().convertTo(CombatData.class, () -> eventData);
                                    ServicePlatform.get().getEventQueue().unregisterListener(AfterEndOfCombatListener.this);
                                    return;
                                }
                                ServicePlatform.get().getService().hold();
                                spendData.pay();
                                ServicePlatform.get().getMonsterService().dealDamageToMonster(eventData.getMonsterInfo(), 1);
                                ServicePlatform.get().getService().convertTo(CombatData.class, () -> eventData);
                                ServicePlatform.get().getEventQueue().unregisterListener(AfterEndOfCombatListener.this);
                                ServicePlatform.get().getService().release();
                            });
                        });
                    });
                });
            }
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }
}
