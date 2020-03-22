package videodemos.example.restaurantinspector.UI;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import videodemos.example.restaurantinspector.R;

/**
 * A class to build a win dialog for game over.
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
                //getActivity().finish();
                ((MainActivity)getActivity()).updateDataAndRefresh(latestDate);
            }
        };

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //getActivity().finish();
            }
        };

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok, okListener)
                .setNegativeButton(android.R.string.cancel,cancelListener )
                .create();
    }
}
