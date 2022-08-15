package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.activity.AddAddress;
import hieutm.dev.snakesneaker.item.AddressItemList;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.response.AddEditRemoveAddressRP;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAddressAdapter extends RecyclerView.Adapter<MyAddressAdapter.ViewHolder> {

    private Activity activity;
    private Method method;
    private List<AddressItemList> addressItemLists;

    public MyAddressAdapter(Activity activity, List<AddressItemList> addressItemLists) {
        this.activity = activity;
        this.addressItemLists = addressItemLists;
        method = new Method(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.my_address_adapter, parent, false);

        return new MyAddressAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textViewName.setText(addressItemLists.get(position).getName());
        holder.textViewAddType.setText(addressItemLists.get(position).getAddress_type());
        holder.textViewAddress.setText(addressItemLists.get(position).getAddress());
        holder.textViewPhoneNo.setText(addressItemLists.get(position).getMobile_no());

        holder.textViewEdit.setOnClickListener(v -> activity.startActivity(new Intent(activity, AddAddress.class)
                .putExtra("type", "edit_my_add")
                .putExtra("address_id", addressItemLists.get(position).getId())));

        holder.textViewRemove.setOnClickListener(v -> {
            if (method.isLogin()) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.DialogTitleTextStyle);
                builder.setMessage(activity.getResources().getString(R.string.delete_msg));
                builder.setCancelable(false);
                builder.setPositiveButton(activity.getResources().getString(R.string.yes),
                        (arg0, arg1) -> deleteAddress(addressItemLists.get(position).getId(), method.userId(), position));
                builder.setNegativeButton(activity.getResources().getString(R.string.no),
                        (dialog, which) -> dialog.dismiss());

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return addressItemLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView textViewName, textViewAddType, textViewAddress,
                textViewPhoneNo, textViewEdit, textViewRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textView_name_myAdd_adapter);
            textViewAddType = itemView.findViewById(R.id.textView_addType_myAdd_adapter);
            textViewAddress = itemView.findViewById(R.id.textView_add_myAdd_adapter);
            textViewPhoneNo = itemView.findViewById(R.id.textView_phoneNo_add_myAdd_adapter);
            textViewEdit = itemView.findViewById(R.id.textView_edit_myAdd_adapter);
            textViewRemove = itemView.findViewById(R.id.textView_remove_myAdd_adapter);

        }
    }

    private void deleteAddress(String id, String userId, int position) {

        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.show();
        progressDialog.setMessage(activity.getResources().getString(R.string.processing));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(activity));
        jsObj.addProperty("id", id);
        jsObj.addProperty("user_id", userId);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AddEditRemoveAddressRP> call = apiService.deleteAddress(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<AddEditRemoveAddressRP>() {
            @Override
            public void onResponse(@NotNull Call<AddEditRemoveAddressRP> call, @NotNull Response<AddEditRemoveAddressRP> response) {

                try {

                    AddEditRemoveAddressRP addEditRemoveAddressRP = response.body();

                    assert addEditRemoveAddressRP != null;
                    if (addEditRemoveAddressRP.getStatus().equals("1")) {
                        if (addEditRemoveAddressRP.getSuccess().equals("1")) {
                            Toast.makeText(activity, addEditRemoveAddressRP.getMsg(), Toast.LENGTH_SHORT).show();
                            Events.EventMYAddress eventMYAddress = new Events.EventMYAddress(addEditRemoveAddressRP.getAddress(), addEditRemoveAddressRP.getAddress_count(),"removeAddress");
                            GlobalBus.getBus().post(eventMYAddress);
                            addressItemLists.remove(position);
                            notifyDataSetChanged();
                        } else {
                            method.alertBox(addEditRemoveAddressRP.getMsg());
                        }

                    } else if (addEditRemoveAddressRP.getStatus().equals("2")) {
                        method.suspend(addEditRemoveAddressRP.getMessage());
                    } else {
                        method.alertBox(addEditRemoveAddressRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(activity.getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<AddEditRemoveAddressRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
                method.alertBox(activity.getResources().getString(R.string.failed_try_again));
            }

        });

    }

}
