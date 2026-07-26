package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;

/**
 * @author msivak
 */
public interface InvestigatorActionsWrite extends InvestigatorActionsRead {

    void resetActions();

    void init(InvestigatorInfo[] res);
}
