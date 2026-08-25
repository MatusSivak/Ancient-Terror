package sk.sivak.eldritchhorror.core.eventtype.data.encounter;

import com.badlogic.gdx.math.Vector2;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;

public class SkipEncounter extends Encounter {

    public SkipEncounter() {
        super(EncounterType.SKIP);
    }

    @Override
    public EncounterButtonData buildButtonData() {
        EncounterButtonData encounterButtonData = super.buildButtonData();
        encounterButtonData.setButtonIcon("encounter/skip.png");
        encounterButtonData.setFirstLine("Skip");
        encounterButtonData.setScaleDownPercentage(1.05f);
        encounterButtonData.setOffset(new Vector2(0,1));
        return encounterButtonData;
    }
}
