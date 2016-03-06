package com.fstprj.teejayc.vividcard;

import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import com.fstprj.teejayc.vividcard.DBSchema.CardTable;

import com.fstprj.teejayc.vividcard.SortVIVIDItemDialog.SortCategory;

public class CardFragment extends Fragment {

    private static int NUM_ARRAYS = 3;

    private static final String ARG_CARD_POSITION = "card_position";
    private static final String ARG_DECK_ID = "deck_id";
    private static final String ARG_SORT_CATEGORY = "sort_category";

    //saved instance
    private static final String CARD_POSITION = "card_position";
    private static final String CURR_CARD_TYPE = "curr_card_type";

    private static final int RANDOM = 0;
    private static final int MOST_FORGOTTEN = 1;
    private static final int LAST_VISITED = 2;

    int mCurrCardType;
    int mCurrCardIndex;
    int mCurrList;
    UUID mDKID;
    Card mCard;
    List<VIVIDItem> mCards;
    boolean isRandomMode;

    ImageView mImage;
    TextView mName;
    TextView mDetail;
    ImageView mPrevButton;
    ImageView mNextButton;

    SortCategory mSortCategory;

    View mView;

    TextToSpeech tTS;

    public static CardFragment newInstance(int cardPosition, UUID dKID, SortCategory sortCategory) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CARD_POSITION, cardPosition);
        args.putSerializable(ARG_DECK_ID, dKID);
        args.putSerializable(ARG_SORT_CATEGORY, sortCategory);

        CardFragment fragment = new CardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void updateCard(View view) {
        int color = mCard.getColor();
        view.setBackgroundColor(color);
    }

    private void getRandomArray() {
        Random rand = new Random();
        mCurrList = rand.nextInt(NUM_ARRAYS);
    }

    private void getRandomCard() {
        Random rand = new Random();
        mCurrCardIndex = rand.nextInt(mCards.size());
    }

    private void getNextRandomCard() {
        getRandomArray();
        Lab lab = Lab.get(getActivity());
        switch(mCurrList) {
            case RANDOM:
                getRandomCard();
                mCard = (Card) mCards.get(mCurrCardIndex);
                break;
            case MOST_FORGOTTEN:
                mCard = lab.getMostForgotten(mDKID);
                break;
            case LAST_VISITED:
                mCard = lab.getLastVisited(mDKID);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Lab lab = Lab.get(getActivity());
        mDKID = (UUID) getArguments().getSerializable(ARG_DECK_ID);
        mCurrCardIndex = getArguments().getInt(ARG_CARD_POSITION);
        mSortCategory = (SortCategory)getArguments().getSerializable(ARG_SORT_CATEGORY);
        mCards = lab.getItems(mSortCategory, mDKID, CardTable.NAME);
        if (mCurrCardIndex == -1) {
            isRandomMode = true;
        }
        else {
            isRandomMode = false;
        }
        if (savedInstanceState != null) {
            mCurrCardIndex = savedInstanceState.getInt(CARD_POSITION);
            if (isRandomMode) {
                mCurrCardType = savedInstanceState.getInt(CURR_CARD_TYPE);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(CARD_POSITION, mCurrCardIndex);
        if (isRandomMode) {
            savedInstanceState.putInt(CURR_CARD_TYPE, mCurrCardType);
        }
    }

    private static long daysBetween(Date one, Date two) {
        return (one.getTime()-two.getTime()) / 86400000;
    }


    private void setTextColor(TextView text) {
        int color = mCard.getColor();
        int darkerColor = ColorPallet.get().getDarkColorWithColor(color);
        text.setTextColor(darkerColor);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_card, container, false);

        if (isRandomMode) {
            getNextRandomCard();
        }
        else {
            mCard = (Card)mCards.get(mCurrCardIndex);
        }

        updateCard(mView);

        tTS = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tTS.setLanguage(Locale.US);
                }
            }
        });

        mImage = (ImageView) mView.findViewById(R.id.imageImageView);
        mName = (TextView) mView.findViewById(R.id.cardNameTextView);
        mDetail = (TextView) mView.findViewById(R.id.cardDetailTextView);
        mPrevButton = (ImageView) mView.findViewById(R.id.leftImageButton);
        mNextButton = (ImageView) mView.findViewById(R.id.rightImageButton);

        if (mCard.getImage() == null) {
            mImage.setVisibility(View.GONE);
        }
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImage.setImageBitmap(mCard.getImage());
                mImage.setBackground(null);
            }
        });

        mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextColor(mName);
                mName.setText(mCard.getName());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tTS.speak(mName.getText().toString(),
                            TextToSpeech.QUEUE_FLUSH, null, null);
                }
                else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    tTS.speak(mName.getText().toString(),
                            TextToSpeech.QUEUE_FLUSH, null);
                }

            }
        });

        mDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextColor(mDetail);
                mDetail.setText(mCard.getName());
            }
        });

        if (isRandomMode) {
            mPrevButton.setImageResource(R.drawable.forgot_button_image);
            mNextButton.setImageResource(R.drawable.remember_button_image);
        }

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lab lab = Lab.get(getActivity());
                if (isRandomMode) {
                    Date dateCreated = mCard.getDateCreated();
                    Date lastVisited = new Date();
                    long daysDiff = daysBetween(lastVisited, dateCreated) + 1;
                    long numForget = mCard.getNumForget();
                    mCard.setLastVisitDate(lastVisited);
                    mCard.setNumForget(numForget + 1);
                    mCard.setNumForgetOverNumDates((double)numForget / (double)daysDiff);
                    lab.updateItem(mCard, CardTable.NAME);
                    getNextRandomCard();
                }
                else {
                    --mCurrCardIndex;
                    if (mCurrCardIndex < 0) {
                        mCurrCardIndex = mCards.size() - 1;
                    }
                    mCard = (Card)mCards.get(mCurrCardIndex);
                }
                updateCard(mView);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lab lab = Lab.get(getActivity());
                if (isRandomMode) {
                    Date dateCreated = mCard.getDateCreated();
                    Date lastVisited = new Date();
                    long daysDiff = daysBetween(lastVisited, dateCreated) + 1;
                    long numForget = mCard.getNumForget();
                    mCard.setLastVisitDate(lastVisited);
                    mCard.setNumForgetOverNumDates((double) numForget / (double) daysDiff);
                    lab.updateItem(mCard, CardTable.NAME);
                    getNextRandomCard();
                } else {
                    ++mCurrCardIndex;
                    mCurrCardIndex %= mCards.size();
                    mCard = (Card) mCards.get(mCurrCardIndex);
                }
                updateCard(mView);

            }
        });

        return mView;
    }

}
