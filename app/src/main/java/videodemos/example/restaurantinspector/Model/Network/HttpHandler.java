package videodemos.example.restaurantinspector.Model.Network;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpHandler {
    private final String RESTAURANTS_CSV_URL = "http://data.surrey.ca/dataset/948e994d-74f5-41a2-b3cb-33fa6a98aa96/resource/30b38b66-649f-4507-a632-d5f6f5fe87f1/download/fraserhealthrestaurantinspectionreports.csv";

    private OkHttpClient client = new OkHttpClient();

    private String body = "";

    private boolean status = false;

    public String getBody() {
        return body;
    }

    private boolean exceptionThrown = false;

    public void getData() {
        Thread gettingDataThread = new Thread(new Runnable(){
            public void run(){
                try{
                    Request request = new Request.Builder().
                                          url(RESTAURANTS_CSV_URL).
                                          get().build();

                    Response response = client.newCall(request).execute();
                    body = response.body().string();



                    status = true;
                } catch(Exception e){
                    exceptionThrown = true;
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

    public static void main(String[] args) throws InterruptedException {
        HttpHandler httpHandler = new HttpHandler();

        httpHandler.getData();
        System.out.println("We are done here");
        System.out.println(httpHandler.getBody());

    }
}
