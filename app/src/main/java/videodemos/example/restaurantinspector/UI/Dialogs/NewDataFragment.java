package videodemos.example.restaurantinspector.UI.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import videodemos.example.restaurantinspector.R;
import videodemos.example.restaurantinspector.UI.MapsActivity;

/**
 * A class to build a dialog to ask user if they want to download new data.
 */
public class NewDataFragment extends AppCompatDialogFragment {

    private String latestDate;

    public NewDataFragment(String latestDate) {
        super();
        this.latestDate = latestDate;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.new_data_dialog_layout, null);

        DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((MapsActivity)getActivity()).updateDataAndRefresh(latestDate);
            }
        };

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        };

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok, okListener)
                .setNegativeButton(android.R.string.cancel,cancelListener )
                .create();
    }
}
