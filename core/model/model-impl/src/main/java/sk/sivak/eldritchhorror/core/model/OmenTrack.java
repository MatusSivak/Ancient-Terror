package sk.sivak.eldritchhorror.core.model;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.model.save.OmenTrackSaveData;
import sk.sivak.eldritchhorror.core.model.save.OmenTrackSaveDataRead;
import sk.sivak.eldritchhorror.core.model.util.OmenTrackHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author msivak
 */
public class OmenTrack implements OmenTrackWrite {

    private List<OmenTrackHelper.OmenInfoImpl> omenTrack;
    private OmenId currentOmenId;

    @Override
    public void initOmenTrack() {
        omenTrack = new OmenTrackHelper().init();
        currentOmenId = omenTrack.get(0).getOmenId();
    }

    @Override
    public OmenInfo getCurrentOmen() {
        for (OmenInfo omenInfo : omenTrack) {
            if (omenInfo.getOmenId().equals(currentOmenId)) {
                return omenInfo;
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public void advanceOmen() {
        this.currentOmenId = this.currentOmenId.next();
    }

    @Override
    public OmenTrackHelper.OmenInfoImpl getOmenInfo(OmenId omenId) {
        for (OmenTrackHelper.OmenInfoImpl omenInfo : omenTrack) {
            if (omenInfo.getOmenId().equals(omenId)) {
                return omenInfo;
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public void addTokenToOmen(OmenId omenId) {
        OmenTrackHelper.OmenInfoImpl omenInfo = getOmenInfo(omenId);
        omenInfo.setTokensCount(omenInfo.getTokensCount() + 1);
    }

    @Override
    public void setCurrentOmen(OmenId omenId) {
        this.currentOmenId = omenId;
    }

    @Override
    public void removeTokenFromOmen(OmenId omenId) {
        OmenTrackHelper.OmenInfoImpl omenInfo = getOmenInfo(omenId);
        omenInfo.setTokensCount(Math.max(0, omenInfo.getTokensCount() - 1));
    }

    @Override
    public OmenTrackSaveData save() {
        OmenTrackSaveData omenTrackSaveData = new OmenTrackSaveData();
        omenTrackSaveData.setCurrentOmenId(currentOmenId);
        HashMap<String, Integer> omenTokensMap = new HashMap<>();
        for (OmenTrackHelper.OmenInfoImpl omenInfo : omenTrack) {
            if (omenInfo.getTokensCount() == 0) {
                continue;
            }
            omenTokensMap.put(omenInfo.getOmenId().name(), omenInfo.getTokensCount());
        }
        omenTrackSaveData.setOmenTokensMap(omenTokensMap);
        return omenTrackSaveData;
    }

    @Override
    public void load(OmenTrackSaveDataRead saveData) {
        initOmenTrack();
        currentOmenId = saveData.getCurrentOmenId();
        for (Map.Entry<String, Integer> entry : saveData.getOmenTokensMap().entrySet()) {
            OmenTrackHelper.OmenInfoImpl omenInfo = Stream.findFirstOrException(omenTrack,
                    ot -> ot.getOmenId() == OmenId.valueOf(entry.getKey()));
            omenInfo.setTokensCount(entry.getValue());
        }
    }
}
