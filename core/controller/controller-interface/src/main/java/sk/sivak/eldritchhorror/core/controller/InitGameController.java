package sk.sivak.eldritchhorror.core.controller;

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
public interface InitGameController {

    Single<Integer> getNumberOfPlayers();

    Single<InvestigatorInfo[]> selectInvestigators(Integer input, Supplier<List<InvestigatorInfo>> initInvestigatorsAction);

    Single<AncientOneInfo> selectAncientOne();

    Single<DifficultyId> selectDifficulty();

    void toGameScreen();

    Completable displayChalkboard(String text, int positionX, int positionY);

    void hideChalkboard();

    void clearTouchBlocker(TouchBlockerData.BlockerTarget blockerTarget);

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

    void rewriteUnlockedAssets(String unlockedAssets);

    void rewriteUnlockedArtifacts(String unlockedArtifacts);

    String getUnlockedAssets();

    String getUnlockedArtifacts();

    boolean hasPurchasedNoAds();

    boolean hasPurchasedBonusInvestigators();

}
