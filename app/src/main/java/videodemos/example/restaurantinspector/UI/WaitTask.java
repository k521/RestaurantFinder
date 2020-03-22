package videodemos.example.restaurantinspector.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import androidx.fragment.app.FragmentManager;

class WaitTask extends AsyncTask<Void, Void,  String[]> {

    private MainActivity activity;
    private String latestDate;
    private WaitFragment dialog;
    private FragmentManager manager;


    public WaitTask(MainActivity activity, String latestDate) {
        this.activity = activity;
        this.latestDate = latestDate;

        manager = activity.getSupportFragmentManager();
        dialog = new WaitFragment();

    }


    @Override
    protected void onPreExecute() {
        dialog.show(manager, "wait dialog");
    }
    @Override
    protected String[] doInBackground(Void... args) {
        // do background work here
        return activity.getBodiesFromServer();
    }

    @Override
    protected void onPostExecute(String[] bodies) {
        if (!dialog.cancel){
            dialog.dismiss();
            activity.updateFiles(bodies[0], bodies[1], latestDate);
        }
    }

}