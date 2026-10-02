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
            assertFalse(key,text.getProperty(key+".info","").isEmpty());
            assertEquals(type,new YigResearchEncounter(page,type).getLocationType());
        }
    }
    @Test public void startingAnotherAncientOneDoesNotReuseCachedResearchText() {
        String yig=new ResearchEncounterTextBuilder(1,LocationType.CITY,AncientOneId.YIG).withFlavor().build();
        String azathoth=new ResearchEncounterTextBuilder(1,LocationType.CITY,AncientOneId.AZATHOTH).withFlavor().build();
        assertNotEquals(yig,azathoth);
        assertEquals(yig,new ResearchEncounterTextBuilder(1,LocationType.CITY,AncientOneId.YIG).withFlavor().build());
    }
}
