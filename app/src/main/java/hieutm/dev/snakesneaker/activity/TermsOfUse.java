package hieutm.dev.snakesneaker.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Method;
import hieutm.dev.snakesneaker.response.TermsOfUseRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TermsOfUse extends AppCompatActivity {

    private Method method;
    private ProgressBar progressBar;
    private WebView webView;
    private RelativeLayout relNoData;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        method = new Method(TermsOfUse.this);
        method.forceRTLIfSupported();

        MaterialToolbar toolbar = findViewById(R.id.toolbar_privacy_policy);
        toolbar.setTitle(getResources().getString(R.string.terms_Of_use));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressBar = findViewById(R.id.progressBar_privacy_policy);
        relNoData = findViewById(R.id.rel_noDataFound);
        webView = findViewById(R.id.webView_privacy_policy);

        relNoData.setVisibility(View.GONE);

        LinearLayout linearLayout = findViewById(R.id.linearLayout_privacy_policy);
        method.bannerAd(linearLayout);

        if (method.isNetworkAvailable()) {
            terms_of_use();
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    public void terms_of_use() {

        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(TermsOfUse.this));
        jsObj.addProperty("type", "term_of_use");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<TermsOfUseRP> call = apiService.getTermsOfUse(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<TermsOfUseRP>() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onResponse(@NotNull Call<TermsOfUseRP> call, @NotNull Response<TermsOfUseRP> response) {

                try {

                    TermsOfUseRP termsOfUseRP = response.body();
                    assert termsOfUseRP != null;

                    if (termsOfUseRP.getStatus().equals("1")) {

                        webView.setBackgroundColor(Color.TRANSPARENT);
                        webView.setFocusableInTouchMode(false);
                        webView.setFocusable(false);
                        webView.getSettings().setDefaultTextEncodingName("UTF-8");
                        webView.getSettings().setJavaScriptEnabled(true);
                        String mimeType = "text/html";
                        String encoding = "utf-8";

                        String text = "<html dir=" + method.isWebViewTextRtl() + "><head>"
                                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/poppins_medium.ttf\")}body{font-family: MyFont;color: " + method.webViewText() + "line-height:1.6}"
                                + "a {color:" + method.webViewLink() + "text-decoration:none}"
                                + "</style></head>"
                                + "<body>"
                                + termsOfUseRP.getContent()
                                + "</body></html>";

                        webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

                    } else {
                        relNoData.setVisibility(View.VISIBLE);
                        method.alertBox(termsOfUseRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<TermsOfUseRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                relNoData.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
