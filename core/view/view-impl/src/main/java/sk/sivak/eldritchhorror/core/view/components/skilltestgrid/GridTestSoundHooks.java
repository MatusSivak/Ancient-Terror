package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

public interface GridTestSoundHooks {
    void onShift();
    void onSymbolEnter();
    void onMatch(int matchedLines, boolean cascade);
    void onCascade();
    void onTestComplete(GridTestResult result);

    GridTestSoundHooks NO_OP = new GridTestSoundHooks() {
        @Override
        public void onShift() {
        }

        @Override
        public void onSymbolEnter() {
        }

        @Override
        public void onMatch(int matchedLines, boolean cascade) {
        }

        @Override
        public void onCascade() {
        }

        @Override
        public void onTestComplete(GridTestResult result) {
        }
    };
}
