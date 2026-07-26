package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;

/**
 * @author msivak
 */
public interface OmenTrackRead {

    OmenInfo getCurrentOmen();

    OmenInfo getOmenInfo(OmenId omenId);

}
