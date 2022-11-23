package com.rafaelcosio.mathhelper.views.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rafaelcosio.mathhelper.R;
import com.rafaelcosio.mathhelper.database.UserDBH;
import com.rafaelcosio.mathhelper.databinding.ActivityLoginBinding;
import com.rafaelcosio.mathhelper.models.User;
import com.rafaelcosio.mathhelper.utils.Utils;

import java.util.Objects;

public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        binding.customSigninButton.setOnClickListener(v -> signIn(binding.emailEdittext.getText().toString(), binding.passwordEdittext.getText().toString()));
        binding.customSignupButton.setOnClickListener(v -> createAccount(binding.emailEdittext.getText().toString(), binding.passwordEdittext.getText().toString()));
        binding.googleLoginButton.setOnClickListener(v -> signInGoogle());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.error), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        writeNewUser(Objects.requireNonNull(user).getUid(), user.getDisplayName(), user.getEmail(), 1);
                        finish();
                        Utils.openActivity(LoginActivity.this, MainActivity.class);
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.error), Toast.LENGTH_LONG).show();
                    }
                    hideProgressDialog();
                });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    //region Signup With Email

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Utils.openActivity(LoginActivity.this, MainActivity.class);
                        finish();
                        writeNewUser(Objects.requireNonNull(user).getUid(), user.getEmail(), user.getEmail(), 3);
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_email_in_use), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.error), Toast.LENGTH_LONG).show();
                        }
                    }
                    hideProgressDialog();
                });
    }

    //endregion

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //region SignIn With Email
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Utils.openActivity(LoginActivity.this, MainActivity.class);
                        finish();
                        writeNewUser(Objects.requireNonNull(user).getUid(), user.getEmail(), user.getEmail(), 3);
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.error), Toast.LENGTH_LONG).show();
                    }
                    hideProgressDialog();
                });
    }

    //endregion

    private void writeNewUser(String userId, String name, String email, int type) {
        SharedPreferences settings = getSharedPreferences("user_preferences", 0);
        User user = UserDBH.getUser(this, userId);
        if (user == null) {
            user = new User(userId, name, email, 0, 0, type);
            UserDBH.addOrUpdate(this, user);
        }
        Log.d(TAG, user.toString());
        settings.edit().putString("last_user_uid", user.getUid()).apply();
        mDatabase.child("users").child(userId).setValue(user);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = binding.emailEdittext.getText().toString();
        if (TextUtils.isEmpty(email)) {
            binding.emailEdittext.setError(getResources().getString(R.string.required));
            valid = false;
        } else {
            binding.emailEdittext.setError(null);
        }

        String password = binding.passwordEdittext.getText().toString();
        if (TextUtils.isEmpty(password)) {
            binding.passwordEdittext.setError(getResources().getString(R.string.required));
            valid = false;
        } else if (password.length() < 6) {
            binding.passwordEdittext.setError(getResources().getString(R.string.password_short));
            valid = false;
        } else {
            binding.passwordEdittext.setError(null);
        }

        return valid;
    }
}
