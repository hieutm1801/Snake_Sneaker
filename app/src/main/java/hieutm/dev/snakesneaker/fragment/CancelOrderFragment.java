package hieutm.dev.snakesneaker.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.activity.MainActivity;
import hieutm.dev.snakesneaker.adapter.BankDetailAdapter;
import hieutm.dev.snakesneaker.interfaces.OnClickSend;
import hieutm.dev.snakesneaker.response.BankDetailRP;
import hieutm.dev.snakesneaker.response.CancelOrderProRP;
import hieutm.dev.snakesneaker.response.DataRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CancelOrderFragment extends Fragment {

    private Method method;
    private OnClickSend onClickSend;
    private ProgressBar progressBar;
    private MaterialCardView cardViewBankDetail;
    private LinearLayout llBank;
    private MaterialButton buttonCancel;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private BankDetailAdapter bankDetailAdapter;
    private LinearLayout llMain;
    private InputMethodManager imm;
    private MaterialTextView textViewTitle;
    private TextInputLayout textInputLayout;
    private TextInputEditText editTextReason;
    private String type, orderId, orderUniqueId, productId, paymentType, bankId = "0";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.cancel_order_fragment, container, false);

        GlobalBus.getBus().register(this);
        method = new Method(getActivity());

        if (MainActivity.toolbar != null) {
            if (method.isDarkMode()) {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.cancel_order) + "</font>"));
            } else {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + getResources().getString(R.string.cancel_order) + "</font>"));
            }
        }

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        onClickSend = (id, sendType, size, position) -> bankId = id;

        assert getArguments() != null;
        type = getArguments().getString("type");
        orderId = getArguments().getString("order_id");
        orderUniqueId = getArguments().getString("order_unique_id");
        productId = getArguments().getString("product_id");
        paymentType = getArguments().getString("payment_type");

        progressDialog = new ProgressDialog(getActivity());

        llMain = view.findViewById(R.id.ll_main_cancel_order);
        progressBar = view.findViewById(R.id.progressBar_cancel_order);
        textViewTitle = view.findViewById(R.id.textView_title_cancel_order);
        textInputLayout = view.findViewById(R.id.textInput_reason_cancel_order);
        editTextReason = view.findViewById(R.id.editText_reason_cancel_order);
        llBank = view.findViewById(R.id.ll_bank_cancel_order);
        cardViewBankDetail = view.findViewById(R.id.cardView_bankDetail_cancel_order);
        recyclerView = view.findViewById(R.id.recyclerView_changeAdd_cancel_order);
        buttonCancel = view.findViewById(R.id.button_cancel_order);

        progressBar.setVisibility(View.GONE);

        if (paymentType.equals("isCancel")) {
            textInputLayout.setVisibility(View.GONE);
            textViewTitle.setText(getResources().getString(R.string.choose_you_refund_account));
        } else {
            textViewTitle.setText(getResources().getString(R.string.cancel_order_title));
        }

        if (paymentType.equals("COD")) {
            llBank.setVisibility(View.GONE);
        } else {
            llBank.setVisibility(View.VISIBLE);
        }

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);

        cardViewBankDetail.setOnClickListener(v -> {
            AddBankDetailFragment addBankDetailFragment = new AddBankDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("type", "add_bank_cancel");
            addBankDetailFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, addBankDetailFragment,
                    getResources().getString(R.string.add_bank_detail)).addToBackStack(getResources().getString(R.string.add_bank_detail))
                    .commitAllowingStateLoss();
        });

        buttonCancel.setOnClickListener(v -> {

            if (!paymentType.equals("isCancel")) {

                String reason = editTextReason.getText().toString();
                editTextReason.setError(null);

                if (reason.equals("") || reason.isEmpty()) {
                    editTextReason.requestFocus();
                    editTextReason.setError(getResources().getString(R.string.please_enter_reason));
                } else if (!paymentType.equals("COD") && bankId.equals("0")) {
                    method.alertBox(getResources().getString(R.string.please_select_bank));
                } else {

                    editTextReason.clearFocus();
                    imm.hideSoftInputFromWindow(editTextReason.getWindowToken(), 0);

                    if (method.isNetworkAvailable()) {
                        cancelOrder(method.userId(), orderId, orderUniqueId, productId, reason, bankId);
                    } else {
                        method.alertBox(getResources().getString(R.string.internet_connection));
                    }

                }
            } else if (bankId.equals("0")) {
                method.alertBox(getResources().getString(R.string.please_select_bank));
            } else {
                if (method.isNetworkAvailable()) {
                    claimOrder(method.userId(), orderId, orderUniqueId, productId, bankId);
                } else {
                    method.alertBox(getResources().getString(R.string.internet_connection));
                }
            }
        });

        if (method.isNetworkAvailable()) {
            if (!paymentType.equals("COD")) {
                bankDetail(method.userId());
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        return view;

    }

    @Subscribe
    public void getData(Events.EventBankDetail eventBankDetail) {
        if (MainActivity.toolbar != null) {
            if (method.isDarkMode()) {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.cancel_order) + "</font>"));
            } else {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + getResources().getString(R.string.cancel_order) + "</font>"));
            }
        }
        if (eventBankDetail.getType().equals("addEditBank")) {
            bankDetail(method.userId());
        }
    }

    public void bankDetail(String userId) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<BankDetailRP> call = apiService.getBankList(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<BankDetailRP>() {
                @Override
                public void onResponse(@NotNull Call<BankDetailRP> call, @NotNull Response<BankDetailRP> response) {

                    if (getActivity() != null) {

                        try {
                            BankDetailRP bankDetailRP = response.body();
                            assert bankDetailRP != null;

                            if (bankDetailRP.getStatus().equals("1")) {

                                if (bankDetailRP.getSuccess().equals("1")) {

                                    if (bankDetailRP.getBankDetailLists().size() != 0) {
                                        bankDetailAdapter = new BankDetailAdapter(getActivity(), bankDetailRP.getBankDetailLists(), onClickSend);
                                        recyclerView.setAdapter(bankDetailAdapter);
                                    }

                                } else {
                                    method.alertBox(bankDetailRP.getMsg());
                                }

                            } else if (bankDetailRP.getStatus().equals("2")) {
                                method.suspend(bankDetailRP.getMessage());
                            } else {
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
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    private void cancelOrder(String userId, String orderId, String orderUniqueId, String productId, String reason, String bankId) {

        if (getActivity() != null) {

            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("order_id", orderId);
            if (type.equals("cancel_order")) {
                jsObj.addProperty("product_id", "0");
            } else {
                jsObj.addProperty("product_id", productId);
            }
            jsObj.addProperty("reason", reason);
            jsObj.addProperty("bank_id", bankId);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<CancelOrderProRP> call = apiService.cancelOrderORProduct(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<CancelOrderProRP>() {
                @Override
                public void onResponse(@NotNull Call<CancelOrderProRP> call, @NotNull Response<CancelOrderProRP> response) {

                    if (getActivity() != null) {

                        try {
                            CancelOrderProRP cancelOrderProRP = response.body();
                            assert cancelOrderProRP != null;

                            if (cancelOrderProRP.getStatus().equals("1")) {

                                if (cancelOrderProRP.getSuccess().equals("1")) {
                                    Events.CancelOrder cancelOrder = new Events.CancelOrder(productId, orderUniqueId, "", cancelOrderProRP.getMyOrderLists());
                                    GlobalBus.getBus().post(cancelOrder);
                                    getActivity().getSupportFragmentManager().popBackStack();
                                    Toast.makeText(getActivity(), cancelOrderProRP.getMsg(), Toast.LENGTH_SHORT).show();
                                } else {
                                    method.alertBox(cancelOrderProRP.getMsg());
                                }

                            } else if (cancelOrderProRP.getStatus().equals("2")) {
                                method.suspend(cancelOrderProRP.getMessage());
                            } else {
                                method.alertBox(cancelOrderProRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressDialog.dismiss();

                }

                @Override
                public void onFailure(@NotNull Call<CancelOrderProRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    progressDialog.dismiss();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }
    }

    private void claimOrder(String userId, String orderId, String orderUniqueId, String productId, String bankId) {

        if (getActivity() != null) {

            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("order_id", orderId);
            if (type.equals("claim_order")) {
                jsObj.addProperty("product_id", "0");
            } else {
                jsObj.addProperty("product_id", productId);
            }
            jsObj.addProperty("bank_id", bankId);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<DataRP> call = apiService.claimOrderORProduct(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<DataRP>() {
                @Override
                public void onResponse(@NotNull Call<DataRP> call, @NotNull Response<DataRP> response) {

                    if (getActivity() != null) {

                        try {
                            DataRP body = response.body();
                            assert body != null;

                            if (body.getStatus().equals("1")) {

                                if (body.getSuccess().equals("1")) {
                                    Events.ClaimOrder claimOrder = new Events.ClaimOrder(productId, orderUniqueId);
                                    GlobalBus.getBus().post(claimOrder);
                                    getActivity().getSupportFragmentManager().popBackStack();
                                    Toast.makeText(getActivity(), body.getMsg(), Toast.LENGTH_SHORT).show();
                                } else {
                                    method.alertBox(body.getMsg());
                                }

                            } else if (body.getStatus().equals("2")) {
                                method.suspend(body.getMessage());
                            } else {
                                method.alertBox(body.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressDialog.dismiss();

                }

                @Override
                public void onFailure(@NotNull Call<DataRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    progressDialog.dismiss();
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
