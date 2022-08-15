package hieutm.dev.snakesneaker.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.activity.MainActivity;
import hieutm.dev.snakesneaker.interfaces.OnClick;
import hieutm.dev.snakesneaker.response.PaymentSubmitRP;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.CacheFlag;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.login.LoginManager;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

public class Method {

    private Activity activity;
    private OnClick onClick;
    public static boolean loginBack = false;
    public static boolean personalizationAd = false;

    private String logTagFb = "facebookAd";

    public static boolean goToLogin() {
        return loginBack;
    }

    public static void login(boolean isValue) {
        loginBack = isValue;
    }

    public static void setPersonalizationAd(boolean isValue) {
        personalizationAd = isValue;
    }

    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    private final String myPreference = "ECommerce";
    public String prefLogin = "prefLogin";
    private String firstTime = "firstTime";
    public String profileId = "profileId";
    public String loginType = "loginType";
    public String notification = "notification";
    public String showLogin = "show_login";
    public String verificationCode = "verification_code";
    public String isVerification = "is_verification";
    public String themSetting = "them";
    public String isWelcome = "is_welcome";

    public String regName = "reg_name";
    public String regEmail = "reg_email";
    public String regPassword = "reg_password";
    public String regPhoneNo = "reg_phoneNo";

    @SuppressLint("CommitPrefEdits")
    public Method(Activity activity) {
        this.activity = activity;
        pref = activity.getSharedPreferences(myPreference, 0); // 0 - for private mode
        editor = pref.edit();
    }

    @SuppressLint("CommitPrefEdits")
    public Method(Activity activity, OnClick onClick) {
        this.activity = activity;
        this.onClick = onClick;
        pref = activity.getSharedPreferences(myPreference, 0); // 0 - for private mode
        editor = pref.edit();
    }

    public void firstTimeLogin() {
        if (!pref.getBoolean(firstTime, false)) {
            editor.putBoolean(prefLogin, false);
            editor.putBoolean(firstTime, true);
            editor.commit();
        }
    }

    //user login or not
    public boolean isLogin() {
        return pref.getBoolean(prefLogin, false);
    }

    //get login type
    public String getLoginType() {
        return pref.getString(loginType, null);
    }

    //get user id
    public String userId() {
        return pref.getString(profileId, null);
    }

    //get device id
    @SuppressLint("HardwareIds")
    public String getDeviceId() {
        String deviceId;
        try {
            deviceId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            deviceId = "NotFound";
        }
        return deviceId;
    }

    //Welcome screen stop launch every time
    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(isWelcome, isFirstTime);
        editor.commit();
    }

    //Welcome screen first time launch or not
    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(isWelcome, true);
    }

    //rtl
    public void forceRTLIfSupported() {
        if (activity.getResources().getString(R.string.isRTL).equals("true")) {
            activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    public String themMode() {
        return pref.getString(themSetting, "system");
    }

    //rtl or not
    public boolean isRtl() {
        return activity.getResources().getString(R.string.isRTL).equals("true");
    }

    /**
     * Making notification bar transparent
     */
    public void changeStatusBarColor() {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    //network check
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //get screen width
    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) activity
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }

    //---------------Interstitial Ad---------------//

    public void click(final int position, final String type, final String title, final String id, String subId, String subSubId, String tag) {

        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.show();
        progressDialog.setMessage(activity.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        if (Constant.appRP != null) {

            if (Constant.appRP.isInterstitial_ad()) {

                Constant.ADCOUNT = Constant.ADCOUNT + 1;
                if (Constant.ADCOUNT == Constant.ADCOUNTSHOW) {
                    Constant.ADCOUNT = 0;

                    if (Constant.appRP.getInterstitial_ad_type().equals("admob")) {

                        AdRequest adRequest;
                        if (personalizationAd) {
                            adRequest = new AdRequest.Builder()
                                    .build();
                        } else {
                            Bundle extras = new Bundle();
                            extras.putString("npa", "1");
                            adRequest = new AdRequest.Builder()
                                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                    .build();
                        }

                        InterstitialAd.load(activity, Constant.appRP.getInterstitial_ad_id(), adRequest, new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                // The mInterstitialAd reference will be null until
                                // an ad is loaded.
                                Log.i("admob_error", "onAdLoaded");
                                progressDialog.dismiss();
                                interstitialAd.show(activity);
                                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        Log.d("TAG", "The ad was dismissed.");
                                        onClick.click(position, type, title, id, subId, subSubId, tag);
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        Log.d("TAG", "The ad failed to show.");
                                        onClick.click(position, type, title, id, subId, subSubId, tag);
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                // Handle the error
                                Log.i("admob_error", loadAdError.getMessage());
                                progressDialog.dismiss();
                                onClick.click(position, type, title, id, subId, subSubId, tag);
                            }
                        });

                    } else {

                        final com.facebook.ads.InterstitialAd interstitialAd = new com.facebook.ads.InterstitialAd(activity, Constant.appRP.getInterstitial_ad_id());
                        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                            @Override
                            public void onInterstitialDisplayed(Ad ad) {
                                // Interstitial ad displayed callback
                            }

                            @Override
                            public void onInterstitialDismissed(Ad ad) {
                                // Interstitial dismissed callback
                                progressDialog.dismiss();
                                onClick.click(position, type, title, id, subId, subSubId, tag);
                                Log.e(logTagFb, "Interstitial ad dismissed.");
                            }

                            @Override
                            public void onError(Ad ad, AdError adError) {
                                // Ad error callback
                                progressDialog.dismiss();
                                onClick.click(position, type, title, id, subId, subSubId, tag);
                                Log.e(logTagFb, "Interstitial ad failed to load: " + adError.getErrorMessage());
                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                // Interstitial ad is loaded and ready to be displayed
                                Log.d(logTagFb, "Interstitial ad is loaded and ready to be displayed!");
                                progressDialog.dismiss();
                                // Show the ad
                                interstitialAd.show();
                            }

                            @Override
                            public void onAdClicked(Ad ad) {
                                // Ad clicked callback
                                Log.d(logTagFb, "Interstitial ad clicked!");
                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {
                                // Ad impression logged callback
                                Log.d(logTagFb, "Interstitial ad impression logged!");
                            }
                        };

                        // For auto play video ads, it's recommended to load the ad
                        // at least 30 seconds before it is shown
                        com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = interstitialAd.buildLoadAdConfig().
                                withAdListener(interstitialAdListener).withCacheFlags(CacheFlag.ALL).build();
                        interstitialAd.loadAd(loadAdConfig);

                    }

                } else {
                    progressDialog.dismiss();
                    onClick.click(position, type, title, id, subId, subSubId, tag);
                }
            } else {
                progressDialog.dismiss();
                onClick.click(position, type, title, id, subId, subSubId, tag);
            }
        } else {
            progressDialog.dismiss();
            onClick.click(position, type, title, id, subId, subSubId, tag);
        }
    }

    //---------------Interstitial Ad---------------//

    //---------------Banner Ad---------------//

    public void bannerAd(LinearLayout linearLayout) {

        if (Constant.appRP != null) {
            if (Constant.appRP.isBanner_ad()) {
                if (Constant.appRP.getBanner_ad_type().equals("admob")) {
                    if (personalizationAd) {
                        showPersonalizedAds(linearLayout);
                    } else {
                        showNonPersonalizedAds(linearLayout);
                    }
                } else {
                    fbBannerAd(linearLayout);
                }
            } else {
                linearLayout.setVisibility(View.GONE);
            }
        } else {
            linearLayout.setVisibility(View.GONE);
        }

    }

    public void showPersonalizedAds(LinearLayout linearLayout) {
        AdView adView = new AdView(activity);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        adView.setAdUnitId(Constant.appRP.getBanner_ad_id());
        adView.setAdSize(AdSize.BANNER);
        linearLayout.addView(adView);
        adView.loadAd(adRequest);
    }

    public void showNonPersonalizedAds(LinearLayout linearLayout) {
        Bundle extras = new Bundle();
        extras.putString("npa", "1");
        AdView adView = new AdView(activity);
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .build();
        adView.setAdUnitId(Constant.appRP.getBanner_ad_id());
        adView.setAdSize(AdSize.BANNER);
        linearLayout.addView(adView);
        adView.loadAd(adRequest);
    }

    public void fbBannerAd(LinearLayout linearLayout) {
        com.facebook.ads.AdView adView = new com.facebook.ads.AdView(activity, Constant.appRP.getBanner_ad_id(), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
        // Find the Ad Container
        // Add the ad view to your activity layout
        linearLayout.addView(adView);
        // Request an ad
        adView.loadAd();
    }

    //---------------Banner Ad---------------//

    //check number or note
    public boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //alert message box
    public void alertBox(String message) {
        try {
            if (activity != null) {
                if (!activity.isFinishing()) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.DialogTitleTextStyle);
                    builder.setMessage(Html.fromHtml(message));
                    builder.setCancelable(false);
                    builder.setPositiveButton(activity.getResources().getString(R.string.ok),
                            (arg0, arg1) -> {

                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        } catch (Exception e) {
            Log.d("error_message", e.toString());
        }
    }

    //account suspend
    public void suspend(String message) {

        if (isLogin()) {

            String typeLogin = getLoginType();
            assert typeLogin != null;
            if (typeLogin.equals("google")) {

                //Google login
                GoogleSignInClient mGoogleSignInClient;

                // Configure sign-in to request the user's ID, email address, and basic
                // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

                // Build a GoogleSignInClient with the options specified by gso.
                mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(activity, task -> {

                        });
            } else if (typeLogin.equals("facebook")) {
                LoginManager.getInstance().logOut();
            }

            editor.putBoolean(prefLogin, false);
            editor.commit();
            Events.Login loginNotify = new Events.Login(activity.getResources().getString(R.string.nav_title),
                    activity.getResources().getString(R.string.nav_sub_title), "");
            GlobalBus.getBus().post(loginNotify);
        }

        try {
            if (activity != null) {
                if (!activity.isFinishing()) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.DialogTitleTextStyle);
                    builder.setMessage(Html.fromHtml(message));
                    builder.setCancelable(false);
                    builder.setPositiveButton(activity.getResources().getString(R.string.ok),
                            (arg0, arg1) -> {
                                activity.startActivity(new Intent(activity, MainActivity.class));
                                activity.finishAffinity();
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        } catch (Exception e) {
            Log.d("error_message", e.toString());
        }

    }


    public void showStatus(String message) {

        try {
            if (activity != null) {
                if (!activity.isFinishing()) {

                    Dialog dialog = new Dialog(activity);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_status);
                    dialog.setCancelable(false);
                    if (isRtl()) {
                        dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    }
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                    MaterialTextView textViewDes = dialog.findViewById(R.id.textView_des_dialog_status);
                    MaterialButton button = dialog.findViewById(R.id.button_dialog_status);

                    textViewDes.setText(message);

                    button.setOnClickListener(v -> {
                        dialog.dismiss();
                        activity.startActivity(new Intent(activity, MainActivity.class));
                        activity.finishAffinity();
                    });

                    dialog.show();

                }
            }
        } catch (Exception e) {
            Log.d("error_message", e.toString());
        }

    }

    //order conform dialog
    public void conformDialog(PaymentSubmitRP paymentSubmitRP) {

        try {
            if (activity != null) {
                if (!activity.isFinishing()) {
                    Dialog dialog = new Dialog(activity);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_order_conform);
                    dialog.setCancelable(false);
                    if (isRtl()) {
                        dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    }
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                    MaterialButton buttonShop = dialog.findViewById(R.id.button_shop_now_doc);
                    MaterialButton buttonMyOrder = dialog.findViewById(R.id.button_myOrder_doc);
                    MaterialTextView textViewTitle = dialog.findViewById(R.id.textView_title_doc);
                    MaterialTextView textViewThankYou = dialog.findViewById(R.id.textView_thankYou_msg_doc);
                    MaterialTextView textViewMsg = dialog.findViewById(R.id.textView_msg_doc);
                    MaterialTextView textViewOrderId = dialog.findViewById(R.id.textView_orderId_doc);

                    textViewTitle.setText(paymentSubmitRP.getTitle());
                    textViewThankYou.setText(paymentSubmitRP.getThank_you_msg());
                    textViewMsg.setText(paymentSubmitRP.getOrd_confirm_msg());
                    textViewOrderId.setText(paymentSubmitRP.getOrder_unique_id());

                    dialog.show();

                    buttonShop.setOnClickListener(v -> {
                        dialog.dismiss();
                        activity.startActivity(new Intent(activity, MainActivity.class));
                        activity.finishAffinity();
                    });

                    buttonMyOrder.setOnClickListener(v -> {
                        dialog.dismiss();
                        activity.startActivity(new Intent(activity, MainActivity.class)
                                .putExtra("type", "my_order"));
                        activity.finishAffinity();
                    });
                }
            }
        } catch (Exception e) {
            Log.d("error_message", e.toString());
        }

    }

    //check dark mode or not
    public boolean isDarkMode() {
        int currentNightMode = activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                return false;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                return true;
            default:
                return false;
        }
    }

    public String webViewText() {
        String color;
        if (isDarkMode()) {
            color = Constant.webTextDark;
        } else {
            color = Constant.webTextLight;
        }
        return color;
    }

    public String webViewLink() {
        String color;
        if (isDarkMode()) {
            color = Constant.webLinkDark;
        } else {
            color = Constant.webLinkLight;
        }
        return color;
    }

    public String isWebViewTextRtl() {
        String isRtl;
        if (isRtl()) {
            isRtl = "rtl";
        } else {
            isRtl = "ltr";
        }
        return isRtl;
    }

}
