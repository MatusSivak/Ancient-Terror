package sk.sivak.eldritchhorror.core.view;

import java8.features.function.Supplier;
import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.difficulty.DifficultyId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.constants.tutorial.TouchBlockerData;

import java.util.List;

/**
 * @author msivak
 */
public interface InitGameView {

    Single<Integer> getNumberOfPlayers();

    Single<InvestigatorInfo[]> selectInvestigators(Integer input, List<InvestigatorInfo> availableInvestigators, Supplier<List<InvestigatorInfo>> initInvestigatorsAction);

    Single<AncientOneInfo> selectAncientOne(List<AncientOneInfo> availableAncientOnes);

    void toGameScreen();

    Single<DifficultyId> selectDifficulty();

    Completable displayChalkboard(String text, int positionX, int positionY);

    void hideChalkboard();

    void clearTouchBlockers(TouchBlockerData.BlockerTarget blockerTarget);

    void displayTouchBlocker(TouchBlockerData touchBlockerData);

    Completable showHudButtons();

    Completable waitForAncientOneClick();

    Completable waitForReserveClick();

    Completable waitForMysteryClick();

    Completable waitForInvestigatorClick();

    Completable waitForAncientOneHide();

    Completable waitForMysteryHide();

    Completable waitForInvestigatorHide();

    Completable waitForCharterFlightInspected();

    void save(Object saveData);

    <T> T load(Class<T> saveDataClazz);

    String getUnlockedAssets();

    String getUnlockedArtifacts();

    void rewriteUnlockedAssets(String unlockedAssets);

    void rewriteUnlockedArtifacts(String unlockedArtifacts);

    boolean hasPurchasedNoAds();

    boolean hasPurchasedBonusInvestigators();
}
