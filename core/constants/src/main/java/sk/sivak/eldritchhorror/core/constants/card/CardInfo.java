package sk.sivak.eldritchhorror.core.constants.card;

import java.util.Set;

public interface CardInfo {

    CardId getId();

    String getName();

    Set<? extends Trait> getTraits();

    String getDescription();

    boolean hasReckoning();

    boolean isDisabled();
}
