package sk.sivak.eldritchhorror.core.eventtype.data.tutorial;

import java.util.LinkedList;
import java.util.List;

public class CompositeTouchBlockerData {

    private List<TouchBlockerData> touchBlockerDataList = new LinkedList<>();
    private final BlockerTarget blockerTarget;

    public CompositeTouchBlockerData(BlockerTarget blockerTarget) {
        this.blockerTarget = blockerTarget;
    }

    public void addTouchBlockerData(TouchBlockerData touchBlockerData) {
        touchBlockerDataList.add(touchBlockerData);
    }

    public List<TouchBlockerData> getTouchBlockerDataList() {
        return touchBlockerDataList;
    }

    public BlockerTarget getBlockerTarget() {
        return blockerTarget;
    }

    public static class TouchBlockerData {

        private final Rectangle blockerArea;

        private Rectangle windowArea = null;
        private boolean semiTransparent = false;
        private boolean clickthrough = false;

        public TouchBlockerData(Rectangle blockerArea) {
            this.blockerArea = blockerArea;
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

        public boolean isClickthrough() {
            return clickthrough;
        }

        public void setClickthrough(boolean clickthrough) {
            this.clickthrough = clickthrough;
        }
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
