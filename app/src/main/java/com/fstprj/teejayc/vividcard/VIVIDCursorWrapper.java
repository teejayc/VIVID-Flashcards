package com.fstprj.teejayc.vividcard;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.UUID;

import com.fstprj.teejayc.vividcard.DBSchema.CardTable;
import com.fstprj.teejayc.vividcard.DBSchema.DeckTable;
import com.fstprj.teejayc.vividcard.DBSchema.DirectoryTable;

public class VIVIDCursorWrapper extends CursorWrapper {
    public VIVIDCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Card getCard() {
        String dKID = getString(getColumnIndex(CardTable.Attributes.PID));
        String cID = getString(getColumnIndex(CardTable.Attributes.ID));
        long date = getLong(getColumnIndex(CardTable.Attributes.DATE));
        String name = getString(getColumnIndex(CardTable.Attributes.NAME));
        String detail = getString(getColumnIndex(CardTable.Attributes.DETAIL));
        int color = getInt(getColumnIndex(CardTable.Attributes.COLOR));
        byte[] outImage = getBlob(getColumnIndex(CardTable.Attributes.IMAGE));
        ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
        Bitmap image = BitmapFactory.decodeStream(imageStream);
        long lastVisitDate = getLong(getColumnIndex(CardTable.Attributes.LAST_VISIT_DATE));
        long numForget
                = getLong(getColumnIndex(CardTable.Attributes.NUM_FORGET));
        double numForgetOverDates
                = getDouble(getColumnIndex(CardTable.Attributes.NUM_FORGET_OVER_NUM_DATES));
        String creator
                = getString(getColumnIndex(CardTable.Attributes.CREATOR));


        Card card = new Card(UUID.fromString(cID));
        card.setPID(UUID.fromString(dKID));
        card.setDateCreated(new Date(date));
        card.setName(name);
        card.setDetail(detail);
        card.setColor(color);
        card.setImage(image);
        card.setLastVisitDate(new Date(lastVisitDate));
        card.setNumForget(numForget);
        card.setNumForgetOverNumDates(numForgetOverDates);
        card.setCreator(creator);

        return card;
    }

    public Deck getDeck() {
        String dId = getString(getColumnIndex(DeckTable.Attributes.PID));
        String dKId = getString(getColumnIndex(DeckTable.Attributes.ID));
        long date = getLong(getColumnIndex(DeckTable.Attributes.DATE));
        String name = getString(getColumnIndex(DeckTable.Attributes.NAME));
        int numDecks = getInt(getColumnIndex(DeckTable.Attributes.NUM_CARDS));
        String creator
                = getString(getColumnIndex(DeckTable.Attributes.CREATOR));

        Deck deck = new Deck(UUID.fromString(dKId));
        deck.setPID(UUID.fromString(dId));
        deck.setName(name);
        deck.setDateCreated(new Date(date));
        deck.setNumCards(numDecks);
        deck.setCreator(creator);

        return deck;
    }

    public Directory getDirectory() {
        String did = getString(getColumnIndex(DirectoryTable.Attributes.ID));
        long date = getLong(getColumnIndex(DirectoryTable.Attributes.DATE));
        String name = getString(getColumnIndex(DirectoryTable.Attributes.NAME));
        int numDecks = getInt(getColumnIndex(DirectoryTable.Attributes.NUM_DECKS));

        Directory directory = new Directory(UUID.fromString(did));
        directory.setName(name);
        directory.setDateCreated(new Date(date));
        directory.setNumDecks(numDecks);

        return directory;
    }
}
