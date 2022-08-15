package hieutm.dev.snakesneaker.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import hieutm.dev.snakesneaker.BuildConfig;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.fragment.BrandFragment;
import hieutm.dev.snakesneaker.fragment.CategoryFragment;
import hieutm.dev.snakesneaker.fragment.HomeFragment;
import hieutm.dev.snakesneaker.fragment.MyOrderFragment;
import hieutm.dev.snakesneaker.fragment.OfferFragment;
import hieutm.dev.snakesneaker.fragment.ProDetailFragment;
import hieutm.dev.snakesneaker.fragment.ProductFragment;
import hieutm.dev.snakesneaker.fragment.ProfileFragment;
import hieutm.dev.snakesneaker.fragment.SettingFragment;
import hieutm.dev.snakesneaker.fragment.WishListFragment;
import hieutm.dev.snakesneaker.response.AppRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;
import com.facebook.login.LoginManager;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.onesignal.OneSignal;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Method method;
    public static MaterialToolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View headerLayout;
    private CircleImageView imageView;
    private MaterialTextView textViewName, textViewEmail;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private ConsentForm form;
    private Menu menu;
    private boolean isMenu = false;
    private InputMethodManager imm;
    private String id, subId, title, type = "";
    private boolean isAdMOb = false;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GlobalBus.getBus().register(this);

        Intent intent = getIntent();
        if (intent.hasExtra("type")) {
            type = intent.getStringExtra("type");
            assert type != null;
            if (!type.equals("my_order")) {
                id = intent.getStringExtra("id");
                subId = intent.getStringExtra("sub_id");
                type = intent.getStringExtra("type");
                title = intent.getStringExtra("title");
            }
        }

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        method = new Method(MainActivity.this);
        method.forceRTLIfSupported();

        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progressBar_main);
        linearLayout = findViewById(R.id.linearLayout_main);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);

        navigationView = findViewById(R.id.nav_view);
        headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        RelativeLayout relativeLayout = headerLayout.findViewById(R.id.rel_nav);
        textViewName = headerLayout.findViewById(R.id.textView_name_nav);
        textViewEmail = headerLayout.findViewById(R.id.textView_email_nav);
        imageView = headerLayout.findViewById(R.id.imageView_nav);

        relativeLayout.setOnClickListener(v -> {
            drawer.closeDrawers();
            backStackRemove();
            selectDrawerItem(6);
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new ProfileFragment(), getResources().getString(R.string.profile)).commitAllowingStateLoss();
        });

        selectDrawerItem(0);

        checkLogin();

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                appDetail(method.userId());
            } else {
                appDetail("0");
            }
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            }
            if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                String titleBack = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount() - 1).getTag();
                if (titleBack != null) {
                    if (method.isDarkMode()) {
                        MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" +titleBack + "</font>"));
                    } else {
                        MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + titleBack + "</font>"));
                    }
                }
                super.onBackPressed();
            } else {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, getResources().getString(R.string.Please_click_BACK_again_to_exit), Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;

        if (!isMenu) {
            isMenu = true;
        }

        View menuItem = menu.findItem(R.id.action_settings).getActionView();
        menuItem.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Cart.class)));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    public void geString(Events.CartItem cartItem) {

        View menuItem = menu.findItem(R.id.action_settings).getActionView();
        MaterialTextView textView = menuItem.findViewById(R.id.textView_cart);
        textView.setText(cartItem.getCart_item());

    }

    public void appDetail(String userId) {

        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(MainActivity.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("device_id", method.getDeviceId());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AppRP> call = apiService.getAppData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<AppRP>() {
            @Override
            public void onResponse(@NotNull Call<AppRP> call, @NotNull Response<AppRP> response) {

                try {

                    Constant.appRP = response.body();
                    assert Constant.appRP != null;

                    if (Constant.appRP.getStatus().equals("1")) {

                        if (Constant.appRP.getApp_update_status() && Constant.appRP.getApp_new_version() > BuildConfig.VERSION_CODE) {
                            showAppDialog(Constant.appRP.getApp_update_desc(),
                                    Constant.appRP.getApp_redirect_url(),
                                    Constant.appRP.getCancel_update_status());
                        }

                        Constant.currency = Constant.appRP.getApp_currency_code();

                        if (!userId.equals("0")) {
                            textViewName.setText(Constant.appRP.getUser_name());
                            if (Constant.appRP.getUser_email().equals("")) {
                                textViewEmail.setVisibility(View.GONE);
                            } else {
                                textViewEmail.setVisibility(View.VISIBLE);
                                textViewEmail.setText(Constant.appRP.getUser_email());
                            }
                            Glide.with(MainActivity.this.getApplicationContext()).load(Constant.appRP.getUser_image())
                                    .placeholder(R.drawable.user_profile).into(imageView);
                        }

                        if (isMenu) {
                            View menuItem = menu.findItem(R.id.action_settings).getActionView();
                            MaterialTextView textView = menuItem.findViewById(R.id.textView_cart);
                            textView.setText(Constant.appRP.getCart_items());
                        }

                        if (Constant.appRP.getInterstitial_ad_click().equals("")) {
                            Constant.ADCOUNTSHOW = 0;
                        } else {
                            Constant.ADCOUNTSHOW = Integer.parseInt(Constant.appRP.getInterstitial_ad_click());
                        }

                        navigationView.setNavigationItemSelectedListener(MainActivity.this);

                        if (Constant.appRP.isBanner_ad() || Constant.appRP.isInterstitial_ad()) {
                            if (getBannerAdType() || Constant.appRP.getInterstitial_ad_type().equals("admob")) {
                                if (getBannerAdType() && Constant.appRP.isBanner_ad()) {
                                    isAdMOb = true;
                                }
                                if (!getBannerAdType() && Constant.appRP.isBanner_ad()) {
                                    method.fbBannerAd(linearLayout);
                                } else {
                                    if (!Constant.appRP.isBanner_ad()) {
                                        linearLayout.setVisibility(View.GONE);
                                    }
                                }
                                checkForConsent();
                            } else {
                                if (Constant.appRP.isBanner_ad()) {
                                    method.fbBannerAd(linearLayout);
                                } else {
                                    linearLayout.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            linearLayout.setVisibility(View.GONE);
                        }

                        try {
                            switch (type) {
                                case "deep_link":
                                case "product": {
                                    ProDetailFragment proDetailFragment = new ProDetailFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("id", id);
                                    bundle.putString("title", title);
                                    proDetailFragment.setArguments(bundle);
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.frameLayout_main, proDetailFragment, title)
                                            .commitAllowingStateLoss();
                                    break;
                                }
                                case "my_order":
                                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new MyOrderFragment(),
                                            getResources().getString(R.string.my_order)).commitAllowingStateLoss();
                                    break;
                                case "category":
                                case "sub_category":
                                case "brand":
                                case "offer":
                                case "banner":

                                    String typeSend = null;

                                    switch (type) {
                                        case "brand":
                                            typeSend = "brand";
                                            break;
                                        case "offer":
                                            typeSend = "offer";
                                            break;
                                        case "banner":
                                            typeSend = "slider";
                                            break;
                                        default:
                                            typeSend = "cat";
                                            break;
                                    }

                                    ProductFragment productFragment = new ProductFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("type", typeSend);
                                    bundle.putString("title", title);
                                    bundle.putString("cat_id", id);
                                    bundle.putString("sub_cat_id", subId);
                                    bundle.putString("search", "");
                                    productFragment.setArguments(bundle);
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.frameLayout_main, productFragment, title)
                                            .commitAllowingStateLoss();
                                    break;

                                default:
                                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new HomeFragment(),
                                            getResources().getString(R.string.home)).commitAllowingStateLoss();
                                    break;
                            }

                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.wrong),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else if (Constant.appRP.getStatus().equals("2")) {
                        method.suspend(Constant.appRP.getMessage());
                    } else {
                        method.alertBox(Constant.appRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<AppRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NotNull MenuItem item) {

        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        // Handle bottom_navigation view item clicks here.
        //Checking if the item is in checked state or not, if not make it in checked state
        item.setChecked(!item.isChecked());

        //Closing drawer on item click
        drawer.closeDrawers();

        // Handle bottom_navigation view item clicks here.
        switch (item.getItemId()) {
            // Handle the camera action

            case R.id.home:
                backStackRemove();
                selectDrawerItem(0);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new HomeFragment(), getResources().getString(R.string.home)).commitAllowingStateLoss();
                break;

            case R.id.category:
                backStackRemove();
                selectDrawerItem(1);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new CategoryFragment(), getResources().getString(R.string.category)).commitAllowingStateLoss();
                break;

            case R.id.brand:
                backStackRemove();
                selectDrawerItem(2);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new BrandFragment(), getResources().getString(R.string.brand)).commitAllowingStateLoss();
                break;

            case R.id.offer:
                backStackRemove();
                selectDrawerItem(3);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new OfferFragment(), getResources().getString(R.string.offers)).commitAllowingStateLoss();
                break;

            case R.id.wish_list:
                backStackRemove();
                selectDrawerItem(4);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new WishListFragment(), getResources().getString(R.string.wish_list)).commitAllowingStateLoss();
                break;

            case R.id.my_order:
                backStackRemove();
                selectDrawerItem(5);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new MyOrderFragment(), getResources().getString(R.string.my_order)).commitAllowingStateLoss();
                break;

            case R.id.profile:
                backStackRemove();
                selectDrawerItem(6);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new ProfileFragment(), getResources().getString(R.string.profile)).commitAllowingStateLoss();
                break;

            case R.id.setting:
                backStackRemove();
                selectDrawerItem(7);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new SettingFragment(), getResources().getString(R.string.setting)).commitAllowingStateLoss();
                break;

            case R.id.login:
                deselectDrawerItem(8);
                if (method.isLogin()) {
                    logout();
                } else {
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finishAffinity();
                }
                break;

            default:
                break;
        }
        return true;
    }

    public void selectDrawerItem(int position) {
        navigationView.getMenu().getItem(position).setChecked(true);
    }

    public void deselectDrawerItem(int position) {
        navigationView.getMenu().getItem(position).setCheckable(false);
        navigationView.getMenu().getItem(position).setChecked(false);
    }

    public void backStackRemove() {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }
    }

    public void checkForConsent() {

        ConsentInformation consentInformation = ConsentInformation.getInstance(MainActivity.this);
        String[] publisherIds = {Constant.appRP.getPublisher_id()};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                Log.d("consentStatus", consentStatus.toString());
                // User's consent status successfully updated.
                switch (consentStatus) {
                    case PERSONALIZED:
                        Method.setPersonalizationAd(true);
                        if (isAdMOb) {
                            method.showPersonalizedAds(linearLayout);
                        }
                        break;
                    case NON_PERSONALIZED:
                        Method.setPersonalizationAd(false);
                        if (isAdMOb) {
                            method.showNonPersonalizedAds(linearLayout);
                        }
                        break;
                    case UNKNOWN:
                        if (ConsentInformation.getInstance(getBaseContext()).isRequestLocationInEeaOrUnknown()) {
                            requestConsent();
                        } else {
                            Method.setPersonalizationAd(true);
                            if (isAdMOb) {
                                method.showPersonalizedAds(linearLayout);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });

    }

    public void requestConsent() {
        URL privacyUrl = null;
        try {
            //Replace with your app's privacy policy URL.
            privacyUrl = new URL(Constant.appRP.getPrivacy_policy());
        } catch (MalformedURLException e) {
            e.printStackTrace();// Handle error.
        }
        form = new ConsentForm.Builder(MainActivity.this, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        showForm(); // Consent form loaded successfully.
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                    }

                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        Log.d("consentStatus_form", consentStatus.toString());
                        switch (consentStatus) {
                            case PERSONALIZED:
                                Method.setPersonalizationAd(true);
                                if (isAdMOb) {
                                    method.showPersonalizedAds(linearLayout);
                                }
                                break;
                            case NON_PERSONALIZED:
                            case UNKNOWN:
                                Method.setPersonalizationAd(false);
                                if (isAdMOb) {
                                    method.showNonPersonalizedAds(linearLayout);
                                }
                                break;
                        }
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        Log.d("errorDescription", errorDescription);
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build();
        form.load();
    }

    private void showForm() {
        if (form != null) {
            form.show();
        }
    }

    private boolean getBannerAdType() {
        return Constant.appRP.getBanner_ad_type().equals("admob");
    }

    //alert message box
    public void logout() {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this, R.style.DialogTitleTextStyle);
        builder.setCancelable(false);
        builder.setMessage(getResources().getString(R.string.logout_message));
        builder.setPositiveButton(getResources().getString(R.string.logout),
                (arg0, arg1) -> {
                    OneSignal.sendTag("user_id", method.userId());
                    if (method.getLoginType().equals("google")) {

                        // Configure sign-in to request the user's ID, email address, and basic
                        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .build();

                        // Build a GoogleSignInClient with the options specified by gso.
                        //Google login
                        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

                        mGoogleSignInClient.signOut()
                                .addOnCompleteListener(MainActivity.this, task -> {
                                    method.editor.putBoolean(method.prefLogin, false);
                                    method.editor.commit();
                                    startActivity(new Intent(MainActivity.this, Login.class));
                                    finishAffinity();
                                });
                    } else if (method.getLoginType().equals("facebook")) {
                        LoginManager.getInstance().logOut();
                        method.editor.putBoolean(method.prefLogin, false);
                        method.editor.commit();
                        startActivity(new Intent(MainActivity.this, Login.class));
                        finishAffinity();
                    } else {
                        method.editor.putBoolean(method.prefLogin, false);
                        method.editor.commit();
                        startActivity(new Intent(MainActivity.this, Login.class));
                        finishAffinity();
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.cancel),
                (dialogInterface, i) -> {

                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Subscribe
    public void getMyOrder(Events.GoToHome goToHome) {
        selectDrawerItem(0);
    }

    @Subscribe
    public void getLogin(Events.Login login) {
        if (navigationView != null) {
            checkLogin();
            textViewName.setText(login.getName());
            if (login.getEmail().equals("")) {
                textViewEmail.setVisibility(View.GONE);
            } else {
                textViewEmail.setVisibility(View.VISIBLE);
                textViewEmail.setText(login.getEmail());
            }
            Glide.with(MainActivity.this.getApplicationContext()).load(login.getUserImage())
                    .placeholder(R.drawable.user_profile).into(imageView);
        }
    }

    private void checkLogin() {
        if (method != null) {
            if (method.isLogin()) {
                navigationView.getMenu().getItem(8).setIcon(R.drawable.logout);
                navigationView.getMenu().getItem(8).setTitle(getResources().getString(R.string.action_logout));
            } else {
                navigationView.getMenu().getItem(8).setIcon(R.drawable.login);
                navigationView.getMenu().getItem(8).setTitle(getResources().getString(R.string.login));
            }
        }
    }

    private void showAppDialog(String description, String link, boolean isCancel) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_app);
        dialog.setCancelable(false);
        if (method.isRtl()) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);

        MaterialTextView textViewDes = dialog.findViewById(R.id.textView_description_dialog_update);
        MaterialButton buttonUpdate = dialog.findViewById(R.id.button_update_dialog_update);
        MaterialButton buttonCancel = dialog.findViewById(R.id.button_cancel_dialog_update);

        if (isCancel) {
            buttonCancel.setVisibility(View.VISIBLE);
        } else {
            buttonCancel.setVisibility(View.GONE);
        }
        textViewDes.setText(description);

        buttonUpdate.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
            } catch (Exception e) {
                method.alertBox(getResources().getString(R.string.wrong));
            }
            dialog.dismiss();
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        GlobalBus.getBus().unregister(this);
        super.onDestroy();
    }

}
