/*
 * Copyright (C) 2010 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.andy.demo.authenticator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andy.android.util.AsyncFramework;
import com.andy.demo.R;
import com.andy.demo.base.Constant;
import com.andy.demo.utils.CommonUtils;
import com.andy.demo.utils.SimpleDES;

/**
 * Activity which displays login screen to the user.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity {
    public static final String PARAM_CONFIRMCREDENTIALS = "confirmCredentials";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

    private static final String TAG = "AuthenticatorActivity";

    private AccountManager mAccountManager;
    private String mAuthtoken;
    private String mAuthtokenType;

    private TextView mExistedTv;

    /**
     * If set we are just checking that the user knows their credentials; this
     * doesn't cause the user's password to be changed on the device.
     */
    private Boolean mConfirmCredentials = false;

    /**
     * for posting authentication attempts back to UI thread
     */
    private String mPassword;
    private EditText mPasswordEdit;

    /**
     * Was the original caller asking for an entirely new account?
     */
    protected boolean mRequestNewAccount = false;

    private String mUsername;
    private EditText mUsernameEdit;

    // add by udb
    private AuthTask mAuthTask;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle icicle) {
        Log.i(TAG, "onCreate(" + icicle + ")");
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mAccountManager = AccountManager.get(this);
        Log.i(TAG, "loading data from Intent");
        final Intent intent = getIntent();
        mUsername = intent.getStringExtra(PARAM_USERNAME);
        mAuthtokenType = intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);
        mRequestNewAccount = mUsername == null;
        mConfirmCredentials = intent.getBooleanExtra(PARAM_CONFIRMCREDENTIALS,
                false);

        Log.i(TAG, "    request new: " + mRequestNewAccount);
        //requestWindowFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.authenticator_layout);
        /*getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
				android.R.drawable.ic_dialog_alert);*/

        mUsernameEdit = (EditText) findViewById(R.id.authenticator_account_et);
        mPasswordEdit = (EditText) findViewById(R.id.authenticator_password_et);

        mExistedTv = (TextView) findViewById(R.id.account_tv);
        if (!TextUtils.isEmpty(CommonUtils.getAccount(this, Constant.ACCOUNT_TYPE))) {
            mExistedTv.setText(CommonUtils.getAccount(this, Constant.ACCOUNT_TYPE));
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("loading...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Log.i(TAG, "dialog cancel has been invoked");
                if (mAuthTask != null) {
                    mAuthTask.cancel();
                }
            }
        });
        return dialog;
    }

    /**
     * Handles onClick event on the Submit button. Sends username/password to
     * the server for authentication.
     *
     * @param view The Submit button for which this method is invoked
     */
    public void handleLogin(View view) {
        if (mRequestNewAccount) {
            mUsername = mUsernameEdit.getText().toString();
        }
        mPassword = mPasswordEdit.getText().toString();
        if (TextUtils.isEmpty(mUsername)) {
            Toast.makeText(this, "username is invalid", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mPassword)) {
            Toast.makeText(this, "password is invalid", Toast.LENGTH_SHORT).show();
        } else {
            mAuthTask = new AuthTask();
            mAuthTask.execute(mUsername, mPassword);
        }
    }

    /**
     * Called when response is received from the server for confirm credentials
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller.
     *
     * @param the confirmCredentials result.
     */
    protected void finishConfirmCredentials(boolean result) {
        Log.i(TAG, "finishConfirmCredentials()");
        final Account account = new Account(mUsername, Constant.ACCOUNT_TYPE);
        mAccountManager.setPassword(account, SimpleDES.getEncString(mPassword));
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, result);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Called when response is received from the server for authentication
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller. Also sets
     * the authToken in AccountManager for this account.
     *
     * @param the confirmCredentials result.
     */

    protected void finishLogin() {
        Log.i(TAG, "finishLogin()");
        final Account account = new Account(mUsername, Constant.ACCOUNT_TYPE);

        String encryptedPassword = SimpleDES.getEncString(mPassword);
        if (mRequestNewAccount) {
            mAccountManager.addAccountExplicitly(account, encryptedPassword, null);
            // Set contacts sync for this account.
            // ContentResolver.setSyncAutomatically(account,
            // ContactsContract.AUTHORITY, true);
        } else {
            mAccountManager.setPassword(account, encryptedPassword);
        }
        final Intent intent = new Intent();
        mAuthtoken = encryptedPassword;
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUsername);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constant.ACCOUNT_TYPE);
        if (mAuthtokenType != null
                && mAuthtokenType.equals(Constant.AUTHTOKEN_TYPE)) {
            intent.putExtra(AccountManager.KEY_AUTHTOKEN, mAuthtoken);
        }
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Hides the progress UI for a lengthy operation.
     */
    protected void hideProgress() {
        dismissDialog(0);
    }

    /**
     * Called when the authentication process completes (see attemptLogin()).
     */
    public void onAuthenticationResult(boolean result) {
        Log.i(TAG, "onAuthenticationResult(" + result + ")");
        // Hide the progress dialog
        hideProgress();
        if (result) {
            if (!mConfirmCredentials) {
                finishLogin();
            } else {
                finishConfirmCredentials(true);
            }
        } else {
            Log.e(TAG, "onAuthenticationResult: failed to authenticate");
            Toast.makeText(this, "authenticate failed", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Shows the progress UI for a lengthy operation.
     */
    protected void showProgress() {
        showDialog(0);
    }

    class AuthTask extends AsyncFramework<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = false;
            String username = params[0];
            String password = params[1];
            try {
                Thread.sleep(3000);
                result = true;
            } catch (Exception e) {
                result = false;
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            onAuthenticationResult(result);
        }
    }

}
