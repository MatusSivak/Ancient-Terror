package sk.sivak.eldritchhorror.core.constants.monster.epic;

import sk.sivak.eldritchhorror.core.constants.card.CardIdUtils;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterId;

public enum EpicMonsterId implements MonsterId {
    ZOMBIE_HORDE,
    DOPPELGANGER,
    TULZSCHA,
    TICK_TOCK_MEN,
    HYDRA,
    WIND_WALKER,
    CTHYLLA,
    CTHULHU,
    YEB,
    NUG,
    SHUB_NIGGURATH,
    DUNWICH_HORROR;


    @Override
    public String asString() {
        return CardIdUtils.asString(this);
    }
}
