package videodemos.example.restaurantinspector.Model.Network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpHandler {


    private String url;

    private OkHttpClient client = new OkHttpClient();

    private String body = "";



    public HttpHandler(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public void getData() {
        Thread gettingDataThread = new Thread(new Runnable(){
            public void run(){
                try{
                    Request request = new Request.Builder().
                                          url(url).
                                          get().build();

                    Response response = client.newCall(request).execute();
                    String responseFromFirst = response.body().string();
                    JSONObject jsonObjectFile = new JSONObject(responseFromFirst);
                    JSONArray jsonArrayResult = jsonObjectFile
                            .getJSONObject("result")
                            .getJSONArray("resources");

                    JSONObject jsonObjectResources = jsonArrayResult.getJSONObject(0);
                    String csvUrl = jsonObjectResources.getString("url");
                    Log.d("URL", csvUrl);

                    csvUrl = csvUrl.substring(0, 4) + "s" + csvUrl.substring(4, csvUrl.length());

                    Log.d("URL", csvUrl);

                    Request requestCSV = new Request.Builder().
                            url(csvUrl).
                            get().build();

                    Response responseCSV = client.newCall(requestCSV).execute();
                    body = responseCSV.body().string();




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

}
