package com.fstprj.teejayc.vividcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.MalformedInputException;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

import com.fstprj.teejayc.vividcard.SortVIVIDItemDialog.SortCategory;
import com.fstprj.teejayc.vividcard.DBSchema.CardTable;
import com.fstprj.teejayc.vividcard.DBSchema.DeckTable;
import com.fstprj.teejayc.vividcard.DBSchema.DirectoryTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VIVIDItemListFragment extends Fragment {

    private static final String ARG_PID = "pid";
    private static final String ARG_TABLE_NAME = "table_name";

    //dialog request code
    private static final String NEW_ITEM = "new_item";
    private static final int REQUEST_NEW_ITEM_CODE = 0;
    private static final String SORT_ITEM = "sort_item";
    private static final int REQUEST_SORT_ITEM_CODE = 1;
    private static final String EDIT_ITEM = "sort_item";
    private static final int REQUEST_EDIT_ITEM_CODE = 2;
    private static final String LOGIN = "log_in";
    private static final int REQUEST_LOGIN_CODE = 3;
    private static final String REGISTER = "register";
    private static final int REQUEST_REGISTER_CODE = 4;

    //savedInstanceState
    private static final String DELETE_MODE = "delete_mode";
    private static final String EDIT_MODE = "edit_mode";
    private static final String TO_DELETE_TREE_SET = "to_delete_tree_set";
    private static final String SORT_CATEGORY = "sort_category";
    private static final String TO_EDIT = "to_edit";
    private static final String ONLINE_LIB = "online_lib";
    private static final String UPLOAD_MODE = "upload_mode";
    private static final String USER_ID = "user_id";

    private static final int MAX_UPLOAD_NUM = 3;

    private RecyclerView mRecyclerView;
    private ItemAdapter mItemAdapter;

    private UUID mPID;
    private String mTableName;
    private boolean mDeleteMode;
    private boolean mEditMode;
    private TreeSet<UUID> mToDeleteItems;
    private Menu mMenu;
    private SortVIVIDItemDialog.SortCategory mSortCategory;
    private UUID mToEdit;
    private int mNumItems;
    private boolean mOnlineLib;
    private boolean mUploadMode;
    private TreeSet<UUID> mToUploadItems;

    public static VIVIDItemListFragment newInstance(UUID pID, String tableName) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PID, pID);
        args.putSerializable(ARG_TABLE_NAME, tableName);

        VIVIDItemListFragment fragment = new VIVIDItemListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mPID = (UUID) getArguments().getSerializable(ARG_PID);
        mTableName = getArguments().getString(ARG_TABLE_NAME);
        Lab lab = Lab.get(getActivity());
        VIVIDItem parentItem = lab.getItem(mPID, lab.getParentTableName(mTableName));
        mSortCategory = null;
        mToEdit = null;
        mOnlineLib = false;
        mUploadMode = false;

        //Set Action bar title
        if (parentItem != null) {
            String title = null;
            if (mTableName.equals(DeckTable.NAME)) {
                title = "Folder ";
            }
            else if (mTableName.equals(CardTable.NAME)) {
                title = "Deck ";
            }
            getActivity().setTitle(title + parentItem.getName());
        }

        if (savedInstanceState != null) {
            mDeleteMode = savedInstanceState.getBoolean(DELETE_MODE);
            mEditMode = savedInstanceState.getBoolean(EDIT_MODE);
            mToDeleteItems
                    = (TreeSet<UUID>) savedInstanceState.getSerializable(TO_DELETE_TREE_SET);
            mSortCategory = (SortCategory) savedInstanceState.getSerializable(SORT_CATEGORY);
            mToEdit = (UUID) savedInstanceState.getSerializable(TO_EDIT);
            mOnlineLib = savedInstanceState.getBoolean(ONLINE_LIB);
            mUploadMode = savedInstanceState.getBoolean(UPLOAD_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vivid_item_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.vivid_item_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(DELETE_MODE, mDeleteMode);
        savedInstanceState.putBoolean(EDIT_MODE, mEditMode);
        savedInstanceState.putSerializable(TO_DELETE_TREE_SET, mToDeleteItems);
        savedInstanceState.putSerializable(SORT_CATEGORY, mSortCategory);
        savedInstanceState.putSerializable(TO_EDIT, mToEdit);
        savedInstanceState.putBoolean(ONLINE_LIB, mOnlineLib);
        savedInstanceState.putBoolean(UPLOAD_MODE, mUploadMode);
    }

    //enables menu items other than delete and edit
    private void enableAllMenu(boolean flag) {
        mMenu.findItem(R.id.new_item_menu_item).setVisible(flag);
        mMenu.findItem(R.id.sort_item_menu_item).setVisible(flag);
        if (mTableName.equals(CardTable.NAME)) {
            mMenu.findItem(R.id.start_item).setVisible(flag);
        }
        if (mTableName.equals(DeckTable.NAME)) {
            mMenu.findItem(R.id.download_menu_item).setVisible(flag);
            mMenu.findItem(R.id.upload_menu_item).setVisible(flag);
        }
        mMenu.findItem(R.id.edit_item_menu_item).setVisible(flag);
        mMenu.findItem(R.id.delete_item_menu_item).setVisible(flag);
    }

    private void disableBasedOnTable() {
        if (!mTableName.equals(CardTable.NAME)) {
            mMenu.findItem(R.id.start_item).setVisible(false);
        }
        if (!mTableName.equals(DeckTable.NAME)) {
            mMenu.findItem(R.id.download_menu_item).setVisible(false);
            mMenu.findItem(R.id.upload_menu_item).setVisible(false);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        mMenu = menu;
        if (mOnlineLib) {
            enableAllMenu(false);
            mMenu.findItem(R.id.download_menu_item)
                    .setVisible(true);
            mMenu.findItem(R.id.download_menu_item)
                    .setIcon(R.drawable.check_menu_item);
        }
        else if (mDeleteMode) {
            enableAllMenu(false);
            menu.findItem(R.id.delete_item_menu_item).setVisible(true);
        }
        else if (mEditMode) {
            enableAllMenu(false);
            menu.findItem(R.id.edit_item_menu_item).setVisible(true);
        }
        else if (mUploadMode) {
            enableAllMenu(false);
            menu.findItem(R.id.upload_menu_item).setVisible(true);
        }
        disableBasedOnTable();
    }

    //The FragmentManager is responsible for calling this from activity
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_vivid_item_list, menu);
    }

    private void sortItem() {
        FragmentManager manager = getFragmentManager();
        SortVIVIDItemDialog dialog = new SortVIVIDItemDialog();
        dialog.setTargetFragment(VIVIDItemListFragment.this, REQUEST_SORT_ITEM_CODE);
        dialog.show(manager, SORT_ITEM);
    }

    private void showLogInDialog() {
        FragmentManager manager = getFragmentManager();
        LogInDialog dialog = new LogInDialog();
        dialog.setTargetFragment(VIVIDItemListFragment.this, REQUEST_LOGIN_CODE);
        dialog.show(manager, LOGIN);
    }

    private void askToLogIn() {
        Dialog warnLogIn = new AlertDialog
                .Builder(getActivity())
                .setTitle("Please Login First!")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showLogInDialog();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        warnLogIn.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.start_item:
                if (mNumItems != 0) {
                    Intent intent = CardActivity.newIntent(
                            getActivity(),
                            -1, mPID, null);
                    startActivity(intent);
                }
                return true;
            case R.id.new_item_menu_item:
                if (mTableName.equals(CardTable.NAME)) {
                    FragmentManager manager = getFragmentManager();
                    NewCardDialog dialog
                            = new NewCardDialog().newInstance(
                            null,
                            null,
                            ColorPallet.get().getColor(5),//color blue
                            null);
                    dialog.setTargetFragment(VIVIDItemListFragment.this, REQUEST_NEW_ITEM_CODE);
                    dialog.show(manager, NEW_ITEM);
                }
                else if (mTableName.equals(DeckTable.NAME)) {
                    FragmentManager manager = getFragmentManager();
                    NewVIVIDItemDialog dialog = new NewVIVIDItemDialog().newInstance(
                            NewVIVIDItemDialog.DECK_NEW_DIALOG,
                            null);
                    dialog.setTargetFragment(VIVIDItemListFragment.this, REQUEST_NEW_ITEM_CODE);
                    dialog.show(manager, NEW_ITEM);
                }
                else {
                    FragmentManager manager = getFragmentManager();
                    NewVIVIDItemDialog dialog = new NewVIVIDItemDialog().newInstance(
                            NewVIVIDItemDialog.DIR_NEW_DIALOG,
                            null);
                    dialog.setTargetFragment(VIVIDItemListFragment.this, REQUEST_NEW_ITEM_CODE);
                    dialog.show(manager, NEW_ITEM);
                }

                return true;
            case R.id.delete_item_menu_item:
                if (!mDeleteMode) {
                    mDeleteMode = true;
                    enableAllMenu(false);
                    mMenu.findItem(R.id.delete_item_menu_item).setVisible(true);
                }
                else {
                    for (UUID toDeleteItem : mToDeleteItems) {
                        Lab.get(getActivity()).deleteItem(toDeleteItem, mTableName);
                    }
                    mDeleteMode = false;
                    enableAllMenu(true);
                }
                mToDeleteItems = new TreeSet<>();
                mItemAdapter = null;
                updateUI();
                return true;
            case R.id.download_menu_item:
                if (mOnlineLib) {
                    mOnlineLib = false;
                    enableAllMenu(true);
                    mMenu.findItem(R.id.download_menu_item)
                            .setIcon(R.drawable.download_menu_item);
                    updateUI();
                }
                else {
                    if (!Lab.get(getActivity()).loggedIn()) {
                        askToLogIn();
                    }
                    else {
                        mOnlineLib = true;
                        enableAllMenu(false);
                        mMenu.findItem(R.id.download_menu_item)
                                .setVisible(true);
                        mMenu.findItem(R.id.download_menu_item)
                                .setIcon(R.drawable.check_menu_item);
                        updateUI();
                    }
                }

                return true;
            case R.id.upload_menu_item:
                if (!Lab.get(getActivity()).loggedIn()) {
                    askToLogIn();
                }
                else {
                    if (mUploadMode) {
                        mUploadMode = false;
                        enableAllMenu(true);
                        for (UUID mToUpload : mToUploadItems) {
                            //TODO : change it it longer thread
                            UploadBackgroundTask uploadBackgroundTask
                                    = new UploadBackgroundTask();
                            uploadBackgroundTask.execute(mToUpload.toString());
                        }

                    }
                    else {
                        mUploadMode = true;
                        enableAllMenu(false);
                        mMenu.findItem(R.id.upload_menu_item)
                                .setVisible(true);
                        updateUI();
                    }
                    mToUploadItems = new TreeSet<>();
                    mItemAdapter = null;
                    updateUI();

                }
                return true;
            case R.id.edit_item_menu_item:
                if (!mEditMode) {
                    mEditMode = true;
                    enableAllMenu(false);
                    mMenu.findItem(R.id.edit_item_menu_item).setVisible(true);
                }
                else {
                    mEditMode = false;
                    enableAllMenu(true);
                }
                return true;
            case R.id.sort_item_menu_item:
                sortItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //TODO: CONTINUE HERE
    private class UploadBackgroundTask extends AsyncTask<String, Void, String> {

        public static final String UPLOAD_DECK_URL
                = "http://162.243.102.106/android_connect/upload_deck.php";

        public static final String UPLOAD_FAIL
                = "Upload failed! ";
        public static final String UPLOAD_SUCCESS
                = "Upload success! ";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(UPLOAD_DECK_URL);
                HttpURLConnection httpURLConnection
                        = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter
                        = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                JSONObject jsonObject = new JSONObject();

                JSONObject deckJson = new JSONObject();
                Deck deck = (Deck)Lab.get(getActivity())
                        .getItem(UUID.fromString(params[0]), DeckTable.NAME);
                deckJson.put("pid", deck.getPID().toString());
                deckJson.put("id", deck.getID().toString());
                //TODO: change to Long in server db
                deckJson.put("date", deck.getDateCreated().getTime());
                deckJson.put("name", deck.getName());
                deckJson.put("num_cards", deck.getNumCards());
                deckJson.put("creator", Lab.get(getActivity()).getUserId());

                jsonObject.put("deck", deckJson);

                JSONArray cardsJson = new JSONArray();
                List<VIVIDItem> cards
                        = Lab.get(getActivity()).getItems(null, deck.getID(), CardTable.NAME);
                for (VIVIDItem card : cards) {
                    JSONObject cardJson = new JSONObject();
                    Card c = (Card)card;
                    cardJson.put("pid", c.getPID().toString());
                    cardJson.put("id", c.getID().toString());
                    //TODO: change to Long in server db
                    cardJson.put("date", c.getDateCreated().getTime());
                    cardJson.put("name", c.getName());
                    cardJson.put("detail", c.getDetail());
                    cardJson.put("color", c.getColor());
                    //TODO: save image
                    cardJson.put("image", c.getImagePath());
                    //TODO: change to Long in server db
                    cardJson.put("last_visit_date", c.getLastVisitDate().getTime());
                    cardJson.put("num_forget", c.getNumForget());
                    cardJson.put("num_forget_over_dates", c.getNumForgetOverNumDates());
                    cardJson.put("creator", Lab.get(getActivity()).getUserId());

                    cardsJson.put(cardJson);
                }

                jsonObject.put("cards", cardsJson);

                String json_string = jsonObject.toString();
                String data
                        = URLEncoder.encode("json_string", "UTF-8") +
                                "=" + URLEncoder.encode(json_string, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader
                        = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));
                String response = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }
                bufferedReader.close();
                IS.close();
                httpURLConnection.disconnect();
                return response;
            } catch (MalformedInputException e) {
                e.printStackTrace();
                return UPLOAD_FAIL + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return UPLOAD_FAIL + e.getMessage();
            } catch (JSONException e) {
                e.printStackTrace();
                return UPLOAD_FAIL + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getActivity(), result, Toast.LENGTH_LONG);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private void updateListView(List<VIVIDItem> items) {
        mNumItems = items.size();
        if (mItemAdapter == null) {
            mItemAdapter = new ItemAdapter(items);
            mRecyclerView.setAdapter(mItemAdapter);
        }
        else {
            mItemAdapter.setItems(items);
            mItemAdapter.notifyDataSetChanged();
        }
    }

    private void updateUI() {
        if (mOnlineLib) {
            DownloadBackgroundTask backgroundTask = new DownloadBackgroundTask();
            backgroundTask.execute(DownloadBackgroundTask.JSON_DECKS_URL);
        }
        else {
            updateListView(Lab.get(getActivity()).getItems(mSortCategory, mPID, mTableName));
        }

    }



    private class DownloadBackgroundTask extends AsyncTask<String, Void, String> {

        public static final String JSON_DECKS_URL
                = "http://162.243.102.106/android_connect/json_get_decks.php";
        public static final String JSON_CARDS_URL
                = "http://162.243.102.106/android_connect/json_get_cards.php";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection httpURLConnection
                        = (HttpURLConnection)url.openConnection();
                InputStream inputStream
                        = httpURLConnection.getInputStream();
                BufferedReader bufferedReader
                        = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String json_string;
                while ((json_string = bufferedReader.readLine()) != null) {
                    stringBuilder.append(json_string + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            updateListView(Lab.get(getActivity()).parseJSONtoDeck(result));
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }


    private class ItemHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        private VIVIDItem mItem;

        private ImageView mImageView;
        private TextView mNameTextView;
        private TextView mDateTextView;
        private TextView mDetailTextView;
        public CheckBox mToDeleteCheckBox;
        private int mPosition;

        public ItemHolder(View view) {
            super(view);

            view.setOnClickListener(this);
            view.setLongClickable(true);
            view.setOnLongClickListener(this);
            mImageView = (ImageView) view.findViewById(R.id.list_item_image_view);

            mNameTextView = (TextView) view.findViewById(R.id.list_item_name_text_view);
            mDateTextView = (TextView) view.findViewById(R.id.list_item_date_text_view);
            mDetailTextView = (TextView) view.findViewById(R.id.list_item_detail_text_view);
            mToDeleteCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_check_box);
            if (mDeleteMode || mUploadMode) {
                mToDeleteCheckBox.setClickable(false);

            }
            else {
                mToDeleteCheckBox.setVisibility(View.GONE);
            }
        }
        private void editDialog() {
            mToEdit = mItem.getID();
            if (mTableName.equals(CardTable.NAME)) {
                Card card = (Card) mItem;
                FragmentManager manager = getFragmentManager();
                NewCardDialog dialog
                        = new NewCardDialog().newInstance(
                        card.getName(),
                        card.getDetail(),
                        card.getColor(),
                        card.getImagePath());
                dialog.setTargetFragment(VIVIDItemListFragment.this, REQUEST_EDIT_ITEM_CODE);
                dialog.show(manager, EDIT_ITEM);
            }
            else if (mTableName.equals(DeckTable.NAME)) {
                FragmentManager manager = getFragmentManager();
                NewVIVIDItemDialog dialog = new NewVIVIDItemDialog().newInstance(
                        NewVIVIDItemDialog.DECK_EDIT_DIALOG, mItem.getName());
                dialog.setTargetFragment(VIVIDItemListFragment.this, REQUEST_EDIT_ITEM_CODE);
                dialog.show(manager, EDIT_ITEM);
            }
            else {
                FragmentManager manager = getFragmentManager();
                NewVIVIDItemDialog dialog = new NewVIVIDItemDialog().newInstance(
                        NewVIVIDItemDialog.DIR_EDIT_DIALOG, mItem.getName());
                dialog.setTargetFragment(VIVIDItemListFragment.this, REQUEST_EDIT_ITEM_CODE);
                dialog.show(manager, EDIT_ITEM);
            }
        }

        @Override
        public boolean onLongClick (View v) {
            editDialog();
            return true;
        }

        @Override
        public void onClick(View v) {
            if (mUploadMode) {
                if (mToDeleteCheckBox.isChecked()) {
                    mToDeleteCheckBox.setChecked(false);
                    mToUploadItems.remove(mItem.getID());
                }
                else {
                    mToDeleteCheckBox.setChecked(true);
                    mToUploadItems.add(mItem.getID());
                }
            }
            else if (mDeleteMode) {
                if (mToDeleteCheckBox.isChecked()) {
                    mToDeleteCheckBox.setChecked(false);
                    mToDeleteItems.remove(mItem.getID());
                }
                else {
                    if (mToDeleteItems.size() == MAX_UPLOAD_NUM) {
                        Dialog d = new AlertDialog
                                .Builder(getActivity())
                                .setTitle("The number of selected decks exceeds" +
                                        " the maximum number of decks can be uploaded at a time..")
                                .setPositiveButton(android.R.string.ok, null)
                                .create();
                        d.show();
                    }
                    else {
                        mToDeleteCheckBox.setChecked(true);
                        mToDeleteItems.add(mItem.getID());
                    }

                }
            }
            else if (mEditMode) {
                editDialog();
            }
            else if (mTableName.equals(CardTable.NAME)) {
                Intent intent = CardActivity.newIntent(
                        getActivity(),
                        mPosition,
                        ((Card)mItem).getPID(),
                        mSortCategory);
                startActivity(intent);
            }
            else {
                Intent intent = VIVIDItemListActivity.newIntent(
                        getActivity(),
                        mItem.getID(),
                        Lab.get(getActivity()).getChildTableName(mTableName));
                startActivity(intent);
            }

        }
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
        List<VIVIDItem> mItems;

        public ItemAdapter(List<VIVIDItem> items) {
            mItems = items;
        }

        public void setItems(List<VIVIDItem> items) {
            mItems = items;
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            holder.mItem = mItems.get(position);
            if (mTableName.equals(CardTable.NAME)) {
                holder.mImageView.setImageResource(R.drawable.card_image);
            }
            else if (mTableName.equals(DeckTable.NAME)) {
                holder.mImageView.setImageResource(R.drawable.deck_image);
            }

            holder.mNameTextView.setText(holder.mItem.getName());
            holder.mDateTextView.setText(holder.mItem.getDateCreated().toString());
            String detail;

            if (mTableName.equals(CardTable.NAME)) {
                detail = ((Card)holder.mItem).getDetail();
            }
            else if (mTableName.equals(DeckTable.NAME)) {
                int numCards = ((Deck)holder.mItem).getNumCards();
                detail = Integer.toString(numCards) + " card";
                if (numCards > 1) {
                    detail += "s";
                }
            }
            else {
                int numDeck = ((Directory)holder.mItem).getNumDecks();
                detail = Integer.toString(numDeck) + " deck";
                if (numDeck > 1) {
                    detail += "s";
                }
            }
            holder.mDetailTextView.setText(detail);

            if (mDeleteMode) {
                if (mToDeleteItems.contains(holder.mItem.getID())) {
                    holder.mToDeleteCheckBox.setChecked(true);
                }
                else {
                    holder.mToDeleteCheckBox.setChecked(false);
                }
            }

            if (mUploadMode) {
                if (mToUploadItems.contains(holder.mItem.getID())) {
                    holder.mToDeleteCheckBox.setChecked(true);
                }
                else {
                    holder.mToDeleteCheckBox.setChecked(false);
                }
            }

            holder.mPosition = position;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_vivid_item, viewGroup, false);
            return new ItemHolder(view);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_NEW_ITEM_CODE) {
            if (mTableName.equals(CardTable.NAME)) {
                String newCardName = data.getStringExtra(NewCardDialog.EXTRA_NEW_CARD_NAME);
                String newCardDetail = data.getStringExtra(NewCardDialog.EXTRA_NEW_CARD_DETAIL);
                int newCardColor = data.getIntExtra(NewCardDialog.EXTRA_NEW_CARD_COLOR, 0);
                Bitmap scaledImage = (Bitmap)data.getParcelableExtra(
                        NewCardDialog.EXTRA_NEW_CARD_ORIGINAL_IMAGE);
                Bitmap originalImage = (Bitmap)data.getParcelableExtra(
                        NewCardDialog.EXTRA_NEW_CARD_SCALED_IMAGE);
                Lab.get(getActivity()).addItem(
                        new Card(newCardName, newCardDetail, newCardColor,
                                scaledImage, originalImage, mPID), CardTable.NAME);
            } else if (mTableName.equals(DeckTable.NAME)) {
                String newDeckName = data.getStringExtra(NewVIVIDItemDialog.EXTRA_NEW_ITEM_NAME);
                Lab.get(getActivity()).addItem(new Deck(newDeckName, mPID), DeckTable.NAME);
            } else {
                String newDirName = data.getStringExtra(NewVIVIDItemDialog.EXTRA_NEW_ITEM_NAME);
                Lab.get(getActivity()).addItem(new Directory(newDirName), DirectoryTable.NAME);
            }
        }

        else if (requestCode == REQUEST_EDIT_ITEM_CODE) {
            VIVIDItem vividItem = Lab.get(getActivity()).getItem(mToEdit, mTableName);
            if (mTableName.equals(CardTable.NAME)) {
                Card card = (Card)vividItem;
                card.setName(data.getStringExtra(NewCardDialog.EXTRA_NEW_CARD_NAME));
                card.setDetail(data.getStringExtra(NewCardDialog.EXTRA_NEW_CARD_DETAIL));
                card.setColor(data.getIntExtra(NewCardDialog.EXTRA_NEW_CARD_COLOR, 0));
                if (data.getBooleanExtra(NewCardDialog.EXTRA_IMAGE_CHANGED, false)) {
                    card.setScaledImage(
                            (Bitmap)data.getParcelableExtra(
                                    NewCardDialog.EXTRA_NEW_CARD_SCALED_IMAGE));
                    card.setOriginalImage(
                            (Bitmap)data.getParcelableExtra(
                                    NewCardDialog.EXTRA_NEW_CARD_ORIGINAL_IMAGE));
                }

                Lab.get(getActivity()).updateItem(card, CardTable.NAME);
            }
            else if (mTableName.equals(DeckTable.NAME)) {
                Deck deck = (Deck)vividItem;
                deck.setName(data.getStringExtra(NewVIVIDItemDialog.EXTRA_NEW_ITEM_NAME));
                Lab.get(getActivity()).updateItem(deck, DeckTable.NAME);
            }
            else {
                Directory directory = (Directory)vividItem;
                directory.setName(data.getStringExtra(NewVIVIDItemDialog.EXTRA_NEW_ITEM_NAME));
                Lab.get(getActivity()).updateItem(directory, DirectoryTable.NAME);
            }
            mEditMode = false;
            enableAllMenu(true);
        }
        else if (requestCode == REQUEST_SORT_ITEM_CODE) {
            mSortCategory = (SortVIVIDItemDialog.SortCategory)data.getSerializableExtra(
                    SortVIVIDItemDialog.EXTRA_SORT_CATEGORY);
        }
        else if (requestCode == REQUEST_LOGIN_CODE) {
            //TODO:save user name
            if (data.getIntExtra(LogInDialog.EXTRA_AUTHORIZED, 0)
                    == LogInDialog.LOGGED_IN) {
                Lab.get(getActivity()).logIn(true);
                Lab.get(getActivity()).setUserId(data.getStringExtra(LogInDialog.EXTRA_USER_ID));
            }
            else if (data.getIntExtra(LogInDialog.EXTRA_AUTHORIZED, 0)
                    == LogInDialog.REGISTER)  {
                FragmentManager manager = getFragmentManager();
                RegisterDialog dialog = new RegisterDialog();
                dialog.setTargetFragment(VIVIDItemListFragment.this, REQUEST_REGISTER_CODE);
                dialog.show(manager, REGISTER);
            }
        }
        else if (requestCode == REQUEST_REGISTER_CODE) {
            showLogInDialog();
        }
        updateUI();
    }
}
