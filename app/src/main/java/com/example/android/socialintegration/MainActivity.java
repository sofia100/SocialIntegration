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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    CallbackManager callbackManager;
    LoginButton loginButton;
    TextView profileName;
    ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser==null)
        {
            FacebookSdk.sdkInitialize(getApplicationContext());
//    AppEventsLogger.activateApp(this);
            profileName=findViewById(R.id.profile_name);
            profilePic=findViewById(R.id.profile_pic);
            loginButton=findViewById(R.id.login_button);
            callbackManager=CallbackManager.Factory.create();
            loginButton.setReadPermissions(Arrays.asList("public_profile"));
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {

                          //  Toast.makeText(getApplicationContext(),"CALLBACK SUCCESSFUL", Toast.LENGTH_SHORT).show();

                            handleFacebookToken(loginResult.getAccessToken());


                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(getApplicationContext(),"CALLBACK CANCELLED", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onError(FacebookException error) {

                            Toast.makeText(getApplicationContext(),"CALLBACK ERROR"+error.toString(), Toast.LENGTH_LONG).show();


                        }
                    });
                }
            });

        }
        else
        {
          updateUi(firebaseUser);
        }
  }

    private void handleFacebookToken(AccessToken accessToken) {

        AuthCredential authCredential= FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
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
    }

    private void updateUi(FirebaseUser myuser) {

        profilePic.setImageURI(myuser.getPhotoUrl());
        profileName.setText(myuser.getDisplayName());
    }
}

