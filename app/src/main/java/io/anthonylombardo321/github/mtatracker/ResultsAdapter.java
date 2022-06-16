package io.anthonylombardo321.github.mtatracker;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsAdapterViewHolder> {
    private Bitmap serviceIcon;
    private ArrayList<BusResult> busResults;
    private ArrayList<SubwayResult> subwayResults;
    private String routeDirection;
    private String resultType;

    public ResultsAdapter(Bitmap serviceIcon, String direction, String resultType){
        this.serviceIcon = serviceIcon;
        this.routeDirection = direction;
        this.resultType = resultType;
        busResults = new ArrayList<>();
        subwayResults = new ArrayList<>();
    }

    public void setBusResults(ArrayList<BusResult> busResults) {
        this.busResults = busResults;
    }

    public void setSubwayResults(ArrayList<SubwayResult> subwayResults) {
        this.subwayResults = subwayResults;
    }

    //Appends new item layout to RecyclerView
    @NonNull
    @Override
    public ResultsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_resultitem, parent, false);
        return new ResultsAdapterViewHolder(view);
    }

    //Assigns all relevant information to item layout
    @Override
    public void onBindViewHolder(@NonNull ResultsAdapterViewHolder holder, int position) {
        holder.serviceIcon.setImageBitmap(serviceIcon);
        holder.routeDirectionText.setText(routeDirection);
        if(resultType.equals("Subway")) {
            SubwayResult subwayResult = subwayResults.get(position);
            int waitTimeMinutes = Integer.parseInt(subwayResult.getWaitTime());
            boolean isExpanded = subwayResult.isExpanded();
            holder.waitTimeText.setText(getWaitTimeString(waitTimeMinutes));
            holder.arrivalTimeText.setText(subwayResult.getArrivalTime());
            holder.stopsUntilDestinationText.setText(subwayResult.getStopsUntilDestination());
            holder.vehicleStatusText.setText(subwayResult.getCurrentStatus());
            holder.expandableResultInformation.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        }
        else{
            BusResult busResult = busResults.get(position);
            int waitTimeMinutes = Integer.parseInt(busResult.getWaitTime());
            boolean isExpanded = busResult.isExpanded();
            holder.waitTimeText.setText(getWaitTimeString(waitTimeMinutes));
            holder.arrivalTimeText.setText(busResult.getArrivalTime());
            holder.stopsUntilDestinationText.setText(busResult.getStopsUntilBusStop());
            holder.vehicleStatusText.setText(busResult.getDistanceFromStop());
            holder.expandableResultInformation.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        }
    }

    private String getWaitTimeString(int waitTimeMinutes){
        int hours = waitTimeMinutes / 60;
        int minutes = waitTimeMinutes % 60;
        String waitTimeString = "";
        if(hours > 0){
            waitTimeString = hours == 1 ? hours + " Hour " + minutes + " Minutes" : hours + " Hours " + minutes + " Minutes";
        }
        else{
            waitTimeString = minutes + " Minutes";
        }
        return waitTimeString;
    }

    @Override
    public int getItemCount() {
        if(resultType.equals("Bus")){
            return busResults.size();
        }
        else{
            return subwayResults.size();
        }
    }

    public class ResultsAdapterViewHolder extends RecyclerView.ViewHolder {

        ImageView serviceIcon;
        TextView routeDirectionText;
        TextView waitTimeText;
        TextView arrivalTimeText;
        TextView stopsUntilDestinationText;
        TextView vehicleStatusText;

        ConstraintLayout expandableResultInformation;
        CardView mainResultView;

        public ResultsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            //Getting the ID of all the items in the item layout
            serviceIcon = itemView.findViewById(R.id.serviceIcon);
            routeDirectionText = itemView.findViewById(R.id.routeDirection);
            waitTimeText = itemView.findViewById(R.id.waitTimeText);
            arrivalTimeText = itemView.findViewById(R.id.arrivalTimeText);
            stopsUntilDestinationText = itemView.findViewById(R.id.stopsUntilDestinationText);
            vehicleStatusText = itemView.findViewById(R.id.vehicleStatusText);
            expandableResultInformation = itemView.findViewById(R.id.resultInformation);
            mainResultView = itemView.findViewById(R.id.mainResultView);

            mainResultView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Expanding/Collapsing Result Information Section on Click
                    if(resultType.equals("Bus")){
                        BusResult busResult = busResults.get(getAdapterPosition());
                        busResult.setExpanded(!busResult.isExpanded());
                    }
                    else{
                        SubwayResult subwayResult = subwayResults.get(getAdapterPosition());
                        subwayResult.setExpanded(!subwayResult.isExpanded());
                    }
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}
