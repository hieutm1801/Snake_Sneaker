package hieutm.dev.snakesneaker.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.DecimalConverter;
import hieutm.dev.snakesneaker.util.Method;
import hieutm.dev.snakesneaker.response.PriceSelectionRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PriceSelectionFragment extends Fragment {

    private Method method;
    private String sign;
    private ProgressBar progressBar;
    private CrystalRangeSeekbar rangeSeekBar;
    private MaterialTextView textViewMin, textViewMax;
    private String distance = " ";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.price_fragment, container, false);

        method = new Method(getActivity());

        sign =distance + Constant.currency;

        assert getArguments() != null;
        String filterType = getArguments().getString("filter_type");
        String search = getArguments().getString("search");
        String id = getArguments().getString("id");

        progressBar = view.findViewById(R.id.progressBar_price_fragment);
        textViewMin = view.findViewById(R.id.textView_min_price_fragment);
        textViewMax = view.findViewById(R.id.textView_max_price_fragment);
        rangeSeekBar = view.findViewById(R.id.seekBar_price_fragment);

        if (method.isNetworkAvailable()) {
            priceFilter(filterType, search, id);
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        return view;
    }

    private void priceFilter(String type, String search, String id) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("type", type);
            jsObj.addProperty("id", id);
            jsObj.addProperty("pre_min", Constant.pre_min_temp);
            jsObj.addProperty("pre_max", Constant.pre_max_temp);
            if (type.equals("recent_viewed_products")) {
                jsObj.addProperty("user_id", method.userId());
            }
            if (type.equals("search")) {
                jsObj.addProperty("keyword", search);
            }
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<PriceSelectionRP> call = apiService.getPriceSelection(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<PriceSelectionRP>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NotNull Call<PriceSelectionRP> call, @NotNull Response<PriceSelectionRP> response) {

                    if (getActivity() != null) {
                        try {

                            PriceSelectionRP priceSelectionRP = response.body();

                            assert priceSelectionRP != null;
                            if (priceSelectionRP.getStatus().equals("1")) {

                                textViewMin.setText(DecimalConverter.currencyFormat(priceSelectionRP.getPre_price_min()) + sign);
                                textViewMax.setText(DecimalConverter.currencyFormat(priceSelectionRP.getPre_price_max()) + sign);

                                rangeSeekBar.setMinValue(Float.parseFloat(priceSelectionRP.getPrice_min()));
                                rangeSeekBar.setMaxValue(Float.parseFloat(priceSelectionRP.getPrice_max()));

                                rangeSeekBar.setMinStartValue(Float.parseFloat(priceSelectionRP.getPre_price_min()));
                                rangeSeekBar.setMaxStartValue(Float.parseFloat(priceSelectionRP.getPre_price_max()));

                                rangeSeekBar.apply();

                                rangeSeekBar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {

                                    textViewMin.setText(DecimalConverter.currencyFormat(minValue.toString()) + sign);
                                    textViewMax.setText(DecimalConverter.currencyFormat(maxValue.toString()) + sign);

                                    Constant.pre_min_temp = String.valueOf(minValue);
                                    Constant.pre_max_temp = String.valueOf(maxValue);

                                });

                            } else if (priceSelectionRP.getStatus().equals("2")) {
                                method.suspend(priceSelectionRP.getMessage());
                            } else {
                                method.alertBox(priceSelectionRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<PriceSelectionRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }
    }

}
