package sk.sivak.eldritchhorror.core.constants.monster;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

/**
 * @author msivak
 */
public interface MonsterInfo {

    String getUuid();

    boolean isAlive();

    MonsterId getMonsterId();

    LocationId getCurrentLocation();

    String getName();

    Integer getToughness();

    Integer getCurrentHealth();

    Integer getHorror();

    Integer getDamage();

    Stat getHorrorTestType();

    Stat getDamageTestType();

    int getHorrorTestModifier();

    int getDamageTestModifier();

    boolean hasReckoning();

    String getSpawnText();

    String getReckoningText();

    String getSpecialText();

    boolean isEpic();

    String getNameInSelectComponent();

}
