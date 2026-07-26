package sk.sivak.eldritchhorror.core.eventlistener.spell;

import sk.sivak.eldritchhorror.core.constants.displayasset.AssetOriginType;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.stormofspirits.StormOfSpiritsSpell;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

import java.util.Collections;
import java.util.List;

public class StormOfSpiritsListener extends AbstractSpellListener<StormOfSpiritsSpell> {

    private BeforeDamageTestListener beforeDamageTestListener;
    private JustAfterDamageTestListener justAfterDamageTestListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(beforeDamageTestListener);
    }

    @Override
    protected void register() {
        beforeDamageTestListener = new BeforeDamageTestListener();
        getEventQueue().addDirectEventListener(beforeDamageTestListener, DirectEvent.BEFORE_DAMAGE_TEST);
    }

    private class BeforeDamageTestListener extends EventListenerImpl<CombatData> {


        @Override
        public void onNotify(CombatData eventData) {
            if (!isOwner()) {
                return;
            }
            ServicePlatform.get().getService().<CombatData>addEventCommand((input) -> {
                getEventAction(input).run();
            });
        }

        private boolean isOwner() {
            return spellOwnerId.equals(ServicePlatform.get().getInvestigators().getActiveInvestigatorId());
        }

        private Runnable getEventAction(CombatData input) {
            return () -> {
                if (input.getMonsterInfo().getDamageTestType() != Stat.STRENGTH) {
                    return;
                }
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                showCardRequest.setSpellInfo(spellInfo);
                showCardRequest.setHideType(HideType.RETURN);
                showCardRequest.setAssetOriginType(AssetOriginType.INVENTORY);
                showCardRequest.setTitle("Resolve a Lore test in place of a Strength test?");
                ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(answer -> {
                    if (ShowCardResponse.NO == answer) {
                        ServicePlatform.get().getService().convertTo(CombatData.class, () -> input);
                        return;
                    }

                    input.setDamageTestFlavorType(TestFlavorType.SPELL);
                    input.setDamageTestType(Stat.LORE);
                    ServicePlatform.get().getService().convertTo(CombatData.class, () -> input);
                    justAfterDamageTestListener = new JustAfterDamageTestListener();
                    ServicePlatform.get().getEventQueue().addDirectEventListener(justAfterDamageTestListener, DirectEvent.JUST_AFTER_DAMAGE_TEST);
                });
            };
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }

    private class JustAfterDamageTestListener extends EventListenerImpl<CombatData> {

        @Override
        public void onNotify(CombatData eventData) {
            ServicePlatform.get().getService().hold();

            AbstractSpellBackListener spellBackListener = findSpellBackListener();
            spellBackListener.setTestData(eventData.getDamageTestResult());
            spellBackListener.setData("combatData", eventData);
            spellBackListener.setMainSpellAction(() -> {});
            spellBackListener.executeWhole();
            ServicePlatform.get().getInvestigatorService().convertToNull();
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getEventQueue().unregisterListener(this);
            });
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }

}
