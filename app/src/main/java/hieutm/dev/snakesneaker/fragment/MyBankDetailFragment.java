package hieutm.dev.snakesneaker.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.activity.MainActivity;
import hieutm.dev.snakesneaker.adapter.MyBankDetailAdapter;
import hieutm.dev.snakesneaker.response.BankDetailRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBankDetailFragment extends Fragment {

    private Method method;
    private ProgressBar progressBar;
    private MaterialCardView cardViewAddBank;
    private RecyclerView recyclerView;
    private MyBankDetailAdapter myBankDetailAdapter;
    private MaterialTextView textViewTotal;
    private RelativeLayout relNoData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.my_bank_detail_fragment, container, false);

        GlobalBus.getBus().register(this);

        method = new Method(getActivity());

        if (MainActivity.toolbar != null) {
            if (method.isDarkMode()) {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.my_bank_detail) + "</font>"));
            } else {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + getResources().getString(R.string.my_bank_detail) + "</font>"));
            }
        }

        progressBar = view.findViewById(R.id.progressBar_myBankDetail_fragment);
        relNoData = view.findViewById(R.id.rel_noDataFound);
        textViewTotal = view.findViewById(R.id.textView_total_myBankDetail_fragment);
        cardViewAddBank = view.findViewById(R.id.cardView_myBankDetail_fragment);
        recyclerView = view.findViewById(R.id.recyclerView_myBankDetail_fragment);

        relNoData.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);

        cardViewAddBank.setOnClickListener(v -> {
            AddBankDetailFragment addBankDetailFragment = new AddBankDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("type", "add_bank_my");
            addBankDetailFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, addBankDetailFragment,
                    getResources().getString(R.string.add_bank_detail)).addToBackStack(getResources().getString(R.string.add_bank_detail))
                    .commitAllowingStateLoss();
        });

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                bankList(method.userId());
            } else {
                relNoData.setVisibility(View.VISIBLE);
                method.alertBox(getResources().getString(R.string.you_have_not_login));
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        return view;

    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    public void getData(Events.EventBankDetail eventBankDetail) {
        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.my_bank_detail));
        }
        if (MainActivity.toolbar != null) {
            if (method.isDarkMode()) {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.my_bank_detail) + "</font>"));
            } else {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + getResources().getString(R.string.my_bank_detail) + "</font>"));
            }
        }
        if (eventBankDetail.getType().equals("addEditBank")) {
            bankList(method.userId());
        } else {
            textViewTotal.setText(eventBankDetail.getBankCount() + " " + getResources().getString(R.string.bank_account));
        }
    }

    private void bankList(String userId) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<BankDetailRP> call = apiService.getBankList(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<BankDetailRP>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NotNull Call<BankDetailRP> call, @NotNull Response<BankDetailRP> response) {

                    if (getActivity() != null) {

                        try {
                            BankDetailRP bankDetailRP = response.body();
                            assert bankDetailRP != null;

                            if (bankDetailRP.getStatus().equals("1")) {

                                textViewTotal.setText(bankDetailRP.getBank_count() + " " + getResources().getString(R.string.bank_account));

                                if (bankDetailRP.getSuccess().equals("1")) {

                                    if (bankDetailRP.getBankDetailLists().size() != 0) {
                                        relNoData.setVisibility(View.GONE);
                                        myBankDetailAdapter = new MyBankDetailAdapter(getActivity(), bankDetailRP.getBankDetailLists());
                                        recyclerView.setAdapter(myBankDetailAdapter);
                                    } else {
                                        relNoData.setVisibility(View.VISIBLE);
                                    }

                                } else {
                                    relNoData.setVisibility(View.VISIBLE);
                                    Toast.makeText(getActivity(), bankDetailRP.getMsg(), Toast.LENGTH_SHORT).show();
                                }

                            } else if (bankDetailRP.getStatus().equals("2")) {
                                method.suspend(bankDetailRP.getMessage());
                            } else {
                                relNoData.setVisibility(View.VISIBLE);
                                method.alertBox(bankDetailRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<BankDetailRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    relNoData.setVisibility(View.VISIBLE);
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
