package com.fstprj.teejayc.vividcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;


public class NewCardDialog extends DialogFragment {
    public static final String ARG_ORG_NAME
            = "card_name";
    public static final String ARG_ORG_DETAIL
            = "card_detail";
    public static final String ARG_ORG_COLOR
            = "card_color";
    public static final String ARG_ORG_IMAGE
            = "card_image";

    public static final String EXTRA_NEW_CARD_NAME
            = "com.taejungchang.vividcard.new_card_name";
    public static final String EXTRA_NEW_CARD_DETAIL
            = "com.taejungchang.vividcard.new_card_detail";
    public static final String EXTRA_NEW_CARD_COLOR
            = "com.taejungchang.vividcard.new_card_color";
    public static final String EXTRA_NEW_CARD_ORIGINAL_IMAGE
            = "com.taejungchang.vividcard.new_card_original_image";
    public static final String EXTRA_NEW_CARD_SCALED_IMAGE
            = "com.taejungchang.vividcard.new_card_scaled_image";
    public static final String EXTRA_IMAGE_CHANGED
            = "com.taejungchang.vividcard.new_card_image_changed";

    public static final String LOAD_IMAGE = "load_image";
    public static final int REQUEST_IMAGE_TYPE = 0;
    public static final int REQUEST_IMAGE_CAM = 1;
    public static final int REQUEST_IMAGE_GAL = 2;

    //Saved Instance
    private static final String NAME_SI = "name";
    private static final String DETAIL_SI = "detail";
    private static final String IMAGE_PATH_SI = "image_path";
    private static final String CHECKED_RADIO_ID_SI = "checked_radio_button_id";
    private static final String IS_CHECKING_SI = "is_checking";
    private static final String ORIGINAL_IMAGE = "original_image";
    private static final String SCALED_IMAGE = "scaled_image";
    private static final String IMAGE_CHANGED = "image_changed";

    private LinearLayout mHostLayout;
    private EditText mName;
    private EditText mDetail;
    private ImageView mImageView;
    private RadioGroup mColorSet1;
    private RadioGroup mColorSet2;
    private int mCheckedRadioButtonID;
    private boolean isChecking;
    private View mView;
    private Bitmap mScaledImage;
    private Bitmap mOriginalImage;
    private String mImagePath;
    private Boolean mImageChanged;


    public static NewCardDialog newInstance(String originalName,
                                            String originalDetail,
                                            int originalColor,
                                            Bitmap originalImage) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_ORG_NAME, originalName);
        bundle.putString(ARG_ORG_DETAIL, originalDetail);
        bundle.putInt(ARG_ORG_COLOR, originalColor);
        bundle.putParcelable(ARG_ORG_IMAGE, originalImage);

        NewCardDialog newCardDialog = new NewCardDialog();
        newCardDialog.setArguments(bundle);
        return newCardDialog;
    }

    private int getRadioButtonColor() {
        ColorDrawable colorDrawable = (ColorDrawable)
                mView.findViewById(mCheckedRadioButtonID).getBackground();
        return colorDrawable.getColor();
    }

    private void setCardColors(int color) {
        int darkerColor = ColorPallet.get().getDarkColorWithColor(color);
        mHostLayout.setBackgroundColor(color);
        mName.setTextColor(darkerColor);
        mDetail.setTextColor(darkerColor);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_new_card, null);

        mHostLayout = (LinearLayout) mView.findViewById(R.id.new_card_host_linear_layout);
        mImageView = (ImageView) mView.findViewById(R.id.newCardImageView);
        mImagePath = getArguments().getString(ARG_ORG_IMAGE);
        //check if there is camera on the device.
        PackageManager pM = getActivity().getPackageManager();
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (captureImage.resolveActivity(pM) != null) {
            mImageView.setImageResource(R.drawable.add_menu_item);
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getFragmentManager();
                    ImageLoadDialog dialog
                            = new ImageLoadDialog();
                    dialog.setTargetFragment(NewCardDialog.this, REQUEST_IMAGE_TYPE);
                    dialog.show(manager, LOAD_IMAGE);
                }
            });
        }

        mName = (EditText) mView.findViewById(R.id.newCardNameEditText);
        mDetail = (EditText) mView.findViewById(R.id.newCardDetailEditText);

        mColorSet1 = (RadioGroup) mView.findViewById(R.id.color_radio_group1);
        mColorSet2 = (RadioGroup) mView.findViewById(R.id.color_radio_group2);

        mColorSet1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1 && isChecking) {
                    isChecking = false;
                    mColorSet2.clearCheck();
                    mCheckedRadioButtonID = checkedId;
                    setCardColors(getRadioButtonColor());
                }
                isChecking = true;
            }
        });

        mColorSet2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1 && isChecking) {
                    isChecking = false;
                    mColorSet1.clearCheck();
                    mCheckedRadioButtonID = checkedId;
                    setCardColors(getRadioButtonColor());
                }
                isChecking = true;
            }
        });

        mImageChanged = false;

        if (savedInstanceState == null) {
            mName.setText(getArguments().getString(ARG_ORG_NAME));
            mDetail.setText(getArguments().getString(ARG_ORG_DETAIL));

            int color = getArguments().getInt(ARG_ORG_COLOR);
            setCardColors(color);

            mImageView
                    .setImageBitmap((Bitmap) getArguments().getParcelable(ARG_ORG_IMAGE));

            mCheckedRadioButtonID = ColorPallet.get().getRadioButtonId(color);
            isChecking = true;
        }
        else {
            mName.setText(savedInstanceState.getString(NAME_SI));
            mDetail.setText(savedInstanceState.getString(DETAIL_SI));
            mScaledImage = savedInstanceState.getParcelable(SCALED_IMAGE);
            mOriginalImage = savedInstanceState.getParcelable(ORIGINAL_IMAGE);
            mImagePath = savedInstanceState.getString(IMAGE_PATH_SI);
            mCheckedRadioButtonID = savedInstanceState.getInt(CHECKED_RADIO_ID_SI, 0);
            isChecking = savedInstanceState.getBoolean(IS_CHECKING_SI);
            mImageChanged = savedInstanceState.getBoolean(IMAGE_CHANGED);
        }
        if (mImagePath != null) {
            setImage(BitmapFactory.decodeFile(mImagePath));
        }


        return new AlertDialog.Builder(getActivity())
                .setView(mView)
                .setTitle(R.string.new_card)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = mName.getText().toString();
                        String detail = mDetail.getText().toString();
                        int color = getRadioButtonColor();
                        sendResult(Activity.RESULT_OK, name, detail, color);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    private void sendResult(int resultCode, String name,
                            String detail, int color) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_NEW_CARD_NAME, name);
        intent.putExtra(EXTRA_NEW_CARD_DETAIL, detail);
        intent.putExtra(EXTRA_NEW_CARD_COLOR, color);
        intent.putExtra(EXTRA_NEW_CARD_ORIGINAL_IMAGE, mOriginalImage);
        intent.putExtra(EXTRA_NEW_CARD_SCALED_IMAGE, mScaledImage);
        intent.putExtra(EXTRA_IMAGE_CHANGED, mImageChanged);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    private Bitmap rotateImage(Bitmap bitmap, float degree) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        retVal = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return retVal;
    }
        //Saves file
//    private void saveBitmap(Bitmap bitmap) {
//        File file = Lab.get(getActivity()).getPhotoFile(UUID.randomUUID());
//
//        FileOutputStream fos;
//        try {
//            fos = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        mImagePath = file.getPath();
//    }

    private void setImage(Bitmap bitmap) {

        mImageView.setImageBitmap(bitmap);
        mImageView.setBackground(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_IMAGE_TYPE) {
            int loadCode = data.getIntExtra(ImageLoadDialog.EXTRA_LOAD_CODE, REQUEST_IMAGE_TYPE);

            switch (loadCode) {
                case ImageLoadDialog.LOAD_WITH_CAM_CODE:
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, REQUEST_IMAGE_CAM);

                    break;
                case ImageLoadDialog.LOAD_WITH_GAL_CODE:
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            REQUEST_IMAGE_GAL);
                    break;
                default:
                    break;
            }
        }
        else if (requestCode == REQUEST_IMAGE_GAL || requestCode == REQUEST_IMAGE_CAM) {
            //Get String path
            Uri selectedImageUri = data.getData();
            String[] projection = { MediaStore.MediaColumns.DATA };
            CursorLoader cursorLoader = new CursorLoader(getActivity() ,selectedImageUri, projection, null, null,
                    null);
            Cursor cursor = cursorLoader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            String selectedImagePath = cursor.getString(column_index);

            mOriginalImage = BitmapFactory.decodeFile(selectedImagePath);
            mScaledImage = PictureUtils.getScaledBitmap(getActivity(), selectedImagePath);

            //Rotate image
            try {
                ExifInterface ei = new ExifInterface(selectedImagePath);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        mOriginalImage = rotateImage(mOriginalImage, 90);
                        mScaledImage = rotateImage(mScaledImage, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        mOriginalImage = rotateImage(mOriginalImage, 180);
                        mScaledImage = rotateImage(mScaledImage, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        mOriginalImage = rotateImage(mOriginalImage, 270);
                        mScaledImage = rotateImage(mScaledImage, 90);
                        break;
                    default:
                        break;
                }
            }
            catch(IOException ex) {
                //TODO: catch Error
            }

            //Save and set image
            setImage(mScaledImage);
            mImageChanged = true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NAME_SI, mName.getText().toString());
        outState.putString(DETAIL_SI, mDetail.getText().toString());
        outState.putString(IMAGE_PATH_SI, mImagePath);
        outState.putInt(CHECKED_RADIO_ID_SI, mCheckedRadioButtonID);
        outState.putBoolean(IS_CHECKING_SI, isChecking);
        outState.putBoolean(IMAGE_CHANGED, mImageChanged);
    }
}
