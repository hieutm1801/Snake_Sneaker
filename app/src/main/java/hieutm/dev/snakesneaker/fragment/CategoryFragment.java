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
import hieutm.dev.snakesneaker.adapter.CategoryAdapter;
import hieutm.dev.snakesneaker.interfaces.OnClick;
import hieutm.dev.snakesneaker.item.CategoryList;
import hieutm.dev.snakesneaker.response.CategoryRP;
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

public class CategoryFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private ProgressBar progressBar;
    private List<CategoryList> categoryLists;
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private RelativeLayout relNoData;
    private Boolean isOver = false;
    private int paginationIndex = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.category_fragment, container, false);

        method = new Method(getActivity(), onClick);
        categoryLists = new ArrayList<>();
        if (MainActivity.toolbar != null) {
            if (method.isDarkMode()) {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.category) + "</font>"));
            } else {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + getResources().getString(R.string.category) + "</font>"));
            }
        }

        onClick = (position, type, title, id, sub_id, sub_sub_id, tag) -> {
            if (sub_id.equals("true")) {
                SubCatFragment subCatFragment = new SubCatFragment();
                Bundle bundle = new Bundle();
                bundle.putString("cat_id", id);
                bundle.putString("title", title);
                subCatFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.frameLayout_main, subCatFragment, title).addToBackStack(title)
                        .commitAllowingStateLoss();
            } else {
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
            }

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
                if (categoryAdapter.getItemViewType(position) == 0) {
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
                    categoryAdapter.hideHeader();
                }
            }
        });

        callData();

        return view;
    }

    private void callData() {
        if (method.isNetworkAvailable()) {
            category();
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    private void category() {

        if (getActivity() != null) {

            if (categoryAdapter == null) {
                categoryLists.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("page", paginationIndex);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<CategoryRP> call = apiService.getCategory(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<CategoryRP>() {
                @Override
                public void onResponse(@NotNull Call<CategoryRP> call, @NotNull Response<CategoryRP> response) {

                    if (getActivity() != null) {

                        try {

                            CategoryRP categoryRP = response.body();

                            assert categoryRP != null;
                            if (categoryRP.getStatus().equals("1")) {

                                if (categoryRP.getCategoryLists().size() == 0) {
                                    if (categoryAdapter != null) {
                                        categoryAdapter.hideHeader();
                                        isOver = true;
                                    }
                                } else {
                                    categoryLists.addAll(categoryRP.getCategoryLists());
                                }

                                if (categoryAdapter == null) {
                                    if (categoryLists.size() != 0) {
                                        categoryAdapter = new CategoryAdapter(getActivity(), categoryLists, "cat", onClick);
                                        recyclerView.setAdapter(categoryAdapter);
                                    } else {
                                        relNoData.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    categoryAdapter.notifyDataSetChanged();
                                }

                            } else {
                                relNoData.setVisibility(View.VISIBLE);
                                method.alertBox(categoryRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<CategoryRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }
}

