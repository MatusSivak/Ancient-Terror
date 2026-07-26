package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.omen.OmenId;

import java.util.Map;

public interface OmenTrackSaveDataRead {
    OmenId getCurrentOmenId();

    Map<String, Integer> getOmenTokensMap();
}
