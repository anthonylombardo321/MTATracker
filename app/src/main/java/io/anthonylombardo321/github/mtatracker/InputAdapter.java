package io.anthonylombardo321.github.mtatracker;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

//Java Class replaces ArrayAdapter to include "contains" filter (results contains input as substring),
//rather than searching for a word that starts with the search input
public class InputAdapter extends ArrayAdapter<String> {
    private List<String> subwayStationList;

    public InputAdapter(@NonNull Context context, int resource, @NonNull List<String> stationList){
        super(context, resource, stationList);
        subwayStationList = new ArrayList<>(stationList);
    }

    @NonNull
    @Override
    public Filter getFilter(){
        return stationFilter;
    }

    private Filter stationFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<String> suggestions = new ArrayList<>();

            if(constraint == null || constraint.length() == 0) {
                suggestions.addAll(subwayStationList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(String station : subwayStationList){
                    if(station.toLowerCase().contains(filterPattern)) {
                        suggestions.add(station);
                    }
                }
            }
            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
