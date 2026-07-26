package sk.sivak.eldritchhorror.core.view.firebase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class FirebasePurchase {

    public void recordPurchase(String productName) {
        Net.HttpRequest request = new Net.HttpRequest("POST");
        request.setUrl("https://ancient-terror-hall-of-fame.firebaseio.com/purchase.json");
        Purchase purchase = new Purchase(productName);
        purchase.setTimestamp(69);
        String jsonString = new Json(JsonWriter.OutputType.json).toJson(purchase);
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

    private class Purchase {
        private final String productName;
        private long timestamp;

        public Purchase(String productName) {
            this.productName = productName;
        }

        public String getProductName() {
            return productName;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
