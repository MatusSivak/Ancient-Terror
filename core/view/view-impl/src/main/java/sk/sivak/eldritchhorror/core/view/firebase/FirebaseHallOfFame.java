package sk.sivak.eldritchhorror.core.view.firebase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import rx.Single;
import rx.subjects.PublishSubject;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.firebase.HallOfFameData;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FirebaseHallOfFame {

    public void recordHallOfFame(HallOfFameData hallOfFameData) {
        Net.HttpRequest request = new Net.HttpRequest("POST");
        request.setUrl("https://ancient-terror-hall-of-fame.firebaseio.com/hall-of-fame.json");
        String jsonString = new Json(JsonWriter.OutputType.json).toJson(hallOfFameData);
        jsonString = jsonString.replace("\"timestamp\":69","\"timestamp\":{\".sv\" : \"timestamp\"}");
        request.setContent(jsonString);
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {

            }

            @Override
            public void failed(Throwable t) {

            }

            @Override
            public void cancelled() {

            }
        });
    }

    public Single<List<HallOfFameData>> fetchHallOfFameData() {
        PublishSubject<List<HallOfFameData>> publishSubject = PublishSubject.create();
        Net.HttpRequest request = new Net.HttpRequest("GET");
        request.setUrl("https://ancient-terror-hall-of-fame.firebaseio.com/hall-of-fame.json?orderBy=\"timestamp\"&limitToLast=200");
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {

                String resultAsString = httpResponse.getResultAsString();

                List<HallOfFameData> hallOfFameDataList = new LinkedList<>();
                JsonValue jsonValue = new JsonReader().parse(resultAsString);
                for (int i = 0; i < new JsonReader().parse(resultAsString).size; i++) {

                    try {
                        HallOfFameData hallOfFameData = new HallOfFameData();
                        JsonValue record = jsonValue.get(i);
                        if (record.has("ancientOneId")) {
                            hallOfFameData.setAncientOneId(AncientOneId.valueOf(record.getString("ancientOneId")));
                        } else {
                            System.out.println("Hall of Fame: Missing Ancient One - " + record.name());
                            hallOfFameData.setAncientOneId(AncientOneId.AZATHOTH);
                        }
                        hallOfFameData.setTimestamp(record.getLong("timestamp"));
                        hallOfFameData.setInvestigatorsCount(record.getInt("investigatorsCount"));
                        hallOfFameData.setRounds(record.getInt("rounds"));
                        if (record.has("doom")) {
                            hallOfFameData.setDoom(record.getInt("doom"));
                        } else {
                            System.out.println("Hall of Fame: Doom not present - " + record.name());
                            hallOfFameData.setDoom(0);
                        }
                        hallOfFameData.setName(record.getString("name"));
                        hallOfFameData.setDifficulty(record.getString("difficulty"));
                        hallOfFameData.setInvestigators(Arrays.asList(record.get("investigators").get("items").asStringArray()));
                        hallOfFameData.setSolvedMysteries(Arrays.asList(record.get("solvedMysteries").get("items").asStringArray()));
                        if (hallOfFameData.getSolvedMysteries().size() < 3) {
                            System.out.println("Hall of Fame: Spy Bug - " + record.name());
                            continue;
                        }
                        hallOfFameDataList.add(hallOfFameData);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }

                Collections.reverse(hallOfFameDataList);
                publishSubject.onNext(hallOfFameDataList);
                publishSubject.onCompleted();

            }

            @Override
            public void failed(Throwable t) {
                publishSubject.onNext(new LinkedList<>());
                publishSubject.onCompleted();
            }

            @Override
            public void cancelled() {

            }
        });
        return publishSubject.toSingle();
    }

    private static class HallOfFameResponse {

        public HallOfFameResponse() {

        }
        private Map<String, HallOfFameData> map = new HashMap<>();

        public Map<String, HallOfFameData> getMap() {
            return map;
        }

        public void setMap(Map<String, HallOfFameData> map) {
            this.map = map;
        }
    }
}
