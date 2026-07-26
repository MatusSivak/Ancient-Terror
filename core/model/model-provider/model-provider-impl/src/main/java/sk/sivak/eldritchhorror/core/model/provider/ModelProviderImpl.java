package sk.sivak.eldritchhorror.core.model.provider;

import java8.features.function.Supplier;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.model.action.PerformedActions;
import sk.sivak.eldritchhorror.core.model.encounter.GeneralEncounterDeck;
import sk.sivak.eldritchhorror.core.model.encounter.LocationEncounterDeck;
import sk.sivak.eldritchhorror.core.model.encounter.PerformedEncounters;
import sk.sivak.eldritchhorror.core.model.encounter.ResearchEncounterDeck;
import sk.sivak.eldritchhorror.core.model.mythos.MythosDeck;
import sk.sivak.eldritchhorror.core.model.rumor.Rumors;
import sk.sivak.eldritchhorror.core.model.test.TestFlavor;

import java.util.List;

/**
 * @author msivak
 */
public class ModelProviderImpl implements ModelProvider {

    private MysteryDeck mysteryDeck;
    private Rumors rumors;
    private MythosDeck mythosDeck;
    private ExpeditionDeck expeditionDeck;
    private DoomTrack doomTrack;
    private TestFlavor testFlavor;
    private OmenTrack omenTrack;
    private Model model;
    private AssetsDeck assetsDeck;
    private ArtifactsDeck artifactsDeck;
    private PerformedActions performedActions;
    private PerformedEncounters performedEncounters;
    private GeneralEncounterDeck generalEncounterDeck;
    private ResearchEncounterDeck researchEncounterDeck;
    private LocationEncounterDeck locationEncounterDeck;
    private ConditionsDeck conditionsDeck;
    private SpellsDeck spellsDeck;
    private Phase phase;
    private MonsterCup monsterCup;
    private LocationMap locationMap;
    private CluePool cluePool;
    private Vortexes vortexes;
    private GateStack gateStack;
    private Investigators investigators;

    public ModelProviderImpl() {
        this.model = new Model();
        this.assetsDeck = new AssetsDeck();
        this.artifactsDeck = new ArtifactsDeck();
        this.performedActions = new PerformedActions();
        this.performedEncounters = new PerformedEncounters();
        this.generalEncounterDeck = new GeneralEncounterDeck();
        this.researchEncounterDeck = new ResearchEncounterDeck();
        this.locationEncounterDeck = new LocationEncounterDeck();
        this.conditionsDeck = new ConditionsDeck();
        this.spellsDeck = new SpellsDeck();
        this.phase = new Phase();
        this.locationMap = new LocationMap();
        this.cluePool = new CluePool();
        this.vortexes = new Vortexes();
        this.gateStack = new GateStack();
        this.investigators = new Investigators();
        this.omenTrack = new OmenTrack();
        this.doomTrack = new DoomTrack();
        this.expeditionDeck = new ExpeditionDeck();
        this.mysteryDeck = new MysteryDeck();
        this.rumors = new Rumors();
        this.mythosDeck = new MythosDeck();
        this.monsterCup = new MonsterCup();
        this.testFlavor = new TestFlavor();
    }

    @Override
    public ModelWrite getModel() {
        return model;
    }

    public LocationMap getLocationMap() {
        return locationMap;
    }

    @Override
    public AssetsDeckWrite getAssetsDeck() {
        return assetsDeck;
    }

    @Override
    public ArtifactsDeckWrite getArtifactsDeck() {
        return artifactsDeck;
    }

    @Override
    public PerformedActions getPerformedActions() {
        return performedActions;
    }

    @Override
    public PerformedEncountersWrite getPerformedEncounters() {
        return performedEncounters;
    }

    @Override
    public GeneralEncounterDeckWrite getGeneralEncounterDeck() {
        return generalEncounterDeck;
    }

    @Override
    public ResearchEncounterDeckWrite getResearchEncounterDeck() {
        return researchEncounterDeck;
    }

    @Override
    public LocationEncounterDeckWrite getLocationEncounterDeck() {
        return locationEncounterDeck;
    }

    @Override
    public CluePoolWrite getCluePool() {
        return cluePool;
    }

    @Override
    public VortexesWrite getVortexes() {
        return vortexes;
    }

    @Override
    public GateStackWrite getGateStack() {
        return gateStack;
    }

    @Override
    public Investigators getInvestigators() {
        return investigators;
    }

    @Override
    public OmenTrack getOmenTrack() {
        return omenTrack;
    }

    @Override
    public DoomTrack getDoomTrack() {
        return doomTrack;
    }

    @Override
    public ExpeditionDeck getExpeditionDeck() {
        return expeditionDeck;
    }

    @Override
    public MysteryDeck getMysteryDeck() {
        return mysteryDeck;
    }

    @Override
    public Rumors getRumors() {
        return rumors;
    }

    @Override
    public MythosDeck getMythosDeck() {
        return mythosDeck;
    }

    @Override
    public MonsterCup getMonsterCup() {
        return monsterCup;
    }

    @Override
    public ConditionsDeck getConditionsDeck() {
        return conditionsDeck;
    }

    @Override
    public SpellsDeck getSpellsDeck() {
        return spellsDeck;
    }

    @Override
    public PhaseWrite getPhase() {
        return phase;
    }

    @Override
    public TestFlavor getTestFlavor() {
        return testFlavor;
    }

    public void setUnlockedAssetsSupplier(Supplier<List<AssetId>> unlockedAssetsSupplier) {
        assetsDeck.setUnlockedAssetsSupplier(unlockedAssetsSupplier);
    }

    public void setUnlockedArtifactsSupplier(Supplier<List<ArtifactId>> unlockedArtifactsSupplier) {
        artifactsDeck.setUnlockedArtifactsSupplier(unlockedArtifactsSupplier);
    }
}
