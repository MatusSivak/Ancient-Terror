package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

public interface SymbolRandomProvider {
    SymbolType peekNext();

    SymbolType next();

    default void reserveNextToken() {
        throw new UnsupportedOperationException("Next-token reservation is not supported");
    }

    default void releaseNextToken() {
        throw new UnsupportedOperationException("Next-token reservation is not supported");
    }

    default void clearNextTokenReservation() {
    }
}
