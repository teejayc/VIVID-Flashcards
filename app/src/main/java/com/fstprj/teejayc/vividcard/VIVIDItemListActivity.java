package com.fstprj.teejayc.vividcard;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class VIVIDItemListActivity extends SingleFragmentActivity {
    private static final String EXTRA_PID
            = "com.taejungchang.vividcard.pid";
    private static final String EXTRA_TABLE_NAME
            = "com.taejungchang.vividcard.table_name";

    public static Intent newIntent(Context packageContext, UUID pID, String tableName) {
        Intent intent = new Intent(packageContext, VIVIDItemListActivity.class);
        intent.putExtra(EXTRA_PID, pID);
        intent.putExtra(EXTRA_TABLE_NAME, tableName);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID pID = (UUID) getIntent().getSerializableExtra(EXTRA_PID);
        String tableName = getIntent().getStringExtra(EXTRA_TABLE_NAME);
        //TODO: set the action bar color after crash
        if (tableName.equals(DBSchema.DirectoryTable.NAME)) {
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(ColorPallet.get().getColor(12)));//amber
        }
        else if (tableName.equals(DBSchema.DeckTable.NAME)) {
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(ColorPallet.get().getColor(9)));//green
        }
        else {
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(ColorPallet.get().getColor(5)));//blue
        }

        return VIVIDItemListFragment.newInstance(pID, tableName);
    }

}
