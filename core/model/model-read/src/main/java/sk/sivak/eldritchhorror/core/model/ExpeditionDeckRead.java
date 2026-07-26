package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

/**
 * @author msivak
 */
public interface ExpeditionDeckRead {

    LocationId getExpeditionTokenLocation();

    int getExpeditionPage();
}
