package sk.sivak.eldritchhorror.core.view.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import java8.features.function.Supplier;
import java8.features.stream.Stream;
import rx.Completable;
import rx.Observable;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorBasics;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.combat.FireballSmokeBuilder;
import sk.sivak.eldritchhorror.core.view.components.hourglass.HourglassComponent;
import sk.sivak.eldritchhorror.core.view.components.investigator.InvestigatorPuzzleEffect;
import sk.sivak.eldritchhorror.core.view.components.investigator.InvestigatorTransition;
import sk.sivak.eldritchhorror.core.view.components.investigator.SelectMultipleInvestigators;
import sk.sivak.eldritchhorror.core.view.components.select.SelectSingleComponent;
import sk.sivak.eldritchhorror.core.view.components.sheet.investigator.StatsTableData;
import sk.sivak.eldritchhorror.core.view.components.skill.ImproveSkillComponent;
import sk.sivak.eldritchhorror.core.view.components.skill.LoseImprovementComponent;
import sk.sivak.eldritchhorror.core.view.game.HudButtons;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.LocationPositionResolver;
import sk.sivak.eldritchhorror.core.view.map.helper.MoveCameraToLocationHelper;
import sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorImage;
import sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorUtils;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.List;

import static java8.features.stream.Stream.collectToList;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.PURE_WHITE_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.game.MapStage.getInvestigatorLayer;
import static sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorUtils.findInvestigatorsOffset;

public class InvestigatorView {

    private GameController controller;

    public InvestigatorView(GameController controller) {
        this.controller = controller;
    }

    public Completable showActiveInvestigator(boolean displayTransition, boolean lostInTimeAndSpace) {
        InvestigatorBasics currentInvestigator = controller.getInvestigatorBasics();
        InfoStage.displayText("The " + currentInvestigator.getInvestigatorId().toString());
        Completable updateHudCompletable = updateHud(currentInvestigator);

        Completable displayTransitionCompletable;
        if (displayTransition && !lostInTimeAndSpace) {
            InvestigatorTransition transition = InvestigatorTransition.build(currentInvestigator.getInvestigatorId());
            InfoStage.addSmallActorToInfoStage(transition);
            displayTransitionCompletable = transition.display(null);
        } else {
            displayTransitionCompletable = Completable.complete();
        }

        InvestigatorUtils.highlightInvestigators(MapStage.getAllActors(getInvestigatorLayer()), false);
        List<InvestigatorImage> currentInvestigatorList = MapStage
                .getActor(InvestigatorUtils.getIdLayerResolver(currentInvestigator.getInvestigatorId(), lostInTimeAndSpace));
        InvestigatorUtils.highlightInvestigators(currentInvestigatorList, true);

        Completable moveCameraToPosition;
        if (currentInvestigator.getLocationId() != null) {
            moveCameraToPosition = Completable.create(afterMoveSub -> MoveCameraToLocationHelper.moveCameraToPosition(
                    LocationPositionResolver.resolve(currentInvestigator.getLocationId()),
                    afterMoveSub));
        } else {
            moveCameraToPosition = Completable.complete();
        }

        HudButtons.getInstance().show().subscribe();
        return displayTransitionCompletable.mergeWith(moveCameraToPosition).mergeWith(updateHudCompletable);
    }



    public Completable showDelayedAnimation(InvestigatorId investigatorId) {
        InvestigatorTransition transition = InvestigatorTransition.build(investigatorId);
        InfoStage.addSmallActorToInfoStage(transition);

        return transition.display(Actions.sequence(
                Actions.run(() -> {
                    HourglassComponent hourglassComponent = new HourglassComponent(transition.getColor());
                    hourglassComponent.setInternalScale(1f);
                    hourglassComponent.setPosition(
                            transition.getWidth()/2 - hourglassComponent.getWidth()/2,
                            transition.getHeight()/2 - hourglassComponent.getHeight()/1.8f);
                    transition.addActor(hourglassComponent);
                    hourglassComponent.display();

                    List<Actor> investigators = MapStage.getActor(InvestigatorUtils.getIdLayerResolver(investigatorId, false));
                    for (Actor investigator : investigators) {
                        ((InvestigatorImage) investigator).displayHourglass();
                    }
                }),
                new FastForwardAction(Actions.delay(1.8f))
        ));
    }

    public void justAddDelayedAnimation(InvestigatorId investigatorId) {
        List<Actor> investigators = MapStage.getActor(InvestigatorUtils.getIdLayerResolver(investigatorId, false));
        for (Actor investigator : investigators) {
            ((InvestigatorImage) investigator).displayHourglass();
        }
    }

    public Completable hideDelayedAnimation(InvestigatorId investigatorId) {
        List<Actor> investigators = MapStage.getActor(InvestigatorUtils.getIdLayerResolver(investigatorId, false));
        for (Actor investigator : investigators) {
            ((InvestigatorImage) investigator).hideHourglass();
        }
        return Completable.create(onSub ->
                InfoStage.addActionToInfoStage(Actions.delay(1f, Actions.run(onSub::onCompleted))));
    }

    public Completable updateHud(InvestigatorBasics currentInvestigator) {
        return Completable.create(onSub -> {
            InfoStage.getInvestigatorHud().hide().subscribe(
                    () -> {
                        InfoStage.getInvestigatorHud().getHealthBar().setTotalEmptyContainers(currentInvestigator.getMaxHealth());
                        InfoStage.getInvestigatorHud().getHealthBar().setCurrentValue(currentInvestigator.getCurrentHealth());

                        InfoStage.getInvestigatorHud().getSanityBar().setTotalEmptyContainers(currentInvestigator.getMaxSanity());
                        InfoStage.getInvestigatorHud().getSanityBar().setCurrentValue(currentInvestigator.getCurrentSanity());

                        InfoStage.getInvestigatorHud().getTicketBar().clearTickets();
                        for (PathType pathType : currentInvestigator.getTickets()) {
                            InfoStage.getInvestigatorHud().getTicketBar().addTicket(pathType);
                        }

                        InfoStage.getInvestigatorHud().getClueBar().setCurrentValue(currentInvestigator.getClues());
                        InfoStage.getInvestigatorHud().getFocusBar().setCurrentValue(currentInvestigator.getFocusTokens());

                        InfoStage.getInvestigatorHud().recalculateSizes();

                        InfoStage.getInvestigatorHud().show().subscribe(() -> onSub.onCompleted());
                    }
            );
        });

    }

    public Single<InvestigatorId> selectInvestigator(List<InvestigatorId> list, String title) {

        return Single.create(onSub -> {
            SelectSingleComponent<InvestigatorId> selectSingleComponent = new SelectSingleComponent<>(list, onSub);
            selectSingleComponent.init("Display investigators?", title,
                    alpha -> new Color(0.0f, 0.0f, 0.0f, 0.5f * alpha), PURE_WHITE_BACKGROUND);
            selectSingleComponent.show();
        });
    }

    private ImproveSkillComponent improveSkillComponent;
    private LoseImprovementComponent loseImprovementComponent;

    public Single<Stat> improveSkill(Stat skillRestriction) {
        return Single.create(onSub -> {
            if (improveSkillComponent == null) {
                improveSkillComponent = new ImproveSkillComponent();
            }
            StatsTableData statsTableData = new StatsTableData.Builder().fromInvestigatorBasics(controller.getInvestigatorBasics()).build();
            improveSkillComponent.setOnSub(onSub);
            improveSkillComponent.init(statsTableData, skillRestriction);
        });
    }

    public Completable showImproveSkillTable() {
        return Completable.create(onSub -> {
            if (improveSkillComponent == null) {
                improveSkillComponent = new ImproveSkillComponent();
            }
            StatsTableData statsTableData = new StatsTableData.Builder().fromInvestigatorBasics(controller.getInvestigatorBasics()).build();
            improveSkillComponent.setOnSub(onSub);
            improveSkillComponent.justShow(statsTableData);
            onSub.onCompleted();
        });
    }

    public Completable hideImproveSkillTable() {
        return Completable.create(onSub -> {
            improveSkillComponent.justHide();
            onSub.onCompleted();
        });
    }

    public Single<Stat> loseImprovement(Stat stat, int amountToLose) {
        return Single.create(onSub -> {
            if (loseImprovementComponent == null) {
                loseImprovementComponent = new LoseImprovementComponent();
            }
            StatsTableData statsTableData = new StatsTableData.Builder().fromInvestigatorBasics(controller.getInvestigatorBasics()).build();
            loseImprovementComponent.setOnSub(onSub);
            loseImprovementComponent.init(statsTableData, stat, amountToLose);
        });
    }

    public Completable devourInvestigator(InvestigatorId investigatorId) {
        return Completable.create(onSub -> {
            InvestigatorUtils.InvestigatorIdLayerResolver idLayerResolver = InvestigatorUtils.getIdLayerResolver(investigatorId, false);
            InvestigatorImage investigatorImage = (InvestigatorImage) MapStage.getActor(idLayerResolver).get(1);
            FireballSmokeBuilder.FireballSmoke fireballSmoke = FireballSmokeBuilder.get();
            fireballSmoke.getParticleEffect().setPosition(
                    investigatorImage.getX() + investigatorImage.getWidth()/2f,
                    investigatorImage.getY() + investigatorImage.getHeight()/6f);
            investigatorImage.getStage().addActor(fireballSmoke);
            InvestigatorPuzzleEffect.prepareTextureRegions(new InvestigatorImage(investigatorId, null)).subscribe(devourTextureRegions -> {
                fireballSmoke.getParticleEffect().allowCompletion();
                InvestigatorPuzzleEffect.devourInvestigator(investigatorImage, devourTextureRegions, MapStage.getInvestigatorLayer()).subscribe(() -> {
                    fireballSmoke.remove();
                    onSub.onCompleted();
                });
                MapStage.removeActor(idLayerResolver);
            });
        });
    }

    public Completable defeatInvestigator(InvestigatorId investigatorId, boolean health) {
        InvestigatorUtils.InvestigatorIdLayerResolver idLayerResolver = InvestigatorUtils.getIdLayerResolver(investigatorId, false);
        List<InvestigatorImage> investigatorImages = MapStage.getActor(idLayerResolver);
        InvestigatorUtils.highlightInvestigators(investigatorImages, false);
        for (InvestigatorImage investigatorImage : investigatorImages) {
            investigatorImage.setColor(Color.WHITE);
            MapStage.removeActor(idLayerResolver);
            MapStage.addToLayer(investigatorImage, InvestigatorUtils.getDefeatedIdLayerResolver(investigatorId));
        }
        Observable<TextureRegion> textureRegionsForDefeatedObservable = InvestigatorPuzzleEffect.prepareTextureRegionsForDefeated().cache();
        InvestigatorPuzzleEffect.defeatInvestigator(textureRegionsForDefeatedObservable, investigatorImages.get(0), health).subscribe();;
        InvestigatorPuzzleEffect.defeatInvestigator(textureRegionsForDefeatedObservable, investigatorImages.get(2), health).subscribe();
        return InvestigatorPuzzleEffect.defeatInvestigator(textureRegionsForDefeatedObservable, investigatorImages.get(1), health);
    }

    public Completable loadDefeatedInvestigator(InvestigatorId investigatorId, LocationId locationId, boolean defeatedByHealth) {
        Completable spawnInvestigator = Completable.defer(() -> InvestigatorUtils.spawnInvestigator(locationId, investigatorId, controller));
        Completable defeatInvestigator = Completable.defer(() -> defeatInvestigator(investigatorId, defeatedByHealth));
        return spawnInvestigator.andThen(defeatInvestigator);
    }

    public Completable removeDefeatedInvestigator(InvestigatorId investigatorId, LocationId locationId) {
        InvestigatorUtils.InvestigatorIdLayerResolver idLayerResolver = InvestigatorUtils.getDefeatedIdLayerResolver(investigatorId);
        List<InvestigatorImage> investigatorImages = MapStage.getActor(idLayerResolver);
        investigatorImages.get(0).destroyPuzzleImages();
        investigatorImages.get(2).destroyPuzzleImages();
        return investigatorImages.get(1).destroyPuzzleImages().andThen(Completable.create(onSub -> {

            List<InvestigatorId> aliveInvestigators = collectToList(
                    Stream.map(
                            collectToList(controller.getInvestigatorsAtLocation(locationId)),
                            InvestigatorInfo::getInvestigatorId));

            aliveInvestigators.addAll(controller.getDefeatedInvestigatorsAtLocation(locationId));

            findInvestigatorsOffset(aliveInvestigators, 0);
            MapStage.removeActor(idLayerResolver);
            onSub.onCompleted();
        }));
    }

    private SelectMultipleInvestigators selectMultipleInvestigators;

    public Single<InvestigatorInfo[]> selectReplacingInvestigator(InvestigatorId investigatorToReplace,
                                                                  List<InvestigatorInfo> availableInvestigators,
                                                                  Supplier<List<InvestigatorInfo>> initInvestigatorsAction) {
        return Single.create(sub -> {
            InfoStage.getInvestigatorHud().hide().subscribe();
            HudButtons.getTrackHud().hide().subscribe();
            HudButtons.getInstance().hide().subscribe();

            Runnable updateAvailableInvestigatorsAction = () -> {
                selectMultipleInvestigators.remove();
                selectMultipleInvestigators = new SelectMultipleInvestigators(initInvestigatorsAction.get(), sub, 1, InfoStage.getStageSafe());
                selectMultipleInvestigators.show();
            };

            selectMultipleInvestigators = new SelectMultipleInvestigators(availableInvestigators, sub, 1, InfoStage.getStageSafe());
            selectMultipleInvestigators.setUpdateAvailableInvestigatorsAction(updateAvailableInvestigatorsAction);
            selectMultipleInvestigators.setTitle("Select replacement for the " + investigatorToReplace.toString());
            selectMultipleInvestigators.show();
        });
    }
}
