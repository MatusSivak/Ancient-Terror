package sk.sivak.eldritchhorror.core.model.save;

public class GeneralEncounterDeckSaveData implements SaveDataWrite.GeneralEncounterDeckSaveDataWrite {

    private Integer topCityEncounter;
    private Integer topWildernessEncounter;
    private Integer topSeaEncounter;

    public void setTopCityEncounter(Integer topCityEncounter) {
        this.topCityEncounter = topCityEncounter;
    }

    public void setTopWildernessEncounter(Integer topWildernessEncounter) {
        this.topWildernessEncounter = topWildernessEncounter;
    }

    public void setTopSeaEncounter(Integer topSeaEncounter) {
        this.topSeaEncounter = topSeaEncounter;
    }

    public Integer getTopCityEncounter() {
        return topCityEncounter;
    }

    public Integer getTopWildernessEncounter() {
        return topWildernessEncounter;
    }

    public Integer getTopSeaEncounter() {
        return topSeaEncounter;
    }
}
