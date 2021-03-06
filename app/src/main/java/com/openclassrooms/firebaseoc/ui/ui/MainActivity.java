package com.openclassrooms.firebaseoc.ui.ui;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.openclassrooms.firebaseoc.R;
import com.openclassrooms.firebaseoc.databinding.ActivityMainBinding;
import com.openclassrooms.firebaseoc.ui.manager.UserManager;
import com.openclassrooms.firebaseoc.ui.ui.chat.MentorChatActivity;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    // private FirebaseAnalytics mFirebaseAnalytics;
    private static final int RC_SIGN_IN = 123;
    private UserManager userManager = UserManager.getInstance();

    @Override
    protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // TODO : Voir Doc Analytics pour vérifier l'utilisation des utilisateurs https://firebase.google.com/docs/analytics/events?hl=fr&authuser=0&platform=android
        // mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        /*

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        */
        setupListeners();
    }

    private void setupListeners(){
        //Login/Profile Button
        binding.loginButton.setOnClickListener(view -> {
            if(userManager.isCurrentUserLogged()){
                startProfileActivity();
            }else{
                startSignInActivity();
            }
        });

        // Chat Button
        binding.chatButton.setOnClickListener(view -> {
            if(userManager.isCurrentUserLogged()){
                startMentorChatActivity();
            }else{
                showSnackBar(getString(R.string.error_not_connected));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLoginButton();
    }

    // Launching Profile Activity
    private void startProfileActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    // Launch Mentor Chat Activity
    private void startMentorChatActivity(){
        Intent intent = new Intent(this, MentorChatActivity.class);
        startActivity(intent);
    }


    // Update Login Button when activity is resuming
    private void updateLoginButton(){
        binding.loginButton.setText(userManager.isCurrentUserLogged() ? getString(R.string.button_login_text_logged) : getString(R.string.button_login_text_not_logged));
    }

    private void startSignInActivity(){

        // Choose authentication providers
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build());

        // Launch the activity
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_logo_auth)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    // Show Snack Bar with a message
    private void showSnackBar( String message){
        Snackbar.make(binding.mainLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            // SUCCESS
            if (resultCode == RESULT_OK) {
                userManager.createUser();
                showSnackBar(getString(R.string.connection_succeed));
            } else {
                // ERRORS
                if (response == null) {
                    showSnackBar(getString(R.string.error_authentication_canceled));
                } else if (response.getError()!= null) {
                    if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                        showSnackBar(getString(R.string.error_no_internet));
                    } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        showSnackBar(getString(R.string.error_unknown_error));
                    }
                }
            }
        }
    }

}