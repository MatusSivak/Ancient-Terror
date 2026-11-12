package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

public class ResearchWilderness11Encounter extends AbstractAzathothResearchEncounter {

    public ResearchWilderness11Encounter() {
        super(11, LocationType.WILDERNESS);
    }

    private int cluesDiscarded;

    @Override
    protected void execute() {
        cluesDiscarded = 0;
        new InfoFlavorTemplate(getTextBuilder(), () -> {
            ServicePlatform.get().getService().hold();
            gainThisClue();
            ServicePlatform.get().getService().showResearchBackground(LocationType.WILDERNESS);
            ServicePlatform.get().getService().release();
        }, new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, -2, NOTHING,
                () -> {
                    OmenColor omenColor = ServicePlatform.get().getOmenTrack().getCurrentOmen().getOmenColor();
                    List<LocationId> spawnedGates = ServicePlatform.get().getGateStackRead().getSpawnedGates(omenColor.toGateColor());

                    ServicePlatform.get().getService().hideBackground();
                    Question<Object> question = new Question<>();
                    if (spawnedGates.size() == 0) {
                        question.setTitle("No Gates match the current Omen.");
                    } else if (spawnedGates.size() == 1) {
                        question.setTitle("One Gate matches the current Omen.");
                    } else {
                        question.setTitle(spawnedGates.size() + " Gates match the current Omen.");
                    }
                    question.setOptions(Question.Option.okOption);
                    ServicePlatform.get().getGameService().ask(question).subscribe(ok -> afterConfirm(spawnedGates.size()));
                }
        ).withResearchFlavor()).execute();
    }

    private void afterConfirm(int spawnedGatesCount) {
        List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        int totalOwnedClues = 0;

        for (InvestigatorRead investigator : investigators) {
            totalOwnedClues += ServicePlatform.get().getCluePool().getClueCount(investigator.getInfo().getInvestigatorId());
        }

        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        askToDiscardClues(investigators, Math.min(totalOwnedClues, spawnedGatesCount), activeInvestigatorId);
    }

    private void askToDiscardClues(List<? extends InvestigatorRead> investigators, int cluesToDiscard, InvestigatorId activeInvestigatorId) {
        ServicePlatform.get().getService().hold();
        for (InvestigatorRead investigator : investigators) {
            if (ServicePlatform.get().getCluePool().getClueCount(investigator.getInfo().getInvestigatorId()) == 0) {
                continue;
            }

            ServicePlatform.get().getService().addEventCommand(in -> {
                if (cluesDiscarded == cluesToDiscard) {
                    return;
                }
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigator.getInfo().getInvestigatorId());
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                Question<Boolean> discardClueQuestion = new Question<>();
                discardClueQuestion.setTitle("Discard one Clue?");
                discardClueQuestion.setOptions(Question.Option.noYesOptions);
                ServicePlatform.get().getGameService().ask(discardClueQuestion).subscribe(answer -> {
                    if (!answer.getResponseData()) {
                        return;
                    }
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getTokenService().loseClue();
                    cluesDiscarded++;
                    ServicePlatform.get().getService().release();
                });
                ServicePlatform.get().getService().release();
            });



        }
        ServicePlatform.get().getService().addEventCommand(in -> {
            if (cluesDiscarded != cluesToDiscard) {
                askToDiscardClues(investigators, cluesToDiscard, activeInvestigatorId);
            } else {
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(activeInvestigatorId);
            }
        });
        ServicePlatform.get().getService().release();
    }
}
