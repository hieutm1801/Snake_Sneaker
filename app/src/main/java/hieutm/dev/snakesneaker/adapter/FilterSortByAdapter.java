package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.interfaces.FilterType;
import hieutm.dev.snakesneaker.item.FilterSortByList;
import hieutm.dev.snakesneaker.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class FilterSortByAdapter extends RecyclerView.Adapter<FilterSortByAdapter.ViewHolder> {

    private Activity activity;
    private String type;
    private FilterType filterType;
    private List<FilterSortByList> filterSortByLists;

    public FilterSortByAdapter(Activity activity, String type, List<FilterSortByList> filterSortByLists, FilterType filterType) {
        this.activity = activity;
        this.type = type;
        this.filterType = filterType;
        this.filterSortByLists = filterSortByLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.filter_sort_by_adapter, parent, false);

        return new FilterSortByAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textViewName.setText(filterSortByLists.get(position).getTitle());
        holder.cardView.setOnClickListener(v -> {
            for (int i = 0; i < filterSortByLists.size(); i++) {
                if (i == position) {
                    filterSortByLists.get(i).setSelect(true);
                }else {
                    filterSortByLists.get(i).setSelect(false);
                }
            }
            notifyDataSetChanged();
        });

        if (filterSortByLists.get(position).isSelect()) {
            holder.cardView.setCardBackgroundColor(activity.getResources().getColor(R.color.cardView_select_filterSortBy_adapter));
            holder.textViewName.setTextColor(activity.getResources().getColor(R.color.textView_select_filterSortBy_adapter));
            filterType.select(filterSortByLists.get(position).getTitle(), type, filterSortByLists.get(position).getSort());
        } else {
            filterSortByLists.get(position).setSelect(false);
            holder.cardView.setCardBackgroundColor(activity.getResources().getColor(R.color.cardView_filterSortBy_adapter));
            holder.textViewName.setTextColor(activity.getResources().getColor(R.color.textView_filterSortBy_adapter));
        }

    }

    @Override
    public int getItemCount() {
        return filterSortByLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView textViewName;
        private MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textView_filter_sortBy_adapter);
            cardView = itemView.findViewById(R.id.cardView_filter_sortBy_adapter);

        }
    }
}
