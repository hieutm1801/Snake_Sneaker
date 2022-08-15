package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.activity.AddAddress;
import hieutm.dev.snakesneaker.interfaces.OnClickSend;
import hieutm.dev.snakesneaker.item.AddressItemList;
import hieutm.dev.snakesneaker.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class ChangeAddressAdapter extends RecyclerView.Adapter<ChangeAddressAdapter.ViewHolder> {

    private Activity activity;
    private OnClickSend onClickSend;
    private List<AddressItemList> addressItemLists;
    private int lastSelectedPosition = 0;

    public ChangeAddressAdapter(Activity activity, OnClickSend onClickSend, List<AddressItemList> addressItemLists) {
        this.activity = activity;
        this.onClickSend = onClickSend;
        this.addressItemLists = addressItemLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.change_address_adapter, parent, false);

        return new ChangeAddressAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textViewName.setText(addressItemLists.get(position).getName());
        holder.textViewAddType.setText(addressItemLists.get(position).getAddress_type());
        holder.textViewAddress.setText(addressItemLists.get(position).getAddress());
        holder.textViewPhoneNo.setText(addressItemLists.get(position).getMobile_no());

        holder.button.setOnClickListener(v -> activity.startActivity(new Intent(activity, AddAddress.class)
                .putExtra("type", "edit_change_add")
                .putExtra("address_id", addressItemLists.get(position).getId())));

        //since only one radio button is allowed to be selected,
        // this condition un-checks previous selections
        holder.radioButton.setChecked(lastSelectedPosition == position);
        if (lastSelectedPosition == position) {
            holder.button.setVisibility(View.VISIBLE);
            onClickSend.onClick(addressItemLists.get(position).getId(), "", "", position);
        } else {
            holder.button.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return addressItemLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialButton button;
        private RadioButton radioButton;
        private MaterialCardView materialCardView;
        private MaterialTextView textViewName, textViewAddType, textViewAddress, textViewPhoneNo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            materialCardView = itemView.findViewById(R.id.cardView_addList_adapter);
            radioButton = itemView.findViewById(R.id.radioButton_addList_adapter);
            button = itemView.findViewById(R.id.button_edit_addList_adapter);
            textViewName = itemView.findViewById(R.id.textView_name_addList_adapter);
            textViewAddType = itemView.findViewById(R.id.textView_addType_addList_adapter);
            textViewAddress = itemView.findViewById(R.id.textView_add_list_adapter);
            textViewPhoneNo = itemView.findViewById(R.id.textView_phoneNo_add_list_adapter);

            materialCardView.setOnClickListener(v -> {
                lastSelectedPosition = getAdapterPosition();
                notifyDataSetChanged();
            });

            radioButton.setOnClickListener(v -> {
                lastSelectedPosition = getAdapterPosition();
                notifyDataSetChanged();
            });

        }
    }
}
