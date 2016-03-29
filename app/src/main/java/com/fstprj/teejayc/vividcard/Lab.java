package com.fstprj.teejayc.vividcard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.fstprj.teejayc.vividcard.DBSchema.CardTable;
import com.fstprj.teejayc.vividcard.DBSchema.DeckTable;
import com.fstprj.teejayc.vividcard.DBSchema.DirectoryTable;

import com.fstprj.teejayc.vividcard.SortVIVIDItemDialog.SortCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Lab {

    private static HashMap<String, String> sParentTableNameMap;
    private static HashMap<String, String> sChildTableNameMap;

    private static Lab sInstance;

    private Context mContext;
    private SQLiteDatabase mSQLiteDatabase;

    public boolean loggedIn() {
        return mLoggedIn;
    }

    public void logIn(boolean mLoggedIn) {
        this.mLoggedIn = mLoggedIn;
    }

    private boolean mLoggedIn;

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        this.mUserId = mUserId;
    }

    private String mUserId;


    public static Lab get(Context context) {
        if (sInstance == null) {
            sInstance = new Lab(context);
        }
        return sInstance;
    }

    public Lab(Context context) {
        mContext = context;
        mLoggedIn = false;
        mSQLiteDatabase = new BaseHelper(mContext).getWritableDatabase();
        sParentTableNameMap = new HashMap<>();
        sParentTableNameMap.put(CardTable.NAME, DeckTable.NAME);
        sParentTableNameMap.put(DeckTable.NAME, DirectoryTable.NAME);
        sParentTableNameMap.put(DirectoryTable.NAME, null);

        sChildTableNameMap = new HashMap<>();
        sChildTableNameMap.put(CardTable.NAME, null);
        sChildTableNameMap.put(DeckTable.NAME, CardTable.NAME);
        sChildTableNameMap.put(DirectoryTable.NAME, DeckTable.NAME);
    }

    public String getParentTableName(String tableName) {
        return sParentTableNameMap.get(tableName);
    }

    public String getChildTableName(String tableName) {
        return sChildTableNameMap.get(tableName);
    }

    public  List<VIVIDItem> parseJSONtoDeck(String string) {
        List<VIVIDItem> decks = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("server_response");
            int count = 0;
            String pid, id, date, name, numCards, creator;
            while (count < jsonObject.length()) {
                JSONObject JO = jsonArray.getJSONObject(count);
                pid = JO.getString(DeckTable.Attributes.PID);
                id = JO.getString(DeckTable.Attributes.ID);
                date = JO.getString(DeckTable.Attributes.DATE);
                name = JO.getString(DeckTable.Attributes.NAME);
                numCards = JO.getString(DeckTable.Attributes.NUM_CARDS);
                creator = JO.getString(DeckTable.Attributes.CREATOR);
                decks.add(new Deck(pid, id, date, name, numCards, creator));
                ++count;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return decks;
    }

    public  List<Card> parseJSONtoCard(String string) {
        List<Card> cards = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("server_response");
            int count = 0;
            String pid, id, date, name, detail, color, image;
            String last_visit_date, num_forget, num_forget_over_dates, creator;
            while (count < jsonObject.length()) {
                JSONObject JO = jsonArray.getJSONObject(count);
                pid = JO.getString(CardTable.Attributes.PID);
                id = JO.getString(CardTable.Attributes.ID);
                date = JO.getString(CardTable.Attributes.DATE);
                name = JO.getString(CardTable.Attributes.NAME);
                detail = JO.getString(CardTable.Attributes.DETAIL);
                color = JO.getString(CardTable.Attributes.COLOR);
                image = JO.getString(CardTable.Attributes.IMAGE);
                last_visit_date = JO.getString(CardTable.Attributes.LAST_VISIT_DATE);
                num_forget = JO.getString(CardTable.Attributes.NUM_FORGET);
                num_forget_over_dates = JO.getString(CardTable.Attributes.NUM_FORGET_OVER_NUM_DATES);
                creator = JO.getString(CardTable.Attributes.CREATOR);
                cards.add(new Card(pid, id, date, name, detail, color,
                        image, last_visit_date, num_forget,
                        num_forget_over_dates, creator));
                ++count;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cards;
    }


    public List<VIVIDItem> getItems(SortCategory sortCategory, UUID pID, String tableName) {
        List<VIVIDItem> items = new ArrayList<>();

        String whereClause;
        String[] whereArgs;

        if (tableName.equals(DBSchema.DirectoryTable.NAME)) {
            whereClause = null;
            whereArgs = null;
        }
        else {
            whereClause = "pid = ? ";
            whereArgs = new String[]{ pID.toString() };
        }

        String orderBy = null;
        if (sortCategory != null) {
            switch (sortCategory) {
                case NAME:
                    orderBy = "name";
                    break;
                case DATE:
                    orderBy = "date";
                    break;
                default:
                    break;
            }
        }
        Cursor cursor = mSQLiteDatabase.query(tableName,
                null,
                whereClause,
                whereArgs,
                null, null, orderBy);

        VIVIDCursorWrapper vividCursorWrapper = new VIVIDCursorWrapper(cursor);
        //TODO: debug start here
        //Update deck id when deck id changed
        try {
            vividCursorWrapper.moveToFirst();
            while (!vividCursorWrapper.isAfterLast()) {
                if (tableName.equals(CardTable.NAME)) {
                    items.add(vividCursorWrapper.getCard());
                }
                else if(tableName.equals(DeckTable.NAME)) {
                    items.add(vividCursorWrapper.getDeck());
                }
                else {
                    items.add(vividCursorWrapper.getDirectory());
                }
                vividCursorWrapper.moveToNext();
            }

        }
        finally {
            vividCursorWrapper.close();
        }

        return items;
    }

    public VIVIDItem getItem(UUID iD, String tableName) {
        if (tableName == null) {
            return null;
        }
        Cursor cursor = mSQLiteDatabase.query(tableName,
                null, "id = ? ",
                new String[]{iD.toString()},
                null, null, null);

        VIVIDCursorWrapper vividCursorWrapper = new VIVIDCursorWrapper(cursor);

        try {
            if (vividCursorWrapper.getCount() == 0) {
                return null;
            }
            vividCursorWrapper.moveToFirst();
            if (tableName.equals(DBSchema.CardTable.NAME)) {
                return vividCursorWrapper.getCard();
            }
            else if(tableName.equals(DBSchema.DeckTable.NAME)) {
                return vividCursorWrapper.getDeck();
            }
            else {
                return vividCursorWrapper.getDirectory();
            }
        }
        finally {
            vividCursorWrapper.close();
        }
    }


    public UUID addItem(VIVIDItem item, String tableName) {
        ContentValues values = item.getContentValues();
        mSQLiteDatabase.insert(tableName, null, values);
        VIVIDItem parentItem;
        if (tableName.equals(CardTable.NAME)) {
            parentItem = getItem(((Card)item).getPID(), DeckTable.NAME);
            Deck deck = (Deck)parentItem;
            deck.setNumCards(deck.getNumCards() + 1);
            return updateItem(parentItem, getParentTableName(tableName));
        }
        else if(tableName.equals(DeckTable.NAME)) {
            parentItem = getItem(((Deck)item).getPID(), DirectoryTable.NAME);
            Directory directory = (Directory)parentItem;
            directory.setNumDecks(directory.getNumDecks() + 1);
            updateItem(parentItem, getParentTableName(tableName));
        }
        return null;
    }

    public UUID deleteItem(UUID iD, String tableName) {
        UUID id = null;
        if (tableName.equals(CardTable.NAME)) {
            Deck deck
                    = (Deck)getItem(((Card)getItem(iD, tableName)).getPID(),
                    DeckTable.NAME);
            deck.setNumCards(deck.getNumCards() - 1);
            id = updateItem(deck, DeckTable.NAME);
        }
        else if (tableName.equals(DeckTable.NAME)) {
            Directory directory
                    = (Directory)getItem(((Deck)getItem(iD, tableName)).getPID(),
                    DirectoryTable.NAME);
            directory.setNumDecks(directory.getNumDecks() - 1);
            updateItem(directory, DirectoryTable.NAME);
        }

        mSQLiteDatabase.delete(tableName,
                "id = ? ",
                new String[]{iD.toString()});

        return id;
    }

    public void updateItemHelper(VIVIDItem item, String tableName) {
        ContentValues values = item.getContentValues();

        mSQLiteDatabase.update(tableName, values,
                "id = ? ",
                new String[]{item.getID().toString()});
    }

    public UUID updateItem(VIVIDItem item, String tableName) {
        if (tableName.equals(DirectoryTable.NAME)) {
            updateItemHelper(item, tableName);

        }
        else if (tableName.equals(DeckTable.NAME)) {
            UUID newDeckID = UUID.randomUUID();
            List<VIVIDItem> cards = getItems(null, item.getID(), CardTable.NAME);
            for (VIVIDItem card : cards)  {
                Card c = (Card) card;
                c.setPID(newDeckID);
                updateItemHelper(c, CardTable.NAME);
            }

            item.setID(newDeckID);
            updateItemHelper(item, tableName);
            return newDeckID;
        }
        else if (tableName.equals(CardTable.NAME)) {
            updateItemHelper(item, CardTable.NAME);
            Deck parentDeck = (Deck) getItem(((Card)item).getPID(), DeckTable.NAME);
            return updateItem(parentDeck, DeckTable.NAME);
        }
        return null;
    }

    public String getPhotoFileName(UUID iD) {
        return "IMG_" + iD.toString() + ".jpg";
    }

    public File getPhotoFile(UUID iD) {
        File externalFilesDir
                = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            Toast.makeText(mContext, "Not enough memory!", Toast.LENGTH_LONG);
            return null;
        }

        return new File(externalFilesDir, getPhotoFileName(iD));
    }

//    private void getCardHeap(UUID pID, PriorityQueue<Card> cards) {
//        String whereClause = "pid = ? ";
//        String[] whereArgs = new String[]{ pID.toString() };
//
//        Cursor cursor = mSQLiteDatabase.query(CardTable.NAME,
//                null,
//                whereClause,
//                whereArgs,
//                null, null, null);
//
//        VIVIDCursorWrapper cursorWrapper = new VIVIDCursorWrapper(cursor);
//
//        try {
//            cursorWrapper.moveToFirst();
//            while (!cursorWrapper.isAfterLast()) {
//                cards.offer(cursorWrapper.getCard());
//                cursorWrapper.moveToNext();
//            }
//        }
//        finally {
//            cursorWrapper.close();
//        }
//    }


//    public PriorityQueue<Card> getMostForgetHeap(UUID pID) {
//        Comparator<Card> mostForgetComparator = new Comparator<Card>(){
//
//            @Override
//            public int compare(Card c1, Card c2) {
//                return (c1.getNumForget() - c2.getNumForget());
//            }
//        };
//
//        PriorityQueue<Card> cards
//                = new PriorityQueue<>(0, mostForgetComparator);
//
//        getCardHeap(pID, cards);
//
//        return cards;
//    }
//
//    public PriorityQueue<Card> getLastVisitedHeap(UUID pID) {
//        Comparator<Card> lastVisitComparator = new Comparator<Card>(){
//
//            @Override
//            public int compare(Card c1, Card c2) {
//                if (c1.getLastVisitDate().after(c2.getLastVisitDate())) {
//                    return 1;
//                }
//                return 0;
//            }
//        };
//
//        PriorityQueue<Card> cards
//                = new PriorityQueue<>(0, lastVisitComparator);
//
//        getCardHeap(pID, cards);
//
//        return cards;
//    }


    public Card getMostForgotten(UUID pID) {
        Card card = null;
        //get max Most forget
        Cursor mostForgetCursor
                =  mSQLiteDatabase.rawQuery(
                "SELECT " +
                        CardTable.Attributes.PID + ", " +
                        CardTable.Attributes.NAME + ", " +
                        CardTable.Attributes.IMAGE + ", " +
                        CardTable.Attributes.ID + ", " +
                        CardTable.Attributes.DETAIL + ", " +
                        CardTable.Attributes.DATE + ", " +
                        CardTable.Attributes.COLOR + ", " +
                        CardTable.Attributes.NUM_FORGET + ", " +
                        CardTable.Attributes.LAST_VISIT_DATE + ", " +
                        CardTable.Attributes.CREATOR + ", " +
                        " MAX(" +
                        CardTable.Attributes.NUM_FORGET_OVER_NUM_DATES +
                        ") AS " +
                        CardTable.Attributes.NUM_FORGET_OVER_NUM_DATES +
                        " FROM " +
                        CardTable.NAME +
                        " WHERE" +
                        " pid = ?"
                        , new String[] { pID.toString()});

        VIVIDCursorWrapper cursorWrapper = new VIVIDCursorWrapper(mostForgetCursor);

        try {
            cursorWrapper.moveToFirst();
            card = cursorWrapper.getCard();
        }
        finally {
            cursorWrapper.close();
        }

        return card;
    }

    public Card getLastVisited(UUID pID) {
        Card card = null;

        Cursor cursor
                =  mSQLiteDatabase.rawQuery(
                "SELECT " +
                        CardTable.Attributes.PID + ", " +
                        CardTable.Attributes.NAME + ", " +
                        CardTable.Attributes.IMAGE + ", " +
                        CardTable.Attributes.ID + ", " +
                        CardTable.Attributes.DETAIL + ", " +
                        CardTable.Attributes.DATE + ", " +
                        CardTable.Attributes.COLOR + ", " +
                        CardTable.Attributes.NUM_FORGET + ", " +
                        CardTable.Attributes.NUM_FORGET_OVER_NUM_DATES + ", " +
                        CardTable.Attributes.CREATOR + ", " +
                        " MIN(" +
                        CardTable.Attributes.LAST_VISIT_DATE +
                        ") AS " +
                        CardTable.Attributes.LAST_VISIT_DATE +
                        " FROM " +
                        CardTable.NAME +
                        " WHERE" +
                        " pid = ?"
                , new String[] { pID.toString()});

        VIVIDCursorWrapper cursorWrapper = new VIVIDCursorWrapper(cursor);

        try {
            cursorWrapper.moveToFirst();
            card = cursorWrapper.getCard();
        }
        finally {
            cursorWrapper.close();
        }

        return card;
    }
}
