package hieutm.dev.snakesneaker.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.activity.AddAddress;
import hieutm.dev.snakesneaker.activity.Login;
import hieutm.dev.snakesneaker.activity.MainActivity;
import hieutm.dev.snakesneaker.activity.OrderSummary;
import hieutm.dev.snakesneaker.activity.ProReview;
import hieutm.dev.snakesneaker.adapter.HomeProductAdapter;
import hieutm.dev.snakesneaker.adapter.ProColorAdapter;
import hieutm.dev.snakesneaker.adapter.ProDetailProReviewAdapter;
import hieutm.dev.snakesneaker.adapter.ProSizeAdapter;
import hieutm.dev.snakesneaker.adapter.ProViewAdapter;
import hieutm.dev.snakesneaker.interfaces.OnClick;
import hieutm.dev.snakesneaker.interfaces.OnClickSend;
import hieutm.dev.snakesneaker.response.AddToCartRP;
import hieutm.dev.snakesneaker.response.FavouriteRP;
import hieutm.dev.snakesneaker.response.ISAddRP;
import hieutm.dev.snakesneaker.response.ProDetailRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.DecimalConverter;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.GridSpacingItemDecoration;
import hieutm.dev.snakesneaker.util.Method;

import com.github.ornolfr.ratingview.RatingView;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import me.chensir.expandabletextview.ExpandableTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProDetailFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private Animation myAnim;
    private String id, title, sign, tagData = "data_app";
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private WebView webViewFeature;
    private RatingView ratingView;
    private int columnWidth;
    private String proSize;
    private int productSize = 0;
    private OnClickSend onClickSend;
    private View viewColor;
    private ViewPager viewPager;
    private ProDetailProReviewAdapter proDetailProReviewAdapter;
    private HomeProductAdapter productGridAdapter;
    private RelativeLayout relNoData, relMain, relFav, relShare;
    private ImageView imageViewFav, imageViewShare;
    private ExpandableTextView textViewProDetail;
    private RecyclerView recyclerViewSize, recyclerViewColor, recyclerViewReview, recyclerViewRelated;
    private LinearLayout llSize, llSizeShow, llColor, llRelated;
    private MaterialCardView cardViewReview, cardViewSizeColor;
    private MaterialTextView textViewTitle, textViewSellPrice, textViewPrice, textViewSave,
            textViewRating, textViewReviewAll, textViewByNow, textViewAddToCard, textViewMessage;
    private final String DISTANCE = " ";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.pro_detail_fragment, container, false);

        assert getArguments() != null;
        id = getArguments().getString("id");
        title = getArguments().getString("title");

        myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);

        sign = DISTANCE + Constant.currency;
        method = new Method(getActivity(), onClick);

        if (MainActivity.toolbar != null) {
            if (method.isDarkMode()) {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + title + "</font>"));
            } else {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + title + "</font>"));
            }
        }
        onClick = (position, type, sendTitle, sendId, subId, subSubId, tag) -> {
            callFragment(sendId, sendTitle);
        };

        onClickSend = (sendId, type, size, position) -> {
            if (type.equals("pro_size")) {
                proSize = size;
                productSize = position;
            } else {
                proSize = null;
                productSize = 0;
                callFragment(sendId, title);
            }
        };

        progressDialog = new ProgressDialog(getActivity());

        relMain = view.findViewById(R.id.rel_main_pro_detail);
        relNoData = view.findViewById(R.id.rel_noDataFound);
        progressBar = view.findViewById(R.id.progressBar_pro_detail);
        imageViewFav = view.findViewById(R.id.imageView_fav_pro_detail);
        imageViewShare = view.findViewById(R.id.imageView_share_pro_detail);
        relFav = view.findViewById(R.id.rel_fav_pro_detail);
        relShare = view.findViewById(R.id.rel_share_pro_detail);
        viewPager = view.findViewById(R.id.viewpager_pro_detail);
        textViewTitle = view.findViewById(R.id.textView_title_pro_detail);
        textViewSellPrice = view.findViewById(R.id.textView_sellPrice_pro_detail);
        textViewPrice = view.findViewById(R.id.textView_price_pro_detail);
        textViewSave = view.findViewById(R.id.textView_save_pro_detail);
        textViewRating = view.findViewById(R.id.textView_rating_pro_detail);
        textViewReviewAll = view.findViewById(R.id.textView_reviewAll_pro_detail);
        ratingView = view.findViewById(R.id.ratingBar_pro_detail);
        textViewProDetail = view.findViewById(R.id.textView_description_pro_detail);
        webViewFeature = view.findViewById(R.id.webView_feature_pro_detail);
        textViewByNow = view.findViewById(R.id.textView_byNow_pro_detail);
        textViewAddToCard = view.findViewById(R.id.textView_addToCard_pro_detail);
        textViewMessage = view.findViewById(R.id.textView_message_pro_detail);
        llSize = view.findViewById(R.id.ll_size_pro_detail);
        llSizeShow = view.findViewById(R.id.ll_size_show_pro_detail);
        llColor = view.findViewById(R.id.ll_color_size);
        llRelated = view.findViewById(R.id.ll_related_pro_detail);
        viewColor = view.findViewById(R.id.view_color_pro_detail);
        recyclerViewSize = view.findViewById(R.id.recyclerView_size_pro_detail);
        recyclerViewColor = view.findViewById(R.id.recyclerView_color_pro_detail);
        recyclerViewReview = view.findViewById(R.id.recyclerView_review_pro_detail);
        recyclerViewRelated = view.findViewById(R.id.recyclerView_related_pro_detail);
        cardViewSizeColor = view.findViewById(R.id.cardView_size_pro_detail);
        cardViewReview = view.findViewById(R.id.cardView_review_pro_detail);

        relMain.setVisibility(View.GONE);
        relNoData.setVisibility(View.GONE);
        cardViewSizeColor.setVisibility(View.GONE);
        cardViewReview.setVisibility(View.GONE);

        columnWidth = (method.getScreenWidth());
        viewPager.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, columnWidth));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        recyclerViewSize.setLayoutManager(layoutManager);

        FlexboxLayoutManager layoutManager_color = new FlexboxLayoutManager(getActivity());
        layoutManager_color.setFlexDirection(FlexDirection.ROW);
        layoutManager_color.setJustifyContent(JustifyContent.FLEX_START);
        recyclerViewColor.setLayoutManager(layoutManager_color);

        recyclerViewReview.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager_review = new LinearLayoutManager(getActivity());
        recyclerViewReview.setLayoutManager(layoutManager_review);
        recyclerViewReview.setFocusable(false);
        recyclerViewReview.setNestedScrollingEnabled(false);

        recyclerViewRelated.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager_related = new GridLayoutManager(getActivity(), 2);
        recyclerViewRelated.setLayoutManager(layoutManager_related);
        recyclerViewRelated.setFocusable(false);
        recyclerViewRelated.addItemDecoration(new GridSpacingItemDecoration(2, 15, true));
        recyclerViewRelated.setNestedScrollingEnabled(false);

        callData(id);

        return view;

    }

    //	page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            Log.d(tagData, "");
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            Log.d(tagData, "");

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            Log.d(tagData, "");
        }
    };

    private void callFragment(String id, String title) {

        getActivity().getSupportFragmentManager().popBackStack();

        ProDetailFragment proDetailFragment = new ProDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("title", title);
        proDetailFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout_main, proDetailFragment, title).addToBackStack(title)
                .commitAllowingStateLoss();

    }

    private void callData(String id) {
        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                detail(id, method.userId());
            } else {
                detail(id, "0");
            }
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    private void detail(String id, String userId) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("id", id);
            jsObj.addProperty("user_id", userId);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ProDetailRP> call = apiService.getProDetail(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<ProDetailRP>() {
                @SuppressLint({"SetJavaScriptEnabled", "SetTextI18n", "UseCompatLoadingForDrawables"})
                @Override
                public void onResponse(@NotNull Call<ProDetailRP> call, @NotNull Response<ProDetailRP> response) {

                    if (getActivity() != null) {

                        try {

                            ProDetailRP proDetailRP = response.body();
                            assert proDetailRP != null;

                            if (proDetailRP.getStatus().equals("1")) {

                                if (proDetailRP.getSuccess().equals("1")) {

                                    if (proDetailRP.getProduct_status().equals("0")) {
                                        textViewAddToCard.setVisibility(View.GONE);
                                        textViewByNow.setVisibility(View.GONE);
                                        textViewMessage.setVisibility(View.VISIBLE);
                                        textViewMessage.setText(proDetailRP.getProduct_status_lbl());
                                    } else {
                                        textViewAddToCard.setVisibility(View.VISIBLE);
                                        textViewByNow.setVisibility(View.VISIBLE);
                                        textViewMessage.setVisibility(View.GONE);
                                    }

                                    if (proDetailRP.getIs_favorite().equals("true")) {
                                        imageViewFav.setImageDrawable(getResources().getDrawable(R.drawable.fav_ic));
                                    } else {
                                        imageViewFav.setImageDrawable(getResources().getDrawable(R.drawable.unfav_ic));
                                    }

                                    if (proDetailRP.getIs_color().equals("true") || proDetailRP.getIs_size().equals("true")) {
                                        cardViewSizeColor.setVisibility(View.VISIBLE);
                                    } else {
                                        cardViewSizeColor.setVisibility(View.GONE);
                                    }

//                                    if (proDetailRP.getIs_color().equals("true") && proDetailRP.getIs_size().equals("true")) {
//                                        viewColor.setVisibility(View.VISIBLE);
//                                    } else {
//                                        viewColor.setVisibility(View.GONE);
//                                    }

                                    if (proDetailRP.getIs_color().equals("true")) {
                                        llColor.setVisibility(View.VISIBLE);
                                    } else {
                                        llColor.setVisibility(View.GONE);
                                    }

                                    if (proDetailRP.getIs_size().equals("true")) {
                                        llSize.setVisibility(View.VISIBLE);
                                        if (proDetailRP.getSize_chart().equals("")) {
                                            llSizeShow.setVisibility(View.GONE);
                                        } else {
                                            llSizeShow.setVisibility(View.VISIBLE);
                                        }
                                        proSize = proDetailRP.getProSizeLists().get(0).getProduct_size();
                                    } else {
                                        proSize = "0";
                                        llSize.setVisibility(View.GONE);
                                    }

                                    textViewTitle.setText(proDetailRP.getProduct_title());
                                    textViewSellPrice.setTypeface(textViewSellPrice.getTypeface(), Typeface.BOLD);
                                    textViewSellPrice.setText(DecimalConverter.currencyFormat(proDetailRP.getProduct_sell_price()) + sign);
                                    if (proDetailRP.getYou_save().equals("0.00")) {
                                        textViewSave.setVisibility(View.GONE);
                                        textViewPrice.setVisibility(View.GONE);
                                    } else {
                                        textViewSave.setText(proDetailRP.getYou_save_per());
                                        textViewPrice.setText(DecimalConverter.currencyFormat(proDetailRP.getProduct_mrp()) + sign);
                                        textViewPrice.setPaintFlags(textViewPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                    }
                                    textViewRating.setText(proDetailRP.getTotal_rate());
                                    ratingView.setRating(Float.parseFloat(proDetailRP.getRate_avg()));

                                    String mimeType = "text/html";
                                    String encoding = "utf-8";

                                    textViewProDetail.setText(Html.fromHtml(proDetailRP.getProduct_desc()));

                                    webViewFeature.setBackgroundColor(Color.TRANSPARENT);
                                    webViewFeature.setFocusableInTouchMode(false);
                                    webViewFeature.setFocusable(false);
                                    webViewFeature.getSettings().setDefaultTextEncodingName("UTF-8");
                                    webViewFeature.getSettings().setJavaScriptEnabled(true);
                                    webViewFeature.setNestedScrollingEnabled(false);

                                    String table_css = "";
                                    if (proDetailRP.getProduct_features().contains("<table")) {
                                        if (method.isDarkMode()) {
                                            table_css = "file:///android_asset/css/table_night.css";
                                        } else {
                                            table_css = "file:///android_asset/css/table.css";
                                        }
                                    }

                                    String text_feature = "<html dir=" + method.isWebViewTextRtl() + "><head>"
                                            + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/poppins_medium.ttf\")}body{font-family: MyFont;color: " + method.webViewText() + "line-height:1.6}"
                                            + "a {color:" + method.webViewLink() + "text-decoration:none}"
                                            + "</style>"
                                            + "<link rel=\"stylesheet\" href=\"" + table_css + "\">"
                                            + "</head>"
                                            + "<body>"
                                            + "<div class=\"description-content\">"
                                            + proDetailRP.getProduct_features()
                                            + "</div>"
                                            + "</body></html>";

                                    webViewFeature.loadDataWithBaseURL("blarg://ignored", text_feature, mimeType, encoding, "");

                                    if (proDetailRP.getProImageLists().size() != 0) {
                                        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
                                        ProViewAdapter proViewAdapter = new ProViewAdapter(getActivity(), proDetailRP.getProImageLists(), "pro_image");
                                        viewPager.setAdapter(proViewAdapter);
                                    }

                                    if (proDetailRP.getProSizeLists().size() != 0) {
                                        ProSizeAdapter proSizeAdapter = new ProSizeAdapter(getActivity(), "pro_size", onClickSend, proDetailRP.getProSizeLists(), productSize);
                                        recyclerViewSize.setAdapter(proSizeAdapter);
                                    }

                                    if (proDetailRP.getProColorLists().size() != 0) {
                                        ProColorAdapter proColorAdapter = new ProColorAdapter(getActivity(), "pro_color", onClickSend, proDetailRP.getProColorLists());
                                        recyclerViewColor.setAdapter(proColorAdapter);
                                    }

                                    if (proDetailRP.getProductLists().size() != 0) {
                                        llRelated.setVisibility(View.VISIBLE);
                                        productGridAdapter = new HomeProductAdapter(getActivity(), proDetailRP.getProductLists(), "related_pro", onClick);
                                        recyclerViewRelated.setAdapter(productGridAdapter);
                                    } else {
                                        llRelated.setVisibility(View.GONE);
                                    }

                                    if (proDetailRP.getProReviewLists().size() != 0) {

                                        cardViewReview.setVisibility(View.VISIBLE);

                                        proDetailProReviewAdapter = new ProDetailProReviewAdapter(getActivity(), proDetailRP.getProReviewLists());
                                        recyclerViewReview.setAdapter(proDetailProReviewAdapter);

                                        textViewReviewAll.setText(getResources().getString(R.string.view_all)
                                                + " " + "(" + proDetailRP.getProReviewLists().size() + ")");
                                        textViewReviewAll.setOnClickListener(v -> startActivity(new Intent(getActivity(), ProReview.class)
                                                .putExtra("product_id", proDetailRP.getId())));

                                    } else {
                                        cardViewReview.setVisibility(View.GONE);
                                    }

                                    relMain.setVisibility(View.VISIBLE);

                                    relFav.setOnClickListener(v -> {
                                        imageViewFav.startAnimation(myAnim);
                                        if (method.isLogin()) {
                                            favourite(userId, proDetailRP.getId());
                                        } else {
                                            Method.login(true);
                                            startActivity(new Intent(getActivity(), Login.class));
                                        }
                                    });

                                    relShare.setOnClickListener(v -> {
                                        imageViewShare.startAnimation(myAnim);
                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.setType("text/plain");
                                        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                                        intent.putExtra(Intent.EXTRA_TEXT, proDetailRP.getShare_link());
                                        startActivity(Intent.createChooser(intent, getResources().getString(R.string.choose_one)));
                                    });

                                    llSizeShow.setOnClickListener(v -> {
                                        BottomSheetDialogFragment bottomSheetDialogFragment = new SizeChart();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("url", proDetailRP.getSize_chart());
                                        bottomSheetDialogFragment.setArguments(bundle);
                                        bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
                                    });

                                    textViewByNow.setOnClickListener(v -> {
                                        if (method.isNetworkAvailable()) {
                                            if (method.isLogin()) {
                                                if (proSize != null) {
                                                    isAddress(userId, proDetailRP.getId());
                                                } else {
                                                    method.alertBox(getResources().getString(R.string.please_select_size));
                                                }
                                            } else {
                                                Method.login(true);
                                                startActivity(new Intent(getActivity(), Login.class));
                                            }
                                        } else {
                                            method.alertBox(getResources().getString(R.string.internet_connection));
                                        }
                                    });

                                    textViewAddToCard.setOnClickListener(v -> {
                                        if (method.isNetworkAvailable()) {
                                            if (method.isLogin()) {
                                                if (proSize != null) {
                                                    addToCart(proDetailRP.getId(), userId, "1");
                                                } else {
                                                    method.alertBox(getResources().getString(R.string.please_select_size));
                                                }
                                            } else {
                                                Method.login(true);
                                                startActivity(new Intent(getActivity(), Login.class));
                                            }
                                        } else {
                                            method.alertBox(getResources().getString(R.string.internet_connection));
                                        }
                                    });

                                } else {
                                    relNoData.setVisibility(View.VISIBLE);
                                    method.alertBox(proDetailRP.getMsg());
                                }

                            } else if (proDetailRP.getStatus().equals("2")) {
                                method.suspend(proDetailRP.getMessage());
                            } else {
                                relNoData.setVisibility(View.VISIBLE);
                                method.alertBox(proDetailRP.getMessage());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<ProDetailRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    relNoData.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    private void addToCart(String productId, String userId, String productQty) {

        if (getActivity() != null) {

            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("product_id", productId);
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("product_qty", productQty);
            jsObj.addProperty("buy_now", "false");
            jsObj.addProperty("product_size", proSize);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<AddToCartRP> call = apiService.addToCart(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<AddToCartRP>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NotNull Call<AddToCartRP> call, @NotNull Response<AddToCartRP> response) {

                    if (getActivity() != null) {

                        try {

                            AddToCartRP addToCartRP = response.body();

                            assert addToCartRP != null;
                            if (addToCartRP.getStatus().equals("1")) {

                                if (addToCartRP.getSuccess().equals("1")) {

                                    Events.CartItem cartItem = new Events.CartItem(addToCartRP.getTotal_item());
                                    GlobalBus.getBus().post(cartItem);
                                }

                                Toast.makeText(getActivity(), addToCartRP.getMsg(), Toast.LENGTH_SHORT).show();

                            } else if (addToCartRP.getStatus().equals("2")) {
                                method.suspend(addToCartRP.getMessage());
                            } else {
                                method.alertBox(addToCartRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressDialog.dismiss();

                }

                @Override
                public void onFailure(@NotNull Call<AddToCartRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    progressDialog.dismiss();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }
    }

    private void isAddress(String userId, String proId) {

        if (getActivity() != null) {

            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ISAddRP> call = apiService.isAddress(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<ISAddRP>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NotNull Call<ISAddRP> call, @NotNull Response<ISAddRP> response) {

                    if (getActivity() != null) {

                        try {

                            ISAddRP isAddRP = response.body();

                            assert isAddRP != null;
                            if (isAddRP.getStatus().equals("1")) {
                                if (isAddRP.getIs_address_avail().equals("true")) {
                                    startActivity(new Intent(getActivity(), OrderSummary.class)
                                            .putExtra("type", "by_now")
                                            .putExtra("product_size", proSize)
                                            .putExtra("product_id", proId));
                                } else {
                                    //add to address then after go to the order summary page
                                    startActivity(new Intent(getActivity(), AddAddress.class)
                                            .putExtra("type", "check_address")
                                            .putExtra("type_order", "by_now")
                                            .putExtra("product_size", proSize)
                                            .putExtra("product_id", proId));
                                }

                            } else if (isAddRP.getStatus().equals("2")) {
                                method.suspend(isAddRP.getMessage());
                            } else {
                                method.alertBox(isAddRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressDialog.dismiss();

                }

                @Override
                public void onFailure(@NotNull Call<ISAddRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    progressDialog.dismiss();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    private void favourite(String userId, String productId) {

        if (getActivity() != null) {

            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("product_id", productId);
            jsObj.addProperty("user_id", userId);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<FavouriteRP> call = apiService.addToFavourite(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<FavouriteRP>() {
                @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
                @Override
                public void onResponse(@NotNull Call<FavouriteRP> call, @NotNull Response<FavouriteRP> response) {

                    if (getActivity() != null) {

                        try {

                            FavouriteRP favouriteRP = response.body();

                            assert favouriteRP != null;
                            if (favouriteRP.getStatus().equals("1")) {

                                if (favouriteRP.getSuccess().equals("1")) {

                                    if (favouriteRP.getIs_favorite().equals("true")) {
                                        imageViewFav.setImageDrawable(getResources().getDrawable(R.drawable.fav_ic));
                                    } else {
                                        imageViewFav.setImageDrawable(getResources().getDrawable(R.drawable.unfav_ic));
                                    }
                                }

                                Toast.makeText(getActivity(), favouriteRP.getMsg(), Toast.LENGTH_SHORT).show();

                            } else if (favouriteRP.getStatus().equals("2")) {
                                method.suspend(favouriteRP.getMessage());
                            } else {
                                method.alertBox(favouriteRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressDialog.dismiss();

                }

                @Override
                public void onFailure(@NotNull Call<FavouriteRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    progressDialog.dismiss();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }
    }

}
