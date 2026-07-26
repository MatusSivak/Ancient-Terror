package sk.sivak.eldritchhorror.core.model.provider;

import sk.sivak.eldritchhorror.core.model.*;

/**
 * @author msivak
 */
public interface ModelProvider {

    ModelWrite getModel();

    AssetsDeckWrite getAssetsDeck();

    ArtifactsDeckWrite getArtifactsDeck();

    LocationMapWrite getLocationMap();

    PerformedActionsWrite getPerformedActions();

    PerformedEncountersWrite getPerformedEncounters();

    GeneralEncounterDeckWrite getGeneralEncounterDeck();

    ResearchEncounterDeckWrite getResearchEncounterDeck();

    LocationEncounterDeckWrite getLocationEncounterDeck();

    CluePoolWrite getCluePool();

    VortexesWrite getVortexes();

    GateStackWrite getGateStack();

    InvestigatorsWrite getInvestigators();

    OmenTrackWrite getOmenTrack();

    DoomTrackWrite getDoomTrack();

    ExpeditionDeckWrite getExpeditionDeck();

    MysteryDeckWrite getMysteryDeck();

    RumorsWrite getRumors();

    MythosDeckWrite getMythosDeck();

    MonsterCupWrite getMonsterCup();

    ConditionsDeckWrite getConditionsDeck();

    SpellsDeckWrite getSpellsDeck();

    PhaseWrite getPhase();

    TestFlavorWrite getTestFlavor();
}
