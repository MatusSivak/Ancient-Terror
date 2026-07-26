package sk.sivak.eldritchhorror.core.eventlistener.back.spell.mistsofreleh;

import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.EncounterResult;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.List;

public class MistsOfRelehSpellBackListener4 extends AbstractSpellBackListener {

    private RollOneLessDieListener rollOneLessDieListener;

    public MistsOfRelehSpellBackListener4(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
    }

    private void on0() {
        TypewriterUtils.confirmInfos("[#BAD]Roll one less die when resolving tests during the Encounter Phase this round.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            rollOneLessDieDuringEncounter();
            ServicePlatform.get().getService().release();
        });
    }

    private void rollOneLessDieDuringEncounter() {
        rollOneLessDieListener = new RollOneLessDieListener();

        ServicePlatform.get().getEventQueue().addDirectEventListener(rollOneLessDieListener, DirectEvent.REGISTER_BONUS_DICE);
        ServicePlatform.get().getEventQueue().addDirectEventListener(new RemoveRollOneLessDieListener(), DirectEvent.INVESTIGATOR_FINISHED_ENCOUNTER);
    }

    private class RollOneLessDieListener extends EventListenerImpl<TestData> {

        @Override
        public void onNotify(TestData eventData) {
            eventData.setAdditionalDicesCount(eventData.getAdditionalDicesCount() - 1);
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }


    private class RemoveRollOneLessDieListener extends EventListenerImpl<EncounterResult> {

        @Override
        public void onNotify(EncounterResult eventData) {
            ServicePlatform.get().getEventQueue().unregisterListener(rollOneLessDieListener);
            ServicePlatform.get().getEventQueue().unregisterListener(this);
        }

        @Override
        public Class<EncounterResult> getDataClass() {
            return EncounterResult.class;
        }
    }

    private void on1() {
        TypewriterUtils.confirmInfos("No additional effect.").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getService().release();
        });
    }
}
