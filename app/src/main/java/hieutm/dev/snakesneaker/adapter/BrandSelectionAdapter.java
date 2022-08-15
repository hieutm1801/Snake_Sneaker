package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.item.BrandFilterList;
import hieutm.dev.snakesneaker.util.Constant;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class BrandSelectionAdapter extends RecyclerView.Adapter<BrandSelectionAdapter.ViewHolder> {

    private Activity activity;
    private List<BrandFilterList> brandLists, filterBrandLists;

    public BrandSelectionAdapter(Activity activity, List<BrandFilterList> brandLists) {
        this.activity = activity;
        this.brandLists = brandLists;
        this.filterBrandLists = brandLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.brand_select_adapter, parent, false);

        return new BrandSelectionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (brandLists.get(position).getSelected().equals("true")) {
            holder.materialCheckBox.setOnCheckedChangeListener(null);
            holder.materialCheckBox.setChecked(true);
        } else {
            holder.materialCheckBox.setOnCheckedChangeListener(null);
            holder.materialCheckBox.setChecked(false);
        }

        String brand = brandLists.get(position).getBrand_name() + " "
                + "(" + brandLists.get(position).getTotal_cnt() + ")";

        holder.textViewBrand.setText(brand);

        holder.materialCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Constant.brand_ids_temp = Constant.brand_ids_temp.concat(brandLists.get(position).getId().concat(","));
            } else {
                try {
                    Constant.brand_ids_temp = Constant.brand_ids_temp.replace(brandLists.get(position).getId() + ",", "");
                } catch (Exception e) {
                    Log.d("error", e.toString());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return brandLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialCheckBox materialCheckBox;
        private MaterialTextView textViewBrand;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            materialCheckBox = itemView.findViewById(R.id.checkBox_brand_select_adapter);
            textViewBrand = itemView.findViewById(R.id.textView_brand_select_adapter);

        }
    }

    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                brandLists = (List<BrandFilterList>) results.values;
                notifyDataSetChanged();

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                constraint = constraint.toString().toLowerCase();

                List<BrandFilterList> filterLists = new ArrayList<>();

                // perform your search here using the searchConstraint String.
                for (int i = 0; i < filterBrandLists.size(); i++) {
                    String dataNames = filterBrandLists.get(i).getBrand_name();
                    if (dataNames.toLowerCase().startsWith(constraint.toString())) {
                        filterLists.add(filterBrandLists.get(i));
                        for (int j = 0; j < filterLists.size(); j++) {
                            if (Constant.brand_ids_temp.contains(filterLists.get(j).getId().concat(","))) {
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
