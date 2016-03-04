package com.fstprj.teejayc.vividcard;

import android.content.ContentValues;

import java.util.Date;
import java.util.UUID;

import com.fstprj.teejayc.vividcard.DBSchema.DeckTable;

public class Deck extends VIVIDItem {
    private UUID mPID;
    private int mNumCards;
    private String mCreator;

    public Deck(UUID iD) {
        super(iD);
    }

    public Deck(String nameIn, UUID pID) {
        super(nameIn);
        mPID = pID;
        mNumCards = 0;
    }

    public Deck(String pid, String id, String date, String name,
                String numCards, String creator) {
        super(UUID.fromString(id));
        mPID = UUID.fromString(pid);
        setDateCreated(new Date(Long.getLong(date)));
        setName(name);
        setNumCards(Integer.getInteger(numCards));
        setCreator(creator);
    }


    public UUID getPID() {
        return mPID;
    }

    public void setPID(UUID mPID) {
        this.mPID = mPID;
    }

    public int getNumCards() {
        return mNumCards;
    }

    public void setNumCards(int mNumCards) {
        this.mNumCards = mNumCards;
    }

    public String getCreator() {
        return mCreator;
    }

    public void setCreator(String creator) {
        this.mCreator = creator;
    }


    @Override
    public ContentValues getContentValues() {
        ContentValues values = super.getContentValues();

        values.put(DeckTable.Attributes.PID, mPID.toString());
        values.put(DeckTable.Attributes.NUM_CARDS, mNumCards);
        values.put(DeckTable.Attributes.CREATOR, mCreator);

        return values;
    }


}
