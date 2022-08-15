package hieutm.dev.snakesneaker.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.activity.Filter;
import hieutm.dev.snakesneaker.activity.MainActivity;
import hieutm.dev.snakesneaker.adapter.ProductAdapter;
import hieutm.dev.snakesneaker.interfaces.OnClick;
import hieutm.dev.snakesneaker.item.ProductList;
import hieutm.dev.snakesneaker.response.ProRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.EndlessRecyclerViewScrollListener;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private List<ProductList> productLists;
    private ProductAdapter productAdapter;
    private boolean isGrid = false, isFilter = false;
    private LinearLayout llFilter;
    private ImageView imageViewGrid, imageViewList;
    private MaterialTextView textViewTotalItem;
    private RelativeLayout relNoData;
    private Boolean isOver = false;
    private int paginationIndex = 1;
    private EndlessRecyclerViewScrollListener scrollListener;
    private String type, title, catId, subCatId, search, filterType, filterId = "0", brandIds = "", sizes = "", sortBy = "", preMin = "", preMax = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.product_fragment, container, false);

        GlobalBus.getBus().register(this);

        assert getArguments() != null;
        catId = getArguments().getString("cat_id");
        subCatId = getArguments().getString("sub_cat_id");
        title = getArguments().getString("title");
        search = getArguments().getString("search");
        type = getArguments().getString("type");

        method = new Method(getActivity(), onClick);
        if (MainActivity.toolbar != null) {
            if (method.isDarkMode()) {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + title + "</font>"));
            } else {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + title + "</font>"));
            }
        }

        //clear filter variable
        Constant.brand_ids = "";
        Constant.sizes = "";
        Constant.pre_min = "";
        Constant.pre_max = "";

        productLists = new ArrayList<>();

        onClick = (position, sendType, sendTitle, id, subId, subSubId, tag) -> {
            ProDetailFragment proDetailFragment = new ProDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            bundle.putString("title", sendTitle);
            proDetailFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameLayout_main, proDetailFragment, sendTitle).addToBackStack(sendTitle)
                    .commitAllowingStateLoss();
        };

        progressBar = view.findViewById(R.id.progressBar_pro_fragment);
        textViewTotalItem = view.findViewById(R.id.textView_pro_fragment);
        relNoData = view.findViewById(R.id.rel_noDataFound);
        recyclerView = view.findViewById(R.id.recyclerView_pro_fragment);
        imageViewGrid = view.findViewById(R.id.imageView_grid_pro_fragment);
        imageViewList = view.findViewById(R.id.imageView_list_pro_fragment);
        llFilter = view.findViewById(R.id.ll_filter_pro_fragment);

        relNoData.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManagerGrid = new GridLayoutManager(getActivity(), 2);
        layoutManagerGrid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (productAdapter != null) {
                    if (productAdapter.getItemViewType(position) == 0) {
                        return 2;
                    }
                    return 1;
                }
                return 1;
            }
        });
        recyclerView.setLayoutManager(layoutManagerGrid);
        loadMoreData(layoutManagerGrid);
        recyclerView.setFocusable(false);

        imageViewGrid.setOnClickListener(v -> {
            isGrid = true;
            viewChange();
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (productAdapter != null) {
                        if (productAdapter.getItemViewType(position) == 0) {
                            return 2;
                        }
                        return 1;
                    }
                    return 1;
                }
            });
            recyclerView.setLayoutManager(gridLayoutManager);
            loadMoreData(gridLayoutManager);
            productAdapter = new ProductAdapter(getActivity(), productLists, "product", onClick, isGrid);
            recyclerView.setAdapter(productAdapter);
        });

        imageViewList.setOnClickListener(v -> {
            isGrid = false;
            viewChange();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            loadMoreData(layoutManager);
            productAdapter = new ProductAdapter(getActivity(), productLists, "product", onClick, isGrid);
            recyclerView.setAdapter(productAdapter);
        });

        isGrid = true;
        viewChange();
        callData();

        return view;

    }

    @Subscribe
    public void geString(Events.Filter filter) {
        isFilter = true;
        relNoData.setVisibility(View.GONE);
        if (method.isNetworkAvailable()) {

            filterId = filter.getId();
            brandIds = filter.getBrand_ids();
            sizes = filter.getSize();
            sortBy = filter.getSortBy();
            preMin = filter.getPre_min();
            preMax = filter.getPre_max();

            productLists.clear();
            if (productAdapter != null) {
                productAdapter.notifyDataSetChanged();
            }
            scrollListener.resetState();
            paginationIndex = 1;
            isOver = false;
            productAdapter = null;
            filter(filterId, brandIds, sizes, sortBy, preMin, preMax);

        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    private void callData() {
        if (method.isNetworkAvailable()) {
            subCategory(catId, subCatId, search);
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    public void loadMoreData(RecyclerView.LayoutManager layoutManager) {
        scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(() -> {
                        paginationIndex++;
                        if (isFilter) {
                            filter(filterId, brandIds, sizes, sortBy, preMin, preMax);
                        } else {
                            callData();
                        }
                    }, 1000);
                } else {
                    productAdapter.hideHeader();
                }
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void viewChange() {
        if (isGrid) {
            imageViewGrid.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid_hov));
            imageViewList.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
        } else {
            imageViewGrid.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid));
            imageViewList.setImageDrawable(getResources().getDrawable(R.drawable.ic_list_hov));
        }
    }

    private void subCategory(String catId, String subCatId, String search) {

        if (getActivity() != null) {

            if (productAdapter == null) {
                productLists.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("page", paginationIndex);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ProRP> call;
            switch (type) {
                case "brand":
                case "home_brand":
                    filterType = "brand";
                    jsObj.addProperty("brand_id", catId);
                    call = apiService.getBrandPro(API.toBase64(jsObj.toString()));
                    break;
                case "slider":
                    filterType = "banner";
                    jsObj.addProperty("banner_id", catId);
                    call = apiService.getSliderPro(API.toBase64(jsObj.toString()));
                    break;
                case "today_deal":
                    filterType = "today_deal";
                    call = apiService.getTodayDealPro(API.toBase64(jsObj.toString()));
                    break;
                case "home_latest":
                    filterType = "latest_products";
                    call = apiService.getLatestPro(API.toBase64(jsObj.toString()));
                    break;
                case "home_top_rated":
                    filterType = "top_rated_products";
                    call = apiService.getTopRatedPro(API.toBase64(jsObj.toString()));
                    break;
                case "home_recent":
                    filterType = "recent_viewed_products";
                    jsObj.addProperty("user_id", method.userId());
                    call = apiService.getRecentPro(API.toBase64(jsObj.toString()));
                    break;
                case "offer":
                case "home_offer":
                    filterType = "offer";
                    jsObj.addProperty("offer_id", catId);
                    call = apiService.getOfferPro(API.toBase64(jsObj.toString()));
                    break;
                case "search_pro":
                    filterType = "search";
                    jsObj.addProperty("keyword", search);
                    call = apiService.getSearchPro(API.toBase64(jsObj.toString()));
                    break;
                case "home_catSub_pro":
                    filterType = "productList_cat";
                    jsObj.addProperty("cat_id", catId);
                    jsObj.addProperty("sub_cat_id", subCatId);
                    call = apiService.getCatSubPro(API.toBase64(jsObj.toString()));
                    break;
                default:
                    filterType = "productList_cat_sub";
                    jsObj.addProperty("cat_id", catId);
                    jsObj.addProperty("sub_cat_id", subCatId);
                    call = apiService.getCatSubPro(API.toBase64(jsObj.toString()));
                    break;
            }
            call.enqueue(new Callback<ProRP>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NotNull Call<ProRP> call, @NotNull Response<ProRP> response) {

                    if (getActivity() != null) {
                        try {

                            ProRP proRP = response.body();

                            assert proRP != null;
                            if (proRP.getStatus().equals("1")) {

                                textViewTotalItem.setText(proRP.getTotal_products() + " " + getResources().getString(R.string.items));

                                if (proRP.getProductLists().size() == 0) {
                                    if (productAdapter != null) {
                                        productAdapter.hideHeader();
                                        isOver = true;
                                    }
                                } else {
                                    productLists.addAll(proRP.getProductLists());
                                }

                                if (productAdapter == null) {

                                    if (productLists.size() != 0) {

                                        productAdapter = new ProductAdapter(getActivity(), productLists, "product", onClick, isGrid);
                                        recyclerView.setAdapter(productAdapter);

                                        llFilter.setOnClickListener(v -> {

                                            String id;
                                            if (filterType.equals("productList_cat_sub")) {
                                                id = subCatId;
                                            } else {
                                                id = catId;
                                            }
                                            startActivity(new Intent(getActivity(), Filter.class)
                                                    .putExtra("filter_type", filterType)//using which type of filter like as banner brand etc..
                                                    .putExtra("search", search)
                                                    .putExtra("id", id)
                                                    .putExtra("sort", "newest"));
                                        });

                                    } else {
                                        relNoData.setVisibility(View.VISIBLE);
                                    }

                                } else {
                                    productAdapter.notifyDataSetChanged();
                                }


                            } else if (proRP.getStatus().equals("2")) {
                                method.suspend(proRP.getMessage());
                            } else {
                                relNoData.setVisibility(View.VISIBLE);
                                method.alertBox(proRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            isOver = true;
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<ProRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    isOver = true;
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }
    }

    private void filter(String id, String brandIds, String sizes, String sort, String minPrice, String maxPrice) {

        if (getActivity() != null) {

            if (productAdapter == null) {
                productLists.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("type", filterType);
            jsObj.addProperty("id", id);
            jsObj.addProperty("brand_ids", brandIds);
            jsObj.addProperty("sizes", sizes);
            jsObj.addProperty("sort", sort);
            jsObj.addProperty("min_price", minPrice);
            jsObj.addProperty("max_price", maxPrice);
            jsObj.addProperty("page", paginationIndex);
            if (filterType.equals("recent_viewed_products")) {
                jsObj.addProperty("user_id", method.userId());
            }
            if (filterType.equals("search")) {
                jsObj.addProperty("keyword", search);
            }
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ProRP> call = apiService.getFilterPro(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<ProRP>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NotNull Call<ProRP> call, @NotNull Response<ProRP> response) {

                    if (getActivity() != null) {
                        try {

                            ProRP proRP = response.body();

                            assert proRP != null;
                            if (proRP.getStatus().equals("1")) {

                                textViewTotalItem.setText(proRP.getTotal_products() + " " + getResources().getString(R.string.items));

                                if (proRP.getProductLists().size() == 0) {
                                    if (productAdapter != null) {
                                        productAdapter.hideHeader();
                                        isOver = true;
                                    }
                                } else {
                                    productLists.addAll(proRP.getProductLists());
                                }

                                if (productAdapter == null) {

                                    if (productLists.size() != 0) {

                                        productAdapter = new ProductAdapter(getActivity(), productLists, "product", onClick, isGrid);
                                        recyclerView.setAdapter(productAdapter);

                                        llFilter.setOnClickListener(v -> startActivity(new Intent(getActivity(), Filter.class)
                                                .putExtra("filter_type", filterType)
                                                .putExtra("search", search)
                                                .putExtra("id", id)
                                                .putExtra("sort", sort)));

                                    } else {
                                        relNoData.setVisibility(View.VISIBLE);
                                    }

                                } else {
                                    productAdapter.notifyDataSetChanged();
                                }

                            } else if (proRP.getStatus().equals("2")) {
                                method.suspend(proRP.getMessage());
                            } else {
                                relNoData.setVisibility(View.VISIBLE);
                                method.alertBox(proRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            isOver = true;
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<ProRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    isOver = true;
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the registered event.
        GlobalBus.getBus().unregister(this);
    }

}
