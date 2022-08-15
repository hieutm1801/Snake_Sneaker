package hieutm.dev.snakesneaker.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.response.ApplyCouponRP;
import hieutm.dev.snakesneaker.response.CouponsDetailRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponsDetail extends AppCompatActivity {

    private Method method;
    private String id, cartIds, type;
    private ProgressBar progressBar;
    private ImageView imageView;
    private WebView webView;
    private int columnWidth;
    private MaterialButton button;
    private LinearLayout llMain;
    private ProgressDialog progressDialog;
    private RelativeLayout relNoData;
    private MaterialTextView textViewCouponCode, textViewAmount;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_coupons_detail);
        toolbar.setTitle(getResources().getString(R.string.coupons_detail));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        cartIds = intent.getStringExtra("cart_ids");
        type = intent.getStringExtra("type");

        progressDialog = new ProgressDialog(CouponsDetail.this);

        method = new Method(CouponsDetail.this);
        method.forceRTLIfSupported();

        progressBar = findViewById(R.id.progressBar_coupons_detail);
        relNoData = findViewById(R.id.rel_noDataFound);
        textViewCouponCode = findViewById(R.id.textView_code_coupons_detail);
        textViewAmount = findViewById(R.id.textView_amount_coupons_detail);
        imageView = findViewById(R.id.imageView_coupons_detail);
        webView = findViewById(R.id.webView_coupons_detail);
        button = findViewById(R.id.button_coupons_detail);
        llMain = findViewById(R.id.ll_main_coupons_detail);
        LinearLayout linearLayout = findViewById(R.id.linearLayout_coupons_detail);
        method.bannerAd(linearLayout);

        columnWidth = (method.getScreenWidth());
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (columnWidth / 1.5)));

        llMain.setVisibility(View.GONE);
        relNoData.setVisibility(View.GONE);

        if (method.isNetworkAvailable()) {
            couponsDetail(id);
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    private void couponsDetail(String id) {

        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(CouponsDetail.this));
        jsObj.addProperty("id", id);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CouponsDetailRP> call = apiService.getCouponsDetail(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<CouponsDetailRP>() {
            @SuppressLint({"SetTextI18n", "SetJavaScriptEnabled"})
            @Override
            public void onResponse(@NotNull Call<CouponsDetailRP> call, @NotNull Response<CouponsDetailRP> response) {

                try {

                    CouponsDetailRP couponsDetailRP = response.body();

                    assert couponsDetailRP != null;
                    if (couponsDetailRP.getStatus().equals("1")) {

                        Glide.with(CouponsDetail.this).load(couponsDetailRP.getCoupon_image())
                                .placeholder(R.drawable.placeholder_rectangle)
                                .into(imageView);

                        textViewCouponCode.setText(couponsDetailRP.getCoupon_code());
                        textViewAmount.setText(couponsDetailRP.getCoupon_amt());

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
                                + couponsDetailRP.getCoupon_desc()
                                + "</body></html>";

                        webView.loadDataWithBaseURL("blarg://ignored", text, mimeType, encoding, "");

                        llMain.setVisibility(View.VISIBLE);

                        button.setOnClickListener(v -> {
                            if (method.isNetworkAvailable()) {
                                applyCoupons(method.userId(), id, cartIds);
                            } else {
                                method.alertBox(getResources().getString(R.string.internet_connection));
                            }
                        });

                    } else {
                        relNoData.setVisibility(View.VISIBLE);
                        method.alertBox(couponsDetailRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<CouponsDetailRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                relNoData.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    private void applyCoupons(String user_id, String coupon_id, String cart_ids) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(CouponsDetail.this));
        jsObj.addProperty("user_id", user_id);
        jsObj.addProperty("coupon_id", coupon_id);
        jsObj.addProperty("cart_ids", cart_ids);
        if (type.equals("by_now")) {
            jsObj.addProperty("cart_type", "temp_cart");
        } else {
            jsObj.addProperty("cart_type", "main_cart");
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApplyCouponRP> call = apiService.getApplyCouponsDetail(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<ApplyCouponRP>() {
            @Override
            public void onResponse(@NotNull Call<ApplyCouponRP> call, @NotNull Response<ApplyCouponRP> response) {

                try {

                    ApplyCouponRP applyCouponRP = response.body();

                    assert applyCouponRP != null;
                    if (applyCouponRP.getStatus().equals("1")) {

                        if (applyCouponRP.getSuccess().equals("1")) {

                            onBackPressed();

                            Events.OnBackData onBackData = new Events.OnBackData("");
                            GlobalBus.getBus().post(onBackData);

                            Events.CouponCodeAmount couponCodeAmount = new Events.CouponCodeAmount(applyCouponRP.getCoupon_id(), applyCouponRP.getPrice(), applyCouponRP.getPayable_amt(), applyCouponRP.getYou_save_msg());
                            GlobalBus.getBus().post(couponCodeAmount);

                        } else if (applyCouponRP.getSuccess().equals("2")) {
                            method.showStatus(applyCouponRP.getMsg());
                        } else {
                            method.alertBox(applyCouponRP.getMsg());
                        }

                    } else if (applyCouponRP.getStatus().equals("2")) {
                        method.suspend(applyCouponRP.getMessage());
                    } else {
                        method.alertBox(applyCouponRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<ApplyCouponRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
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
