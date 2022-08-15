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

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.activity.MainActivity;
import hieutm.dev.snakesneaker.adapter.SubCatAdapter;
import hieutm.dev.snakesneaker.interfaces.OnClick;
import hieutm.dev.snakesneaker.item.SubCategoryList;
import hieutm.dev.snakesneaker.response.SubCatRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.EndlessRecyclerViewScrollListener;
import hieutm.dev.snakesneaker.util.Method;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubCatFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private String catId;
    private ProgressBar progressBar;
    private List<SubCategoryList> subCategoryLists;
    private RecyclerView recyclerView;
    private SubCatAdapter subCatAdapter;
    private RelativeLayout relNoData;
    private Boolean isOver = false;
    private int paginationIndex = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.category_fragment, container, false);

        assert getArguments() != null;
        catId = getArguments().getString("cat_id");
        String titleToolBar = getArguments().getString("title");

        subCategoryLists = new ArrayList<>();

        onClick = (position, type, title, id, sub_id, sub_sub_id, tag) -> {
            ProductFragment productFragment = new ProductFragment();
            Bundle bundle = new Bundle();
            bundle.putString("type", type);
            bundle.putString("title", title);
            bundle.putString("cat_id", id);
            bundle.putString("sub_cat_id", sub_id);
            bundle.putString("search", "");
            productFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameLayout_main, productFragment, title).addToBackStack(title)
                    .commitAllowingStateLoss();
        };
        method = new Method(getActivity(), onClick);
        if (MainActivity.toolbar != null) {
            if (method.isDarkMode()) {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + titleToolBar + "</font>"));
            } else {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + titleToolBar + "</font>"));
            }
        }

        progressBar = view.findViewById(R.id.progressBar_category);
        relNoData = view.findViewById(R.id.rel_noDataFound);
        recyclerView = view.findViewById(R.id.recyclerView_category);
        relNoData.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (subCatAdapter.getItemViewType(position) == 0) {
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
                    subCatAdapter.hideHeader();
                }
            }
        });

        callData();

        return view;
    }

    private void callData() {
        if (method.isNetworkAvailable()) {
            subCategory(catId);
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    private void subCategory(String catId) {

        if (getActivity() != null) {

            if (subCatAdapter == null) {
                subCategoryLists.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("cat_id", catId);
            jsObj.addProperty("page", paginationIndex);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<SubCatRP> call = apiService.getSubCat(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<SubCatRP>() {
                @Override
                public void onResponse(@NotNull Call<SubCatRP> call, @NotNull Response<SubCatRP> response) {

                    if (getActivity() != null) {
                        try {

                            SubCatRP subCatRP = response.body();

                            assert subCatRP != null;
                            if (subCatRP.getStatus().equals("1")) {

                                if (subCatRP.getSubCategoryLists().size() == 0) {
                                    if (subCatAdapter != null) {
                                        subCatAdapter.hideHeader();
                                        isOver = true;
                                    }
                                } else {
                                    subCategoryLists.addAll(subCatRP.getSubCategoryLists());
                                }

                                if (subCatAdapter == null) {
                                    if (subCategoryLists.size() != 0) {
                                        subCatAdapter = new SubCatAdapter(getActivity(), subCategoryLists, "cat", onClick);
                                        recyclerView.setAdapter(subCatAdapter);
                                    } else {
                                        relNoData.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    subCatAdapter.notifyDataSetChanged();
                                }

                            } else {
                                relNoData.setVisibility(View.VISIBLE);
                                method.alertBox(subCatRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<SubCatRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }
    }

}
