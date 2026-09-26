package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

public interface BackgroundModelWrite extends BackgroundModelRead{
    void init();

    void pushBackground(InvestigatorId investigatorId, BackgroundDataWrite backgroundData);

    void popBackground(InvestigatorId investigatorId);

    void removeBackgrounds(InvestigatorId investigatorId, BackgroundType backgroundType);

    void setCurrentBackground(BackgroundDataRead backgroundData);

    interface BackgroundDataWrite extends BackgroundDataRead {
    }
}
