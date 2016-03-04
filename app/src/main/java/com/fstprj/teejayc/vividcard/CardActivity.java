package com.fstprj.teejayc.vividcard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.UUID;
import com.fstprj.teejayc.vividcard.SortVIVIDItemDialog.SortCategory;

public class CardActivity extends SingleFragmentActivity {
    private static final String EXTRA_CARD_POSITION
            = "com.taejungchang.vividcard.card_position";
    private static final String EXTRA_DECK_ID
            = "com.taejungchang.vividcard.deck_id";
    private static final String EXTRA_SORT_CATEGORY
            = "com.taejungchang.vividcard.sort_category";


    public static Intent newIntent(Context packageContext,
                                   int cardPosition, UUID dKID, SortCategory sortCategory) {
        Intent intent = new Intent(packageContext, CardActivity.class);
        intent.putExtra(EXTRA_CARD_POSITION, cardPosition);
        intent.putExtra(EXTRA_DECK_ID, dKID);
        intent.putExtra(EXTRA_SORT_CATEGORY, sortCategory);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        int cardPosition = getIntent().getIntExtra(EXTRA_CARD_POSITION, 0);
        UUID dKID = (UUID) getIntent().getSerializableExtra(EXTRA_DECK_ID);
        SortCategory sortCategory
                = (SortCategory) getIntent().getSerializableExtra(EXTRA_SORT_CATEGORY);
        return CardFragment.newInstance(cardPosition, dKID, sortCategory);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
    }
}
