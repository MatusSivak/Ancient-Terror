package sk.sivak.eldritchhorror.core.model.save;

public class SaveData implements SaveDataWrite{

    private SaveDataWrite.InvestigatorsSaveDataWrite investigators;
    private SaveDataWrite.MythosDeckSaveDataWrite mythosDeck;
    private SaveDataWrite.AncientOneSaveDataWrite ancientOne;
    private SaveDataWrite.ArtifactsDeckSaveDataWrite artifactsDeck;
    private SaveDataWrite.AssetsDeckSaveDataWrite assetsDeck;
    private SaveDataWrite.ConditionsDeckSaveDataWrite conditionsDeck;
    private SaveDataWrite.SpellsDeckSaveDataWrite spellsDeck;
    private SaveDataWrite.CluePoolSaveDataWrite cluePool;
    private SaveDataWrite.VortexesSaveDataWrite vortexes;
    private SaveDataWrite.DoomTrackSaveDataWrite doomTrack;
    private SaveDataWrite.ExpeditionDeckSaveDataWrite expeditionDeck;
    private SaveDataWrite.GateStackSaveDataWrite gateStack;
    private SaveDataWrite.ModelSaveDataWrite model;
    private SaveDataWrite.MonsterCupSaveDataWrite monsterCup;
    private SaveDataWrite.MysteryDeckSaveDataWrite mysteryDeck;
    private SaveDataWrite.RumorsSaveDataWrite rumors;
    private SaveDataWrite.OmenTrackSaveDataWrite omenTrack;
    private SaveDataWrite.PhaseSaveDataWrite phase;
    private String unlockedAssets;
    private String unlockedArtifacts;
    private GeneralEncounterDeckSaveDataWrite generalEncounterDeck;

    @Override
    public void setInvestigators(InvestigatorsSaveDataWrite investigators) {
        this.investigators = investigators;
    }

    @Override
    public void setMythosDeck(MythosDeckSaveDataWrite mythosDeck) {
        this.mythosDeck = mythosDeck;
    }

    @Override
    public InvestigatorsSaveDataRead getInvestigators() {
        return investigators;
    }

    @Override
    public MythosDeckSaveDataRead getMythosDeck() {
        return mythosDeck;
    }

    @Override
    public void setAncientOne(AncientOneSaveDataWrite ancientOne) {
        this.ancientOne = ancientOne;
    }

    @Override
    public AncientOneSaveDataRead getAncientOne() {
        return ancientOne;
    }

    @Override
    public ArtifactsDeckSaveDataRead getArtifactsDeck() {
        return artifactsDeck;
    }

    @Override
    public AssetsDeckSaveDataRead getAssetsDeck() {
        return assetsDeck;
    }

    @Override
    public ConditionsDeckSaveDataRead getConditionsDeck() {
        return conditionsDeck;
    }

    @Override
    public SpellsDeckSaveDataRead getSpellsDeck() {
        return spellsDeck;
    }

    @Override
    public CluePoolSaveDataRead getCluePool() {
        return cluePool;
    }

    @Override
    public VortexesSaveDataWrite getVortexes() {
        return vortexes;
    }

    @Override
    public DoomTrackSaveDataRead getDoomTrack() {
        return doomTrack;
    }

    @Override
    public ExpeditionDeckSaveDataRead getExpeditionDeck() {
        return expeditionDeck;
    }

    @Override
    public GateStackSaveDataRead getGateStack() {
        return gateStack;
    }

    @Override
    public ModelSaveDataRead getModel() {
        return model;
    }

    @Override
    public MonsterCupSaveDataRead getMonsterCup() {
        return monsterCup;
    }

    @Override
    public MysteryDeckSaveDataRead getMysteryDeck() {
        return mysteryDeck;
    }

    @Override
    public RumorsSaveDataWrite getRumors() {
        return rumors;
    }

    @Override
    public OmenTrackSaveDataRead getOmenTrack() {
        return omenTrack;
    }

    @Override
    public PhaseSaveDataRead getPhase() {
        return phase;
    }

    @Override
    public String getUnlockedAssets() {
        return unlockedAssets;
    }

    @Override
    public String getUnlockedArtifacts() {
        return unlockedArtifacts;
    }

    @Override
    public void setArtifactsDeck(ArtifactsDeckSaveDataWrite artifactsDeck) {
        this.artifactsDeck = artifactsDeck;
    }

    @Override
    public void setAssetsDeck(AssetsDeckSaveDataWrite assetsDeck) {
        this.assetsDeck = assetsDeck;
    }

    @Override
    public void setConditionsDeck(ConditionsDeckSaveDataWrite conditionsDeck) {
        this.conditionsDeck = conditionsDeck;
    }

    @Override
    public void setSpellsDeck(SpellsDeckSaveDataWrite spellsDeck) {
        this.spellsDeck = spellsDeck;
    }

    @Override
    public void setCluePool(CluePoolSaveDataWrite cluePool) {
        this.cluePool = cluePool;
    }

    @Override
    public void setVortexes(VortexesSaveDataWrite vortexes) {
        this.vortexes = vortexes;
    }

    @Override
    public void setDoomTrack(DoomTrackSaveDataWrite doomTrack) {
        this.doomTrack = doomTrack;
    }

    @Override
    public void setExpeditionDeck(ExpeditionDeckSaveDataWrite expeditionDeck) {
        this.expeditionDeck = expeditionDeck;
    }

    @Override
    public void setGateStack(GateStackSaveDataWrite gateStack) {
        this.gateStack = gateStack;
    }

    @Override
    public void setModel(ModelSaveDataWrite model) {
        this.model = model;
    }

    @Override
    public void setMonsterCup(MonsterCupSaveDataWrite monsterCup) {
        this.monsterCup = monsterCup;
    }

    @Override
    public void setMysteryDeck(MysteryDeckSaveDataWrite mysteryDeck) {
        this.mysteryDeck = mysteryDeck;
    }

    @Override
    public void setRumors(RumorsSaveDataWrite rumors) {
        this.rumors = rumors;
    }

    @Override
    public void setOmenTrack(OmenTrackSaveDataWrite omenTrack) {
        this.omenTrack = omenTrack;
    }

    @Override
    public void setPhase(PhaseSaveDataWrite phase) {
        this.phase = phase;
    }

    @Override
    public void setUnlockedArtifacts(String unlockedArtifacts) {
        this.unlockedArtifacts = unlockedArtifacts;
    }

    @Override
    public void setUnlockedAssets(String unlockedAssets) {
        this.unlockedAssets = unlockedAssets;
    }

    @Override
    public void setGeneralEncounterDeck(GeneralEncounterDeckSaveDataWrite generalEncounterDeck) {
        this.generalEncounterDeck = generalEncounterDeck;
    }
}
