package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.AfterEventAction;
import sk.sivak.eldritchhorror.core.action.AfterEventActionImpl;
import sk.sivak.eldritchhorror.core.action.BeforeEventAction;
import sk.sivak.eldritchhorror.core.action.BeforeEventActionImpl;
import sk.sivak.eldritchhorror.core.action.DirectEventAction;
import sk.sivak.eldritchhorror.core.action.HookableAction;
import sk.sivak.eldritchhorror.core.action.InitValueAction;
import sk.sivak.eldritchhorror.core.action.InitValueActionImpl;
import sk.sivak.eldritchhorror.core.action.impl.card.FireSpellResolvedEventAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.CloseGateAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.DiscardGateAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.HideBackgroundAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.HideSelectEncounterTableAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.MoveCameraToLocationAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.ShowDetainedBackgroundAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.ShowLocationBackgroundAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.ShowLocationTypeBackgroundAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.ShowOtherWorldBackgroundAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.ShowResearchBackgroundAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.ShowRlyehRisenBackgroundAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.ShowTheKeyAndTheGateBackgroundAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.ShowVoidBetweenWorldsBackgroundAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.ShowWholeWorldAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.SwapInvestigatorAndGateLayerAction;
import sk.sivak.eldritchhorror.core.action.impl.expedition.DestroyCurrentExpeditionLocationAction;
import sk.sivak.eldritchhorror.core.action.impl.expedition.DiscardTopExpeditionAction;
import sk.sivak.eldritchhorror.core.action.impl.initgame.RegisterNewInvestigatorActionAction;
import sk.sivak.eldritchhorror.core.action.impl.investigator.DefeatInvestigatorAction;
import sk.sivak.eldritchhorror.core.action.impl.investigator.DevourInvestigatorAction;
import sk.sivak.eldritchhorror.core.action.impl.investigator.DisableCardAction;
import sk.sivak.eldritchhorror.core.action.impl.investigator.DiscardArtifactFromInvestigatorAction;
import sk.sivak.eldritchhorror.core.action.impl.investigator.DiscardAssetFromInvestigatorAction;
import sk.sivak.eldritchhorror.core.action.impl.investigator.DiscardConditionFromInvestigatorAction;
import sk.sivak.eldritchhorror.core.action.impl.investigator.DiscardDefeatedInvestigatorAction;
import sk.sivak.eldritchhorror.core.action.impl.investigator.DiscardSpellFromInvestigatorAction;
import sk.sivak.eldritchhorror.core.action.impl.investigator.EnableCardAction;
import sk.sivak.eldritchhorror.core.action.impl.investigator.GainArtifactFromDeckAction;
import sk.sivak.eldritchhorror.core.action.impl.investigator.GainAssetFromDeckAction;
import sk.sivak.eldritchhorror.core.action.impl.investigator.GainConditionFromDeckAction;
import sk.sivak.eldritchhorror.core.action.impl.investigator.GainSpellFromDeckAction;
import sk.sivak.eldritchhorror.core.action.impl.investigator.RemoveDefeatedInvestigatorAction;
import sk.sivak.eldritchhorror.core.action.impl.investigator.ReplaceInvestigatorsAction;
import sk.sivak.eldritchhorror.core.action.impl.investigator.SelectNewLeadInvestigatorAction;
import sk.sivak.eldritchhorror.core.action.impl.test.TestRequestToTestResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.CloseGateData;
import sk.sivak.eldritchhorror.core.eventtype.data.DisableCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.DiscardArtifactFromInvestigatorData;
import sk.sivak.eldritchhorror.core.eventtype.data.DiscardAssetFromInvestigatorData;
import sk.sivak.eldritchhorror.core.eventtype.data.DiscardConditionFromInvestigatorData;
import sk.sivak.eldritchhorror.core.eventtype.data.DiscardGateData;
import sk.sivak.eldritchhorror.core.eventtype.data.DiscardSpellFromInvestigatorData;
import sk.sivak.eldritchhorror.core.eventtype.data.EnableCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.GainArtifactFromDeckData;
import sk.sivak.eldritchhorror.core.eventtype.data.GainAssetFromDeckData;
import sk.sivak.eldritchhorror.core.eventtype.data.GainConditionFromDeckData;
import sk.sivak.eldritchhorror.core.eventtype.data.GainSpellFromDeckData;
import sk.sivak.eldritchhorror.core.eventtype.data.TestRequestData;
import sk.sivak.eldritchhorror.core.eventtype.data.TestResponseData;
import sk.sivak.eldritchhorror.core.eventtype.data.spell.ResolvedSpellData;

/**
 * @author msivak
 */
public class ActionProviderImpl extends AbstractActionProvider implements ActionProvider {

    @Override
    public <InOut> BeforeEventAction<InOut> getBeforeEventAction(BeforeAfterEvent beforeAfterEvent) {
        BeforeEventActionImpl<InOut> beforeEventAction = new BeforeEventActionImpl<>();
        beforeEventAction.setBeforeAfterEvent(beforeAfterEvent);
        return beforeEventAction;
    }

    @Override
    public <InOut> InitValueAction<InOut> getInitValueAction(InOut data) {
        return new InitValueActionImpl<>(data);
    }

    @Override
    public <InOut>
    AfterEventAction<InOut> getAfterEventAction(BeforeAfterEvent beforeAfterEvent) {
        AfterEventActionImpl<InOut> afterEventAction = new AfterEventActionImpl<>();
        afterEventAction.setBeforeAfterEvent(beforeAfterEvent);
        return afterEventAction;
    }

    @Override
    public HookableAction<TestRequestData, TestResponseData> testRequestToTestResponse() {
        return new TestRequestToTestResponse();
    }

    @Override
    public HookableAction<GainAssetFromDeckData, GainAssetFromDeckData> gainAssetFromDeckAction() {
        return new GainAssetFromDeckAction();
    }

    @Override
    public HookableAction<GainArtifactFromDeckData, GainArtifactFromDeckData> gainArtifactFromDeckAction() {
        return new GainArtifactFromDeckAction();
    }

    @Override
    public HookableAction<GainSpellFromDeckData, GainSpellFromDeckData> gainSpellFromDeckAction() {
        return new GainSpellFromDeckAction();
    }

    @Override
    public HookableAction<GainConditionFromDeckData, GainConditionFromDeckData> gainConditionFromDeckAction() {
        return new GainConditionFromDeckAction();
    }

    @Override
    public HookableAction<DiscardAssetFromInvestigatorData, DiscardAssetFromInvestigatorData> discardAssetFromInvestigator() {
        return new DiscardAssetFromInvestigatorAction();
    }

    @Override
    public HookableAction<DiscardArtifactFromInvestigatorData, DiscardArtifactFromInvestigatorData> discardArtifactFromInvestigator() {
        return new DiscardArtifactFromInvestigatorAction();
    }

    @Override
    public HookableAction<DiscardConditionFromInvestigatorData, DiscardConditionFromInvestigatorData> discardConditionFromInvestigator() {
        return new DiscardConditionFromInvestigatorAction();
    }

    @Override
    public HookableAction<DiscardSpellFromInvestigatorData, DiscardSpellFromInvestigatorData> discardSpellFromInvestigator() {
        return new DiscardSpellFromInvestigatorAction();
    }

    @Override
    public Action<InvestigatorId, InvestigatorId> registerNewInvestigatorAction() {
        return new RegisterNewInvestigatorActionAction();
    }

    @Override
    public HookableAction<DisableCardData, DisableCardData> disableCard() {
        return new DisableCardAction();
    }

    @Override
    public HookableAction<EnableCardData, EnableCardData> enableCard() {
        return new EnableCardAction();
    }

    @Override
    public Action<Object, Void> discardTopExpedition() {
        return new DiscardTopExpeditionAction();
    }

    @Override
    public Action<Object, Void> destroyCurrentExpeditionLocation() {
        return new DestroyCurrentExpeditionLocationAction();
    }

    @Override
    public Action<Object, Void> hideSelectEncounterTable() {
        return new HideSelectEncounterTableAction();
    }

    @Override
    public Action<Object, Void> moveCameraToLocation(LocationId locationId) {
        return new MoveCameraToLocationAction(locationId);
    }

    @Override
    public Action<Object, Void> showWholeWorld() {
        return new ShowWholeWorldAction();
    }

    @Override
    public Action<Object, Void> showLocationBackground(LocationId locationId) {
        return new ShowLocationBackgroundAction(locationId);
    }

    @Override
    public Action<Object, Object> showDetainedBackground() {
        return new ShowDetainedBackgroundAction();
    }

    @Override
    public Action<Object, Object> showOtherWorldBackground() {
        return new ShowOtherWorldBackgroundAction();
    }

    @Override
    public Action<Object, Void> showLocationTypeBackground(LocationType locationType) {
        return new ShowLocationTypeBackgroundAction(locationType);
    }

    @Override
    public Action<Object, Void> showResearchBackground(LocationType locationType) {
        return new ShowResearchBackgroundAction(locationType);
    }

    @Override
    public Action<Object, Void> hideBackground() {
        return new HideBackgroundAction();
    }

    @Override
    public HookableAction<Object, CloseGateData> closeGate(LocationId location, boolean isOtherworldEncounter) {
        return new CloseGateAction(location, isOtherworldEncounter);
    }

    @Override
    public HookableAction<Object, DiscardGateData> discardGate(LocationId location) {
        return new DiscardGateAction(location);
    }

    @Override
    public Action<Object, Object> swapInvestigatorAndGateLayer() {
        return new SwapInvestigatorAndGateLayerAction();
    }

    @Override
    public HookableAction<Object, Void> devourInvestigator(InvestigatorId investigatorId) {
        return new DevourInvestigatorAction(investigatorId);
    }

    @Override
    public HookableAction<Object, Void> defeatInvestigator(InvestigatorId investigatorId, boolean health) {
        return new DefeatInvestigatorAction(investigatorId, health);
    }

    @Override
    public Action<Object, Void> discardDefeatedInvestigator(InvestigatorId investigatorId) {
        return new DiscardDefeatedInvestigatorAction(investigatorId);
    }

    @Override
    public Action<Object, Void> removeDefeatedInvestigator(InvestigatorId investigatorId) {
        return new RemoveDefeatedInvestigatorAction(investigatorId);
    }

    @Override
    public Action<Object, InvestigatorId> selectNewLeadInvestigator(boolean reorder) {
        return new SelectNewLeadInvestigatorAction(reorder);
    }

    @Override
    public Action<Object, Object> showRlyehRisenBackground() {
        return new ShowRlyehRisenBackgroundAction();
    }

    @Override
    public Action<Object, Object> showVoidBetweenWorldsBackground() {
        return new ShowVoidBetweenWorldsBackgroundAction();
    }

    @Override
    public Action<Object, Object> showTheKeyAndTheGateBackground() {
        return new ShowTheKeyAndTheGateBackgroundAction();
    }

    @Override
    public HookableAction<Object, Void> replaceInvestigators() {
        return new ReplaceInvestigatorsAction();
    }

    @Override
    public DirectEventAction<ResolvedSpellData> fireSpellResolvedEvent(ResolvedSpellData resolvedSpellData) {
        return new FireSpellResolvedEventAction(resolvedSpellData);
    }
}
