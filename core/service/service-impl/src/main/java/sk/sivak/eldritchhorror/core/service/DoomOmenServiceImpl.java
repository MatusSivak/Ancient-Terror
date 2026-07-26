package sk.sivak.eldritchhorror.core.service;

import sk.sivak.eldritchhorror.core.action.InitValueAction;
import sk.sivak.eldritchhorror.core.action.provider.DoomOmenActionProvider;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.eventtype.data.rumor.InitRumorCardData;
import sk.sivak.eldritchhorror.core.service.command.ActionCommand;
import sk.sivak.eldritchhorror.core.service.util.CommandNameResolver;

/**
 * @author msivak
 */
public class DoomOmenServiceImpl extends AbstractCommonService implements DoomOmenService {

    private DoomOmenActionProvider doomOmenActionProvider;

    public void setDoomOmenActionProvider(DoomOmenActionProvider doomOmenActionProvider) {
        this.doomOmenActionProvider = doomOmenActionProvider;
    }

    @Override
    public void retreatDoom() {
        executeHookable(doomOmenActionProvider.retreatDoom());
    }

    @Override
    public void advanceDoom() {
        executeHookable(1, doomOmenActionProvider.advanceDoom());
    }

    @Override
    public void advanceDoom(int amount) {
        executeHookable(amount, doomOmenActionProvider.advanceDoom());
    }

    @Override
    public void advanceOmen() {
        hold();
        executeHookable(doomOmenActionProvider.advanceOmen());
        execute(new ActionCommand<>(doomOmenActionProvider.omenChangedEvent(), "OMEN_CHANGED_EVENT"));
        executeHookable(doomOmenActionProvider.advanceDoomByCurrentOmen());
        release();
    }

    @Override
    public void advanceDoomByCurrentOmen(OmenInfo omenInfo) {
        executeHookable(omenInfo, doomOmenActionProvider.advanceDoomByCurrentOmen());
    }

    @Override
    public void selectNewOmen() {
        hold();
        execute(new ActionCommand<>(doomOmenActionProvider.selectNewOmen(), "SELECT_NEW_OMEN"));
        execute(new ActionCommand<>(doomOmenActionProvider.omenChangedEvent(), "OMEN_CHANGED_EVENT"));
        release();
    }

    @Override
    public void addTokenToOmenTrack(OmenId omenId) {
        InitValueAction<OmenId> initValueAction = actionProvider.getInitValueAction(omenId);
        String initValueName = CommandNameResolver.createInitValueName(omenId);
        execute(
                new ActionCommand<>(initValueAction, initValueName),
                new ActionCommand<>(doomOmenActionProvider.addTokenToOmenTrack(), "ADD_TOKEN_TO_OMEN_TRACK")
        );
    }

    @Override
    public void removeTokenFromOmenTrack(OmenId omenId) {
        execute(new ActionCommand<>(doomOmenActionProvider.removeTokenFromOmenTrack(omenId), "REMOVE_TOKEN_FROM_OMEN_TRACK"));
    }

    @Override
    public void initMythosPhase() {
        hold();
        drawMythosCard();
        resolveCurrentMystery();
        executeHookable(doomOmenActionProvider.prepareNewRound());
        release();
    }

    @Override
    public void resolveCurrentMystery() {
        executeHookable(doomOmenActionProvider.resolveCurrentMystery());
    }

    @Override
    public void drawMythosCard() {
        executeHookable(doomOmenActionProvider.drawMythosCard());
    }

    @Override
    public void displayVictoryPaper() {
        execute(new ActionCommand<>(doomOmenActionProvider.displayVictoryPaper(), "DISPLAY_VICTORY_PAPER"));
    }

    @Override
    public void onTriggeredReckoning() {
        execute(new ActionCommand<>(doomOmenActionProvider.onTriggeredReckoning(), "ON_TRIGGERED_RECKONING"));
    }

    @Override
    public void unlockRandomCard() {
        execute(new ActionCommand<>(doomOmenActionProvider.unlockRandomCard(), "UNLOCK_RANDOM_CARD"));
    }

    @Override
    public void initRumorCard(InitRumorCardData initRumorCardData) {
        execute(new ActionCommand<>(doomOmenActionProvider.initRumorCard(initRumorCardData), "INIT_RUMOR_CARD"));
    }

    @Override
    public void showRumorCard(String rumorId) {
        execute(new ActionCommand<>(doomOmenActionProvider.showRumorCard(rumorId), "SHOW_RUMOR_CARD"));
    }

    @Override
    public void countdownRumorCard(String rumorId, int amount) {
        execute(new ActionCommand<>(doomOmenActionProvider.countdownRumorCard(rumorId, amount), "COUNTDOWN_RUMOR_CARD"));
    }

    @Override
    public void highlightRumorFailure(String rumorId) {
        execute(new ActionCommand<>(doomOmenActionProvider.highlightRumorFailure(rumorId), "HIGHLIGHT_RUMOR_FAILURE"));
    }

    @Override
    public void highlightRumorObjective(String rumorId) {
        execute(new ActionCommand<>(doomOmenActionProvider.highlightRumorObjective(rumorId), "HIGHLIGHT_RUMOR_OBJECTIVE"));
    }

    @Override
    public void highlightRumorReckoning(String rumorId) {
        execute(new ActionCommand<>(doomOmenActionProvider.highlightRumorReckoning(rumorId), "HIGHLIGHT_RUMOR_RECKONING"));
    }

    @Override
    public void spawnStorm(String stormId, LocationId location) {
        execute(new ActionCommand<>(doomOmenActionProvider.spawnStorm(stormId, location), "SPAWN_STORM"));
    }

    @Override
    public void ancientOneAwakens() {
        execute(new ActionCommand<>(doomOmenActionProvider.ancientOneAwakens(), "ANCIENT_ONE_AWAKENS"));
    }

    @Override
    public void increaseAncientOnePower(int power) {
        execute(new ActionCommand<>(doomOmenActionProvider.increaseAncientOnePower(power), "INCREASE_ANCIENT_ONE_POWER"));
    }
}
