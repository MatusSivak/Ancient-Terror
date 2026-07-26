package sk.sivak.eldritchhorror.core.model;

public class BackgroundData implements BackgroundModelWrite.BackgroundDataWrite {
    private BackgroundModelRead.BackgroundType backgroundType;
    private Object additionalData;
    private String backgroundName;

    /*
    public BackgroundData(BackgroundModelRead.BackgroundType backgroundType, String backgroundName) {
        this.backgroundType = backgroundType;
        this.backgroundName = backgroundName;
    }
    */

    public BackgroundData(BackgroundModelRead.BackgroundType backgroundType, Object additionalData, String backgroundName) {
        this.backgroundType = backgroundType;
        this.additionalData = additionalData;
        this.backgroundName = backgroundName;
    }

    @Override
    public BackgroundModelRead.BackgroundType getBackgroundType() {
        return backgroundType;
    }

    @Override
    public Object getAdditionalData() {
        return additionalData;
    }

    @Override
    public String getBackgroundName() {
        return backgroundName;
    }
}
