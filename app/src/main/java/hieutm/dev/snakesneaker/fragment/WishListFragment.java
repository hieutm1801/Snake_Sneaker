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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.activity.Login;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishListFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private boolean isGrid = false;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private List<ProductList> productLists;
    private ProductAdapter productAdapter;
    private ImageView imageView;
    private MaterialButton button;
    private MaterialTextView textViewNoData;
    private LinearLayout llMain, llEmpty;
    private MaterialCardView cardViewControl;
    private ImageView imageViewGrid, imageViewList;
    private MaterialTextView textViewTotalItem;
    private Boolean isOver = false;
    private int paginationIndex = 1;
    private EndlessRecyclerViewScrollListener scrollListener;

    //Same name value tag
    private String product = "product";


    @SuppressLint("UseCompatLoadingForDrawables")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.wishlist_fragment, container, false);

        method = new Method(getActivity(), onClick);
        productLists = new ArrayList<>();
        if (MainActivity.toolbar != null) {
            if (method.isDarkMode()) {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.wish_list) + "</font>"));
            } else {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + getResources().getString(R.string.wish_list) + "</font>"));
            }
        }

        onClick = (position, type, title, id, sub_id, sub_sub_id, tag) -> {
            ProDetailFragment proDetailFragment = new ProDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            bundle.putString("title", title);
            proDetailFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameLayout_main, proDetailFragment, title).addToBackStack(title)
                    .commitAllowingStateLoss();
        };

        llMain = view.findViewById(R.id.ll_main_wishList_fragment);
        llEmpty = view.findViewById(R.id.ll_wishList_fragment);
        imageView = view.findViewById(R.id.imageView_wishList_fragment);
        textViewNoData = view.findViewById(R.id.textView_noData_wishList_fragment);
        button = view.findViewById(R.id.button_wishList_fragment);
        progressBar = view.findViewById(R.id.progressBar_wishList_fragment);
        cardViewControl = view.findViewById(R.id.cardView_control_wishList_fragment);
        textViewTotalItem = view.findViewById(R.id.textView_wishList_fragment);
        recyclerView = view.findViewById(R.id.recyclerView_wishList_fragment);
        imageViewGrid = view.findViewById(R.id.imageView_grid_wishList_fragment);
        imageViewList = view.findViewById(R.id.imageView_list_wishList_fragment);

        llMain.setVisibility(View.GONE);
        llEmpty.setVisibility(View.GONE);

        cardViewControl.setOnClickListener(v -> {

        });

        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager_gr = new GridLayoutManager(getActivity(), 2);
        layoutManager_gr.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
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
        recyclerView.setLayoutManager(layoutManager_gr);
        loadMoreData(layoutManager_gr);
        recyclerView.setFocusable(false);

        imageViewGrid.setOnClickListener(viewGrid -> {
            isGrid = true;
            viewChange();
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
            productAdapter = new ProductAdapter(getActivity(), productLists, product, onClick, isGrid);
            recyclerView.setAdapter(productAdapter);
        });

        imageViewList.setOnClickListener(v -> {
            isGrid = false;
            viewChange();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            loadMoreData(layoutManager);
            productAdapter = new ProductAdapter(getActivity(), productLists, product, onClick, isGrid);
            recyclerView.setAdapter(productAdapter);
        });

        isGrid = true;
        viewChange();
        callData();

        return view;
    }

    public void backStackRemove() {
        for (int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void callData() {
        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                favourite(method.userId());
            } else {
                progressBar.setVisibility(View.GONE);
                changView(true);
            }
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void changView(boolean isValue) {
        llEmpty.setVisibility(View.VISIBLE);
        if (isValue) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_login));
            textViewNoData.setText(getResources().getString(R.string.you_have_not_login));
            button.setText(getResources().getString(R.string.login));
        } else {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.empty_wish_list));
            textViewNoData.setText(getResources().getString(R.string.no_data_wistList));
            button.setText(getResources().getString(R.string.start_shopping));
        }
        button.setOnClickListener(v -> {
            if (isValue) {
                startActivity(new Intent(getActivity(), Login.class));
            } else {
                Events.GoToHome goToHome = new Events.GoToHome("");
                GlobalBus.getBus().post(goToHome);
                backStackRemove();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new HomeFragment(),
                        getResources().getString(R.string.home)).commitAllowingStateLoss();
            }
        });
    }

    public void loadMoreData(RecyclerView.LayoutManager layoutManager) {
        scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(() -> {
                        paginationIndex++;
                        callData();
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

    private void favourite(String userId) {

        if (getActivity() != null) {

            if (productAdapter == null) {
                productLists.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("page", paginationIndex);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ProRP> call = apiService.getFavourite(API.toBase64(jsObj.toString()));
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
                                        llMain.setVisibility(View.VISIBLE);
                                        productAdapter = new ProductAdapter(getActivity(), productLists, product, onClick, isGrid);
                                        recyclerView.setAdapter(productAdapter);
                                    } else {
                                        changView(false);
                                    }
                                } else {
                                    productAdapter.notifyDataSetChanged();
                                }

                            } else if (proRP.getStatus().equals("2")) {
                                method.suspend(proRP.getMessage());
                            } else {
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

}


