package sk.sivak.eldritchhorror.core.constants.location;

/**
 * Moving straight to a space without travelling along a path (e.g. Plumb the Void).
 * The investigator vanishes and spawns on the target space like at the start of the game.
 */
public class TeleportConnection extends Path {

    public TeleportConnection(LocationId locationId) {
        super(locationId, PathType.WALK);
    }
}
