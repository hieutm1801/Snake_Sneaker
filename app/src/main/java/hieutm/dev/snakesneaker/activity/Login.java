package hieutm.dev.snakesneaker.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.response.LoginRP;
import hieutm.dev.snakesneaker.response.RegisterRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.util.Arrays;

import cn.refactor.library.SmoothCheckBox;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Login extends AppCompatActivity {

    private Method method;
    private String email, password;
    private SmoothCheckBox smoothCheckBox;
    private MaterialCheckBox checkBoxTerms;
    private TextInputEditText editTextEmail, editTextPassword;

    public String myPreference = "ECommerceLogin";
    public String prefEmail = "pref_email";
    public String prefPassword = "pref_password";
    public String prefCheck = "pref_check";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;

    //Google login
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 007;

    //Facebook login
    private CallbackManager callbackManager;

    private InputMethodManager imm;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        method = new Method(Login.this);
        method.forceRTLIfSupported();

        pref = getSharedPreferences(myPreference, 0); // 0 - for private mode
        editor = pref.edit();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        progressDialog = new ProgressDialog(Login.this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //facebook button
        callbackManager = CallbackManager.Factory.create();

        editTextEmail = findViewById(R.id.editText_email_login_activity);
        editTextPassword = findViewById(R.id.editText_password_login_activity);

        MaterialButton buttonLogin = findViewById(R.id.button_login_activity);
        final LinearLayout llGoogleSign = findViewById(R.id.linearLayout_google_login);
        final FrameLayout frameLayoutFbSign = findViewById(R.id.frameLayout_login);
        MaterialTextView textViewSkip = findViewById(R.id.textView_skip_login_activity);
        MaterialTextView textViewRegister = findViewById(R.id.textView_signup_login);
        MaterialTextView textViewFp = findViewById(R.id.textView_forget_password_login);
        MaterialTextView textViewTerms = findViewById(R.id.textView_terms_login);
        checkBoxTerms = findViewById(R.id.checkbox_terms_login);
        smoothCheckBox = findViewById(R.id.checkbox_login_activity);
        smoothCheckBox.setChecked(false);

        if (pref.getBoolean(prefCheck, false)) {
            editTextEmail.setText(pref.getString(prefEmail, null));
            editTextPassword.setText(pref.getString(prefPassword, null));
            smoothCheckBox.setChecked(true);
        } else {
            editTextEmail.setText("");
            editTextPassword.setText("");
            smoothCheckBox.setChecked(false);
        }

        smoothCheckBox.setOnCheckedChangeListener((checkBox, isChecked) -> {
            if (isChecked) {
                editor.putString(prefEmail, editTextEmail.getText().toString());
                editor.putString(prefPassword, editTextPassword.getText().toString());
                editor.putBoolean(prefCheck, true);
            } else {
                editor.putBoolean(prefCheck, false);
            }
            editor.commit();
        });

        buttonLogin.setOnClickListener(v -> {

            email = editTextEmail.getText().toString();
            password = editTextPassword.getText().toString();

            editTextEmail.clearFocus();
            editTextPassword.clearFocus();
            imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextPassword.getWindowToken(), 0);

            login();
        });

        llGoogleSign.setOnClickListener(view -> {
            if (checkBoxTerms.isChecked()) {
                signIn();
            } else {
                method.alertBox(getResources().getString(R.string.please_select_terms));
            }
        });

        frameLayoutFbSign.setOnClickListener(v -> {
            if (checkBoxTerms.isChecked()) {
                if (v == frameLayoutFbSign) {
                    LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("email"));
                }
            } else {
                method.alertBox(getResources().getString(R.string.please_select_terms));
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                fbUser(loginResult);
            }

            @Override
            public void onCancel() {
                Log.d("data_app", "");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("error_fb", error.toString());
                Toast.makeText(Login.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        textViewRegister.setOnClickListener(v -> {
            Method.login(false);
            startActivity(new Intent(Login.this, Register.class));
        });

        textViewTerms.setOnClickListener(v -> {
            Method.loginBack = false;
            startActivity(new Intent(Login.this, TermsOfUse.class));
        });

        textViewSkip.setOnClickListener(v -> {

            if (Method.goToLogin()) {
                Method.login(false);
                onBackPressed();
            } else {
                startActivity(new Intent(Login.this, MainActivity.class));
                finishAffinity();
            }
        });

        textViewFp.setOnClickListener(v -> {
            Method.login(false);
            startActivity(new Intent(Login.this, ForgetPassword.class));
        });

    }

    //Google login
    private void signIn() {
        if (method.isNetworkAvailable()) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    //Google login get callback
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    //Google login
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            registerSocialNetwork(account.getId(), account.getDisplayName(), account.getEmail(), "google");

        } catch (ApiException e) {
            Toast.makeText(Login.this, e.toString(), Toast.LENGTH_SHORT).show();
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
        }
    }

    //facebook login get email and name
    private void fbUser(LoginResult loginResult) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {
            try {
                String id = object.getString("id");
                String name = object.getString("name");
                String email = object.getString("email");
                registerSocialNetwork(id, name, email, "facebook");
            } catch (JSONException e) {
                try {
                    String id = object.getString("id");
                    String name = object.getString("name");
                    registerSocialNetwork(id, name, "", "facebook");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email"); // Parameters that we ask for facebook
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void login() {

        editTextEmail.setError(null);
        editTextPassword.setError(null);

        if (!isValidMail(email) || email.isEmpty()) {
            editTextEmail.requestFocus();
            editTextEmail.setError(getResources().getString(R.string.please_enter_email));
        } else if (password.isEmpty()) {
            editTextPassword.requestFocus();
            editTextPassword.setError(getResources().getString(R.string.please_enter_password));
        } else {

            editTextEmail.clearFocus();
            editTextPassword.clearFocus();
            imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextPassword.getWindowToken(), 0);

            if (method.isNetworkAvailable()) {
                login(email, password, "normal");
            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }

        }
    }

    public void login(final String sendEmail, final String sendPassword, String type) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Login.this));
        jsObj.addProperty("email", sendEmail);
        jsObj.addProperty("password", sendPassword);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<LoginRP> call = apiService.getLogin(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<LoginRP>() {
            @Override
            public void onResponse(@NotNull Call<LoginRP> call, @NotNull Response<LoginRP> response) {

                try {

                    LoginRP loginRP = response.body();

                    assert loginRP != null;
                    if (loginRP.getSuccess().equals("1")) {

                        if (smoothCheckBox.isChecked()) {
                            editor.putString(prefEmail, editTextEmail.getText().toString());
                            editor.putString(prefPassword, editTextPassword.getText().toString());
                            editor.putBoolean(prefCheck, true);
                            editor.commit();
                        }

                        method.editor.putBoolean(method.prefLogin, true);
                        method.editor.putString(method.profileId, loginRP.getUser_id());
                        method.editor.putString(method.loginType, type);
                        method.editor.commit();

                        editTextEmail.setText("");
                        editTextPassword.setText("");

                        if (Method.goToLogin()) {
                            Events.Login loginNotify = new Events.Login(loginRP.getName(), loginRP.getEmail(), loginRP.getUser_image());
                            GlobalBus.getBus().post(loginNotify);
                            Method.login(false);
                            onBackPressed();
                        } else {
                            startActivity(new Intent(Login.this, MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            finishAffinity();
                        }

                    } else {
                        method.alertBox(loginRP.getMsg());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<LoginRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    public void registerSocialNetwork(String id, String sendName, String sendEmail, final String type) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Login.this));
        jsObj.addProperty("name", sendName);
        jsObj.addProperty("email", sendEmail);
        jsObj.addProperty("auth_id", id);
        jsObj.addProperty("device_id", method.getDeviceId());
        jsObj.addProperty("type", type);
        jsObj.addProperty("register_platform", "android");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RegisterRP> call = apiService.getRegister(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<RegisterRP>() {
            @Override
            public void onResponse(@NotNull Call<RegisterRP> call, @NotNull Response<RegisterRP> response) {

                try {
                    RegisterRP registerRP = response.body();
                    assert registerRP != null;

                    if (registerRP.getStatus().equals("1")) {

                        if (registerRP.getSuccess().equals("1")) {

                            method.editor.putBoolean(method.prefLogin, true);
                            method.editor.putString(method.profileId, registerRP.getUser_id());
                            method.editor.putString(method.loginType, type);
                            method.editor.commit();

                            Toast.makeText(Login.this, registerRP.getMsg(), Toast.LENGTH_SHORT).show();

                            if (Method.goToLogin()) {
                                Events.Login loginNotify = new Events.Login(registerRP.getName(), registerRP.getEmail(), registerRP.getUser_image());
                                GlobalBus.getBus().post(loginNotify);
                                Method.login(false);
                                onBackPressed();
                            } else {
                                startActivity(new Intent(Login.this, MainActivity.class));
                                finishAffinity();
                            }

                        } else {
                            failLogin(type);
                            method.alertBox(registerRP.getMsg());
                        }

                    } else {
                        failLogin(type);
                        method.alertBox(registerRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<RegisterRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    private void failLogin(String type) {
        if (type.equals("google")) {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(Login.this, task -> {
                        method.editor.putBoolean(method.prefLogin, false);
                        method.editor.commit();
                    });
        } else {
            LoginManager.getInstance().logOut();
        }
    }

}
