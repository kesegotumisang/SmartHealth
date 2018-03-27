package bw.ub.cs.smarthealth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{


        private EditText inputEmail, inputPassword;
        private FirebaseAuth auth;
        private ProgressBar progressBar;
        private Button btnSignup, btnLogin, btnReset;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView forgotPasswordTextView;
    private TextView createAccountTextView;
    private EditText userNameEditText;
    private Button mEmailSignInButton;
    private boolean signUp = false;
    private TextInputLayout userName;
    private DatabaseReference mUsersDBref;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
// set the view now
            setContentView(R.layout.activity_login);
            //Get Firebase auth instance
            auth = FirebaseAuth.getInstance();

            if (auth.getCurrentUser() != null) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }



            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            //Get Firebase auth instance
            auth = FirebaseAuth.getInstance();

            userName = findViewById(R.id.userName);
            userNameEditText = findViewById(R.id.userNameEditText);
            userName.animate().translationXBy(-1000).setDuration(0).start();
            mPasswordView = findViewById(R.id.password);
            // Set up the login form.
            mEmailView = findViewById(R.id.email);



            //Check to see which state and set the keyboard ime label accordingly
            mPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        //set keyBoard options when focus on password field
                        if (signUp) {
                            mPasswordView.setImeActionLabel("Sign Up", EditorInfo.IME_NULL);
                        } else {
                            mPasswordView.setImeActionLabel("Login", EditorInfo.IME_NULL);
                        }
                    }
                }
            });

            //Check to see which state and sign up or login into the app
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (signUp) {
                        if (id == R.id.login || id == EditorInfo.IME_NULL) {
                            return true;
                        }
                    } else {
                        if (id == R.id.login || id == EditorInfo.IME_NULL) {
                            attemptLogin();
                            return true;
                        }
                    }

                    return false;

                }
            });

            createAccountTextView = findViewById(R.id.createAccountTextView);

            createAccountTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //make the text unclickable while playing the animation
                    createAccountTextView.setClickable(false);
                    //if not in sign up state start it up
                    if (!signUp) {
                        //Attempt to create userAccount
                        setUpSignUp();
                        createAccountTextView.setText("I have an Account");
                        signUp = true;
                    }
                    //start up sign in state
                    else {
                        createAccountTextView.setClickable(false);
                        setUpSignIn();
                        createAccountTextView.setText("SignUp");
                        signUp = false;
                    }
                }
            });

            mEmailSignInButton = findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //if not in sign up state attempt to login
                    if (!signUp) {
                        attemptLogin();
                    }
                    //if not in sign in state attempt sign up
                    else {
                        attemptSignUp();
                        //Start up Intent
                        // launchIntent();
                    }
                }
            });

            mLoginFormView = findViewById(R.id.email_login_form);
            mProgressView = findViewById(R.id.login_progress);




        }



    /*
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private boolean isEmailValid(String email) {

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        return email.matches(emailPattern);
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 8;
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setUpSignIn() {
        /*
         * Animate text slide in
         */
        userName.animate().x(-1000).setDuration(1500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                createAccountTextView.setClickable(true);
                userName.setVisibility(View.GONE);
                mEmailSignInButton.setText("Login");
                animation.cancel();
            }
        }).start();



        /*
         * Animate text slide out
         */
        mEmailSignInButton.setText(R.string.action_sign_in);

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        View focusView;
        focusView = mEmailView;
        focusView.requestFocus();

    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setUpSignUp() {
        /*
         * Animate text slide in
         */

        userName.animate().x(0).setDuration(1500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mEmailSignInButton.setText("Sign Up");
                createAccountTextView.setClickable(true);
                animation.cancel();
            }
        }).start();


      //  forgotPasswordTextView.animate().xBy(-1500).setDuration(1500).start();

        userName.setVisibility(View.VISIBLE);

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        View focusView;
        focusView = userNameEditText;
        focusView.requestFocus();

    }


    private void attemptSignUp() {


        // Reset errors.
        userNameEditText.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.

        String userName = userNameEditText.getText().toString();
        String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check if the username is valid.
        if (TextUtils.isEmpty(userName)) {
            userNameEditText.setError(getString(R.string.error_field_required));
            focusView = userNameEditText;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            // Show a progress spinner, and kick off a background task to
            // perform the user sign up attempt.
            showProgress(true);

            //create user
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(LoginActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                            //progressBar.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                                setUpSignIn();
                                showProgress(false);
                            } else {

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                        }
                    });

        }
    }


    /*
     * Launch intent to set up your trucks
     */
   /* private void launchIntent() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        i.putExtra("userName", userNameEditText.getText());
        i.putExtra("userEmail", mEmailView.getText());
        i.putExtra("userPassword", mPasswordView.getText());
        startActivity(i);
    }*/

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {


        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);


            //authenticate user
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                           // progressBar.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                // there was an error
                                if (password.length() < 6) {
                                    inputPassword.setError("Password is too short");
                                } else {

                                    Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                                    setUpSignIn();
                                    showProgress(false);
                                }
                            } else {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }
    }


    private void createUserInDb(String userId, String displayName, String email){
        mUsersDBref = FirebaseDatabase.getInstance().getReference().child("Users");
        User user = new User(userId, displayName, email);
        mUsersDBref.child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    //error
                    Toast.makeText(LoginActivity.this, "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }else{
                    //success adding user to db as well
                    //go to users chat list
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        });
    }


    }
