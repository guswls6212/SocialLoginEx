package connect.leehyun.com.sociallogin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Google: https://firebase.google.com/docs/auth/android/google-signin?hl=ko (20190106 Sun)
 * */
public class LoginActivity extends FragmentActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    /**
     * Google Login
     */
    private GoogleApiClient mGoogleApiClient;
    static final int AFTER_SIGN_IN = 1000;
    static final int GOOGLE_SIGN_IN = 2000;
    private final String TAG = "LoginActivity";

    /**
     * Facebook Login
     */
    static final int FACEBOOK_SIGN_IN = 3000;
    private CallbackManager callbackManager;


    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Setting Google login
        setGoogleLogin();
        //Setting Facebook login
        setFacebookLogin();


    }

    /**
     * All Click Event Process
     * @param v
     */
    @Override
    public void onClick(View v) {
        Log.d(TAG,"onClick()");
        switch(v.getId()){
            case R.id.google_sign_in_button:
                googleSignIn();
                break;
             default:
                break;
        }
    }

    /**
     * Google Sign in process
     */
    private void googleSignIn() {
        Log.d(TAG,"googleSignIn()");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    /**
     * Google Sign fail process
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG,"onConnectionFailed()");
    }

    /**
     * Setting GoogleLogin
     */
    private void setGoogleLogin() {
        Log.d(TAG,"setGoogleLogin()");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);

        findViewById(R.id.google_sign_in_button).setOnClickListener(this);


    }

    /**
     * Result After login
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,"onActivityResult(requestCode: "+requestCode+", resultCode: "+resultCode+", data: "+data+")");
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = null;

        switch (requestCode){
            case GOOGLE_SIGN_IN:
                Log.d(TAG,"requestCode GOOGLE_SIGN_IN");
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("social", GOOGLE_SIGN_IN);

                break;
            //Logout Process
            case AFTER_SIGN_IN:
                Log.d(TAG,"requestCode AFTER_SIGN_IN");
                if(resultCode == GOOGLE_SIGN_IN){
                     Log.d(TAG,"resultCode GOOGLE_SIGN_IN");
                    if(mGoogleApiClient!=null){
                        if(mGoogleApiClient.isConnected()){
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                        }
                    }
                }else if(resultCode == FACEBOOK_SIGN_IN){
                    Log.d(TAG,"resultCode FACEBOOK_SIGN_IN");
                }
                break;
            default:
                break;
        }

        if(intent!=null){
            startActivityForResult(intent, AFTER_SIGN_IN);
        }
    }

    /**
     * Handle Sign in result
     * @param result
     */
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG,"setGoogleLogin(GoogleSignInResult: "+ result +")");
        if(result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.i(TAG, "email:" + acct.getEmail());
            Log.i(TAG, "id:" + acct.getId());
            Log.i(TAG, "profile:" + acct.getPhotoUrl());
            Log.i(TAG, "DispName > " + acct.getDisplayName());
        }
    }


    private void setFacebookLogin(){
        Log.d(TAG,"setFacebookLogin()");
        Intent intent = null;

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if(isLoggedIn){
            intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("social", FACEBOOK_SIGN_IN);
        }



        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d(TAG,"onSuccess(LoginResult: "+ loginResult +")");
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("social", FACEBOOK_SIGN_IN);

                        if(intent!=null){
                            startActivityForResult(intent, AFTER_SIGN_IN);
                        }
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d(TAG,"onCancel()");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d(TAG,"onError(FacebookException: "+ exception +")");
                    }
                });
    }

}
