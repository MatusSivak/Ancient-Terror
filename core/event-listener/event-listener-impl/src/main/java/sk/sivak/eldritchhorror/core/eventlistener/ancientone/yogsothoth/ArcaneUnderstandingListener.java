package sk.sivak.eldritchhorror.core.eventlistener.ancientone.yogsothoth;

import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AbstractMysteryListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.spell.ResolvedSpellData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ArcaneUnderstandingListener extends AbstractMysteryListener {

    private int progress;
    private SpellResolvedListener spellResolvedListener;

    public ArcaneUnderstandingListener(MysteryCardInfo mysteryCardInfo) {
        super(mysteryCardInfo);
    }

    @Override
    public void register() {
        super.register();

        spellResolvedListener = new SpellResolvedListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(spellResolvedListener, DirectEvent.SPELL_RESOLVED);
    }


    @Override
    public void justRegisterListeners(int progress) {
        spellResolvedListener = new SpellResolvedListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(spellResolvedListener, DirectEvent.SPELL_RESOLVED);
        this.progress = progress;
    }

    @Override
    protected List<LocationId> getPinLocations() {
        List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        List<LocationId> locationIds = new LinkedList<>();

        for (InvestigatorRead investigator : investigators) {
            List<SpellInfo> spells = ServicePlatform.get().getSpellsDeck().getSpells(investigator.getInfo().getInvestigatorId());
            if (!spells.isEmpty()) {
                locationIds.add(investigator.getCurrentLocationId());
            }
        }
        return locationIds;
    }


    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(spellResolvedListener);
    }

    @Override
    protected int getProgress() {
        return progress;
    }

    @Override
    public void justAddRedPins() {

    }

    @Override
    public void advanceActiveMystery() {
        if (progress >= mysteryCardInfo.getMysteryComplexity()) {
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Can't advance active mystery.");
            ServicePlatform.get().getGameService().ask(question).subscribe();
        } else {
            progress++;
            ServicePlatform.get().getGameService().advanceCurrentMysteryCard(1);
        }
    }

    private class SpellResolvedListener extends EventListenerImpl<ResolvedSpellData> {

        @Override
        public void onNotify(ResolvedSpellData input) {
            ServicePlatform.get().getService().<ResolvedSpellData>addEventCommand(eventData -> {
                if (eventData.getSpellInfo() == null) {
                    return;
                }
                InvestigatorId spellOwner = ServicePlatform.get().getSpellsDeck().getSpellOwner(eventData.getSpellInfo());
                if (spellOwner == null) {
                    return;
                }
                if (eventData.getTestData() == null) {
                    return;
                }
                if (!eventData.getTestData().isSuccessful()) {
                    return;
                }
                if (eventData.getTestData().getStat() != Stat.LORE) {
                    return;
                }

                InvestigatorId activeInvestigatorId = getActiveInvestigatorId();

                ServicePlatform.get().getService().hold();
                if (activeInvestigatorId != spellOwner) {
                    ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(spellOwner);
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                }

                ServicePlatform.get().getTokenService().canSpend(1,0,0,0).subscribe(canSpend -> {
                    if (!canSpend.hasEnough()) {
                        end(eventData, spellOwner, activeInvestigatorId);
                        return;
                    }

                    Question<Boolean> question = new Question<>();
                    question.setOptions(Arrays.asList(
                            new Question.Option<>("No", false, 0x800000ff),
                            new Question.Option<>("Yes", true, 0x008000ff)
                    ));
                    question.setTitle("Spend one Clue and discard "+eventData.getSpellInfo().getName()+"?");
                    question.displayCurrentMysteryCard();
                    ServicePlatform.get().getGameService().ask(question).subscribe(
                            answer -> onAnswer(answer, eventData, spellOwner, activeInvestigatorId));

                });

                ServicePlatform.get().getService().release();
            });
        }

        private void onAnswer(Answer<Boolean, Object> answer, ResolvedSpellData eventData, InvestigatorId spellOwner, InvestigatorId activeInvestigatorId) {
            if (!answer.getResponseData()) {
                end(eventData, spellOwner, activeInvestigatorId);
                return;
            }
            ServicePlatform.get().getTokenService().spend(1,0,0,0).subscribe(spendData -> {
                if (!spendData.hasEnough()) {
                    end(eventData, spellOwner, activeInvestigatorId);
                    return;
                }
                ServicePlatform.get().getService().hold();
                spendData.pay();
                EncounterUtils.onSelectCardToDiscardByChoice(eventData.getSpellInfo());
                progress++;
                ServicePlatform.get().getGameService().advanceCurrentMysteryCard(1);
                end(eventData, spellOwner, activeInvestigatorId);
                ServicePlatform.get().getService().release();
            });
        }

        private void end(ResolvedSpellData eventData, InvestigatorId spellOwner, InvestigatorId activeInvestigatorId) {
            ServicePlatform.get().getService().hold();
            if (activeInvestigatorId != spellOwner) {
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(activeInvestigatorId);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            }
            ServicePlatform.get().getService().convertTo(ResolvedSpellData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<ResolvedSpellData> getDataClass() {
            return ResolvedSpellData.class;
        }
    }
}
