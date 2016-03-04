package com.fstprj.teejayc.vividcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class LogInDialog extends DialogFragment {
    public static final String EXTRA_AUTHORIZED
            = "com.taejungchang.vividcard.online";
    public static final String EXTRA_USER_ID
            = "com.taejungchang.vividcard.user_id";

    public static final int LOGGED_IN = 1;
    public static final int LOGIN_FAIL = 0;
    public static final int REGISTER = 2;

    private EditText mUserId;
    private EditText mPassword;
    private AlertDialog mAlertDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_log_in, null);

        mUserId = (EditText) view.findViewById(R.id.user_id_edit_text);

        mPassword = (EditText) view.findViewById(R.id.password_edit_text);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Log In:")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNeutralButton("Register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK, REGISTER, null);
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
                    String id = mUserId.getText().toString();
                    String password = mPassword.getText().toString();

                    if (id.isEmpty() ||
                            password.isEmpty()) {
                        Toast.makeText(getActivity(), "Please fill out all the criteria",
                                Toast.LENGTH_LONG).show();
                    }
                    else {
                        BackgroundTask background = new BackgroundTask();
                        background.execute(id, password);
                    }
                }
            });
        }
    }

    private void sendResult(int resultCode, int response, String user_id) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_AUTHORIZED, response);
        intent.putExtra(EXTRA_USER_ID, user_id);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    private class BackgroundTask extends AsyncTask<String, Void, String> {

        private static final String LOGIN_URI
                = "http://162.243.102.106/android_connect/login.php";

        private static final String LOGIN_SUCCESS = "Login success! ";
        private static final String LOGIN_FAIL = "Login failed! ";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
//TODO : make sure log in once
        @Override
        protected String doInBackground(String... params) {
            String id = params[0];
            String password = params[1];
            try {
                URL url = new URL(LOGIN_URI);
                HttpURLConnection httpURLConnection
                        = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8") + "&" +
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
                return LOGIN_FAIL + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return LOGIN_FAIL + e.getMessage();
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        private String getUserId(String result) {
            return result.substring(22, result.length() - 1);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.regionMatches(
                    0, BackgroundTask.LOGIN_SUCCESS,
                    0, BackgroundTask.LOGIN_SUCCESS.length())) {
                sendResult(Activity.RESULT_OK, LOGGED_IN, getUserId(result));
                mAlertDialog.dismiss();
            }
            Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
        }
    }

}
