package com.fstprj.teejayc.vividcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.MalformedInputException;

public class RegisterDialog extends DialogFragment {
    public static final String EXTRA_AUTHORIZED
            = "com.taejungchang.vividcard.online";

    private EditText mName;
    private EditText mID;
    private EditText mPassword;
    private EditText mRetypedPassword;
    private AlertDialog mAlertDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_register, null);

        mName = (EditText) view.findViewById(R.id.new_user_name_edit_text);
        mID = (EditText) view.findViewById(R.id.new_user_id_edit_text);
        mPassword = (EditText) view.findViewById(R.id.new_password_edit_text);
        mRetypedPassword = (EditText) view.findViewById(R.id.new_password_retry_edit_text);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Log In:")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }


    @Override
    public void onStart() {
        super.onStart();
        mAlertDialog = (AlertDialog)getDialog();
        if (mAlertDialog != null) {
            Button positiveButton = mAlertDialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = mName.getText().toString();
                    String id = mID.getText().toString();
                    String password = mPassword.getText().toString();
                    String retypedPassword = mRetypedPassword.getText().toString();

                    if (name.isEmpty() ||
                            id.isEmpty() ||
                            password.isEmpty() ||
                            retypedPassword.isEmpty()) {
                        Toast.makeText(getActivity(), "Please fill out all the criteria",
                                Toast.LENGTH_LONG).show();
                    }
                    else if (!password.equals(retypedPassword)) {
                        Toast.makeText(getActivity(),
                                "Passwords do not match!",
                                Toast.LENGTH_LONG).show();
                    }
                    else {
                        BackgroundTask background = new BackgroundTask();
                        background.execute(name, id, password);
                    }
                }
            });
        }
    }

    private void sendResult(int resultCode, Boolean authorized) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_AUTHORIZED, authorized);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    private class BackgroundTask extends AsyncTask<String, Void, String> {
        private static final String REGISTER_URI
                = "http://162.243.102.106/android_connect/register.php";

        private static final String REGISTER_SUCCESS = "Registration success!";
        private static final String REGISTER_FAIL = "Registration failed!";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
//todo : make sure register once
        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String id = params[1];
            String password = params[2];
            try {
                URL url = new URL(REGISTER_URI);
                HttpURLConnection httpURLConnection
                        = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter
                        = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&" +
                        URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8") + "&" +
                        URLEncoder.encode("user_pass", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));
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
                return REGISTER_FAIL + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return REGISTER_FAIL + e.getMessage();
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.regionMatches(
                    0, BackgroundTask.REGISTER_SUCCESS,
                    0, BackgroundTask.REGISTER_SUCCESS.length())) {
                sendResult(Activity.RESULT_OK, true);
                mAlertDialog.dismiss();
            }
            Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
        }
    }



}
