package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.*;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.*;
import sk.sivak.eldritchhorror.core.eventtype.data.spell.ResolvedSpellData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

/**
 * @author msivak
 */
public interface ActionProvider {

    <InOut> BeforeEventAction<InOut> getBeforeEventAction(BeforeAfterEvent beforeAfterEvent);

    <InOut> InitValueAction<InOut> getInitValueAction(InOut data);

    <InOut> AfterEventAction<InOut> getAfterEventAction(BeforeAfterEvent beforeAfterEvent);

    HookableAction<TestRequestData, TestResponseData> testRequestToTestResponse();

    HookableAction<GainAssetFromDeckData, GainAssetFromDeckData> gainAssetFromDeckAction();

    HookableAction<GainArtifactFromDeckData, GainArtifactFromDeckData> gainArtifactFromDeckAction();

    HookableAction<GainSpellFromDeckData, GainSpellFromDeckData> gainSpellFromDeckAction();

    HookableAction<GainConditionFromDeckData, GainConditionFromDeckData> gainConditionFromDeckAction();

    HookableAction<DiscardAssetFromInvestigatorData, DiscardAssetFromInvestigatorData> discardAssetFromInvestigator();

    HookableAction<DiscardArtifactFromInvestigatorData, DiscardArtifactFromInvestigatorData> discardArtifactFromInvestigator();

    HookableAction<DiscardConditionFromInvestigatorData, DiscardConditionFromInvestigatorData> discardConditionFromInvestigator();

    HookableAction<DiscardSpellFromInvestigatorData, DiscardSpellFromInvestigatorData> discardSpellFromInvestigator();

    Action<InvestigatorId, InvestigatorId> registerNewInvestigatorAction();

    HookableAction<DisableCardData, DisableCardData> disableCard();

    HookableAction<EnableCardData, EnableCardData> enableCard();

    Action<Object, Void> discardTopExpedition();

    Action<Object, Void> destroyCurrentExpeditionLocation();

    Action<Object, Void> hideSelectEncounterTable();

    Action<Object, Void> moveCameraToLocation(LocationId locationId);

    Action<Object, Void> showWholeWorld();

    Action<Object, Void> showLocationBackground(LocationId locationId);

    Action<Object, Object> showDetainedBackground();

    Action<Object, Void> showLocationTypeBackground(LocationType locationType);

    Action<Object, Void> showResearchBackground(LocationType locationType);

    Action<Object, Void> hideBackground();

    HookableAction<Object, CloseGateData> closeGate(LocationId location, boolean isOtherworldEncounter);

    HookableAction<Object, DiscardGateData> discardGate(LocationId location);

    Action<Object, Object> showOtherWorldBackground();

    Action<Object, Object> swapInvestigatorAndGateLayer();

    HookableAction<Object, Void> devourInvestigator(InvestigatorId investigatorId);

    HookableAction<Object, Void> defeatInvestigator(InvestigatorId investigatorId, boolean health);

    Action<Object, Void> discardDefeatedInvestigator(InvestigatorId investigatorId);

    Action<Object, Void> removeDefeatedInvestigator(InvestigatorId investigatorId);

    Action<Object, InvestigatorId> selectNewLeadInvestigator(boolean reorder);

    Action<Object, Object> showRlyehRisenBackground();

    Action<Object, Object> showVoidBetweenWorldsBackground();

    Action<Object, Object> showTheKeyAndTheGateBackground();

    HookableAction<Object, Void> replaceInvestigators();

    DirectEventAction<ResolvedSpellData> fireSpellResolvedEvent(ResolvedSpellData resolvedSpellData);
}
