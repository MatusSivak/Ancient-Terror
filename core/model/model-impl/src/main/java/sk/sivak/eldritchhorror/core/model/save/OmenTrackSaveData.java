package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.omen.OmenId;

import java.util.HashMap;
import java.util.Map;

public class OmenTrackSaveData implements SaveDataWrite.OmenTrackSaveDataWrite {
    private OmenId currentOmenId;
    private Map<String, Integer> omenTokensMap = new HashMap<>();

    public void setCurrentOmenId(OmenId currentOmenId) {
        this.currentOmenId = currentOmenId;
    }

    public void setOmenTokensMap(Map<String, Integer> omenTokensMap) {
        this.omenTokensMap = omenTokensMap;
    }

    @Override
    public OmenId getCurrentOmenId() {
        return currentOmenId;
    }

    @Override
    public Map<String, Integer> getOmenTokensMap() {
        return omenTokensMap;
    }
}