package videodemos.example.restaurantinspector.Model.Network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import videodemos.example.restaurantinspector.Model.DataHandling.DateCalculations;

/**
 * A class that gets data from the internet.
 */
public class HttpHandler {

    private String url;
    private OkHttpClient client = new OkHttpClient();
    private String body = "";
    private String latestDate = "";

    public HttpHandler(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public String getCurrentDateFromServer() throws IOException, JSONException {
        JSONObject jsonObject = getJsonDataFromUrl();
        latestDate = getDateFromUrl(jsonObject);
        return latestDate;
    }

    public void getData() {
        Thread gettingDataThread = new Thread(new Runnable(){
            public void run(){
                try{

                    JSONObject jsonObject = getJsonDataFromUrl();
                    String csvUrl = getCsvDataUrl(jsonObject);
                    body = getCsvBody(csvUrl);

                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        gettingDataThread.start();
        try {
            gettingDataThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getJsonDataFromUrl() throws IOException, JSONException {
        Request request = new Request.Builder().
                url(url).
                get().build();
        Response response = client.newCall(request).execute();
        String responseFromFirst = response.body().string();
        return new JSONObject(responseFromFirst);
    }

    private String getDateFromUrl(JSONObject jsonObject) throws IOException, JSONException {
        String date = jsonObject
                .getJSONObject("result")
                .getString("metadata_modified");

        return date;
    }

    private String getCsvBody(String csvUrl) throws IOException {
        Request requestCSV = new Request.Builder().
                url(csvUrl).
                get().build();

        Response responseCSV = client.newCall(requestCSV).execute();
        return responseCSV.body().string();
    }

    private String getCsvDataUrl(JSONObject jsonObject) throws IOException, JSONException {
        JSONArray jsonArrayResult = jsonObject
                .getJSONObject("result")
                .getJSONArray("resources");

        JSONObject jsonObjectResources = jsonArrayResult.getJSONObject(0);
        String csvUrl = jsonObjectResources.getString("url");

        //add an s to http to make it https
        String httpsUrl = csvUrl.substring(0, 4) + "s" + csvUrl.substring(4, csvUrl.length());

        return httpsUrl;
    }



}
