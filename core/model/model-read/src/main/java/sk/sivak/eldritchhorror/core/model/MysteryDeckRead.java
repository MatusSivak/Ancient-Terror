package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;

import java.util.List;

/**
 * @author msivak
 */
public interface MysteryDeckRead {

    MysteryCardInfo getCurrentMysteryCard();

    int getSolvedMysteriesCount();

    List<String> getSolvedMysteries(AncientOneId ancientOneId);


}
