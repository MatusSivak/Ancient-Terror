package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class MatchResolution {
    private final Map<GridPosition, SymbolType> replacements;
    private final int successesGained;
    private final int matchedLines;

    public MatchResolution(Map<GridPosition, SymbolType> replacements, int successesGained, int matchedLines) {
        this.replacements = Collections.unmodifiableMap(new LinkedHashMap<>(replacements));
        this.successesGained = successesGained;
        this.matchedLines = matchedLines;
    }

    public Map<GridPosition, SymbolType> getReplacements() {
        return replacements;
    }

    public int getSuccessesGained() {
        return successesGained;
    }

    public int getMatchedLines() {
        return matchedLines;
    }
}
