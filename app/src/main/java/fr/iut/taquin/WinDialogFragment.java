package fr.iut.taquin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by shellcode on 3/11/17.
 */

public class WinDialogFragment extends DialogFragment {

    EditText pseudoInput;

    public interface WinDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    WinDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        LinearLayout layout_dialog = (LinearLayout)inflater.inflate(R.layout.dialog_win, null);
        pseudoInput = (EditText)layout_dialog.findViewById(R.id.pseudoInput);
        builder.setView(layout_dialog)

        .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if(mListener != null)
                    mListener.onDialogPositiveClick(WinDialogFragment.this);
            }
        })

        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(mListener != null)
                    mListener.onDialogNegativeClick(WinDialogFragment.this);

                WinDialogFragment.this.getDialog().cancel();
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void setOnWinDialogEvent(WinDialogListener listener) {
        mListener = listener;
    }

    public String getPseudo() {
        return pseudoInput.getText().toString();
    }
}
