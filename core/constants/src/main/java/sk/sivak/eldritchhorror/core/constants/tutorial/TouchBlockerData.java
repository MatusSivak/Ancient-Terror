package sk.sivak.eldritchhorror.core.constants.tutorial;

public class TouchBlockerData {

    private final Rectangle blockerArea;
    private final BlockerTarget blockerTarget;
    private Rectangle windowArea = null;
    private boolean semiTransparent = false;
    private boolean clickthrough = false;

    public TouchBlockerData(Rectangle blockerArea, BlockerTarget blockerTarget) {
        this.blockerArea = blockerArea;
        this.blockerTarget = blockerTarget;
    }

    public void setWindowArea(Rectangle windowArea) {
        this.windowArea = windowArea;
    }

    public void setSemiTransparent(boolean semiTransparent) {
        this.semiTransparent = semiTransparent;
    }

    public Rectangle getBlockerArea() {
        return blockerArea;
    }

    public Rectangle getWindowArea() {
        return windowArea;
    }

    public boolean isSemiTransparent() {
        return semiTransparent;
    }

    public BlockerTarget getBlockerTarget() {
        return blockerTarget;
    }

    public boolean isClickthrough() {
        return clickthrough;
    }

    public void setClickthrough(boolean clickthrough) {
        this.clickthrough = clickthrough;
    }

    public enum BlockerTarget {
        INFO_STAGE,
        MAP
    }

    public static class Rectangle {
        private int x;
        private int y;
        private int width;
        private int height;

        public Rectangle(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }
}
