package com.example.android.socialintegration;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    LoginButton loginButton;
    TextView profileName;
    ImageView profilePic;
    FirebaseAuth firebaseAuth;
    TwitterLoginButton twitterLoginButton;
  //  private FirebaseAuth mAuth;
/*// ...
@Override
public void onStart() {
    super.onStart();
    // Check if user is signed in (non-null) and update UI accordingly.
    FirebaseUser currentUser = mAuth.getCurrentUser();
    updateUi(currentUser);
}*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Twitter.initialize(this);
        setContentView(R.layout.activity_main);

// Initialize Firebase Auth
     /*   mAuth = FirebaseAuth.getInstance();
            FacebookSdk.sdkInitialize(getApplicationContext());*/
//    AppEventsLogger.activateApp(this);
            profileName=findViewById(R.id.profile_name);
            profilePic=findViewById(R.id.profile_pic);
            twitterLoginButton=findViewById(R.id.twitter_login_button);
            loginButton=findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");//, "user_friends");

        callbackManager=CallbackManager.Factory.create();

      //  loginButton.setReadPermissions("email", "public_profile", "user_friends");
                    LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Log.v("activity",loginResult.toString()+" successs ");

                            Toast.makeText(getApplicationContext(),"CALLBACK SUCCESSFUL", Toast.LENGTH_SHORT).show();

                            handleFacebookToken(loginResult.getAccessToken());

                            loginResult.getAccessToken().getUserId();

                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(getApplicationContext(),"CALLBACK CANCELLED", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(FacebookException error) {

                            Log.v("activity",error.toString()+" error ");
                            Toast.makeText(getApplicationContext(),"CALLBACK ERROR"+error.toString(), Toast.LENGTH_LONG).show();
                        }
                    });

                    twitterLoginButton.setCallback(new Callback<TwitterSession>() {
                        @Override
                        public void success(Result<TwitterSession> result) {
                            TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                            TwitterAuthToken authToken = session.getAuthToken();
                            String token,secret;
                            token=authToken.token;
                            secret=authToken.secret;
                            login(session);

                            Log.d("mainActivity", "twitterLogin:success" + result);
                           // handleTwitterSession(result.data);


                        }

                        @Override
                        public void failure(TwitterException exception) {
                            Toast.makeText(MainActivity.this,"Authentication failed",Toast.LENGTH_SHORT).show();

                        }
                    });
        }
   /* private void handleTwitterSession(TwitterSession session) {
        Log.d("MainActivity", "handleTwitterSession:" + session);

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("mainActi", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUi(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("mainActi", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUi(null);
                        }

                        // ...
                    }
                });
    }*/
    public void login(TwitterSession session)
{
String username = session.getUserName();
Intent i = new Intent(MainActivity.this,ProfileActivity.class);
i.putExtra("username",username);
startActivity(i);
}
    private void handleFacebookToken(AccessToken accessToken) {

        AuthCredential authCredential= FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(),"able to register to firebase", Toast.LENGTH_SHORT).show();

                    FirebaseUser myuser= firebaseAuth.getCurrentUser();
                    updateUi(myuser);

                }
                else

                    Toast.makeText(getApplicationContext(),"unable to register to firebase", Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
        Toast.makeText(getApplicationContext(),"activity result", Toast.LENGTH_SHORT).show();

        twitterLoginButton.onActivityResult(requestCode,resultCode,data);

    }

    private void updateUi(FirebaseUser myuser) {
        Toast.makeText(getApplicationContext(),"update ui", Toast.LENGTH_SHORT).show();


        profilePic.setImageURI(myuser.getPhotoUrl());
        profileName.setText(myuser.getDisplayName());
    }
}


