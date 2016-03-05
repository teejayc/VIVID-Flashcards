package com.fstprj.teejayc.vividcard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fstprj.teejayc.vividcard.DBSchema.CardTable;
import com.fstprj.teejayc.vividcard.DBSchema.DeckTable;
import com.fstprj.teejayc.vividcard.DBSchema.DirectoryTable;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class BaseHelper extends SQLiteOpenHelper{
    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "VIVIDBase.db";

    public BaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

//    @Override
//    public void onOpen(SQLiteDatabase db){
//        super.onOpen(db);
//        db.execSQL("PRAGMA foreign_keys=ON;");
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DirectoryTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                DirectoryTable.Attributes.ID + ", " +
                DirectoryTable.Attributes.DATE + ", " +
                DirectoryTable.Attributes.NAME + ", " +
                DirectoryTable.Attributes.IMAGE + ", " +
                DirectoryTable.Attributes.NUM_DECKS + ")");

        db.execSQL("CREATE TABLE " + DeckTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                DeckTable.Attributes.PID + ", " +
                DeckTable.Attributes.ID + ", " +
                DeckTable.Attributes.DATE + ", " +
                DeckTable.Attributes.NAME + ", " +
                DeckTable.Attributes.IMAGE + ", " +
                DeckTable.Attributes.NUM_CARDS + ", " +
                DeckTable.Attributes.CREATOR + ")");

        db.execSQL("CREATE TRIGGER delete_decks_as_dir "
                + "BEFORE DELETE ON " + DirectoryTable.NAME
                + " FOR EACH ROW "
                + "BEGIN "
                +   "DELETE FROM " + DeckTable.NAME
                +       " WHERE OLD." + DirectoryTable.Attributes.ID
                +                   " = " + DeckTable.NAME + "." + DeckTable.Attributes.PID + ";"
                + " END;");


        db.execSQL("CREATE TABLE " + CardTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                CardTable.Attributes.PID + ", " +
                CardTable.Attributes.ID + ", " +
                CardTable.Attributes.DATE + ", " +
                CardTable.Attributes.NAME + ", " +
                CardTable.Attributes.DETAIL + ", " +
                CardTable.Attributes.IMAGE + ", " +
                CardTable.Attributes.COLOR + ", " +
                CardTable.Attributes.LAST_VISIT_DATE + ", " +
                CardTable.Attributes.NUM_FORGET_OVER_NUM_DATES + ", " +
                CardTable.Attributes.NUM_FORGET + ", " +
                CardTable.Attributes.CREATOR + ")");

        db.execSQL("CREATE TRIGGER delete_cards_as_deck "
                + "BEFORE DELETE ON " + DeckTable.NAME
                + " FOR EACH ROW "
                + "BEGIN "
                +   "DELETE FROM " + CardTable.NAME
                +       " WHERE OLD." + DeckTable.Attributes.ID
                +                   " = " + CardTable.NAME + "." + CardTable.Attributes.PID + ";"
                + " END;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch(oldVersion) {
            case 1:
                db.execSQL("ALTER TABLE " + DeckTable.NAME
                        + " ADD COLUMN " + DeckTable.Attributes.CREATOR + " TEXT;");
                db.execSQL("ALTER TABLE " + CardTable.NAME
                        + " ADD COLUMN " + CardTable.Attributes.CREATOR + " TEXT;");

                db.execSQL("ALTER TABLE " + CardTable.NAME
                        + " ADD COLUMN " + CardTable.Attributes.IMAGE + " TEXT;");
                //TODO: test it
                Cursor cursor = db.query(CardTable.NAME,
                        null, null, null, null, null, null);

                try {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {

                        String dKID = cursor.getString(cursor.getColumnIndex(CardTable.Attributes.PID));
                        String cID = cursor.getString(cursor.getColumnIndex(CardTable.Attributes.ID));
                        long date = cursor.getLong(cursor.getColumnIndex(CardTable.Attributes.DATE));
                        String name = cursor.getString(cursor.getColumnIndex(CardTable.Attributes.NAME));
                        String detail = cursor.getString(cursor.getColumnIndex(CardTable.Attributes.DETAIL));
                        String imagePath = cursor.getString(cursor.getColumnIndex("image"));
                        int color = cursor.getInt(cursor.getColumnIndex(CardTable.Attributes.COLOR));

                        long lastVisitDate = cursor.getLong(cursor.getColumnIndex(CardTable.Attributes.LAST_VISIT_DATE));
                        long numForget
                                = cursor.getLong(cursor.getColumnIndex(CardTable.Attributes.NUM_FORGET));
                        double numForgetOverDates
                                = cursor.getDouble(cursor.getColumnIndex(CardTable.Attributes.NUM_FORGET_OVER_NUM_DATES));

                        Card card = new Card(UUID.fromString(cID));
                        card.setPID(UUID.fromString(dKID));
                        card.setDateCreated(new Date(date));
                        card.setName(name);
                        card.setDetail(detail);
                        card.setColor(color);
                        card.setLastVisitDate(new Date(lastVisitDate));
                        card.setNumForget(numForget);
                        card.setNumForgetOverNumDates(numForgetOverDates);
                        Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
                        card.setImage(imageBitmap);

                        ContentValues values = card.getContentValues();

                        db.update(CardTable.NAME, values,
                                "id = ? ",
                                new String[]{ card.getID().toString() });

                        cursor.moveToNext();
                    }
                }
                finally {
                    cursor.close();
                }

            db.execSQL("ALTER TABLE " + DeckTable.NAME
                    + " DROP COLUMN " + "image");

            case 2:
                break;
            default:
                break;
        }
    }
}
