package hieutm.dev.snakesneaker.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.response.DeepLinkRP;
import hieutm.dev.snakesneaker.response.LoginRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Method;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {

    private Method method;
    private ProgressBar progressBar;
    private Boolean isCancelled = false;
    private String id, subId, title, type = "", deepLink;

    //Google login
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        method = new Method(SplashScreen.this);
        method.firstTimeLogin();
        method.forceRTLIfSupported();
        switch (method.themMode()) {
            case "system":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                break;
        }

        setContentView(R.layout.activity_splash_screen);

        // Making notification bar transparent
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // making notification bar transparent
        method.changeStatusBarColor();

        progressBar = findViewById(R.id.progressBar_splash_screen);
        progressBar.setVisibility(View.GONE);

        Intent intent = getIntent();
        if (intent != null) {
            if (getIntent().hasExtra("id")) {
                id = intent.getStringExtra("id");
                subId = intent.getStringExtra("sub_id");
                type = intent.getStringExtra("type");
                title = intent.getStringExtra("title");
            } else {
                Uri data = intent.getData();
                if (data != null && data.toString().contains(getResources().getString(R.string.deep_link_compare))) {
                    String path = intent.getData().getPath();
                    assert path != null;
                    String[] strings = path.split(getResources().getString(R.string.pathPrefix));
                    deepLink = strings[strings.length - 1];
                    type = "deep_link";
                }
            }
        }

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        splashScreen();

    }

    public void splashScreen() {

        if (method.isNetworkAvailable()) {
            int splashTimeOut = 1000;
            new Handler().postDelayed(() -> {
                if (!isCancelled) {
                    if (method.isLogin()) {
                        login();
                    } else {
                        getData();
                    }
                }
            }, splashTimeOut);
        } else {
            getData();
        }

    }

    private void login() {

        progressBar.setVisibility(View.VISIBLE);

        String loginType = method.getLoginType();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(SplashScreen.this));
        jsObj.addProperty("user_id", method.userId());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<LoginRP> call = apiService.getLogin(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<LoginRP>() {
            @Override
            public void onResponse(@NotNull Call<LoginRP> call, @NotNull Response<LoginRP> response) {

                try {

                    LoginRP loginRP = response.body();
                    assert loginRP != null;

                    if (loginRP.getStatus().equals("1")) {

                        if (loginType.equals("google")) {
                            if (GoogleSignIn.getLastSignedInAccount(SplashScreen.this) != null) {
                                getData();
                            } else {
                                method.editor.putBoolean(method.prefLogin, false);
                                method.editor.commit();
                                startActivity(new Intent(SplashScreen.this, Login.class));
                                finishAffinity();
                            }
                        } else if (loginType.equals("facebook")) {

                            AccessToken accessToken = AccessToken.getCurrentAccessToken();
                            boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

                            if (isLoggedIn) {
                                getData();
                            } else {

                                LoginManager.getInstance().logOut();

                                method.editor.putBoolean(method.prefLogin, false);
                                method.editor.commit();
                                startActivity(new Intent(SplashScreen.this, Login.class));
                                finishAffinity();

                            }

                        } else {
                            getData();
                        }

                    } else if (loginRP.getStatus().equals("2")) {
                        if (loginType.equals("google")) {

                            mGoogleSignInClient.signOut()
                                    .addOnCompleteListener(SplashScreen.this, task -> {

                                    });


                        } else if (loginType.equals("facebook")) {
                            LoginManager.getInstance().logOut();
                        }

                        method.editor.putBoolean(method.prefLogin, false);
                        method.editor.commit();
                        startActivity(new Intent(SplashScreen.this, Login.class));
                        finishAffinity();
                    } else {
                        method.alertBox(loginRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<LoginRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    public void getData() {
        if (type.equals("deep_link")) {
            deepLink();
        } else {
            callActivity();
        }
    }

    private void deepLink() {

        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(SplashScreen.this));
        jsObj.addProperty("product_slug", deepLink);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<DeepLinkRP> call = apiService.getProId(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<DeepLinkRP>() {
            @Override
            public void onResponse(@NotNull Call<DeepLinkRP> call, @NotNull Response<DeepLinkRP> response) {

                try {
                    DeepLinkRP deepLinkRP = response.body();
                    assert deepLinkRP != null;

                    if (deepLinkRP.getStatus().equals("1")) {

                        id = deepLinkRP.getId();
                        title = deepLinkRP.getTitle();

                        callActivity();

                    } else {
                        method.alertBox(deepLinkRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<DeepLinkRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    public void callActivity() {
        if (method.pref.getBoolean(method.isVerification, false)) {
            startActivity(new Intent(SplashScreen.this, Verification.class));
        } else {
            if (method.pref.getBoolean(method.showLogin, true)) {
                method.editor.putBoolean(method.showLogin, false);
                method.editor.commit();
                startActivity(new Intent(SplashScreen.this, Login.class));
            } else {
                startActivity(new Intent(SplashScreen.this, MainActivity.class)
                        .putExtra("id", id)
                        .putExtra("sub_id", subId)
                        .putExtra("type", type)
                        .putExtra("title", title));
            }
            finishAffinity();
        }
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        isCancelled = true;
        super.onDestroy();
    }

}
