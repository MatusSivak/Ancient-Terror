package sk.sivak.eldritchhorror.core.constants.asset;

import sk.sivak.eldritchhorror.core.constants.card.Trait;

/**
 * @author msivak
 */
public enum AssetTrait implements Trait {
    ITEM,
    ELIXIR,
    WEAPON,
    ALLY,
    TRINKET,
    SERVICE,
    TOME,
    TEAMWORK, RELIC, MAGICAL, TASK;

    @Override
    public String asString() {
        return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
    }
}
