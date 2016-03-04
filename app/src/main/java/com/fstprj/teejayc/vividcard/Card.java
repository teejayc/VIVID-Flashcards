package com.fstprj.teejayc.vividcard;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.UUID;
import com.fstprj.teejayc.vividcard.DBSchema.CardTable;

public class Card extends VIVIDItem{
    private UUID mPID;
    private String mDetail;
    private int mColor;
    private Bitmap mScaledImage;
    private Bitmap mOriginalImage;
    private Date mLastVisitDate;
    //TODO: make it unlimited
    private long mNumForget;
    private double mNumForgetOverNumDates;
    private String mCreator;



    public Card(UUID iD) {
        super(iD);
    }

    //for new Card
    public Card(String nameIn, String detailIn, int colorIn,
                Bitmap scaledImageIn, Bitmap originalImageIn, UUID pID) {
        super(nameIn);
        mPID = pID;
        mDetail = detailIn;
        mColor = colorIn;
        mScaledImage = scaledImageIn;
        mOriginalImage = originalImageIn;
        mLastVisitDate = new Date();
        mNumForget = 0;
        mNumForgetOverNumDates = 0.0;
    }

    //for downloading image
    public Card(String pid, String id, String date, String name, String detail,
                String color, String scaledImage, String originalImage,
                String last_visit_date, String num_forget,
                String num_forget_over_dates, String creator) {
        super(UUID.fromString(id));
        mPID = UUID.fromString(pid);
        setDateCreated(new Date(Long.getLong(date)));
        setName(name);
        mDetail = detail;
        mColor = Integer.getInteger(color);
        ByteArrayInputStream imageStream
                = new ByteArrayInputStream(scaledImage.getBytes());
        Bitmap image = BitmapFactory.decodeStream(imageStream);
        setScaledImage(image);
        imageStream = new ByteArrayInputStream(originalImage.getBytes());
        image = BitmapFactory.decodeStream(imageStream);
        setOriginalImage(image);
        mLastVisitDate = new Date(Long.getLong(last_visit_date));
        mNumForget = Long.getLong(num_forget);
        mNumForgetOverNumDates = Double.valueOf(num_forget_over_dates);
        mCreator = creator;
    }

    public UUID getPID() {
        return mPID;
    }

    public void setPID(UUID mPID) {
        this.mPID = mPID;
    }

    public String getDetail() {
        return mDetail;
    }

    public void setDetail(String mDetail) {
        this.mDetail = mDetail;
    }

    public Bitmap getScaledImage() {
        return mScaledImage;
    }

    public void setScaledImage(Bitmap scaledImage) {
        this.mScaledImage = scaledImage;
    }

    public Bitmap getOriginalImage() {
        return mOriginalImage;
    }

    public void setOriginalImage(Bitmap originalImage) {
        this.mOriginalImage = originalImage;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
    }

    public Date getLastVisitDate() {
        return mLastVisitDate;
    }

    public void setLastVisitDate(Date mLastVisitDate) {
        this.mLastVisitDate = mLastVisitDate;
    }

    public long getNumForget() {
        return mNumForget;
    }

    public void setNumForget(long mNumForget) {
        this.mNumForget = mNumForget;
    }

    public double getNumForgetOverNumDates() {
        return mNumForgetOverNumDates;
    }

    public void setNumForgetOverNumDates(double mNumForgetOverNumDates) {
        this.mNumForgetOverNumDates = mNumForgetOverNumDates;
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

        values.put(CardTable.Attributes.PID, mPID.toString());
        values.put(CardTable.Attributes.DETAIL, mDetail);
        values.put(CardTable.Attributes.COLOR, mColor);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mScaledImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte imageInByte[] = stream.toByteArray();
        values.put(CardTable.Attributes.SCALED_IMAGE, imageInByte);

        mOriginalImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        imageInByte = stream.toByteArray();
        values.put(CardTable.Attributes.ORIGINAL_IMAGE, imageInByte);

        values.put(CardTable.Attributes.LAST_VISIT_DATE, mLastVisitDate.getTime());
        values.put(CardTable.Attributes.NUM_FORGET_OVER_NUM_DATES, mNumForgetOverNumDates);
        values.put(CardTable.Attributes.NUM_FORGET, mNumForget);
        values.put(CardTable.Attributes.CREATOR, mCreator);

        return values;
    }


}

