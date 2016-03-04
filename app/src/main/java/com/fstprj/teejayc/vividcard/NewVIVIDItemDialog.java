package com.fstprj.teejayc.vividcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class NewVIVIDItemDialog extends DialogFragment {

    public static final String ARG_ORIGINAL_NAME = "original_name";
    public static final String ARG_DIALOG_TYPE = "dialog_type";

    public static final String EXTRA_NEW_ITEM_NAME
            = "com.taejungchang.vividcard.new_item_name";


    public static final int DECK_NEW_DIALOG = 0;
    public static final int DIR_NEW_DIALOG = 1;
    public static final int DECK_EDIT_DIALOG = 2;
    public static final int DIR_EDIT_DIALOG =3;

    private EditText mName;

    public static NewVIVIDItemDialog newInstance(int dialogType, String originalName) {
        Bundle args = new Bundle();
        args.putString(ARG_ORIGINAL_NAME, originalName);
        args.putInt(ARG_DIALOG_TYPE, dialogType);

        NewVIVIDItemDialog fragment = new NewVIVIDItemDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_new_vivid_item, null);

        mName = (EditText) view.findViewById(R.id.new_item_name_edit_text);
        mName.setText(getArguments().getString(ARG_ORIGINAL_NAME));


        int dialogType = getArguments().getInt(ARG_DIALOG_TYPE);

        String dialogTitle;
        switch (dialogType) {
            case DECK_NEW_DIALOG:
                dialogTitle = "New Deck:";
                break;
            case DIR_NEW_DIALOG:
                dialogTitle = "New Directory:";
                break;
            case DECK_EDIT_DIALOG:
                dialogTitle = "Edit Deck Name:";
                break;
            case DIR_EDIT_DIALOG:
                dialogTitle = "Edit Directory Name:";
                break;
            default:
                dialogTitle = null;
                break;
        }
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(dialogTitle)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = mName.getText().toString();
                        sendResult(Activity.RESULT_OK, newName);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    private void sendResult(int resultCode, String newName) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_NEW_ITEM_NAME, newName);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }


}
