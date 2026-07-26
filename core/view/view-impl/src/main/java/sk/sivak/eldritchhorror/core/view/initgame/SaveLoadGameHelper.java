package sk.sivak.eldritchhorror.core.view.initgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class SaveLoadGameHelper {

    private static final String FILE_NAME = "save.json";
    private final Json json;

    public SaveLoadGameHelper() {
        json = new Json(JsonWriter.OutputType.minimal);
        try {
            json.addClassTag("AncientOne", Class.forName("sk.sivak.eldritchhorror.core.model.save.AncientOneSaveData"));
            json.addClassTag("ArtifactsDeck", Class.forName("sk.sivak.eldritchhorror.core.model.save.ArtifactsDeckSaveData"));
            json.addClassTag("OwnedAsset", Class.forName("sk.sivak.eldritchhorror.core.model.save.AssetsDeckSaveData$OwnedAssetSaveData"));
            json.addClassTag("CluePool", Class.forName("sk.sivak.eldritchhorror.core.model.save.CluePoolSaveData"));
            json.addClassTag("Clue", Class.forName("sk.sivak.eldritchhorror.core.constants.clue.Clue"));
            json.addClassTag("ConditionsDeck", Class.forName("sk.sivak.eldritchhorror.core.model.save.ConditionsDeckSaveData"));
            json.addClassTag("DoomTrack", Class.forName("sk.sivak.eldritchhorror.core.model.save.DoomTrackSaveData"));
            json.addClassTag("ExpeditionDeck", Class.forName("sk.sivak.eldritchhorror.core.model.save.ExpeditionDeckSaveData"));
            json.addClassTag("GateStack", Class.forName("sk.sivak.eldritchhorror.core.model.save.GateStackSaveData"));
            json.addClassTag("Investigators", Class.forName("sk.sivak.eldritchhorror.core.model.save.InvestigatorsSaveData"));
            json.addClassTag("Investigator", Class.forName("sk.sivak.eldritchhorror.core.model.save.InvestigatorsSaveData$InvestigatorSaveData"));
            json.addClassTag("Model", Class.forName("sk.sivak.eldritchhorror.core.model.save.ModelSaveData"));
            json.addClassTag("MonsterCup", Class.forName("sk.sivak.eldritchhorror.core.model.save.MonsterCupSaveData"));
            json.addClassTag("NonEpicMonsterId", Class.forName("sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId"));
            json.addClassTag("EpicMonsterId", Class.forName("sk.sivak.eldritchhorror.core.constants.monster.epic.EpicMonsterId"));
            json.addClassTag("MysteryDeck", Class.forName("sk.sivak.eldritchhorror.core.model.save.MysteryDeckSaveData"));
            json.addClassTag("Azathoth", Class.forName("sk.sivak.eldritchhorror.core.constants.MysteryCardId$Azathoth"));
            json.addClassTag("MythosDeck", Class.forName("sk.sivak.eldritchhorror.core.model.save.MythosDeckSaveData"));
            json.addClassTag("OmenTrack", Class.forName("sk.sivak.eldritchhorror.core.model.save.OmenTrackSaveData"));
            json.addClassTag("Phase", Class.forName("sk.sivak.eldritchhorror.core.model.save.PhaseSaveData"));
            json.addClassTag("SpellsDeck", Class.forName("sk.sivak.eldritchhorror.core.model.save.SpellsDeckSaveData"));
            json.addClassTag("AssetsDeck", Class.forName("sk.sivak.eldritchhorror.core.model.save.AssetsDeckSaveData"));
            json.addClassTag("ConditionDeck", Class.forName("sk.sivak.eldritchhorror.core.model.save.ConditionsDeckSaveData"));
            json.addClassTag("Monster", Class.forName("sk.sivak.eldritchhorror.core.model.save.MonsterCupSaveData$MonsterSaveData"));
            json.addClassTag("SpellInfo", Class.forName("sk.sivak.eldritchhorror.core.model.save.SpellsDeckSaveData$SpellInfoSaveData"));
            json.addClassTag("DefeatedInvestigator",Class.forName("sk.sivak.eldritchhorror.core.model.save.InvestigatorsSaveData$DefeatedInvestigatorSaveData"));
            json.addClassTag("OwnedArtifact",Class.forName("sk.sivak.eldritchhorror.core.model.save.ArtifactsDeckSaveData$OwnedArtifactSaveData"));
            json.addClassTag("GeneralEncounterDeck",Class.forName("sk.sivak.eldritchhorror.core.model.save.GeneralEncounterDeckSaveData"));

            json.addClassTag("Map", Class.forName("java.util.HashMap"));
            json.addClassTag("List", Class.forName("java.util.LinkedList"));



        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }

    }

    public void save(Object saveData) {
        String saveDataString = json.toJson(saveData);
        FileHandle tmpFile = Gdx.files.local(FILE_NAME + ".tmp");
        FileHandle saveFile = Gdx.files.local(FILE_NAME);
        FileHandle bakFile = Gdx.files.local(FILE_NAME + ".bak");
        tmpFile.writeString(saveDataString, false);
        if (saveFile.exists()) {
            saveFile.file().renameTo(bakFile.file());
        }
        tmpFile.file().renameTo(saveFile.file());
    }

    public <T> T load(Class<T> saveDataClazz) {
        FileHandle saveFile = Gdx.files.local(FILE_NAME);
        FileHandle bakFile = Gdx.files.local(FILE_NAME + ".bak");
        if (saveFile.exists()) {
            try {
                return json.fromJson(saveDataClazz, saveFile.readString());
            } catch (Exception e) {
                Gdx.app.error("SaveLoad", "save.json corrupt, trying backup", e);
            }
        }
        if (bakFile.exists()) {
            try {
                return json.fromJson(saveDataClazz, bakFile.readString());
            } catch (Exception e) {
                Gdx.app.error("SaveLoad", "save.json.bak also corrupt", e);
            }
        }
        return null;
    }

}
