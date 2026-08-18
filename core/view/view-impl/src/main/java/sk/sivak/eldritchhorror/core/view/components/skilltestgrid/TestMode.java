package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum TestMode {
    BLESSED("Blessed", GridMatchOrientation.HORIZONTAL, GridMatchOrientation.VERTICAL, GridMatchOrientation.DIAGONAL),
    NORMAL("Normal", GridMatchOrientation.HORIZONTAL, GridMatchOrientation.VERTICAL),
    CURSED("Cursed", GridMatchOrientation.DIAGONAL);

    private final String displayName;
    private final Set<GridMatchOrientation> allowedOrientations;

    TestMode(String displayName, GridMatchOrientation... allowedOrientations) {
        this.displayName = displayName;
        EnumSet<GridMatchOrientation> orientations = EnumSet.noneOf(GridMatchOrientation.class);
        Collections.addAll(orientations, allowedOrientations);
        this.allowedOrientations = Collections.unmodifiableSet(orientations);
    }

    boolean allows(GridMatchOrientation orientation) {
        return allowedOrientations.contains(orientation);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
