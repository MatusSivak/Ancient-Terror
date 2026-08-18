package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MatchResolution {
    private final Map<GridPosition, SymbolType> replacements;
    private final int successesGained;
    private final int matchedLines;
    private final List<GridMatch> matches;

    public MatchResolution(Map<GridPosition, SymbolType> replacements, int successesGained, int matchedLines) {
        this(replacements, successesGained, matchedLines, Collections.emptyList());
    }

    public MatchResolution(Map<GridPosition, SymbolType> replacements, int successesGained, int matchedLines, List<GridMatch> matches) {
        this.replacements = Collections.unmodifiableMap(new LinkedHashMap<>(replacements));
        this.successesGained = successesGained;
        this.matchedLines = matchedLines;
        this.matches = matches == null ? Collections.emptyList() : Collections.unmodifiableList(matches);
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

    public List<GridMatch> getMatches() {
        return matches;
    }
}
