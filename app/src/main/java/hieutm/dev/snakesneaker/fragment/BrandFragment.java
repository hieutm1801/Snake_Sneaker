package hieutm.dev.snakesneaker.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.activity.MainActivity;
import hieutm.dev.snakesneaker.adapter.BrandAdapter;
import hieutm.dev.snakesneaker.interfaces.OnClick;
import hieutm.dev.snakesneaker.item.BrandList;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.EndlessRecyclerViewScrollListener;
import hieutm.dev.snakesneaker.util.Method;
import hieutm.dev.snakesneaker.response.BrandRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrandFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private ProgressBar progressBar;
    private List<BrandList> brandLists;
    private RecyclerView recyclerView;
    private BrandAdapter brandAdapter;
    private RelativeLayout relNoData;
    private Boolean isOver = false;
    private int paginationIndex = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.category_fragment, container, false);

        method = new Method(getActivity(), onClick);
        brandLists = new ArrayList<>();
        if (MainActivity.toolbar != null) {
            if (method.isDarkMode()) {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.brand) + "</font>"));
            } else {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + getResources().getString(R.string.brand) + "</font>"));
            }
        }

        onClick = (position, type, title, id, sub_id, sub_sub_id, tag) -> {
            ProductFragment productFragment = new ProductFragment();
            Bundle bundle = new Bundle();
            bundle.putString("type", type);
            bundle.putString("title", title);
            bundle.putString("cat_id", id);
            bundle.putString("sub_cat_id", "0");
            bundle.putString("search", "");
            productFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameLayout_main, productFragment, title).addToBackStack(title)
                    .commitAllowingStateLoss();
        };

        progressBar = view.findViewById(R.id.progressBar_category);
        relNoData = view.findViewById(R.id.rel_noDataFound);
        recyclerView = view.findViewById(R.id.recyclerView_category);
        relNoData.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (brandAdapter.getItemViewType(position) == 0) {
                    return 3;
                }
                return 1;
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(() -> {
                        paginationIndex++;
                        callData();
                    }, 1000);
                } else {
                    brandAdapter.hideHeader();
                }
            }
        });

        callData();

        return view;
    }

    private void callData() {
        if (method.isNetworkAvailable()) {
            brand();
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    private void brand() {

        if (getActivity() != null) {

            if (brandAdapter == null) {
                brandLists.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("page", paginationIndex);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<BrandRP> call = apiService.getBrand(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<BrandRP>() {
                @Override
                public void onResponse(@NotNull Call<BrandRP> call, @NotNull Response<BrandRP> response) {

                    if (getActivity() != null) {
                        try {

                            BrandRP brandRP = response.body();

                            assert brandRP != null;
                            if (brandRP.getStatus().equals("1")) {

                                if (brandRP.getBrandLists().size() == 0) {
                                    if (brandAdapter != null) {
                                        brandAdapter.hideHeader();
                                        isOver = true;
                                    }
                                } else {
                                    brandLists.addAll(brandRP.getBrandLists());
                                }

                                if (brandAdapter == null) {
                                    if (brandLists.size() != 0) {
                                        brandAdapter = new BrandAdapter(getActivity(), brandLists, "brand", onClick);
                                        recyclerView.setAdapter(brandAdapter);
                                    } else {
                                        relNoData.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    brandAdapter.notifyDataSetChanged();
                                }

                            } else {
                                relNoData.setVisibility(View.VISIBLE);
                                method.alertBox(brandRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<BrandRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }
    }

}
