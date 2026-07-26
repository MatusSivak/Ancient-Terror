package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

public interface BackgroundModelRead {
    BackgroundDataRead peekBackground(InvestigatorId investigatorId);

    BackgroundDataRead getCurrentBackground();

    interface BackgroundDataRead {

        BackgroundType getBackgroundType();

        Object getAdditionalData();

        String getBackgroundName();

    }

    enum BackgroundType {
        LOST_IN_TIME_AND_SPACE,
        CUSTOM,
        LOCATION,
        RESEARCH,
        COMBAT,
        LOCATION_TYPE;
    }
}
