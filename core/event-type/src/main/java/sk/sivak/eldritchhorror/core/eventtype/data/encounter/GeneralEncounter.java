package sk.sivak.eldritchhorror.core.eventtype.data.encounter;

import com.badlogic.gdx.math.Vector2;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;

public class GeneralEncounter extends Encounter {

    private LocationType locationType;

    public GeneralEncounter(LocationType locationType) {
        super(EncounterType.GENERAL);
        this.locationType = locationType;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    @Override
    public EncounterButtonData buildButtonData() {
        EncounterButtonData encounterButtonData = super.buildButtonData();
        encounterButtonData.setFirstLine(locationType.getValue());
        encounterButtonData.setButtonIcon("encounter/"+locationType+".png");
        encounterButtonData.setOffset(new Vector2(0,1));
        return encounterButtonData;
    }
}
