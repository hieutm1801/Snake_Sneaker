package hieutm.dev.snakesneaker.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.item.OrderTrackingList;
import hieutm.dev.snakesneaker.R;
import com.github.vipulasri.timelineview.TimelineView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class OrderTrackingAdapter extends RecyclerView.Adapter<OrderTrackingAdapter.ViewHolder> {

    private Activity activity;
    private List<OrderTrackingList> orderTrackingLists;

    public OrderTrackingAdapter(Activity activity, List<OrderTrackingList> orderTrackingLists) {
        this.activity = activity;
        this.orderTrackingLists = orderTrackingLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.order_tracking_adapter, parent, false);

        return new OrderTrackingAdapter.ViewHolder(view, viewType);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textViewTitle.setText(orderTrackingLists.get(position).getStatus_title());
        holder.textViewDate.setText(orderTrackingLists.get(position).getDatetime());
        holder.textViewDes.setText(orderTrackingLists.get(position).getStatus_desc());

        if (orderTrackingLists.get(position).getIs_status().equals("true")) {
            holder.timelineView.setEndLineColor(activity.getResources().getColor(R.color.transparent), 0);
            holder.timelineView.setMarkerColor(activity.getResources().getColor(R.color.red));
        } else {
            if (orderTrackingLists.get(position).getIs_delivered().equals("true")) {
                holder.timelineView.setEndLineColor(activity.getResources().getColor(R.color.transparent), 0);
            } else {
                holder.timelineView.setEndLineColor(activity.getResources().getColor(R.color.green), 0);
            }
            holder.timelineView.setMarkerColor(activity.getResources().getColor(R.color.green));
        }

    }

    @Override
    public int getItemCount() {
        return orderTrackingLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TimelineView timelineView;
        private MaterialTextView textViewTitle, textViewDate, textViewDes;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textView_title_orderTrack_adapter);
            textViewDate = itemView.findViewById(R.id.textView_date_orderTrack_adapter);
            textViewDes = itemView.findViewById(R.id.textView_des_orderTrack_adapter);
            timelineView = itemView.findViewById(R.id.timeline_orderTrack_adapter);
            timelineView.initLine(viewType);

        }
    }
}
