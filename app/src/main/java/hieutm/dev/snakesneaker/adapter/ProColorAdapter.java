package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.interfaces.OnClickSend;
import hieutm.dev.snakesneaker.item.ProColorList;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.util.Constant;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ProColorAdapter extends RecyclerView.Adapter<ProColorAdapter.ViewHolder> {

    private Activity activity;
    private String type;
    private OnClickSend onClickSend;
    private List<ProColorList> proColorLists;
    private int lastSelectedPosition = 0;

    public ProColorAdapter(Activity activity, String type, OnClickSend onClickSend, List<ProColorList> proColorLists) {
        this.activity = activity;
        this.type = type;
        this.onClickSend = onClickSend;
        this.proColorLists = proColorLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.pro_color_adapter, parent, false);

        return new ProColorAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.viewColor.setBackgroundColor(Color.parseColor(proColorLists.get(position).getColor_code()));

        if (lastSelectedPosition == position) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.cardView.setStrokeWidth(4);
            if (proColorLists.get(position).getColor_code().equals(Constant.colorCompareBg)) {
                holder.cardView.setStrokeColor(activity.getResources().getColor(R.color.stroke_second_color_adapter));
            } else {
                holder.cardView.setStrokeColor(activity.getResources().getColor(R.color.stroke_color_adapter));
            }
            if (proColorLists.get(position).getColor_code().equals(Constant.colorCompareIcon)) {
                holder.imageView.setColorFilter(ContextCompat.getColor(activity, R.color.image_color_adapter), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                holder.imageView.setColorFilter(ContextCompat.getColor(activity, R.color.image_second_color_adapter), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        } else {
            holder.imageView.setVisibility(View.GONE);
            holder.cardView.setStrokeWidth(0);
        }
    }

    @Override
    public int getItemCount() {
        return proColorLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View viewColor;
        private ImageView imageView;
        private MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            viewColor = itemView.findViewById(R.id.view_pro_color_adapter);
            imageView = itemView.findViewById(R.id.imageView_pro_color_adapter);
            cardView = itemView.findViewById(R.id.cardView_pro_color_adapter);

            viewColor.setOnClickListener(v -> {
                lastSelectedPosition = getAdapterPosition();
                onClickSend.onClick(proColorLists.get(getAdapterPosition()).getColor_id(), type, "", getAdapterPosition());
                notifyDataSetChanged();
            });

        }
    }

}
