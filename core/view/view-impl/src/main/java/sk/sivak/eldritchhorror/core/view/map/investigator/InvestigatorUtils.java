package sk.sivak.eldritchhorror.core.view.map.investigator;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import java8.features.stream.Stream;
import java8.features.util.MapUtils;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorBasics;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.helper.Spawner;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static java8.features.util.IterableUtils.forEach;

/**
 * @author msivak
 */
public class InvestigatorUtils {

    public static Completable spawnInvestigator(LocationId location, InvestigatorId investigatorId,
                                                GameController gameController) {

        Spawner.SpawnConfig spawnConfig = new Spawner.SpawnConfigImpl(0, 0, 0, 1);

        Spawner.ActorSpawner actorSpawner = (boolean isCenter) -> new InvestigatorImage(investigatorId, gameController);

        Completable spawn = new Spawner().spawn(actorSpawner, spawnConfig,
                new InvestigatorIdLayerResolver(investigatorId, false, false), location);
        return spawn.andThen(Completable.create(onSub -> {
            List<InvestigatorBasics> investigatorsAtLocation = gameController.getInvestigatorsAtLocation(location);
            List<InvestigatorId> investigatorIds = Stream.collectToList(Stream.map(investigatorsAtLocation, InvestigatorInfo::getInvestigatorId));
            investigatorIds.addAll(gameController.getDefeatedInvestigatorsAtLocation(location));
            findInvestigatorsOffset(investigatorIds, 0);
            onSub.onCompleted();
        }));
    }

    public static InvestigatorIdLayerResolver getIdLayerResolver(InvestigatorId investigatorId, boolean lostInTimeAndSpace) {
        return new InvestigatorIdLayerResolver(investigatorId, lostInTimeAndSpace, false);
    }

    public static InvestigatorIdLayerResolver getDefeatedIdLayerResolver(InvestigatorId investigatorId) {
        return new InvestigatorIdLayerResolver(investigatorId, false, true);
    }

    public static void highlightInvestigators(List<InvestigatorImage> currentInvestigatorList, boolean active) {
        forEach(currentInvestigatorList, investigatorImage -> investigatorImage.highlight(active));
    }

    public static void setCurrentInvestigatorOffset(int investigatorsSize, InvestigatorImage investigatorImage, int modifier, int index) {
        if (investigatorsSize + modifier == 1) {
            investigatorImage.setOffset(0, 0);
        } else {
            int angle = (360 / (investigatorsSize + modifier)) * index;
            float offsetX = MathUtils.cosDeg(angle) * investigatorImage.getWidth() / 2;
            float offsetY = MathUtils.sinDeg(angle) * investigatorImage.getWidth() / 2; //this is correct

            investigatorImage.setOffset(offsetX, offsetY);
        }
    }

    public static void findInvestigatorsOffset(List<InvestigatorId> investigatorsAtLocation, int modifier) {
        for (int i = 0; i < investigatorsAtLocation.size(); i++) {
            List<InvestigatorImage> otherInvestigatorImages = MapStage.getActor(
                    InvestigatorUtils.getIdLayerResolver(investigatorsAtLocation.get(i), false));
            if (otherInvestigatorImages == null) {
                continue;
            }
            for (InvestigatorImage otherInvestigatorImage : otherInvestigatorImages) {
                setCurrentInvestigatorOffset(investigatorsAtLocation.size(), otherInvestigatorImage, modifier, i);
            }
        }

        for (int i = 0; i < investigatorsAtLocation.size(); i++) {
            List<InvestigatorImage> otherInvestigatorImages = MapStage.getActor(
                    InvestigatorUtils.getDefeatedIdLayerResolver(investigatorsAtLocation.get(i)));
            if (otherInvestigatorImages == null) {
                continue;
            }
            for (InvestigatorImage otherInvestigatorImage : otherInvestigatorImages) {
                setCurrentInvestigatorOffset(investigatorsAtLocation.size(), otherInvestigatorImage, modifier, i);
            }
        }
    }

    public static class InvestigatorIdLayerResolver implements MapStage.IdLayerResolver<InvestigatorId> {

        private InvestigatorId investigatorId;
        private final boolean lostInTimeAndSpace;
        private final boolean defeated;

        public InvestigatorIdLayerResolver(InvestigatorId investigatorId, boolean lostInTimeAndSpace, boolean defeated) {
            this.investigatorId = investigatorId;
            this.lostInTimeAndSpace = lostInTimeAndSpace;
            this.defeated = defeated;
        }

        @Override
        public InvestigatorId getId() {
            return investigatorId;
        }

        @Override
        public Group getLayer() {
            if (defeated) {
                return MapStage.getDefeatedInvestigatorLayer();
            }
            return lostInTimeAndSpace? MapStage.getLostInTimeAndSpaceLayer() : MapStage.getInvestigatorLayer();
        }
    }
}
