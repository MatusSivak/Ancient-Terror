package sk.sivak.eldritchhorror.core.eventlistener.ancientone.yig;

import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.ResearchEncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yig.YigResearchEncounter;
import static org.junit.Assert.*;

public class YigContentTest {
    @Test public void everyResearchCardHasNarrativeRulesAndExecutableEncounter() throws Exception {
        Properties text=new Properties();
        try(Reader reader=new InputStreamReader(getClass().getClassLoader().getResourceAsStream("encounter/research_YIG.properties"),StandardCharsets.UTF_8)){text.load(reader);}
        for(LocationType type:LocationType.values())for(int page=1;page<=24;page++) {
            String key=page+"."+type.name().toLowerCase(Locale.ROOT);
            assertFalse(key,text.getProperty(key+".flavor","").isEmpty());
            assertNotNull(key,text.getProperty(key+".info"));
            assertFalse(key,text.getProperty(key+".info").contains("Pass:"));
            assertFalse(key,text.getProperty(key+".info").contains("Fail:"));
            assertEquals(type,new YigResearchEncounter(page,type).getLocationType());
        }
    }
    @Test public void everyResearchTestHasSeparateColoredOutcomes() throws Exception {
        Properties text = new Properties();
        try (Reader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("encounter/research_YIG.properties"), StandardCharsets.UTF_8)) {
            text.load(reader);
        }
        assertOutcomes(text, "city", 1,2,3,5,6,8,10,11,12,13,14,17,19,20,21,24);
        assertOutcomes(text, "wilderness", 1,2,3,4,5,9,10,11,12,13,14,16,18,19,20,22,23,24);
        assertOutcomes(text, "sea", 1,2,4,5,6,8,9,10,14,16,17,18,19,20,21,22,23,24);
    }
    private void assertOutcomes(Properties text, String type, int... pages) {
        for (int page : pages) {
            String key = page + "." + type;
            String pass = text.getProperty(key + ".pass.info");
            String fail = text.getProperty(key + ".fail.info");
            assertNotNull(key, pass);
            assertNotNull(key, fail);
            assertTrue(key, pass.contains("[#GOOD]"));
            assertTrue(key, fail.isEmpty() || fail.contains("[#BAD]"));
        }
    }
    @Test public void startingAnotherAncientOneDoesNotReuseCachedResearchText() {
        String yig=new ResearchEncounterTextBuilder(1,LocationType.CITY,AncientOneId.YIG).withFlavor().build();
        String azathoth=new ResearchEncounterTextBuilder(1,LocationType.CITY,AncientOneId.AZATHOTH).withFlavor().build();
        assertNotEquals(yig,azathoth);
        assertEquals(yig,new ResearchEncounterTextBuilder(1,LocationType.CITY,AncientOneId.YIG).withFlavor().build());
    }
}
