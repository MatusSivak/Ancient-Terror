package sk.sivak.eldritchhorror.core.model.save;

public interface SaveDataRead {
    InvestigatorsSaveDataRead getInvestigators();

    MythosDeckSaveDataRead getMythosDeck();

    AncientOneSaveDataRead getAncientOne();

    ArtifactsDeckSaveDataRead getArtifactsDeck();

    AssetsDeckSaveDataRead getAssetsDeck();

    VortexesSaveDataRead getVortexes();

    ConditionsDeckSaveDataRead getConditionsDeck();

    SpellsDeckSaveDataRead getSpellsDeck();

    CluePoolSaveDataRead getCluePool();

    DoomTrackSaveDataRead getDoomTrack();

    ExpeditionDeckSaveDataRead getExpeditionDeck();

    GateStackSaveDataRead getGateStack();

    ModelSaveDataRead getModel();

    MonsterCupSaveDataRead getMonsterCup();

    MysteryDeckSaveDataRead getMysteryDeck();

    RumorsSaveDataRead getRumors();

    OmenTrackSaveDataRead getOmenTrack();

    PhaseSaveDataRead getPhase();

    String getUnlockedAssets();

    String getUnlockedArtifacts();
}
