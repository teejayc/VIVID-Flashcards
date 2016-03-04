package com.fstprj.teejayc.vividcard;

import android.content.ContentValues;

import java.util.UUID;

import com.fstprj.teejayc.vividcard.DBSchema.DirectoryTable;

public class Directory extends VIVIDItem {

    private int mNumDecks;

    public Directory(UUID iD) {
        super(iD);
    }

    public Directory(String nameIn) {
        super(nameIn);
        mNumDecks = 0;
    }

    public int getNumDecks() {
        return mNumDecks;
    }

    public void setNumDecks(int mNumDecks) {
        this.mNumDecks = mNumDecks;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = super.getContentValues();

        values.put(DirectoryTable.Attributes.NUM_DECKS, mNumDecks);

        return values;
    }
}
