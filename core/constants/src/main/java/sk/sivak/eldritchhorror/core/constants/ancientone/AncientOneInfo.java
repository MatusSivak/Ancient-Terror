package sk.sivak.eldritchhorror.core.constants.ancientone;

import sk.sivak.eldritchhorror.core.constants.monster.MonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;

import java.util.List;

/**
 * @author msivak
 */
public interface AncientOneInfo {

    AncientOneId getAncientOneId();

    boolean isAwaken();

    int getStartingDoom();

    int getMythosCardCount();

    String getSetupText();

    String getName();

    String getAltName();

    String getSpecialText();

    String getMidnightText();

    String getReckoningText();

    String getWinText();

    String getFlavorText();

    List<MonsterId> getRemovedMonsters();

    int getPower();

    int getMysteriesRequired();

    String getEndGameText();

    void setPower(int power);
}
