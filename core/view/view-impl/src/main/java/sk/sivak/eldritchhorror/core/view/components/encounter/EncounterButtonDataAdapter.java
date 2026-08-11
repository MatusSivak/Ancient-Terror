package sk.sivak.eldritchhorror.core.view.components.encounter;

import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;

public class EncounterButtonDataAdapter {

    private final EncounterButtonData encounterButtonData;

    public EncounterButtonDataAdapter(EncounterButtonData encounterButtonData) {
        this.encounterButtonData = encounterButtonData;
    }

    ActionButtonData asActionButtonData() {
        return new ActionButtonData() {

            @Override
            public ActionButtonId getActionButtonId() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getTexturePath() {
                return encounterButtonData.getButtonIcon();
            }

            @Override
            public String getActionName() {
                throw new UnsupportedOperationException();
            }

            @Override
            public float getScaleDownPercentage() {
                return 0.5f;
            }

            @Override
            public boolean isEnabled() {
                return encounterButtonData.isEnabled();
            }

            @Override
            public boolean needsMask() {
                return false;
            }
        };
    }
}
