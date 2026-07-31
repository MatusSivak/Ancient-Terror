package sk.sivak.eldritchhorror.core.view.action.travel;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import java8.features.stream.Stream;
import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorBasics;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.TokenActor;
import sk.sivak.eldritchhorror.core.view.components.hud.TicketContainerBar;
import sk.sivak.eldritchhorror.core.view.game.HudButtons;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.CompositeMap;
import sk.sivak.eldritchhorror.core.view.map.LocationPositionResolver;
import sk.sivak.eldritchhorror.core.view.map.helper.MoveCameraToLocationHelper;
import sk.sivak.eldritchhorror.core.view.map.helper.TravelHelper;
import sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorImage;
import sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorUtils;
import sk.sivak.eldritchhorror.core.view.utils.BackgroundUtils;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.List;

import static java8.features.stream.Stream.collectToList;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.FAST_ACTION_DURATION;
import static sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorUtils.findInvestigatorsOffset;
import static sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorUtils.setCurrentInvestigatorOffset;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/**
 * @author msivak
 */
public class TravelActionView {

    private final CompositeMap compositeMap;
    private GameController controller;
    private TextButton stopButton;

    private BackgroundUtils backgroundUtils;
    public TravelActionView(CompositeMap compositeMap, GameController controller) {
        this.controller = controller;
        this.compositeMap = compositeMap;
    }


    public void setBackgroundUtils(BackgroundUtils backgroundUtils) {
        this.backgroundUtils = backgroundUtils;
    }

    public Single<LocationInfo.Connection> selectTravelLocation(
            LocationId currentLocationId, List<LocationInfo.Connection> connections, boolean optional) {
        return backgroundUtils.hideBackground().andThen(Single.create(onSub -> {
            InfoStage.displayTextForce(get("travel.selectDestination"));
            compositeMap.showCityInfoLabels();
            new TravelHelper().selectLocation(currentLocationId, connections, optional, onSub);
        }));
    }

    public Single<LocationInfo.Connection> selectLocation(List<LocationId> locations) {
        return backgroundUtils.hideBackground().andThen(Single.create(onSub -> {

            InfoStage.displayTextForce(get("travel.selectLocation"));

            HudButtons.getInstance().hide().subscribe();
            InfoStage.getInvestigatorHud().hide().subscribe();
            HudButtons.getTrackHud().hide().subscribe();

            new TravelHelper().selectLocation(locations, onSub);
        }));
    }

    public Completable travelToLocation(LocationId locationFrom, LocationId locationTo) {

        return backgroundUtils.hideBackground().andThen(Completable.create((sub) -> {
            InvestigatorBasics currentInvestigator = controller.getInvestigatorBasics();
            InvestigatorUtils.InvestigatorIdLayerResolver idLayerResolver =
                    InvestigatorUtils.getIdLayerResolver(currentInvestigator.getInvestigatorId(), false);
            List<InvestigatorImage> currentInvestigatorList = MapStage.getActor(idLayerResolver);

            Vector2 positionTo = LocationPositionResolver.resolve(locationTo);
            Vector2 positionFrom = LocationPositionResolver.resolve(locationFrom);
            Vector2 positionDiff = new Vector2(positionTo).sub(positionFrom);


            List<InvestigatorId> otherInvestigators = collectToList(
                    Stream.map(
                            collectToList(controller.getInvestigatorsAtLocation(locationFrom),
                                    inv -> inv.getInvestigatorId() != currentInvestigator.getInvestigatorId()),
                            InvestigatorInfo::getInvestigatorId));

            otherInvestigators.addAll(controller.getDefeatedInvestigatorsAtLocation(locationFrom));

            if (locationFrom != locationTo) {
                findInvestigatorsOffset(otherInvestigators, 0); //0 - because current investigator is not there yet
            }

            Completable.create(sub2 -> {
                SequenceAction sequence = Actions.sequence(
                        Actions.run(() -> {
                            for (InvestigatorImage investigatorImage : currentInvestigatorList) {
                                AlphaAction action = new AlphaAction();
                                action.setTarget(investigatorImage);
                                action.setDuration(FAST_ACTION_DURATION);
                                action.setAlpha(0);
                                investigatorImage.addAction(new FastForwardAction<>(action));
                            }
                        }),
                        new FastForwardAction<>(Actions.delay(FAST_ACTION_DURATION)),
                        Actions.run(() -> MoveCameraToLocationHelper.moveCameraToPosition(positionTo, sub2))
                );
                MapStage.getStage().addAction(sequence);
            }).subscribe(() -> {

                List<InvestigatorId> investigatorsAtLocation = collectToList(Stream.map(controller.getInvestigatorsAtLocation(locationTo), InvestigatorInfo::getInvestigatorId));
                investigatorsAtLocation.addAll(controller.getDefeatedInvestigatorsAtLocation(locationTo));

                if (locationFrom != locationTo) {
                    findInvestigatorsOffset(investigatorsAtLocation, 1); //1 - because current investigator is there, but not in model
                }

                SequenceAction sequence = Actions.sequence(
                        Actions.run(() -> {
                            for (InvestigatorImage investigatorImage : currentInvestigatorList) {
                                investigatorImage.setX(investigatorImage.getX() + positionDiff.x);
                                investigatorImage.setY(investigatorImage.getY() + positionDiff.y);

                                if (locationFrom != locationTo) {
                                    setCurrentInvestigatorOffset(investigatorsAtLocation.size(), investigatorImage, 1, investigatorsAtLocation.size());
                                }

                                MapStage.getInvestigatorLayer().removeActor(investigatorImage);
                                MapStage.getInvestigatorLayer().addActor(investigatorImage);

                                AlphaAction action = new AlphaAction();
                                action.setTarget(investigatorImage);
                                action.setDuration(FAST_ACTION_DURATION);
                                action.setAlpha(1);
                                investigatorImage.addAction(new FastForwardAction<>(action));
                            }
                        }),
                        new FastForwardAction<>(Actions.delay(FAST_ACTION_DURATION)),
                        Actions.run(sub::onCompleted)
                );
                MapStage.getStage().addAction(sequence);
            });
        }));
    }

    public Single<LocationInfo.Connection> selectTicketTravelLocation(LocationId currentLocationId, List<LocationInfo.Connection> connections) {
        return Single.create(onSub -> {
            if (connections.size() == 0) {
                onSub.onSuccess(null);
                return;
            }
            InfoStage.displayTextForce(get("travel.spendTicket"));
            new TravelHelper().selectLocation(currentLocationId, connections, true, onSub);
        });
    }

    public Completable loseTicket(PathType input) {
        TokenActor tokenActor;
        if (input == PathType.TRAIN) {
            tokenActor = new TokenActor(
                    CustomAssetManager.getTexture(CustomAssetManager.TICKET_TRAIN_LEFT),
                    CustomAssetManager.getTexture(CustomAssetManager.TICKET_TRAIN_RIGHT)
            );
        } else {
            tokenActor = new TokenActor(
                    CustomAssetManager.getTexture(CustomAssetManager.TICKET_SHIP_LEFT),
                    CustomAssetManager.getTexture(CustomAssetManager.TICKET_SHIP_RIGHT)
            );
        }

        TicketContainerBar ticketBar = InfoStage.getInvestigatorHud().getTicketBar();
        tokenActor.setHolderLocation(ticketBar.getTicketPosition(input));
        InfoStage.addSmallActorToInfoStage(tokenActor);
        ticketBar.removeTicket(input);
        return tokenActor.discard(0);
    }
}
