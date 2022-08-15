package hieutm.dev.snakesneaker.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.interfaces.PaymentSubmit;
import hieutm.dev.snakesneaker.response.PayStackKeyRP;
import hieutm.dev.snakesneaker.response.PaymentSubmitRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Method;
import hieutm.dev.snakesneaker.util.PaymentSubmitData;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayStackPayment extends AppCompatActivity {

    private Method method;
    private ProgressDialog progressDialog;
    private MaterialButton button;
    private InputMethodManager imm;
    private String type, couponId, addressId, cartIds;
    private TextInputEditText editTextCardNumber, editTextCardExpiry, editTextCvv;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paystack_payment);

        method = new Method(PayStackPayment.this);
        method.forceRTLIfSupported();

        progressDialog = new ProgressDialog(PayStackPayment.this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        type = getIntent().getStringExtra("type");
        couponId = getIntent().getStringExtra("coupon_id");
        addressId = getIntent().getStringExtra("address_id");
        cartIds = getIntent().getStringExtra("cart_ids");

        MaterialToolbar toolbar = findViewById(R.id.toolbar_payStack_payment);
        toolbar.setTitle(getResources().getString(R.string.payStack));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editTextCardNumber = findViewById(R.id.editText_cardNumber_payStack_payment);
        editTextCardExpiry = findViewById(R.id.editText_cardExpiry_payStack_payment);
        editTextCvv = findViewById(R.id.editText_cvv_payStack_payment);
        button = findViewById(R.id.button_payStack_payment);

        if (method.isNetworkAvailable()) {
            payStackAmount();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    private void payStackAmount() {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.processing));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(PayStackPayment.this));
        jsObj.addProperty("user_id", method.userId());
        jsObj.addProperty("cart_ids", cartIds);
        jsObj.addProperty("address_id", addressId);
        if (type.equals("by_now")) {
            jsObj.addProperty("cart_type", "temp_cart");
        } else {
            jsObj.addProperty("cart_type", "main_cart");
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PayStackKeyRP> call = apiService.getPayStackKey(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<PayStackKeyRP>() {
            @Override
            public void onResponse(@NotNull Call<PayStackKeyRP> call, @NotNull Response<PayStackKeyRP> response) {

                try {
                    PayStackKeyRP payStackKeyRP = response.body();
                    assert payStackKeyRP != null;

                    if (payStackKeyRP.getStatus().equals("1")) {

                        if (payStackKeyRP.getSuccess().equals("1")) {

                            PaystackSdk.initialize(PayStackPayment.this);
                            PaystackSdk.setPublicKey(payStackKeyRP.getPaystack_key());

                            editTextCardExpiry.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    String current = s.toString();
                                    if (current.length() == 2 && start == 1) {
                                        editTextCardExpiry.setText(current + "/");
                                        editTextCardExpiry.setSelection(current.length() + 1);
                                    } else if (current.length() == 2 && before == 1) {
                                        current = current.substring(0, 1);
                                        editTextCardExpiry.setText(current);
                                        editTextCardExpiry.setSelection(current.length());
                                    }
                                }

                                @Override
                                public void afterTextChanged(Editable s) {

                                }
                            });

                            button.setOnClickListener(v -> {

                                String cardNumber = editTextCardNumber.getText().toString();
                                String cardExpiry = editTextCardExpiry.getText().toString();
                                String cvv = editTextCvv.getText().toString();

                                editTextCardNumber.setError(null);
                                editTextCardExpiry.setError(null);
                                editTextCvv.setError(null);

                                if (cardNumber.equals("") || cardNumber.isEmpty()) {
                                    editTextCardNumber.requestFocus();
                                    editTextCardNumber.setError(getResources().getString(R.string.please_enter_cardNumber));
                                } else if (cardExpiry.equals("") || cardExpiry.isEmpty()) {
                                    editTextCardExpiry.requestFocus();
                                    editTextCardExpiry.setError(getResources().getString(R.string.please_enter_cardExpire));
                                } else if (!cardExpiry.contains("/")) {
                                    editTextCardExpiry.requestFocus();
                                    editTextCardExpiry.setError(getResources().getString(R.string.please_enter_cardExpire));
                                } else if (cvv.equals("") || cvv.isEmpty()) {
                                    editTextCvv.requestFocus();
                                    editTextCvv.setError(getResources().getString(R.string.please_enter_cardCvv));
                                } else {
                                    if (method.isNetworkAvailable()) {

                                        progressDialog.show();
                                        progressDialog.setMessage(getResources().getString(R.string.processing));
                                        progressDialog.setCancelable(false);

                                        editTextCardNumber.clearFocus();
                                        editTextCardExpiry.clearFocus();
                                        editTextCvv.clearFocus();
                                        imm.hideSoftInputFromWindow(editTextCardNumber.getWindowToken(), 0);
                                        imm.hideSoftInputFromWindow(editTextCardExpiry.getWindowToken(), 0);
                                        imm.hideSoftInputFromWindow(editTextCvv.getWindowToken(), 0);

                                        String[] cardExpiryArray = cardExpiry.split("/");
                                        int expiryMonth = Integer.parseInt(cardExpiryArray[0]);
                                        int expiryYear = Integer.parseInt(cardExpiryArray[1]);

                                        Card card = new Card(cardNumber, expiryMonth, expiryYear, cvv);
                                        if (card.isValid()) {
                                            Charge charge = new Charge();
                                            charge.setAmount(payStackKeyRP.getPayable_amt());
                                            charge.setEmail(payStackKeyRP.getEmail());
                                            charge.setCard(card);
                                            PaystackSdk.chargeCard(PayStackPayment.this, charge, new Paystack.TransactionCallback() {
                                                @Override
                                                public void onSuccess(Transaction transaction) {
                                                    // This is called only after transaction is deemed successful.
                                                    // Retrieve the transaction, and send its reference to your server
                                                    // for verification.
                                                    new PaymentSubmitData(PayStackPayment.this).payMeantSubmit(method.userId(), couponId, addressId, cartIds, type, "paystack", transaction.getReference(), "0", new PaymentSubmit() {
                                                        @Override
                                                        public void paymentStart() {

                                                        }

                                                        @Override
                                                        public void paymentSuccess(PaymentSubmitRP paymentSubmitRP) {
                                                            progressDialog.dismiss();
                                                            method.conformDialog(paymentSubmitRP);
                                                        }

                                                        @Override
                                                        public void paymentFail(String message) {
                                                            progressDialog.dismiss();
                                                            method.showStatus(message);
                                                        }

                                                        @Override
                                                        public void suspendUser(String message) {
                                                            progressDialog.dismiss();
                                                            method.suspend(message);
                                                        }
                                                    });
                                                    Log.d("paystack_data", "onSuccess :-" + transaction.getReference());
                                                }

                                                @Override
                                                public void beforeValidate(Transaction transaction) {
                                                    // This is called only before requesting OTP.
                                                    // Save reference so you may send to server. If
                                                    // error occurs with OTP, you should still verify on server.
                                                    Log.d("paystack_data", "beforeValidate :-" + transaction.getReference());
                                                }

                                                @Override
                                                public void onError(Throwable error, Transaction transaction) {
                                                    //handle error here
                                                    Log.d("paystack_data", "onError :-" + error.getMessage());
                                                    method.alertBox(getResources().getString(R.string.payment_fail));
                                                }

                                            });
                                        } else {
                                            progressDialog.dismiss();
                                            method.alertBox(getResources().getString(R.string.please_valid_card));
                                        }

                                    } else {
                                        method.alertBox(getResources().getString(R.string.internet_connection));
                                    }
                                }

                            });

                        } else if (payStackKeyRP.getSuccess().equals("2")) {
                            method.showStatus(payStackKeyRP.getMsg());
                        } else {
                            method.alertBox(payStackKeyRP.getMsg());
                        }

                    } else if (payStackKeyRP.getStatus().equals("2")) {
                        method.suspend(payStackKeyRP.getMessage());
                    } else {
                        method.alertBox(payStackKeyRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<PayStackKeyRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
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
