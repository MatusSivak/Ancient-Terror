package sk.sivak.eldritchhorror.core.service;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventtype.data.*;
import sk.sivak.eldritchhorror.core.eventtype.data.spell.ResolvedSpellData;
import sk.sivak.eldritchhorror.core.service.command.ActionCommand;

/**
 * @author msivak
 */
public class ServiceImpl extends AbstractCommonService implements Service {

    @Override
    public void gainArtifactFromDeck(InvestigatorId investigatorId, ArtifactId artifactId) {
        executeHookable(new GainArtifactFromDeckData(investigatorId, artifactId), actionProvider.gainArtifactFromDeckAction());
    }

    @Override
    public void gainAssetFromDeck(InvestigatorId investigatorId, AssetId assetId) {
        executeHookable(new GainAssetFromDeckData(investigatorId, assetId), actionProvider.gainAssetFromDeckAction());
    }

    @Override
    public void gainSpellFromDeck(InvestigatorId investigatorId, SpellId spellId) {
        executeHookable(new GainSpellFromDeckData(investigatorId, spellId), actionProvider.gainSpellFromDeckAction());
    }

    @Override
    public void gainConditionFromDeck(InvestigatorId investigatorId, ConditionId conditionId) {
        executeHookable(new GainConditionFromDeckData(investigatorId, conditionId), actionProvider.gainConditionFromDeckAction());
    }

    @Override
    public void discardTopExpedition() {
        execute(new ActionCommand<>(actionProvider.discardTopExpedition(), "DISCARD_TOP_EXPEDITION"));
    }

    @Override
    public void destroyCurrentExpeditionLocation() {
        execute(new ActionCommand<>(actionProvider.destroyCurrentExpeditionLocation(), "DESTROY_CURRENT_EXPEDITION_LOCATION"));
    }

    @Override
    public void showRlyehRisenBackground() {
        execute(new ActionCommand<>(actionProvider.showRlyehRisenBackground(), "SHOW_RLYEH_RISEN_BACKGROUND"));
    }

    @Override
    public void showVoidBetweenWorldsBackground() {
        execute(new ActionCommand<>(actionProvider.showVoidBetweenWorldsBackground(), "SHOW_VOID_BETWEEN_WORLDS_BACKGROUND"));
    }

    @Override
    public void showTheKeyAndTheGateBackground() {
        execute(new ActionCommand<>(actionProvider.showTheKeyAndTheGateBackground(), "SHOW_THE_KEY_AND_THE_GATE_BACKGROUND"));
    }

    @Override
    public void discardAssetFromInvestigator(InvestigatorId investigatorId, AssetInfo assetInfo, boolean forced) {
        executeHookable(new DiscardAssetFromInvestigatorData(investigatorId, assetInfo, forced), actionProvider.discardAssetFromInvestigator());
    }

    @Override
    public void discardArtifactFromInvestigator(InvestigatorId investigatorId, ArtifactInfo artifactInfo, boolean forced) {
        executeHookable(new DiscardArtifactFromInvestigatorData(investigatorId, artifactInfo, forced), actionProvider.discardArtifactFromInvestigator());
    }

    @Override
    public void discardConditionFromInvestigator(InvestigatorId investigatorId, ConditionInfo conditionInfo) {
        executeHookable(new DiscardConditionFromInvestigatorData(investigatorId, conditionInfo),
                actionProvider.discardConditionFromInvestigator());
    }

    @Override
    public void discardSpellFromInvestigator(InvestigatorId investigatorId, SpellInfo spellInfo) {
        executeHookable(new DiscardSpellFromInvestigatorData(investigatorId, spellInfo),
                actionProvider.discardSpellFromInvestigator());
    }

    @Override
    public void registerNewInvestigatorAction(InvestigatorId investigatorId) {
        execute(new ActionCommand<>(actionProvider.registerNewInvestigatorAction(), "REGISTER_NEW_INVESTIGATOR_ACTION"));
    }

    @Override
    public void disableCard(CardInfo cardInfo) {
        executeHookable(new DisableCardData(cardInfo), actionProvider.disableCard());
    }

    @Override
    public void enableCard(CardInfo cardInfo) {
        executeHookable(new EnableCardData(cardInfo), actionProvider.enableCard());
    }

    @Override
    public void hideSelectEncounterTable() {
        execute(new ActionCommand<>(actionProvider.hideSelectEncounterTable(), "HIDE_SELECT_ENCOUNTER_TABLE"));
    }

    @Override
    public void moveCameraToLocation(LocationId locationId) {
        execute(new ActionCommand<>(actionProvider.moveCameraToLocation(locationId), "MOVE_CAMERA_TO_LOCATION"));
    }

    @Override
    public void showWholeWorld() {
        execute(new ActionCommand<>(actionProvider.showWholeWorld(), "SHOW_WHOLE_WORLD"));
    }

    @Override
    public void showLocationBackground(LocationId locationId) {
        execute(new ActionCommand<>(actionProvider.showLocationBackground(locationId), "SHOW_LOCATION_BACKGROUND"));
    }

    @Override
    public void showDetainedBackground() {
        execute(new ActionCommand<>(actionProvider.showDetainedBackground(), "SHOW_DETAINED_BACKGROUND"));
    }

    @Override
    public void showOtherWorldBackground() {
        execute(new ActionCommand<>(actionProvider.showOtherWorldBackground(), "SHOW_OTHER_WORLD_BACKGROUND"));
    }

    @Override
    public void showLocationTypeBackground(LocationType locationType) {
        execute(new ActionCommand<>(actionProvider.showLocationTypeBackground(locationType), "SHOW_LOCATION_TYPE_BACKGROUND"));
    }

    @Override
    public void showResearchBackground(LocationType locationType) {
        execute(new ActionCommand<>(actionProvider.showResearchBackground(locationType), "SHOW_RESEARCH_BACKGROUND"));
    }

    @Override
    public void hideBackground() {
        execute(new ActionCommand<>(actionProvider.hideBackground(), "HIDE_BACKGROUND"));
    }

    @Override
    public void closeGate(LocationId location, boolean isOtherworldEncounter) {
        executeHookable(actionProvider.closeGate(location, isOtherworldEncounter));
    }

    @Override
    public void discardGate(LocationId location) {
        executeHookable(actionProvider.discardGate(location));
    }

    @Override
    public void swapInvestigatorAndGateLayer() {
        execute(new ActionCommand<>(actionProvider.swapInvestigatorAndGateLayer(), "SWAP_INVESTIGATOR_AND_GATE_LAYER"));
    }

    @Override
    public void fireSpellResolvedEvent(ResolvedSpellData resolvedSpellData) {
        execute(new ActionCommand<>(actionProvider.fireSpellResolvedEvent(resolvedSpellData), "FIRE_SPELL_RESOLVED_EVENT"));
    }
}
