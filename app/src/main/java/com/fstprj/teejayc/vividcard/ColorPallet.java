package com.fstprj.teejayc.vividcard;

import android.util.SparseIntArray;

public class ColorPallet {
    private static ColorPallet sColorPallet;
    private final int[] mPallet;
    private final int[] mDarkPallet;
    private final int[] mRadioButtonId;
    //color to darker color map
    private final SparseIntArray mDarPalletMap;
    private final SparseIntArray mRadioIDMap;

    private static final int NUM_COLORS = 18;

    public static ColorPallet get() {
        if (sColorPallet == null) {
            sColorPallet = new ColorPallet();
        }
        return sColorPallet;
    }

    ColorPallet() {
        mPallet = new int[] {
                0xFFF44336, 0xFFE91E63, 0xFF9C27B0, 0xFF673AB7, 0xFF3F51B5, 0xFF2196F3,
                0xFF03A9F4, 0xFF00BCD4, 0xFF009688, 0xFF4CAF50, 0xFF8BC34A, 0xFFCDDC39,
                0xFFFFC107, 0xFFFF9800, 0xFFFF5722, 0xFF795548, 0xFF9E9E9E, 0xFF607D8B };

        mDarkPallet = new int[] {
                0xFFD32F2F, 0xFFC2185B, 0xFF7B1FA2, 0xFF512DA8, 0xFF303F9F, 0xFF1976D2,
                0xFF0288D1, 0xFF0097A7, 0xFF00796B, 0xFF388E3C, 0xFF689F38, 0xFFAFB42B,
                0xFFFFA000, 0xFFF57C00, 0xFFE64A19, 0xFF5D4037, 0xFF616161, 0xFF455A64 };

        mRadioButtonId = new int[] { R.id.red_radio_button, R.id.pink_radio_button,

                R.id.purple_radio_button, R.id.deep_purple_radio_button,

                R.id.indigo_radio_button, R.id.blue_radio_button,

                R.id.light_blue_radio_button, R.id.cyan_radio_button,

                R.id.teal_radio_button, R.id.green_radio_button,

                R.id.light_green_radio_button, R.id.lime_radio_button,

                R.id.amber_radio_button, R.id.orange_radio_button,

                R.id.deep_orange_radio_button, R.id.brown_radio_button,

                R.id.grey_radio_button, R.id.blue_grey_radio_button };

        mDarPalletMap = new SparseIntArray();

        for (int i = 0; i < NUM_COLORS; ++i) {
            mDarPalletMap.put(mPallet[i], mDarkPallet[i]);
        }

        mRadioIDMap = new SparseIntArray();

        for (int i = 0; i < NUM_COLORS; ++i) {
            mRadioIDMap.put(mPallet[i], mRadioButtonId[i]);
        }
    }

    public int getColor(int index) {
        return mPallet[index];
    }

    public int getDarkColor(int index) {
        return mDarkPallet[index];
    }

    public int getDarkColorWithColor(int color) {
        return mDarPalletMap.get(color);
    }

    public int getRadioButtonId(int color) {
        return mRadioIDMap.get(color);
    }

}
