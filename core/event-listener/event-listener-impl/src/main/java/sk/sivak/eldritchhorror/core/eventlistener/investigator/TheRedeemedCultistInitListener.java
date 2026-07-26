package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.asset.AbstractAssetEventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;

import java.util.Collections;

/**
 * @author msivak
 */
public class TheRedeemedCultistInitListener extends AbstractInvestigatorInitListener {

    private ReduceHorrorListener reduceHorrorListener;

    @Override
    protected InvestigatorId getInvestigatorId() {
        return InvestigatorId.THE_REDEEMED_CULTIST;
    }

    @Override
    protected void initInvestigator() {
        reduceHorrorListener = new ReduceHorrorListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(reduceHorrorListener, BeforeAfterEvent.UPDATE_MONSTER_HORROR);
        getService().hold();
        getService().gainSpellFromDeck(getInvestigatorId(), SpellId.WITHER);
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.ARCANE_MANUSCRIPTS);
        getService().convertTo(Object.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override
    public void justRegisterListeners() {
        reduceHorrorListener = new ReduceHorrorListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(reduceHorrorListener, BeforeAfterEvent.UPDATE_MONSTER_HORROR);
    }

    @Override
    public void unregisterInvestigator() {
        ServicePlatform.get().getEventQueue().unregisterListener(reduceHorrorListener);
        ServicePlatform.get().getEventQueue().unregisterListener(this);
    }

    private class ReduceHorrorListener extends EventListenerImpl<CombatData> {


        private CombatData input;

        @Override
        public void onNotify(CombatData eventData) {
            this.input = eventData;
            if (ServicePlatform.get().getInvestigators().getActiveInvestigatorId() != InvestigatorId.THE_REDEEMED_CULTIST) {
                return;
            }
            ServicePlatform.get().getService().addEventCommand(in -> {getEventAction();});
        }

        protected void getEventAction() {
            if (input.getActualHorror() == null) {
                return;
            }
            if (input.getActualHorror() <= 1) {
                return;
            }

            askQuestion().subscribe(answer -> {
                input.setActualHorror(1);
                ServicePlatform.get().getService().convertTo(CombatData.class, () -> input);
            });
        }

        private Single<Answer<Boolean, Object>> askQuestion() {
            Question<Boolean> question = new Question<>();

            question.setTitle("Reduce the Horror of Monsters you encounter to 1.");
            question.setPortraitBeforeTitle(InvestigatorId.THE_REDEEMED_CULTIST);
            question.setOptions(Collections.singletonList(
                    new Question.Option<>("OK", true)

            ));
            return ServicePlatform.get().getGameService().ask(question);
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }
}
