package hieutm.dev.snakesneaker.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.response.DataRP;
import hieutm.dev.snakesneaker.response.RegisterRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Verification extends AppCompatActivity {

    private Method method;
    private PinView pinView;
    private InputMethodManager imm;
    private ProgressDialog progressDialog;
    private String verificationCode, name, email, password, phoneNo;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        method = new Method(Verification.this);
        method.forceRTLIfSupported();

        progressDialog = new ProgressDialog(Verification.this);

        Intent intent = getIntent();
        if (intent.hasExtra("name")) {
            name = intent.getStringExtra("name");
            email = intent.getStringExtra("email");
            password = intent.getStringExtra("password");
            phoneNo = intent.getStringExtra("phoneNo");
        } else {
            name = method.pref.getString(method.regName, null);
            email = method.pref.getString(method.regEmail, null);
            password = method.pref.getString(method.regPassword, null);
            phoneNo = method.pref.getString(method.regPhoneNo, null);
        }

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        pinView = findViewById(R.id.firstPinView);
        MaterialButton buttonVerification = findViewById(R.id.button_verification);
        MaterialButton buttonRegister = findViewById(R.id.button_register_verification);
        MaterialTextView textViewResend = findViewById(R.id.resend_verification);

        textViewResend.setOnClickListener(v -> reSendVerificationConform());

        buttonVerification.setOnClickListener(v -> {
            verificationCode = pinView.getText().toString();
            verification();
        });

        buttonRegister.setOnClickListener(v -> {
            method.editor.putBoolean(method.isVerification, false);
            method.editor.commit();
            startActivity(new Intent(Verification.this, Register.class));
            finishAffinity();
        });

    }

    public void verification() {

        pinView.clearFocus();
        imm.hideSoftInputFromWindow(pinView.getWindowToken(), 0);

        if (verificationCode == null || verificationCode.equals("") || verificationCode.isEmpty()) {
            method.alertBox(getResources().getString(R.string.please_enter_verification_code));
        } else {
            if (method.isNetworkAvailable()) {
                pinView.setText("");
                if (verificationCode.equals(method.pref.getString(method.verificationCode, null))) {
                    register(name, email, password, phoneNo);
                } else {
                    method.alertBox(getResources().getString(R.string.verification_message));
                }
            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }

        }
    }

    public void register(String sendName, String sendEmail, String sendPassword, String sendPhone) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Verification.this));
        jsObj.addProperty("name", sendName);
        jsObj.addProperty("email", sendEmail);
        jsObj.addProperty("password", sendPassword);
        jsObj.addProperty("phone", sendPhone);
        jsObj.addProperty("device_id", method.getDeviceId());
        jsObj.addProperty("type", "normal");
        jsObj.addProperty("register_platform", "android");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RegisterRP> call = apiService.getRegister(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<RegisterRP>() {
            @Override
            public void onResponse(@NotNull Call<RegisterRP> call, @NotNull Response<RegisterRP> response) {

                try {

                    RegisterRP registerRP = response.body();

                    assert registerRP != null;

                    if (registerRP.getSuccess().equals("1")) {

                        method.editor.putBoolean(method.isVerification, false);
                        method.editor.commit();

                        startActivity(new Intent(Verification.this, Login.class));
                        finishAffinity();
                    }

                    Toast.makeText(Verification.this, registerRP.getMsg(), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<RegisterRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    public void resendVerification(String sendName, String sendEmail, String otp) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Verification.this));
        jsObj.addProperty("otp", otp);
        jsObj.addProperty("name", sendName);
        jsObj.addProperty("email", sendEmail);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<DataRP> call = apiService.getOtp(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<DataRP>() {
            @Override
            public void onResponse(@NotNull Call<DataRP> call, @NotNull Response<DataRP> response) {

                try {

                    DataRP dataRP = response.body();

                    assert dataRP != null;

                    if (dataRP.getStatus().equals("1")) {

                        if (dataRP.getSuccess().equals("1")) {

                            method.editor.putString(method.verificationCode, otp);
                            method.editor.commit();

                            Toast.makeText(Verification.this, dataRP.getMsg(), Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        method.alertBox(dataRP.getMsg());
                    }


                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<DataRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
                Log.e(Constant.failApi, t.toString());
            }
        });
    }

    private void reSendVerificationConform() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Verification.this, R.style.DialogTitleTextStyle);
        builder.setMessage(getResources().getString(R.string.resend_verification_not));
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.yes),
                (arg0, arg1) -> {
                    int n = new Random().nextInt(9999 - 1000) + 1000;
                    resendVerification(name, email, String.valueOf(n));
                });
        builder.setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
