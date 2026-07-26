package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.MysteryCardId;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.clue.Clue;
import sk.sivak.eldritchhorror.core.constants.gate.Gate;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.constants.phase.PhaseType;

import java.util.List;
import java.util.Map;

public interface SaveDataWrite extends SaveDataRead {
    void setInvestigators(InvestigatorsSaveDataWrite investigators);

    void setMythosDeck(MythosDeckSaveDataWrite mythosDeckSaveDataWrite);

    void setAncientOne(AncientOneSaveDataWrite ancientOne);

    void setArtifactsDeck(ArtifactsDeckSaveDataWrite artifactsDeck);

    void setAssetsDeck(AssetsDeckSaveDataWrite assetsDeck);

    void setConditionsDeck(ConditionsDeckSaveDataWrite conditionsDeck);

    void setSpellsDeck(SpellsDeckSaveDataWrite spellsDeck);

    void setCluePool(CluePoolSaveDataWrite cluePool);

    void setVortexes(VortexesSaveDataWrite vortexes);

    void setDoomTrack(DoomTrackSaveDataWrite doomTrack);

    void setExpeditionDeck(ExpeditionDeckSaveDataWrite expeditionDeck);

    void setGateStack(GateStackSaveDataWrite gateStack);

    void setModel(ModelSaveDataWrite model);

    void setMonsterCup(MonsterCupSaveDataWrite monsterCup);

    void setMysteryDeck(MysteryDeckSaveDataWrite mysteryDeck);

    void setRumors(RumorsSaveDataWrite rumors);

    void setOmenTrack(OmenTrackSaveDataWrite omenTrack);

    void setPhase(PhaseSaveDataWrite phase);

    void setUnlockedArtifacts(String unlockedArtifacts);

    void setUnlockedAssets(String unlockedAssets);

    void setGeneralEncounterDeck(GeneralEncounterDeckSaveDataWrite generalEncounterDeck);

    interface InvestigatorsSaveDataWrite extends InvestigatorsSaveDataRead{
    }

    interface MythosDeckSaveDataWrite extends MythosDeckSaveDataRead{
    }

    interface AncientOneSaveDataWrite extends AncientOneSaveDataRead{
    }

    interface ArtifactsDeckSaveDataWrite extends ArtifactsDeckSaveDataRead{
        interface OwnedArtifactSaveDataWrite extends ArtifactsDeckSaveDataRead.OwnedArtifactSaveDataRead {}
    }

    interface AssetsDeckSaveDataWrite extends AssetsDeckSaveDataRead{
        interface OwnedAssetSaveDataWrite extends OwnedAssetSaveDataRead {}
    }

    interface CluePoolSaveDataWrite extends CluePoolSaveDataRead{

    }

    interface VortexesSaveDataWrite extends VortexesSaveDataRead{

    }

    interface ConditionsDeckSaveDataWrite extends ConditionsDeckSaveDataRead{
    }

    interface DoomTrackSaveDataWrite extends DoomTrackSaveDataRead{

    }

    interface ExpeditionDeckSaveDataWrite extends ExpeditionDeckSaveDataRead{

    }

    interface GateStackSaveDataWrite extends GateStackSaveDataRead{

    }

    interface ModelSaveDataWrite extends ModelSaveDataRead{

    }

    interface MonsterCupSaveDataWrite extends MonsterCupSaveDataRead{


    }

    interface MysteryDeckSaveDataWrite extends MysteryDeckSaveDataRead{

    }

    interface RumorsSaveDataWrite extends RumorsSaveDataRead{

    }

    interface OmenTrackSaveDataWrite extends OmenTrackSaveDataRead{

    }

    interface PhaseSaveDataWrite extends PhaseSaveDataRead{

    }

    interface SpellsDeckSaveDataWrite extends SpellsDeckSaveDataRead{

    }

    interface GeneralEncounterDeckSaveDataWrite {

    }
}
