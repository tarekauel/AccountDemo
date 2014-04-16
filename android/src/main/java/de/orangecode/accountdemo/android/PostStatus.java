package de.orangecode.accountdemo.android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PostStatus extends Activity {

    private Context mContext;
    private Activity mActivity;
    private EditText mStatusET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;
        setContentView(R.layout.activity_post_status);
        mStatusET = (EditText) findViewById(R.id.status);
    }

    public void postStatus(View view) {
        final String status = mStatusET.getText().toString();
        final AccountManager accountManager = AccountManager.get(this);
        final Account[] accounts = accountManager.getAccountsByType(DemoAccountAuthenticator.ACCOUNT_TYPE);
        if (accounts.length == 0) {
            Toast.makeText(this, "Register Account first!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginUser.class));
        } else if (accounts.length == 1) {
            new PostStatusTask().execute(accounts[0], status);
        } else {
            final String[] accountNames = new String[accounts.length];
            for (int i = 0; i < accounts.length; i++) {
                accountNames[i] = accounts[i].name;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setItems(accountNames, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new PostStatusTask().execute(accounts[i], status);
                }
            });
            builder.show();
        }
    }

    public void showStatus(View view) {
        Intent intent = new Intent(this, ShowStatus.class);
        startActivity(intent);
    }

    public void gotoOptions(View view) {
        Intent intent = new Intent(this, Prefs.class);
        startActivity(intent);
    }

    private class PostStatusTask extends AsyncTask<Object, Void, String> {
        private Account account;
        private String token;
        private String status;

        @Override
        protected String doInBackground(Object... objects) {
            try {
                account = (Account) objects[0];
                status = (String) objects[1];
                AccountManager accountManager = AccountManager.get(mContext);
                AccountManagerFuture<Bundle> future = accountManager.getAuthToken(account, DemoAccountAuthenticator.AUTH_TOKEN_TYPE_FULL, null, mActivity, null, null);
                token = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
                String json = "{\"token\":\""+token+"\", \"status\":\""+status+"\"}";
                return NetworkHelper.doRequest(mContext, "status", "POST", json);

            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String firstLine) {
            if (firstLine == null) {
                Toast.makeText(mContext, "Posting status failed", Toast.LENGTH_LONG).show();
                return;
            }
            if (firstLine.equals(NetworkHelper.INVALID_TOKEN)) {
                AccountManager.get(mContext).invalidateAuthToken(DemoAccountAuthenticator.ACCOUNT_TYPE, token);
                new PostStatusTask().execute(account, status);
                return;
            }
            try {
                if (firstLine.equals("{\"success\":1}")) {
                    Toast.makeText(mContext, "Status successfully posted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, "Posting status failed", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(mContext, "Posting status failed", Toast.LENGTH_LONG).show();
            }
        }
    }

}
