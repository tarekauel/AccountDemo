package de.orangecode.accountdemo.android;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONObject;

public class DemoAccountAuthenticator extends AbstractAccountAuthenticator {

    public static final String ACCOUNT_TYPE = "de.orangecode.accountdemo.DEMO_ACCOUNT_TYPE";
    public static final String AUTH_TOKEN_TYPE_FULL = "FULL_ACCESS";
    public static final String AUTH_TOKEN_PASS_SET = "AUTH_TOKEN_PASS_SET";

    private final Context mContext;

    public DemoAccountAuthenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        final Intent intent = new Intent(mContext, LoginUser.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        Bundle result = new Bundle();
        try {
            AccountManager accountManager = AccountManager.get(mContext);
            String password = accountManager.getPassword(account);
            String json = "{\"username\":\""+account.name+"\", \"password\":\""+password+"\"}";
            String webResponse = NetworkHelper.doRequest(mContext, "token", "POST", json);
            if (webResponse != null) {
                JSONObject responseJSON = new JSONObject(webResponse);
                String token = responseJSON.getString("token");
                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, DemoAccountAuthenticator.ACCOUNT_TYPE);
                result.putString(AccountManager.KEY_AUTHTOKEN, token);
            } else {
                Intent intent = new Intent(mContext, LoginUser.class);
                intent.putExtra(LoginUser.PARAM_USERNAME, account.name);
                intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
                result.putParcelable(AccountManager.KEY_INTENT, intent);
            }
        } catch (Exception e) {
            result.putInt(AccountManager.KEY_ERROR_CODE, AccountManager.ERROR_CODE_CANCELED);
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "Error");
        }
        return result;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        return null;
    }
}
