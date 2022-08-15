package hieutm.dev.snakesneaker.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.response.DataRP;
import hieutm.dev.snakesneaker.response.OtpCheck;
import hieutm.dev.snakesneaker.response.RegisterRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    private Method method;
    private InputMethodManager imm;
    private ProgressDialog progressDialog;
    private MaterialButton buttonSubmit;
    private MaterialCheckBox checkBox;
    private String name, email, password, conformPassword, phoneNo;
    private TextInputEditText editTextName, editTextEmail, editTextPassword, editTextConformPassword, editTextPhoneNo;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        method = new Method(Register.this);
        method.forceRTLIfSupported();

        progressDialog = new ProgressDialog(Register.this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        editTextName = findViewById(R.id.editText_name_register);
        editTextEmail = findViewById(R.id.editText_email_register);
        editTextPassword = findViewById(R.id.editText_password_register);
        editTextConformPassword = findViewById(R.id.editText_conform_password_register);
        editTextPhoneNo = findViewById(R.id.editText_phoneNo_register);
        checkBox = findViewById(R.id.checkbox_register);
        MaterialTextView textViewTerms = findViewById(R.id.textView_terms_register);
        MaterialTextView textViewLogin = findViewById(R.id.textView_login_register);
        buttonSubmit = findViewById(R.id.button_submit);

        textViewTerms.setOnClickListener(v -> startActivity(new Intent(Register.this, TermsOfUse.class)));

        textViewLogin.setOnClickListener(v -> {
            startActivity(new Intent(Register.this, Login.class));
            finishAffinity();
        });

        if (method.isNetworkAvailable()) {
            checkOtp();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }


    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void checkOtp() {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Register.this));
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<OtpCheck> call = apiService.checkOtp(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<OtpCheck>() {
            @Override
            public void onResponse(@NotNull Call<OtpCheck> call, @NotNull Response<OtpCheck> response) {

                try {

                    OtpCheck otpCheck = response.body();

                    assert otpCheck != null;

                    if (otpCheck.getStatus().equals("1")) {

                        buttonSubmit.setOnClickListener(v -> {

                            name = editTextName.getText().toString();
                            email = editTextEmail.getText().toString();
                            password = editTextPassword.getText().toString();
                            conformPassword = editTextConformPassword.getText().toString();
                            phoneNo = editTextPhoneNo.getText().toString();

                            form(otpCheck.getEmail_otp_status());

                        });

                    } else {
                        method.alertBox(otpCheck.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<OtpCheck> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    public void form(String status) {

        editTextName.setError(null);
        editTextEmail.setError(null);
        editTextPassword.setError(null);
        editTextConformPassword.setError(null);
        editTextPhoneNo.setError(null);

        if (name.equals("")) {
            editTextName.requestFocus();
            editTextName.setError(getResources().getString(R.string.please_enter_name));
        } else if (!isValidMail(email)) {
            editTextEmail.requestFocus();
            editTextEmail.setError(getResources().getString(R.string.please_enter_email));
        } else if (password.equals("")) {
            editTextPassword.requestFocus();
            editTextPassword.setError(getResources().getString(R.string.please_enter_password));
        } else if (conformPassword.equals("")) {
            editTextConformPassword.requestFocus();
            editTextConformPassword.setError(getResources().getString(R.string.please_enter_confirm_password));
        } else if (phoneNo.equals("")) {
            editTextPhoneNo.requestFocus();
            editTextPhoneNo.setError(getResources().getString(R.string.please_enter_phone));
        } else if (!password.equals(conformPassword)) {
            method.alertBox(getResources().getString(R.string.password_not_match));
        } else if (!checkBox.isChecked()) {
            method.alertBox(getResources().getString(R.string.please_select_terms));
        } else {

            editTextName.clearFocus();
            editTextEmail.clearFocus();
            editTextPassword.clearFocus();
            editTextConformPassword.clearFocus();
            editTextPhoneNo.clearFocus();
            imm.hideSoftInputFromWindow(editTextName.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextPassword.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextConformPassword.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextPhoneNo.getWindowToken(), 0);

            if (method.isNetworkAvailable()) {
                if (status.equals("true")) {
                    int n = new Random().nextInt(9999 - 1000) + 1000;
                    verification_call(name, email, password, phoneNo, String.valueOf(n));
                } else {
                    register(name, email, password, phoneNo);
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void verification_call(String sendName, String sendEmail, String sendPassword, String sendPhone, String otp) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Register.this));
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

                            method.editor.putBoolean(method.isVerification, true);
                            method.editor.putString(method.regName, sendName);
                            method.editor.putString(method.regEmail, sendEmail);
                            method.editor.putString(method.regPassword, sendPassword);
                            method.editor.putString(method.regPhoneNo, sendPhone);
                            method.editor.putString(method.verificationCode, otp);
                            method.editor.commit();

                            editTextName.setText("");
                            editTextEmail.setText("");
                            editTextPassword.setText("");
                            editTextConformPassword.setText("");
                            editTextPhoneNo.setText("");

                            startActivity(new Intent(Register.this, Verification.class)
                                    .putExtra("name", sendName)
                                    .putExtra("email", sendEmail)
                                    .putExtra("password", sendPassword)
                                    .putExtra("phoneNo", sendPhone));
                            finishAffinity();

                        } else {
                            method.alertBox(dataRP.getMsg());
                        }

                    } else {
                        method.alertBox(dataRP.getMessage());
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
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    public void register(String sendName, String sendEmail, String sendPassword, String sendPhone) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Register.this));
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

                        startActivity(new Intent(Register.this, Login.class));
                        finishAffinity();
                    }

                    Toast.makeText(Register.this, registerRP.getMsg(), Toast.LENGTH_SHORT).show();

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

}
