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


//TODO: sort by last visit and sort by often forget
public class SortVIVIDItemDialog extends DialogFragment {
    public static final String EXTRA_SORT_CATEGORY
            = "com.fstprj.teejayc.vividcard.sort_category";

    public enum SortCategory {
        NAME, DATE
    }

    private SortCategory mSortCategory;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_sort_dialog, null);

        RadioGroup sortCategoryGroup = (RadioGroup) view.findViewById(R.id.sort_category_radio_group);


        sortCategoryGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.sort_by_name_radio_button:
                        mSortCategory = SortCategory.NAME;
                        break;
                    case R.id.sort_by_date_radio_button:
                        mSortCategory = SortCategory.DATE;
                        break;
                    default:
                        break;
                }
            }
        });
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.sort_by_dialog_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK, mSortCategory);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    private void sendResult(int resultCode, SortCategory sortCategory) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_SORT_CATEGORY, sortCategory);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
