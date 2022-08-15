package hieutm.dev.snakesneaker.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.adapter.FilterListAdapter;
import hieutm.dev.snakesneaker.adapter.FilterSortByAdapter;
import hieutm.dev.snakesneaker.fragment.BrandSelectionFragment;
import hieutm.dev.snakesneaker.fragment.PriceSelectionFragment;
import hieutm.dev.snakesneaker.fragment.SizeSelectionFragment;
import hieutm.dev.snakesneaker.interfaces.FilterType;
import hieutm.dev.snakesneaker.item.FilterList;
import hieutm.dev.snakesneaker.response.FilterRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Filter extends AppCompatActivity {

    private Method method;
    private ProgressBar progressBar;
    private String sortBy;
    private FilterType filterType;
    private List<FilterList> filterLists;
    private FilterListAdapter filterListAdapter;
    private FilterSortByAdapter filterSortByAdapter;
    private RecyclerView recyclerViewFilter, recyclerViewSortBy;
    private RelativeLayout relMain, relNoData;
    private MaterialTextView textViewClose, textViewApply;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        method = new Method(Filter.this);
        method.forceRTLIfSupported();

        filterLists = new ArrayList<>();

        Intent intent = getIntent();
        String filterType = intent.getStringExtra("filter_type");//send to api filter type and get price and brand
        String search = intent.getStringExtra("search");
        String id = intent.getStringExtra("id");
        String sort = intent.getStringExtra("sort");

        MaterialToolbar toolbar = findViewById(R.id.toolbar_filter);
        toolbar.setTitle(getResources().getString(R.string.filter));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LinearLayout linearLayout = findViewById(R.id.linearLayout_filter);
        method.bannerAd(linearLayout);

        relMain = findViewById(R.id.rel_main_filter);
        relNoData = findViewById(R.id.rel_noDataFound);
        progressBar = findViewById(R.id.progressBar_filter);
        recyclerViewFilter = findViewById(R.id.recyclerView_filter);
        recyclerViewSortBy = findViewById(R.id.recyclerView_sortBy_filter);
        textViewClose = findViewById(R.id.textView_close_filter);
        textViewApply = findViewById(R.id.textView_apply_filter);

        relMain.setVisibility(View.GONE);
        relNoData.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        recyclerViewFilter.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerFilter = new LinearLayoutManager(Filter.this);
        recyclerViewFilter.setLayoutManager(layoutManagerFilter);
        recyclerViewFilter.setFocusable(false);
        recyclerViewFilter.setNestedScrollingEnabled(false);

        recyclerViewSortBy.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerSortBy = new LinearLayoutManager(Filter.this, RecyclerView.HORIZONTAL, false);
        recyclerViewSortBy.setLayoutManager(layoutManagerSortBy);
        recyclerViewSortBy.setFocusable(false);
        recyclerViewSortBy.setNestedScrollingEnabled(false);

        if (method.isNetworkAvailable()) {
            checkFilter(filterType, search, id, sort);
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    private void checkFilter(String type, String search, String id, String sort) {

        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Filter.this));
        jsObj.addProperty("type", type);
        jsObj.addProperty("id", id);
        jsObj.addProperty("sort", sort);
        if (type.equals("recent_viewed_products")) {
            jsObj.addProperty("user_id", method.userId());
        }
        if (type.equals("search")) {
            jsObj.addProperty("keyword", search);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<FilterRP> call = apiService.getFilterType(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<FilterRP>() {
            @SuppressLint({"SetTextI18n", "SetJavaScriptEnabled"})
            @Override
            public void onResponse(@NotNull Call<FilterRP> call, @NotNull Response<FilterRP> response) {

                try {

                    FilterRP filterRP = response.body();

                    assert filterRP != null;
                    if (filterRP.getStatus().equals("1")) {

                        Constant.brand_ids_temp = Constant.brand_ids;
                        Constant.sizes_temp = Constant.sizes;

                        Constant.pre_min_temp = Constant.pre_min;
                        Constant.pre_max_temp = Constant.pre_max;

                        filterLists.add(new FilterList(getResources().getString(R.string.price), "price", "true"));
                        if (filterRP.getBrand_filter().equals("true")) {
                            filterLists.add(new FilterList(getResources().getString(R.string.brand), "brand", filterRP.getBrand_filter()));
                        }
                        if (filterRP.getSize_status().equals("true")) {
                            filterLists.add(new FilterList(getResources().getString(R.string.size), "size", filterRP.getSize_status()));
                        }

                        filterType = (title_filter, type_filter, selectData) -> {

                            if (type_filter.equals("filter_list")) {
                                if (selectData.equals("price")) {
                                    PriceSelectionFragment priceSelectionFragment = new PriceSelectionFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("filter_type", type);
                                    bundle.putString("search", search);
                                    bundle.putString("id", id);
                                    bundle.putString("sort", sort);
                                    priceSelectionFragment.setArguments(bundle);
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.frameLayout_filter, priceSelectionFragment)
                                            .commitAllowingStateLoss();
                                } else if (selectData.equals("brand")) {

                                    BrandSelectionFragment brandSelectionFragment = new BrandSelectionFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("filter_type", type);
                                    bundle.putString("search", search);
                                    bundle.putString("id", id);
                                    bundle.putString("sort", sort);
                                    brandSelectionFragment.setArguments(bundle);
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.frameLayout_filter, brandSelectionFragment)
                                            .commitAllowingStateLoss();
                                } else {
                                    SizeSelectionFragment sizeSelectionFragment = new SizeSelectionFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("filter_type", type);
                                    bundle.putString("search", search);
                                    bundle.putString("id", id);
                                    bundle.putString("sort", sort);
                                    sizeSelectionFragment.setArguments(bundle);
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.frameLayout_filter, sizeSelectionFragment)
                                            .commitAllowingStateLoss();
                                }
                            } else {
                                sortBy = selectData;
                            }

                        };

                        filterSortByAdapter = new FilterSortByAdapter(Filter.this, "filter_sortBy", filterRP.getFilterSortByLists(), filterType);
                        recyclerViewSortBy.setAdapter(filterSortByAdapter);

                        filterListAdapter = new FilterListAdapter(Filter.this, "filter_list", filterLists, filterType);
                        recyclerViewFilter.setAdapter(filterListAdapter);

                        relMain.setVisibility(View.VISIBLE);

                        PriceSelectionFragment priceSelectionFragment = new PriceSelectionFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("filter_type", type);
                        bundle.putString("search", search);
                        bundle.putString("id", id);
                        bundle.putString("sort", sort);
                        priceSelectionFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frameLayout_filter, priceSelectionFragment)
                                .commitAllowingStateLoss();

                        textViewApply.setOnClickListener(v -> {
                            onBackPressed();
                            Constant.brand_ids = Constant.brand_ids_temp;
                            Constant.brand_ids_temp = "";

                            Constant.sizes = Constant.sizes_temp;
                            Constant.sizes_temp = "";

                            Constant.pre_min = Constant.pre_min_temp;
                            Constant.pre_max = Constant.pre_max_temp;

                            Constant.pre_min_temp = "";
                            Constant.pre_max_temp = "";

                            Events.Filter filter = new Events.Filter(id, Constant.brand_ids, Constant.sizes, sortBy, Constant.pre_min, Constant.pre_max);
                            GlobalBus.getBus().post(filter);
                        });

                        textViewClose.setOnClickListener(v -> {
                            Constant.brand_ids_temp = "";
                            Constant.sizes_temp = "";
                            Constant.pre_min_temp = "";
                            Constant.pre_max_temp = "";
                            onBackPressed();
                        });

                    } else if (filterRP.getStatus().equals("2")) {
                        method.suspend(filterRP.getMessage());
                    } else {
                        relNoData.setVisibility(View.VISIBLE);
                        method.alertBox(filterRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<FilterRP> call, @NotNull Throwable t) {
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
