package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.spell.SpellId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellsDeckSaveData implements SaveDataWrite.SpellsDeckSaveDataWrite {
    private Map<String, List<? extends SpellInfoSaveDataRead>> investigatorSpellInfoMap = new HashMap<>();


    public void setInvestigatorSpellInfoMap(Map<String, List<? extends SpellInfoSaveDataRead>> investigatorSpellInfoMap) {
        this.investigatorSpellInfoMap = investigatorSpellInfoMap;
    }

    @Override
    public Map<String, List<? extends SpellInfoSaveDataRead>> getInvestigatorSpellInfoMap() {
        return investigatorSpellInfoMap;
    }

    public static class SpellInfoSaveData implements SpellInfoSaveDataRead{
        private SpellId spellId;
        private int spellBackId;
        private boolean disabled;

        // FOR json
        public SpellInfoSaveData() {
        }

        public void setSpellId(SpellId spellId) {
            this.spellId = spellId;
        }

        public void setSpellBackId(int spellBackId) {
            this.spellBackId = spellBackId;
        }

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }

        @Override
        public SpellId getSpellId() {
            return spellId;
        }

        @Override
        public int getSpellBackId() {
            return spellBackId;
        }

        @Override
        public boolean isDisabled() {
            return disabled;
        }
    }
}
