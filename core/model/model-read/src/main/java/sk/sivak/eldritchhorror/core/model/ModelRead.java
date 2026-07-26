package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.reference.ReferenceInfo;

import java.util.List;

/**
 * @author msivak
 */
public interface ModelRead {

    List<AncientOneInfo> getAvailableAncientOnes();

    ReferenceInfo getReferenceCard();

    AncientOneRead getAncientOne();

    int getCurrentRound();

    boolean isReckoningTriggered();

    int rollDie();

    boolean isMythosCardTextEffect();

    String getDifficulty();
}
