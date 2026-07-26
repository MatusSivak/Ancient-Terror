package sk.sivak.eldritchhorror.core.view.components.lightning;

import java.util.List;

public final class CompositeBoltAnimationBuilder {

    private CompositeBoltAnimationBuilder() {
    }

    public static CompositeBoltAnimation build(List<CompositeBolt> compositeBolts) {
        return new CompositeBoltAnimation(compositeBolts.toArray(new CompositeBolt[0]));
    }
}
