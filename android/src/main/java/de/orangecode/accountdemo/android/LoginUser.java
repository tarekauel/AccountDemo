package de.orangecode.accountdemo.android;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginUser extends AccountAuthenticatorActivity {

    private EditText userET;
    private EditText passET;
    private Bundle mResult = new Bundle();
    private Context mContext;

    public static final String PARAM_USERNAME = "PARAM_USERNAME";


    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mContext = this;
        setContentView(R.layout.activity_login_user);

        userET = (EditText) findViewById(R.id.username);
        passET = (EditText) findViewById(R.id.password);

        Intent intent = getIntent();
        if (intent != null) {
            String username = intent.getStringExtra(PARAM_USERNAME);
            if (username != null) {
                userET.setText(username);
                userET.setEnabled(false);
                findViewById(R.id.register).setEnabled(false);
            }
        }
    }

    public void register(View view) {
        String username = userET.getText().toString();
        String password = passET.getText().toString();
        new RegisterUser().execute(username, password);
    }

    private class RegisterUser extends AsyncTask<String, Void, String> {

        private String username;
        private String password;

        @Override
        protected String doInBackground(String... strings) {
           username = strings[0];
           password = strings[1];
           String json = "{\"username\":\""+username+"\", \"password\":\""+password+"\"}";
           return NetworkHelper.doRequest(mContext, "register", "POST", json);
        }

        @Override
        protected void onPostExecute(String firstLine) {
            if (firstLine == null) {
               fail();
            } else {
                try {
                    if (firstLine.equals("{\"success\":1}")) {
                        Account account = new Account(username, DemoAccountAuthenticator.ACCOUNT_TYPE);
                        AccountManager accountManager = AccountManager.get(mContext);
                        if (accountManager.addAccountExplicitly(account, password, null)) {
                            mResult.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                            mResult.putString(AccountManager.KEY_ACCOUNT_TYPE, DemoAccountAuthenticator.ACCOUNT_TYPE);
                            Toast.makeText(mContext, "User wurde registriert", Toast.LENGTH_LONG).show();
                        } else {
                            fail();
                        }
                    } else {
                        fail();
                    }
                } catch (Exception e) {
                    fail();
                }
            }

            setAccountAuthenticatorResult(mResult);
            setResult(RESULT_OK);
            finish();
        }

        private void fail() {
            mResult.putInt(AccountManager.KEY_ERROR_CODE, AccountManager.ERROR_CODE_CANCELED);
            mResult.putString(AccountManager.KEY_ERROR_MESSAGE, "Error");
            Toast.makeText(mContext, "Fehler beim erstellen des Users", Toast.LENGTH_LONG).show();
        }
    }
}
