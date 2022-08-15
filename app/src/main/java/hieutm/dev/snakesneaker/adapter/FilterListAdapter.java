package hieutm.dev.snakesneaker.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.interfaces.FilterType;
import hieutm.dev.snakesneaker.item.FilterList;
import hieutm.dev.snakesneaker.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class FilterListAdapter extends RecyclerView.Adapter<FilterListAdapter.ViewHolder> {

    private Activity activity;
    private String type;
    private FilterType filterType;
    private List<FilterList> filterLists;
    private int lastSelectedPosition = 0;

    public FilterListAdapter(Activity activity, String type, List<FilterList> filterLists, FilterType filterType) {
        this.activity = activity;
        this.type = type;
        this.filterLists = filterLists;
        this.filterType = filterType;
    }

    @NonNull
    @Override
    public FilterListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.filter_list_adapter, parent, false);

        return new FilterListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.textViewName.setText(filterLists.get(position).getName());

        holder.relativeLayout.setOnClickListener(v -> {
            lastSelectedPosition = position;
            notifyDataSetChanged();
            filterType.select(filterLists.get(position).getName(), type, filterLists.get(position).getType());
        });

        if (lastSelectedPosition == position) {
            holder.relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.select_bg_filter));
            holder.textViewName.setTextColor(activity.getResources().getColor(R.color.textView_select_filter_adapter));
        } else {
            holder.relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.transparent));
            holder.textViewName.setTextColor(activity.getResources().getColor(R.color.textView_filter_adapter));
        }

    }


    @Override
    public int getItemCount() {
        return filterLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout relativeLayout;
        private MaterialTextView textViewName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.rel_filter_list_adapter);
            textViewName = itemView.findViewById(R.id.textView_filter_list_adapter);

        }
    }
}
