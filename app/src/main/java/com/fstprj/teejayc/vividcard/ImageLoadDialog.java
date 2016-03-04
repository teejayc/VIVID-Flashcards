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
import android.widget.RadioGroup;



public class ImageLoadDialog extends DialogFragment {

    public static final String EXTRA_LOAD_CODE
            = "com.taejungchang.vividcard.load_code";

    public static final int LOAD_WITH_CAM_CODE = 0;
    public static final int LOAD_WITH_GAL_CODE = 1;

    private int mCheckedRadioButtonID;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_image_load, null);

        RadioGroup radioGroup;

        radioGroup = (RadioGroup) view.findViewById(R.id.load_image_type_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mCheckedRadioButtonID = checkedId;
            }
        });



        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.new_card)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (mCheckedRadioButtonID) {
                            case R.id.load_with_camera_radio_button:
                                sendResult(Activity.RESULT_OK, LOAD_WITH_CAM_CODE);
                                break;
                            case R.id.load_with_gallery_radio_button:
                                sendResult(Activity.RESULT_OK, LOAD_WITH_GAL_CODE);
                                break;
                            default:
                                break;
                        }

                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    private void sendResult(int resultCode, int loadCode) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_LOAD_CODE, loadCode);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

}
