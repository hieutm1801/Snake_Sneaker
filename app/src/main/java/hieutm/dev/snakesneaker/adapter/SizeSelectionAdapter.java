package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.item.SizeFilterList;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.util.Constant;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class SizeSelectionAdapter extends RecyclerView.Adapter<SizeSelectionAdapter.ViewHolder> {

    private Activity activity;
    private List<SizeFilterList> sizeLists,filterSizeLists;

    public SizeSelectionAdapter(Activity activity, List<SizeFilterList> sizeLists) {
        this.activity = activity;
        this.sizeLists = sizeLists;
        this.filterSizeLists = sizeLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.size_select_adapter, parent, false);

        return new SizeSelectionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (sizeLists.get(position).getSelected().equals("true")) {
            holder.materialCheckBox.setOnCheckedChangeListener(null);
            holder.materialCheckBox.setChecked(true);
        } else {
            holder.materialCheckBox.setOnCheckedChangeListener(null);
            holder.materialCheckBox.setChecked(false);
        }

        holder.textViewBrand.setText(sizeLists.get(position).getSize());

        holder.materialCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Constant.sizes_temp = Constant.sizes_temp.concat(sizeLists.get(position).getSize().concat(","));
            } else {
                try {
                    Constant.sizes_temp = Constant.sizes_temp.replace(sizeLists.get(position).getSize() + ",", "");
                } catch (Exception e) {
                    Log.d("error",e.toString());
                }
            }

        });

    }

    @Override
    public int getItemCount() {
        return sizeLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialCheckBox materialCheckBox;
        private MaterialTextView textViewBrand;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            materialCheckBox = itemView.findViewById(R.id.checkBox_size_select_adapter);
            textViewBrand = itemView.findViewById(R.id.textView_size_select_adapter);

        }
    }

    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                sizeLists = (List<SizeFilterList>) results.values;
                notifyDataSetChanged();

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                constraint = constraint.toString().toLowerCase();

                List<SizeFilterList> filterLists = new ArrayList<>();

                // perform your search here using the searchConstraint String.
                for (int i = 0; i < filterSizeLists.size(); i++) {
                    String dataNames = filterSizeLists.get(i).getSize();
                    if (dataNames.toLowerCase().startsWith(constraint.toString())) {
                        filterLists.add(filterSizeLists.get(i));
                        for (int j = 0; j < filterLists.size(); j++) {
                            if (Constant.sizes_temp.contains(filterLists.get(j).getSize().concat(","))) {
                                filterLists.get(j).setSelected("true");
                            } else {
                                filterLists.get(j).setSelected("false");
                            }
                        }
                    }
                }

                results.count = filterLists.size();
                results.values = filterLists;

                return results;
            }
        };

        return filter;
    }

}
