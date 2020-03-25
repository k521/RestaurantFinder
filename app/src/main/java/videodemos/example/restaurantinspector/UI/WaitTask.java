package videodemos.example.restaurantinspector.UI;

import android.os.AsyncTask;

import androidx.fragment.app.FragmentManager;

import videodemos.example.restaurantinspector.UI.Dialogs.WaitFragment;

class WaitTask extends AsyncTask<Void, Void,  String[]> {

    private MapsActivity activity;
    private String latestDate;
    private WaitFragment dialog;
    private FragmentManager manager;


    public WaitTask(MapsActivity activity, String latestDate) {
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