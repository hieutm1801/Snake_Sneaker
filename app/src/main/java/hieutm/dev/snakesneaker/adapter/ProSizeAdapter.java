package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.interfaces.OnClickSend;
import hieutm.dev.snakesneaker.item.ProSizeList;
import hieutm.dev.snakesneaker.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class ProSizeAdapter extends RecyclerView.Adapter<ProSizeAdapter.ViewHolder> {

    private Activity activity;
    private String type;
    private OnClickSend onClickSend;
    private List<ProSizeList> proSizeLists;
    private int lastSelectedPosition;

    public ProSizeAdapter(Activity activity, String type, OnClickSend onClickSend, List<ProSizeList> proSizeLists,int lastSelectedPosition) {
        this.activity = activity;
        this.type = type;
        this.lastSelectedPosition = lastSelectedPosition;
        this.onClickSend = onClickSend;
        this.proSizeLists = proSizeLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.pro_size_adapter, parent, false);

        return new ProSizeAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textView.setText(proSizeLists.get(position).getProduct_size());

        // this condition un-checks previous selections
        if (lastSelectedPosition == position) {
            holder.cardView.setCardBackgroundColor(activity.getResources().getColor(R.color.cardView_select_pro_size_adapter));
            holder.textView.setTextColor(activity.getResources().getColor(R.color.textView_select_pro_size_adapter));
        } else {
            holder.cardView.setCardBackgroundColor(activity.getResources().getColor(R.color.cardView_pro_size_adapter));
            holder.textView.setTextColor(activity.getResources().getColor(R.color.textView_pro_size_adapter));
        }

    }

    @Override
    public int getItemCount() {
        return proSizeLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView textView;
        private MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView_pro_size_adapter);
            cardView = itemView.findViewById(R.id.cartView_pro_size_adapter);

            cardView.setOnClickListener(v -> {
                lastSelectedPosition = getAdapterPosition();
                onClickSend.onClick("", type, proSizeLists.get(getAdapterPosition()).getProduct_size(), getAdapterPosition());
                notifyDataSetChanged();
            });

        }
    }

}
