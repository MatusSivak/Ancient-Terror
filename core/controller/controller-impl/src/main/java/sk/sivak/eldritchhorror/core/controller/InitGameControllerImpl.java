package sk.sivak.eldritchhorror.core.controller;

import java8.features.function.Supplier;
import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.difficulty.DifficultyId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.constants.tutorial.TouchBlockerData;
import sk.sivak.eldritchhorror.core.model.InvestigatorsRead;
import sk.sivak.eldritchhorror.core.model.ModelRead;
import sk.sivak.eldritchhorror.core.model.MysteryDeckRead;
import sk.sivak.eldritchhorror.core.view.InitGameView;

import java.util.List;

/**
 * @author msivak
 */
public class InitGameControllerImpl implements InitGameController {

    private ModelRead model;
    private MysteryDeckRead mysteryDeck;
    private InitGameView view;
    private InvestigatorsRead investigatorsRead;

    public void setModel(ModelRead model) {
        this.model = model;
    }

    public void setInvestigatorsRead(InvestigatorsRead investigatorsRead) {
        this.investigatorsRead = investigatorsRead;
    }

    public void setView(InitGameView view) {
        this.view = view;
    }

    @Override
    public Single<Integer> getNumberOfPlayers() {
        return view.getNumberOfPlayers();
    }

    @Override
    public Single<InvestigatorInfo[]> selectInvestigators(Integer input, Supplier<List<InvestigatorInfo>> initInvestigatorsAction) {
        return view.selectInvestigators(input, investigatorsRead.getAvailableInvestigators(), initInvestigatorsAction);
    }

    @Override
    public Single<AncientOneInfo> selectAncientOne() {
        return view.selectAncientOne(model.getAvailableAncientOnes());
    }

    @Override
    public Single<DifficultyId> selectDifficulty() {
        return view.selectDifficulty();
    }

    @Override
    public void toGameScreen() {
        view.toGameScreen();
    }

    @Override
    public Completable displayChalkboard(String text, int positionX, int positionY) {
        return view.displayChalkboard(text, positionX, positionY);
    }

    @Override
    public void hideChalkboard() {
        view.hideChalkboard();
    }

    @Override
    public void clearTouchBlocker(TouchBlockerData.BlockerTarget blockerTarget) {
        view.clearTouchBlockers(blockerTarget);
    }
    @Override
    public void displayTouchBlocker(TouchBlockerData touchBlockerData) {
        view.displayTouchBlocker(touchBlockerData);
    }

    @Override
    public Completable showHudButtons() {
        return view.showHudButtons();
    }

    @Override
    public Completable waitForAncientOneClick() {
        return view.waitForAncientOneClick();
    }

    @Override
    public Completable waitForReserveClick() {
        return view.waitForReserveClick();
    }

    @Override
    public Completable waitForMysteryClick() {
        return view.waitForMysteryClick();
    }

    @Override
    public Completable waitForInvestigatorClick() {
        return view.waitForInvestigatorClick();
    }

    @Override
    public Completable waitForAncientOneHide() {
        return view.waitForAncientOneHide();
    }

    @Override
    public Completable waitForMysteryHide() {
        return view.waitForMysteryHide();
    }

    @Override
    public Completable waitForInvestigatorHide() {
        return view.waitForInvestigatorHide();
    }

    @Override
    public Completable waitForCharterFlightInspected() {
        return view.waitForCharterFlightInspected();
    }

    public void setMysteryDeck(MysteryDeckRead mysteryDeck) {
        this.mysteryDeck = mysteryDeck;
    }

    @Override
    public void save(Object saveData) {
        view.save(saveData);
    }

    @Override
    public <T> T load(Class<T> saveDataClazz) {
        return view.load(saveDataClazz);
    }

    public void rewriteUnlockedAssets(String unlockedAssets) {
        view.rewriteUnlockedAssets(unlockedAssets);
    }

    @Override
    public void rewriteUnlockedArtifacts(String unlockedArtifacts) {
        view.rewriteUnlockedArtifacts(unlockedArtifacts);
    }

    @Override
    public String getUnlockedAssets() {
        return view.getUnlockedAssets();
    }

    @Override
    public String getUnlockedArtifacts() {
        return view.getUnlockedArtifacts();
    }

    @Override
    public boolean hasPurchasedNoAds() {
        return view.hasPurchasedNoAds();
    }

    @Override
    public boolean hasPurchasedBonusInvestigators() {
        return view.hasPurchasedBonusInvestigators();
    }
}
