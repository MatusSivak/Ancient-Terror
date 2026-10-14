package sk.sivak.eldritchhorror.core.view.firebase;

import com.badlogic.gdx.utils.*;
import java.util.*;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.firebase.HallOfFameData;
import static org.junit.Assert.*;

public class HallOfFameOutcomesTest {
    @Test public void outcomesSurviveTheActualUploadJsonFormat() {
        HallOfFameData record = new HallOfFameData();
        record.setDefeatedInvestigators(new LinkedList<>(Arrays.asList("Priest", "Explorer")));
        Json json = new Json(JsonWriter.OutputType.json);
        assertEquals(record.getDefeatedInvestigators(), FirebaseHallOfFame.readDefeatedInvestigators(
                new JsonReader().parse(json.toJson(record))));
        record.setDefeatedInvestigators(new LinkedList<>());
        assertEquals(Collections.emptyList(), FirebaseHallOfFame.readDefeatedInvestigators(
                new JsonReader().parse(json.toJson(record))));
    }

    @Test public void legacyUnknownIsDifferentFromKnownNoCasualties() {
        assertNull(FirebaseHallOfFame.readDefeatedInvestigators(new JsonReader().parse("{}")));
        assertNull(FirebaseHallOfFame.readDefeatedInvestigators(new JsonReader().parse("{defeatedInvestigators:null}")));
        assertEquals(Collections.emptyList(), FirebaseHallOfFame.readDefeatedInvestigators(
                new JsonReader().parse("{defeatedInvestigators:[]}")));
        assertEquals(Collections.singletonList("Priest"), FirebaseHallOfFame.readDefeatedInvestigators(
                new JsonReader().parse("{defeatedInvestigators:[Priest]}")));
    }
}
