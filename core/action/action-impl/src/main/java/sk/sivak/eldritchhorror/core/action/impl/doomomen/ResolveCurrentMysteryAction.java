package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import rx.Completable;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.phase.PhaseType;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.model.BackgroundData;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.model.BackgroundModelRead.BackgroundType.CUSTOM;

public class ResolveCurrentMysteryAction extends AbstractHookableAction<Object, Boolean> {

    public ResolveCurrentMysteryAction() {
        super(BeforeAfterEvent.RESOLVE_CURRENT_MYSTERY);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Boolean> ss) {
        MysteryCardInfo currentMysteryCard = ServicePlatform.get().getMysteryDeck().getCurrentMysteryCard();
        Integer progress = currentMysteryCard.getProgress();
        Integer mysteryComplexity = currentMysteryCard.getMysteryComplexity();
        if (progress >= mysteryComplexity) {
            onMysterySolved(ss);
        } else {
            onMysteryNotSolved(ss);
        }
    }

    private void onMysterySolved(SingleSubscriber<? super Boolean> ss) {
        ServicePlatform.get().getGameController().showWholeWorld().subscribe();
        ServicePlatform.get().getGameController().showCurrentMysteryCard(false)
                .subscribe(() -> {});
        Question<Object> question = new Question<>();
        question.setTitle("Mystery was solved!");
        question.setOptions(Question.Option.okOption);
        question.displayCurrentMysteryCard();
        ServicePlatform.get().getGameController().ask(question).subscribe(ok -> {
            trackSolvedMystery();
            onQuestionConfirmation(ss);
        });
    }

    private void trackSolvedMystery() {
        String mysteryName = ServicePlatform.get().getMysteryDeck().getCurrentMysteryCard().getName();
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(
                AnalyticsCategory.SOLVED_MYSTERY, mysteryName);
    }

    private void onQuestionConfirmation(SingleSubscriber<? super Boolean> ss) {
        ServicePlatform.get().getMonsterController().tearMysteryCardApart().subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getDoomOmenService().unlockRandomCard();
            ServicePlatform.get().getService().addEventCommand(x -> {
                if (isMythosPhase()) {
                    showBackground();
                }
            });
            ServicePlatform.get().getInitGameService().unregisterCurrentMysteryCard();
            if (isMythosPhase()) {
                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
            }
            ServicePlatform.get().getService().addEventCommand(x -> {
                confirmSolvedMysteriesCount();
                ServicePlatform.get().getGameController().showCityInfoLabels();
            });
            ServicePlatform.get().getService().release();
            ss.onSuccess(true);
        });
    }

    private void confirmSolvedMysteriesCount() {
        int solvedMysteries = ServicePlatform.get().getMysteryDeck().getSolvedMysteriesCount();
        Completable completable;
        if (isMythosPhase()) {
            String text = getSolvedMysteriesText(solvedMysteries);
            completable = TypewriterUtils.confirmInfos(text);
        } else {
            completable = Completable.complete();
        }


        completable.subscribe(() -> {
            if (solvedMysteries == getMysteriesRequired()) {
                ServicePlatform.get().getService().hold();
                if (isMythosPhase()) {
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                }


                String ancientOneId = ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo().getAncientOneId().toString();
                GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.GAME_ENDED, ancientOneId,"victory");

                ServicePlatform.get().getDoomOmenService().displayVictoryPaper();

                ServicePlatform.get().getService().addEventCommand(in -> {
                    ServicePlatform.get().getGameController().restartGame() ;
                });
                ServicePlatform.get().getService().release();
            } else {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                checkIfGameIsNotOver();
                drawNewMysteryCard();
                ServicePlatform.get().getService().release();
            }

        });
    }

    private int getMysteriesRequired() {
        return ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo().getMysteriesRequired();
    }

    private void drawNewMysteryCard() {
        ServicePlatform.get().getService().addEventCommand(something -> {
            return Single.create(innerOnSub -> {
                ServicePlatform.get().getModel().getBackgroundModel().setCurrentBackground(null);
                ServicePlatform.get().getGameController().showWorldBackground().subscribe(() -> {
                    ServicePlatform.get().getMysteryDeck().drawNewMystery();
                    ServicePlatform.get().getInitGameService().registerCurrentMysteryCard();
                    innerOnSub.onSuccess(something);
                });
            });
        });
    }

    private void onMysteryNotSolved(SingleSubscriber<? super Boolean> ss) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().addEventCommand(x -> {
            showBackground();
        });
        ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
        int solvedMysteries = ServicePlatform.get().getMysteryDeck().getSolvedMysteriesCount();
        String text = getSolvedMysteriesText(solvedMysteries);
        TypewriterUtils.confirmInfos(text).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getModel().getBackgroundModel().setCurrentBackground(null);
            ServicePlatform.get().getGameController().showWorldBackground().subscribe();
            checkIfGameIsNotOver();
            ServicePlatform.get().getGameController().showCityInfoLabels();
            ServicePlatform.get().getService().release();
        });
        ServicePlatform.get().getService().release();
        ss.onSuccess(false);
    }

    private void showBackground() {
        ServicePlatform.get().getModel().getBackgroundModel().setCurrentBackground(
                new BackgroundData(CUSTOM, "mythos", "encounter/background/mythos.jpg"));
        ServicePlatform.get().getGameController().showCustomBackground("mythos").subscribe();
        ServicePlatform.get().getGameController().showWholeWorld().subscribe();
    }


    private String getSolvedMysteriesText(int solvedMysteries) {
        return solvedMysteries < getMysteriesRequired() ?
                " \nSolved mysteries: [#BAD]" + solvedMysteries + "/"+getMysteriesRequired()+"[]" :
                " \nSolved mysteries: [#GOOD]" + solvedMysteries + "/"+getMysteriesRequired()+"[]";
    }

    private void checkIfGameIsNotOver() {
        int currentRound = ServicePlatform.get().getMythosDeck().getDrawnCardsCount();
        int totalRounds = ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo().getMythosCardCount();
        if (currentRound == totalRounds) {
            ServicePlatform.get().getService().hold();
            String endGameText = sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo().getEndGameText();
            String ancientOneId = ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo().getAncientOneId().toString();
            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.GAME_ENDED, ancientOneId, "defeat: Cards");
            ServicePlatform.get().getTutorialService().displayChalkboard(endGameText,
                    VIEWPORT_WIDTH/2, VIEWPORT_HEIGHT/2, true);
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getGameController().restartGame() ;
            });
            ServicePlatform.get().getService().release();
        }
    }

    private boolean isMythosPhase() {
        return ServicePlatform.get().getPhase().getPhaseType() == PhaseType.MYTHOS;
    }
}
