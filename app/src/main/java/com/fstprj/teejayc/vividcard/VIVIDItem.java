package com.fstprj.teejayc.vividcard;

import android.content.ContentValues;

import java.util.Date;
import java.util.UUID;

import com.fstprj.teejayc.vividcard.DBSchema.CommonAtt;

public class VIVIDItem {
    private UUID mID;
    private Date mDateCreated;
    private String mName;

    public VIVIDItem(UUID iD) { mID = iD; }

    public VIVIDItem(String nameIn) {
        mID = UUID.randomUUID();
        mDateCreated = new Date();
        mName = nameIn;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public UUID getID() {
        return mID;
    }

    public void setID(UUID iDIn) { mID = iDIn; }

    public Date getDateCreated() {
        return mDateCreated;
    }

    public void setDateCreated(Date mDateCreated) {
        this.mDateCreated = mDateCreated;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(CommonAtt.ID, mID.toString());
        values.put(CommonAtt.DATE, mDateCreated.getTime());
        values.put(CommonAtt.NAME, mName);

        return values;
    }


}
