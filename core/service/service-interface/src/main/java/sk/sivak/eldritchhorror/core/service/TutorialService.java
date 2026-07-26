package sk.sivak.eldritchhorror.core.service;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.CountRerollDiceData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;
import sk.sivak.eldritchhorror.core.eventtype.data.tutorial.CompositeTouchBlockerData;

import java.util.List;

/**
 * @author msivak
 */
public interface TutorialService extends CommonService {

    void startTutorial();

    void highlightSpySpecial();

    void waitForInvestigatorHide();

    void waitForInvestigatorClick();

    void displayChalkboard(String text, int positionX, int positionY, boolean waitOnClick);

    void hideChalkboard();

    void displayTouchBlockers(CompositeTouchBlockerData compositeTouchBlockerData);

    void displayBlockerOnInfoStage();

    void displayBlockerOnMapStage();

    void waitFewMilliseconds(int milliseconds);

    CompositeTouchBlockerData.TouchBlockerData displayWindowOnInfoStage(int windowX, int windowY, int windowWidth, int windowHeight, boolean semiTransparent);
}
