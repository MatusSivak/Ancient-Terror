package sk.sivak.eldritchhorror.core.service;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventtype.data.TestResponseData;
import sk.sivak.eldritchhorror.core.eventtype.data.spell.ResolvedSpellData;

/**
 * @author msivak
 */
public interface Service extends CommonService {

    void gainArtifactFromDeck(InvestigatorId investigatorId, ArtifactId artifactId);

    void gainAssetFromDeck(InvestigatorId investigatorId, AssetId assetId);

    void gainSpellFromDeck(InvestigatorId investigatorId, SpellId spellId);

    void registerNewInvestigatorAction(InvestigatorId investigatorId);

    void discardAssetFromInvestigator(InvestigatorId investigatorId, AssetInfo assetInfo, boolean forced);

    void discardArtifactFromInvestigator(InvestigatorId investigatorId, ArtifactInfo assetInfo, boolean forced);

    void discardConditionFromInvestigator(InvestigatorId investigatorId, ConditionInfo conditionInfo);

    void discardSpellFromInvestigator(InvestigatorId activeInvestigatorId, SpellInfo spellInfo);

    void disableCard(CardInfo cardInfo);

    void gainConditionFromDeck(InvestigatorId investigatorId, ConditionId conditionId);

    void discardTopExpedition();

    void enableCard(CardInfo cardInfo);

    void hideSelectEncounterTable();

    void moveCameraToLocation(LocationId locationId);

    void showWholeWorld();

    void showOtherWorldBackground();

    void showLocationBackground(LocationId locationId);

    void showDetainedBackground();

    void showLocationTypeBackground(LocationType locationType);

    void showResearchBackground(LocationType locationType);

    void hideBackground();

    void closeGate(LocationId location, boolean isOtherworldEncounter);

    void discardGate(LocationId location);

    void swapInvestigatorAndGateLayer();

    void destroyCurrentExpeditionLocation();

    void showRlyehRisenBackground();

    void showVoidBetweenWorldsBackground();

    void showTheKeyAndTheGateBackground();

    void fireSpellResolvedEvent(ResolvedSpellData resolvedSpellData);
}
