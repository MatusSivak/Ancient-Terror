package sk.sivak.eldritchhorror.core.service;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.InitValueAction;
import sk.sivak.eldritchhorror.core.action.provider.TokenActionProvider;
import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.GainHealthData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.GainSanityData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.MoveMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.*;
import sk.sivak.eldritchhorror.core.service.command.ActionCommand;
import sk.sivak.eldritchhorror.core.service.data.ClueFocusHealthSanity;
import sk.sivak.eldritchhorror.core.service.util.CommandNameResolver;

import java.util.List;

/**
 * @author msivak
 */
public class TokenServiceImpl extends AbstractCommonService implements TokenService {

    private TokenActionProvider tokenActionProvider;

    public void setTokenActionProvider(TokenActionProvider tokenActionProvider) {
        this.tokenActionProvider = tokenActionProvider;
    }

    @Override
    public Single<List<ClueInfo>> spawnClues(int amount) {
        return Single.create(onSub -> executeHookable(amount, tokenActionProvider.spawnCluesAction(false), onSub));
    }

    @Override
    public Single<List<ClueInfo>> spawnCluesQuick(int amount) {
        return Single.create(onSub -> executeHookable(amount, tokenActionProvider.spawnCluesAction(true), onSub));
    }

    @Override
    public void gainHealth(int amount) {
        executeHookable(new GainHealthData(amount), tokenActionProvider.gainHealth());
    }

    @Override
    public void gainSanity(int amount) {
        executeHookable(new GainSanityData(amount), tokenActionProvider.gainSanity());
    }


    @Override
    public void gainClueFromPool() {
        gainClueFromPool(new GainClueData(false));
    }

    @Override
    public void gainClueFromSpace(GainClueData gainClueData) {
        executeHookable(gainClueData, tokenActionProvider.gainClueFromSpace());
    }

    @Override
    public void gainClueFromPool(InvestigatorId investigatorId) {
        gainClueFromPool(new GainClueData(false, investigatorId));
    }

    private void gainClueFromPool(GainClueData gainClueData) {
        executeHookable(gainClueData, tokenActionProvider.gainClueFromPool());
    }

    @Override
    public void gainFocus() {
        executeHookable(new GainFocusData(), tokenActionProvider.gainFocus());
    }

    @Override
    public void loseFocus() {
        executeHookable(new LoseTokenData(1, TokenType.FOCUS), tokenActionProvider.loseFocus());
    }

    @Override
    public void loseClue() {
        executeHookable(new LoseTokenData(1, TokenType.CLUE), tokenActionProvider.loseClue());
    }

    @Override
    public void loseHealth(int amount) {
        executeHookable(new LoseTokenData(amount, TokenType.HEALTH), tokenActionProvider.loseHealth());
    }

    @Override
    public void spendHealth(int amount) {
        executeHookable(new LoseTokenData(amount, TokenType.HEALTH), tokenActionProvider.spendHealth());
    }

    @Override
    public void loseSanity(int amount) {
        executeHookable(new LoseTokenData(amount, TokenType.SANITY), tokenActionProvider.loseSanity());
    }

    @Override
    public void spendSanity(int amount) {
        executeHookable(new LoseTokenData(amount, TokenType.SANITY), tokenActionProvider.spendSanity());
    }

    @Override
    public Single<SpendData> canSpend(int clue, int focus, int health, int sanity) {
        return spend(clue, focus, health, sanity, true);
    }

    @Override
    public Single<SpendData> spend(int clue, int focus, int health, int sanity) {
        return spend(clue, focus, health, sanity, false);
    }

    @Override
    public Single<SpendData> canSpend(ClueFocusHealthSanity data) {
        return canSpend(data.getClue(), data.getFocus(), data.getHealth(), data.getSanity());
    }

    @Override
    public Single<SpendData> spend(ClueFocusHealthSanity data) {
        return spend(data.getClue(), data.getFocus(), data.getHealth(), data.getSanity());
    }

    @Override
    public void discardClue(LocationId locationId) {
        executeHookable(locationId, tokenActionProvider.discardClue());
    }

    @Override
    public void spawnClueAt(LocationId locationId) {
        execute(new ActionCommand<>(tokenActionProvider.spawnClueAt(locationId), "SPAWN_CLUE_AT"));
    }

    public Single<SpendData> spend(int clue, int focus, int health, int sanity, boolean silent) {
        return Single.create(onSub -> {
            SpendData spendData = new SpendData();
            spendData.setSilent(silent);
            spendData.getTokenAmountMap().put(TokenType.CLUE, clue);
            spendData.getTokenAmountMap().put(TokenType.FOCUS, focus);
            spendData.getTokenAmountMap().put(TokenType.HEALTH, health);
            spendData.getTokenAmountMap().put(TokenType.SANITY, sanity);
            executeHookable(spendData, tokenActionProvider.spend(), onSub);
        });
    }

    @Override
    public void moveClue(LocationId spawnLocation, LocationId targetLocation) {
        executeHookable(new MoveTokenData(spawnLocation, targetLocation), tokenActionProvider.moveClue());
    }
}
