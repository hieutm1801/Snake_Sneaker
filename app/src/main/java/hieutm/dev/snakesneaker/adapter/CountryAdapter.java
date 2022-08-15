package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.interfaces.CountryIF;
import hieutm.dev.snakesneaker.item.CountryList;
import hieutm.dev.snakesneaker.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {

    private Activity activity;
    private CountryIF countryIF;
    private int select_position;
    private List<CountryList> countryLists, filterCountryLists;

    public CountryAdapter(Activity activity, CountryIF countryIF, List<CountryList> countryLists, int select_position) {
        this.activity = activity;
        this.countryIF = countryIF;
        this.countryLists = countryLists;
        this.filterCountryLists = countryLists;
        this.select_position = select_position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.country_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (select_position == position) {
            holder.textView.setTextColor(activity.getResources().getColor(R.color.textView_app_color));
        } else {
            holder.textView.setTextColor(activity.getResources().getColor(R.color.textView_country_adapter));
        }

        if (position == countryLists.size() - 1) {
            holder.view.setVisibility(View.GONE);
        } else {
            holder.view.setVisibility(View.VISIBLE);
        }

        holder.textView.setText(countryLists.get(position).getCountry_name());
        holder.textView.setOnClickListener(v -> countryIF.country(countryLists.get(position).getCountry_name()));

    }

    @Override
    public int getItemCount() {
        return countryLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private MaterialTextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView_country_adapter);
            view = itemView.findViewById(R.id.view_country_adapter);

        }
    }

    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                countryLists = (List<CountryList>) results.values;
                notifyDataSetChanged();

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                constraint = constraint.toString().toLowerCase();

                List<CountryList> filterLists = new ArrayList<>();

                // perform your search here using the searchConstraint String.
                for (int i = 0; i < filterCountryLists.size(); i++) {
                    String dataNames = filterCountryLists.get(i).getCountry_name();
                    if (dataNames.toLowerCase().startsWith(constraint.toString())) {
                        filterLists.add(filterCountryLists.get(i));
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
