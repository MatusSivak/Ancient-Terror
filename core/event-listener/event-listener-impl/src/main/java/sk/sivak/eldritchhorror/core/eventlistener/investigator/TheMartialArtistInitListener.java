package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import java8.features.stream.Stream;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.GainAssetFromDeckData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.CountRerollDiceData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Arrays;

/**
 * @author msivak
 */
public class TheMartialArtistInitListener extends AbstractInvestigatorInitListener {

    private TheMartialArtistPassiveListener theMartialArtistPassiveListener;

    @Override
    protected InvestigatorId getInvestigatorId() {
        return InvestigatorId.THE_MARTIAL_ARTIST;
    }

    @Override
    protected void initInvestigator() {
        theMartialArtistPassiveListener = new TheMartialArtistPassiveListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(theMartialArtistPassiveListener, BeforeAfterEvent.IMPROVE_SKILL);
        getService().hold();
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.LUCKY_RABBITS_FOOT);
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.PROTECTIVE_AMULET);
        getService().convertFromTo(GainAssetFromDeckData.class, InvestigatorId.class, (in) -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override
    public void justRegisterListeners() {
        theMartialArtistPassiveListener = new TheMartialArtistPassiveListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(theMartialArtistPassiveListener, BeforeAfterEvent.IMPROVE_SKILL);
    }

    @Override
    public void unregisterInvestigator() {
        ServicePlatform.get().getEventQueue().unregisterListener(theMartialArtistPassiveListener);
        ServicePlatform.get().getEventQueue().unregisterListener(this);
    }

    private class TheMartialArtistPassiveListener extends EventListenerImpl<Stat> {

        private boolean enabled = true;

        @Override
        public void onNotify(Stat eventData) {
            if (!enabled) {
                return;
            }
            if (eventData == null) {
                return;
            }
            if (ServicePlatform.get().getInvestigators().getActiveInvestigatorId() != InvestigatorId.THE_MARTIAL_ARTIST) {
                return;
            }
            InvestigatorRead investigator = ServicePlatform.get().getInvestigators().getInvestigator(InvestigatorId.THE_MARTIAL_ARTIST);
            if (investigator.getStatBonus(eventData) == 2) {
                return;
            }
            askQuestion().subscribe(answer -> {
                if (Boolean.TRUE.equals(answer.getResponseData())) {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getService().addEventCommand(in -> {enabled = false;});
                    ServicePlatform.get().getInvestigatorService().improveSkill(eventData);
                    ServicePlatform.get().getService().addEventCommand(in -> {enabled = true;});
                    ServicePlatform.get().getService().release();
                } else {
                    ServicePlatform.get().getService().convertTo(Stat.class, () -> eventData);
                }
            });
        }

        private Single<Answer<Boolean, Object>> askQuestion() {
            Question<Boolean> question = new Question<>();
            question.setTitle("Improve same skill again?");
            question.setPortraitBeforeTitle(InvestigatorId.THE_MARTIAL_ARTIST);
            question.setOptions(Arrays.asList(
                    new Question.Option<>("No", false, 0x800000ff),
                    new Question.Option<>("Yes", true, 0x008000ff)
            ));
            return ServicePlatform.get().getGameService().ask(question);
        }

        @Override
        public Class<Stat> getDataClass() {
            return Stat.class;
        }
    }
}
