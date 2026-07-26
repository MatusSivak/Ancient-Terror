package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;

import java.util.List;

public interface RumorsRead {
    RumorCardInfo getActiveRumor(String rumorId);
    List<RumorCardInfo> getActiveRumors();
}
