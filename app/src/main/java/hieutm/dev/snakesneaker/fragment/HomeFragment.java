package hieutm.dev.snakesneaker.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.activity.MainActivity;
import hieutm.dev.snakesneaker.adapter.HomeBrandAdapter;
import hieutm.dev.snakesneaker.adapter.HomeCatAdapter;
import hieutm.dev.snakesneaker.adapter.HomeCatSubProAdapter;
import hieutm.dev.snakesneaker.adapter.HomeMyOrder;
import hieutm.dev.snakesneaker.adapter.HomeOfferAdapter;
import hieutm.dev.snakesneaker.adapter.HomeProductAdapter;
import hieutm.dev.snakesneaker.adapter.SliderAdapter;
import hieutm.dev.snakesneaker.interfaces.OnClick;
import hieutm.dev.snakesneaker.item.BrandList;
import hieutm.dev.snakesneaker.item.CategoryList;
import hieutm.dev.snakesneaker.item.HomeCatSubProList;
import hieutm.dev.snakesneaker.item.MyOrderList;
import hieutm.dev.snakesneaker.item.OfferList;
import hieutm.dev.snakesneaker.item.ProductList;
import hieutm.dev.snakesneaker.item.SliderList;
import hieutm.dev.snakesneaker.response.HomeRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.EnchantedViewPager;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private ProgressBar progressBar;
    private List<CategoryList> categoryLists;
    private List<MyOrderList> myOrderLists;
    private List<BrandList> brandLists;
    private List<SliderList> sliderLists;
    private List<ProductList> todayDealLists;
    private List<ProductList> latestLists;
    private List<ProductList> topRatedLists;
    private List<HomeCatSubProList> homeCatSubProLists;
    private List<ProductList> recentList;
    private List<OfferList> offerLists;
    private EnchantedViewPager enchantedViewPager;
    private HomeCatAdapter homeCatAdapter;
    private HomeMyOrder homeMyOrder;
    private HomeBrandAdapter homeBrandAdapter;
    private HomeProductAdapter homeTodayDealAdapter;
    private HomeProductAdapter homeLatestAdapter;
    private HomeProductAdapter homeTopRatedAdapter;
    private HomeCatSubProAdapter homeCatSubProAdapter;
    private HomeProductAdapter homeRecentAdapter;
    private HomeOfferAdapter homeOfferAdapter;
    private SliderAdapter sliderAdapter;
    private ImageView imageViewSearch;
    private TextInputEditText editTextSearch;
    private RelativeLayout relNoData, relIndicator;
    private LinearLayout llMain, llSlider, llMyOrder, llCat, llBrand, llTodayDeal, llLatest, llTopRated, llOffer, llRecent;
    private MaterialTextView textViewMyOrder, textViewCat, textViewBrand, textViewTodayDeal, textViewLatest, textViewTopRated, textViewOffer, textViewRecent;
    private RecyclerView recyclerViewMyOrder, recyclerViewCat, recyclerViewBrand, recyclerViewTodayDeal, recyclerViewLatest, recyclerViewTopRated, recyclerViewCatSubPro, recyclerViewOffer, recyclerViewRecent;

    private Timer timer;
    private Runnable update;
    private final long delayMs = 500;//delay in milliseconds before task is to be executed
    private final long periodMs = 3000;
    private final Handler handler = new Handler();

    private InputMethodManager imm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.home_fragment, container, false);

        method = new Method(getActivity(), onClick);
        if (MainActivity.toolbar != null) {
            if (method.isDarkMode()) {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.home) + "</font>"));
            } else {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + getResources().getString(R.string.home) + "</font>"));
            }
        }

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        categoryLists = new ArrayList<>();
        myOrderLists = new ArrayList<>();
        brandLists = new ArrayList<>();
        sliderLists = new ArrayList<>();
        todayDealLists = new ArrayList<>();
        latestLists = new ArrayList<>();
        topRatedLists = new ArrayList<>();
        homeCatSubProLists = new ArrayList<>();
        recentList = new ArrayList<>();
        offerLists = new ArrayList<>();

        onClick = (position, type, title, id, subId, subSubId, tag) -> {

            switch (type) {
                case "home_cat":
                    if (subId.equals("true")) {
                        SubCatFragment subCatFragment = new SubCatFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("cat_id", id);
                        bundle.putString("title", title);
                        subCatFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .add(R.id.frameLayout_main, subCatFragment, title).addToBackStack(title)
                                .commitAllowingStateLoss();
                    } else {
                        productFragment(id, "0", title, type, "");
                    }
                    break;
                case "home_my_order":
                    MyOrderDetailFragment myOrderDetailFragment = new MyOrderDetailFragment();
                    Bundle bundle_myOrder = new Bundle();
                    bundle_myOrder.putString("order_unique_id", id);
                    bundle_myOrder.putString("product_id", subId);
                    myOrderDetailFragment.setArguments(bundle_myOrder);
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main,
                            myOrderDetailFragment, getResources().getString(R.string.order_detail))
                            .addToBackStack(getResources().getString(R.string.order_detail)).commitAllowingStateLoss();
                    break;
                case "slider":
                case "home_brand":
                case "home_offer":
                    productFragment(id, "0", title, type, "");
                    break;
                case "home_today_deal":
                case "home_latest":
                case "home_top_rated":
                case "home_recent":
                case "home_catSub_pro":
                    ProDetailFragment proDetailFragment = new ProDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", id);
                    bundle.putString("title", title);
                    proDetailFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.frameLayout_main, proDetailFragment, title).addToBackStack(title)
                            .commitAllowingStateLoss();
                    break;
                default:
                    break;
            }
        };

        progressBar = view.findViewById(R.id.progressBar_home);
        relNoData = view.findViewById(R.id.rel_noDataFound);
        enchantedViewPager = view.findViewById(R.id.viewPager_home);
        relIndicator = view.findViewById(R.id.rel_indicator_home);
        textViewCat = view.findViewById(R.id.textView_viewAll_cat_home);
        textViewMyOrder = view.findViewById(R.id.textView_viewAll_myOrder_home);
        textViewTodayDeal = view.findViewById(R.id.textView_viewAll_todayDeal_home);
        textViewLatest = view.findViewById(R.id.textView_viewAll_latest_home);
        textViewTopRated = view.findViewById(R.id.textView_viewAll_topRated_home);
        textViewBrand = view.findViewById(R.id.textView_viewAll_brand_home);
        textViewOffer = view.findViewById(R.id.textView_viewAll_offer_home);
        textViewRecent = view.findViewById(R.id.textView_viewAll_recent_home);
        editTextSearch = view.findViewById(R.id.editText_home_fragment);
        imageViewSearch = view.findViewById(R.id.imageView_search_home_fragment);
        recyclerViewCat = view.findViewById(R.id.recyclerView_cat_home);
        recyclerViewMyOrder = view.findViewById(R.id.recyclerView_myOrder_home);
        recyclerViewBrand = view.findViewById(R.id.recyclerView_brand_home);
        recyclerViewTodayDeal = view.findViewById(R.id.recyclerView_todayDeal_home);
        recyclerViewLatest = view.findViewById(R.id.recyclerView_latest_home);
        recyclerViewTopRated = view.findViewById(R.id.recyclerView_topRated_home);
        recyclerViewCatSubPro = view.findViewById(R.id.recyclerView_catSubPro_home);
        recyclerViewOffer = view.findViewById(R.id.recyclerView_offer_home);
        recyclerViewRecent = view.findViewById(R.id.recyclerView_recent_home);
        llMain = view.findViewById(R.id.ll_main_home);
        llSlider = view.findViewById(R.id.ll_slider_home);
        llMyOrder = view.findViewById(R.id.ll_myOrder_home);
        llCat = view.findViewById(R.id.ll_cat_home);
        llBrand = view.findViewById(R.id.ll_brand_home);
        llTodayDeal = view.findViewById(R.id.ll_todayDeal_home);
        llLatest = view.findViewById(R.id.ll_latest_home);
        llTopRated = view.findViewById(R.id.ll_topRated_home);
        llOffer = view.findViewById(R.id.ll_offer_home);
        llRecent = view.findViewById(R.id.ll_recent_home);

        llMain.setVisibility(View.GONE);
        relNoData.setVisibility(View.GONE);

        recyclerViewMyOrder.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerMyOrder = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewMyOrder.setLayoutManager(layoutManagerMyOrder);
        recyclerViewMyOrder.setFocusable(false);
        recyclerViewMyOrder.setNestedScrollingEnabled(false);

        recyclerViewCat.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerCat = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCat.setLayoutManager(layoutManagerCat);
        recyclerViewCat.setFocusable(false);
        recyclerViewCat.setNestedScrollingEnabled(false);

        recyclerViewBrand.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerBrand = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewBrand.setLayoutManager(layoutManagerBrand);
        recyclerViewBrand.setFocusable(false);
        recyclerViewBrand.setNestedScrollingEnabled(false);

        recyclerViewTodayDeal.setHasFixedSize(false);
        LinearLayoutManager layoutManagerTodayDeal = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTodayDeal.setLayoutManager(layoutManagerTodayDeal);
        recyclerViewTodayDeal.setFocusable(false);
        recyclerViewTodayDeal.setNestedScrollingEnabled(false);

        recyclerViewLatest.setHasFixedSize(false);
        LinearLayoutManager layoutManagerLatest = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewLatest.setLayoutManager(layoutManagerLatest);
        recyclerViewLatest.setFocusable(false);
        recyclerViewLatest.setNestedScrollingEnabled(false);

        recyclerViewTopRated.setHasFixedSize(false);
        LinearLayoutManager layoutManagerTopRated = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTopRated.setLayoutManager(layoutManagerTopRated);
        recyclerViewTopRated.setFocusable(false);
        recyclerViewTopRated.setNestedScrollingEnabled(false);

        recyclerViewCatSubPro.setHasFixedSize(false);
        LinearLayoutManager layoutManagerCatSubPro = new LinearLayoutManager(getContext());
        recyclerViewCatSubPro.setLayoutManager(layoutManagerCatSubPro);
        recyclerViewCatSubPro.setFocusable(false);
        recyclerViewCatSubPro.setNestedScrollingEnabled(false);

        recyclerViewOffer.setHasFixedSize(false);
        LinearLayoutManager layoutManagerOffer = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewOffer.setLayoutManager(layoutManagerOffer);
        recyclerViewOffer.setFocusable(false);
        recyclerViewOffer.setNestedScrollingEnabled(false);

        recyclerViewRecent.setHasFixedSize(false);
        LinearLayoutManager layoutManagerRecent = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewRecent.setLayoutManager(layoutManagerRecent);
        recyclerViewRecent.setFocusable(false);
        recyclerViewRecent.setNestedScrollingEnabled(false);

        editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search();
            }
            return false;
        });

        imageViewSearch.setOnClickListener(v -> search());

        textViewMyOrder.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout_main, new MyOrderFragment(), getResources().getString(R.string.my_order))
                .addToBackStack(getResources().getString(R.string.my_order))
                .commitAllowingStateLoss());

        textViewCat.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout_main, new CategoryFragment(), getResources().getString(R.string.category))
                .addToBackStack(getResources().getString(R.string.category))
                .commitAllowingStateLoss());

        textViewBrand.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout_main, new BrandFragment(), getResources().getString(R.string.brand))
                .addToBackStack(getResources().getString(R.string.brand))
                .commitAllowingStateLoss());

        textViewTodayDeal.setOnClickListener(v -> productFragment("0", "0", getResources().getString(R.string.today_deal), "today_deal", ""));

        textViewLatest.setOnClickListener(v -> productFragment("0", "0", getResources().getString(R.string.latest_products), "home_latest", ""));

        textViewTopRated.setOnClickListener(v -> productFragment("0", "0", getResources().getString(R.string.top_rated_products), "home_top_rated", ""));

        textViewOffer.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout_main, new OfferFragment(), getResources().getString(R.string.offers))
                .addToBackStack(getResources().getString(R.string.offers))
                .commitAllowingStateLoss());

        textViewRecent.setOnClickListener(v -> productFragment("0", "0", getResources().getString(R.string.recent_view), "home_recent", ""));

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                home(method.userId());
            } else {
                home("0");
            }
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        return view;

    }

    private void search() {

        String search = editTextSearch.getText().toString();
        if (search.equals("") || search.isEmpty()) {
            method.alertBox(getResources().getString(R.string.please_enter_data));
        } else {

            editTextSearch.clearFocus();
            imm.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);

            productFragment("0", "0", search, "search_pro", search);

        }

    }

    @Subscribe
    public void geString(Events.CancelOrder cancelOrder) {
        for (int i = 0; i < myOrderLists.size(); i++) {
            for (int j = 0; j < cancelOrder.getMyOrderLists().size(); j++) {
                if (myOrderLists.get(i).getProduct_id().equals(cancelOrder.getMyOrderLists().get(j).getProduct_id())
                        && myOrderLists.get(i).getOrder_id().equals(cancelOrder.getMyOrderLists().get(j).getOrder_id())) {
                    myOrderLists.get(i).setOrder_status(cancelOrder.getMyOrderLists().get(j).getOrder_status());
                    myOrderLists.get(i).setCurrent_order_status(cancelOrder.getMyOrderLists().get(j).getCurrent_order_status());
                }
            }
        }
        if (homeMyOrder != null) {
            homeMyOrder.notifyDataSetChanged();
        }
    }

    private void productFragment(String id, String subCatId, String title, String type, String search) {

        ProductFragment productFragment = new ProductFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putString("title", title);
        bundle.putString("cat_id", id);
        bundle.putString("sub_cat_id", subCatId);
        bundle.putString("search", search);
        productFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout_main, productFragment, title).addToBackStack(title)
                .commitAllowingStateLoss();

    }

    private void home(String userId) {

        if (getActivity() != null) {

            sliderLists.clear();
            categoryLists.clear();
            brandLists.clear();
            todayDealLists.clear();
            recentList.clear();

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<HomeRP> call = apiService.getHome(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<HomeRP>() {
                @Override
                public void onResponse(@NotNull Call<HomeRP> call, @NotNull Response<HomeRP> response) {

                    if (getActivity() != null) {
                        try {

                            HomeRP homeRP = response.body();

                            assert homeRP != null;
                            if (homeRP.getStatus().equals("1")) {

                                sliderLists.addAll(homeRP.getSliderLists());
                                myOrderLists.addAll(homeRP.getMyOrderLists());
                                categoryLists.addAll(homeRP.getCategoryLists());
                                brandLists.addAll(homeRP.getBrandLists());
                                todayDealLists.addAll(homeRP.getTodayLists());
                                latestLists.addAll(homeRP.getLatestLists());
                                topRatedLists.addAll(homeRP.getTopRatedLists());
                                homeCatSubProLists.addAll(homeRP.getHomeCatSubProLists());
                                offerLists.addAll(homeRP.getOfferLists());
                                recentList.addAll(homeRP.getRecentViewList());

                                if (sliderLists.size() != 0) {

                                    int columnWidth = method.getScreenWidth();
                                    enchantedViewPager.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 2));

                                    enchantedViewPager.useScale();
                                    enchantedViewPager.removeAlpha();

                                    if (sliderLists.size() > 1) {
                                        relIndicator.setVisibility(View.VISIBLE);
                                    } else {
                                        relIndicator.setVisibility(View.GONE);
                                    }

                                    sliderAdapter = new SliderAdapter(getActivity(), "slider", sliderLists, onClick);
                                    enchantedViewPager.setAdapter(sliderAdapter);

                                    update = () -> {
                                        if (enchantedViewPager.getCurrentItem() == (sliderAdapter.getCount() - 1)) {
                                            enchantedViewPager.setCurrentItem(0, true);
                                        } else {
                                            enchantedViewPager.setCurrentItem(enchantedViewPager.getCurrentItem() + 1, true);
                                        }
                                    };

                                    if (sliderAdapter.getCount() > 1) {
                                        timer = new Timer(); // This will create a new Thread
                                        timer.schedule(new TimerTask() { // task to be scheduled
                                            @Override
                                            public void run() {
                                                handler.post(update);
                                            }
                                        }, delayMs, periodMs);
                                    }

                                } else {
                                    llSlider.setVisibility(View.GONE);
                                }

                                if (myOrderLists.size() != 0) {
                                    homeMyOrder = new HomeMyOrder(getActivity(), myOrderLists, "home_my_order", onClick);
                                    recyclerViewMyOrder.setAdapter(homeMyOrder);
                                } else {
                                    llMyOrder.setVisibility(View.GONE);
                                }

                                if (categoryLists.size() != 0) {
                                    homeCatAdapter = new HomeCatAdapter(getActivity(), categoryLists, "home_cat", onClick);
                                    recyclerViewCat.setAdapter(homeCatAdapter);
                                } else {
                                    llCat.setVisibility(View.GONE);
                                }

                                if (brandLists.size() != 0) {
                                    homeBrandAdapter = new HomeBrandAdapter(getActivity(), brandLists, "home_brand", onClick);
                                    recyclerViewBrand.setAdapter(homeBrandAdapter);
                                } else {
                                    llBrand.setVisibility(View.GONE);
                                }

                                if (todayDealLists.size() != 0) {
                                    homeTodayDealAdapter = new HomeProductAdapter(getActivity(), todayDealLists, "home_today_deal", onClick);
                                    recyclerViewTodayDeal.setAdapter(homeTodayDealAdapter);
                                } else {
                                    llTodayDeal.setVisibility(View.GONE);
                                }

                                if (latestLists.size() != 0) {
                                    homeLatestAdapter = new HomeProductAdapter(getActivity(), latestLists, "home_latest", onClick);
                                    recyclerViewLatest.setAdapter(homeLatestAdapter);
                                } else {
                                    llLatest.setVisibility(View.GONE);
                                }

                                if (topRatedLists.size() != 0) {
                                    homeTopRatedAdapter = new HomeProductAdapter(getActivity(), topRatedLists, "home_top_rated", onClick);
                                    recyclerViewTopRated.setAdapter(homeTopRatedAdapter);
                                } else {
                                    llTopRated.setVisibility(View.GONE);
                                }

                                if (homeCatSubProLists.size() != 0) {
                                    recyclerViewCatSubPro.setVisibility(View.VISIBLE);
                                    homeCatSubProAdapter = new HomeCatSubProAdapter(getActivity(), homeCatSubProLists, "home_catSub_pro", onClick);
                                    recyclerViewCatSubPro.setAdapter(homeCatSubProAdapter);
                                } else {
                                    recyclerViewCatSubPro.setVisibility(View.GONE);
                                }

                                if (offerLists.size() != 0) {
                                    homeOfferAdapter = new HomeOfferAdapter(getActivity(), offerLists, "home_offer", onClick);
                                    recyclerViewOffer.setAdapter(homeOfferAdapter);
                                } else {
                                    llOffer.setVisibility(View.GONE);
                                }

                                if (recentList.size() != 0) {
                                    llRecent.setVisibility(View.VISIBLE);
                                    homeRecentAdapter = new HomeProductAdapter(getActivity(), recentList, "home_recent", onClick);
                                    recyclerViewRecent.setAdapter(homeRecentAdapter);
                                } else {
                                    llRecent.setVisibility(View.GONE);
                                }

                                llMain.setVisibility(View.VISIBLE);

                            } else if (homeRP.getStatus().equals("2")) {
                                method.suspend(homeRP.getMessage());
                            } else {
                                relNoData.setVisibility(View.VISIBLE);
                                method.alertBox(homeRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<HomeRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    relNoData.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

}
