package sk.sivak.eldritchhorror.core.model.util;

import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class OmenTrackHelper {

    public List<OmenInfoImpl> init() {
        LinkedList<OmenInfoImpl> result = new LinkedList<>();
        result.add(new OmenInfoImpl(OmenId.NORTH, OmenColor.GREEN));
        result.add(new OmenInfoImpl(OmenId.EAST, OmenColor.BLUE));
        result.add(new OmenInfoImpl(OmenId.SOUTH, OmenColor.RED));
        result.add(new OmenInfoImpl(OmenId.WEST, OmenColor.BLUE));
        return result;
    }

    public static class OmenInfoImpl implements OmenInfo {

        private OmenId omenId;
        private OmenColor omenColor;
        private int tokensCount;

        OmenInfoImpl(OmenId omenId, OmenColor omenColor) {
            this.omenId = omenId;
            this.omenColor = omenColor;
        }

        @Override
        public OmenId getOmenId() {
            return omenId;
        }

        @Override
        public OmenColor getOmenColor() {
            return omenColor;
        }

        @Override
        public int getTokensCount() {
            return tokensCount;
        }

        public void setTokensCount(int tokensCount) {
            this.tokensCount = tokensCount;
        }
    }
}
