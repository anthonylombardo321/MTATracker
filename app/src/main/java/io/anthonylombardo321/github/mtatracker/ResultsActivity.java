package io.anthonylombardo321.github.mtatracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity  {
    TextView locationName;
    SwipeRefreshLayout refreshResults;
    ImageView serviceIcon1;
    ImageView serviceIcon2;
    ImageView serviceIcon3;
    ImageView serviceIcon4;
    ImageView serviceIcon5;
    ImageView serviceIcon6;
    ImageView serviceIcon7;
    ImageView serviceIcon8;
    ImageView serviceIcon9;
    ImageView serviceIcon10;
    RecyclerView resultsList;
    String resultType;
    SubwayInput subwayInput = new SubwayInput();
    BusInput busInput = new BusInput();
    ArrayList<BusResult> busResults = new ArrayList<>();
    ArrayList<SubwayResult> subwayResults = new ArrayList<>();

    String selectedService = "";
    String serviceDirection = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        //Getting the ID of all the items in the results layout
        locationName = findViewById(R.id.locationName);
        refreshResults = findViewById(R.id.refreshResults);
        serviceIcon1 = findViewById(R.id.serviceIcon1);
        serviceIcon2 = findViewById(R.id.serviceIcon2);
        serviceIcon3 = findViewById(R.id.serviceIcon3);
        serviceIcon4 = findViewById(R.id.serviceIcon4);
        serviceIcon5 = findViewById(R.id.serviceIcon5);
        serviceIcon6 = findViewById(R.id.serviceIcon6);
        serviceIcon7 = findViewById(R.id.serviceIcon7);
        serviceIcon8 = findViewById(R.id.serviceIcon8);
        serviceIcon9 = findViewById(R.id.serviceIcon9);
        serviceIcon10 = findViewById(R.id.serviceIcon10);
        resultsList = findViewById(R.id.recyclerViewResults);

        String location = "";
        ArrayList<String> services = new ArrayList<>();

        resultType = getIntent().getStringExtra("resultType");
        if(resultType.equals("Subway")){
            //Gets all the data from the intent and assigns it to the appropriate variable
            location = getIntent().getStringExtra("subwayStation");
            services = getIntent().getStringArrayListExtra("subwayStationServices");
            selectedService = getIntent().getStringExtra("subwayService");
            serviceDirection = getIntent().getStringExtra("subwayDirection");
            subwayResults = getIntent().getParcelableArrayListExtra("subwayResults");
            subwayInput = getIntent().getParcelableExtra("subwayInput");

            locationName.setText(location);
            setServiceIcons(services);
            Bitmap selectedServiceIcon = getMainServiceIcon(selectedService);
            //Sets up the RecyclerView
            ResultsAdapter resultsAdapter = new ResultsAdapter(selectedServiceIcon, serviceDirection, "Subway");
            resultsAdapter.setSubwayResults(subwayResults);
            resultsList.setAdapter(resultsAdapter);
            resultsList.setLayoutManager(new LinearLayoutManager(this));
            resultsList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            resultsList.setHasFixedSize(true);
        }
        else if(resultType.equals("Bus")){
            //Gets all the data from the intent and assigns it to the appropriate variable
            location = getIntent().getStringExtra("busStop");
            services = getIntent().getStringArrayListExtra("busStopServices");
            selectedService = getIntent().getStringExtra("busService");
            serviceDirection = getIntent().getStringExtra("busDirection");
            busResults = getIntent().getParcelableArrayListExtra("busResults");
            busInput = getIntent().getParcelableExtra("busInput");

            locationName.setText(location);
            setServiceIcons(services);
            Bitmap selectedServiceIcon = getMainServiceIcon(selectedService);
            //Sets up the RecyclerView
            ResultsAdapter resultsAdapter = new ResultsAdapter(selectedServiceIcon, serviceDirection, "Bus");
            resultsAdapter.setBusResults(busResults);
            resultsList.setAdapter(resultsAdapter);
            resultsList.setLayoutManager(new LinearLayoutManager(this));
            resultsList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
            resultsList.setHasFixedSize(true);
        }


        refreshResults.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(resultType.equals("Subway")){
                    //Upon Refresh, get a new list of SubwayResults
                    //before updating the RecyclerView
                    SubwayAPI subwayAPI = new SubwayAPI(subwayInput.getSubwayAPIService(), ResultsActivity.this);
                    subwayAPI.getData(subwayInput, new SubwayAPI.subwayResponseListener() {
                        @Override
                        public void onResponse(ArrayList<SubwayResult> response) {
                            ApplicationExecutors executors = new ApplicationExecutors();
                            executors.getMainThread().execute(() -> {
                                updateSubwayResults(response);
                            });
                            refreshResults.setRefreshing(false);
                        }
                    });
                }
                else{
                    //Upon Refresh, get a new list of BusResults
                    //before updating the RecyclerView
                    BusAPI busAPI = new BusAPI(ResultsActivity.this);
                    busAPI.getBusResults(busInput, new BusAPI.BusResultsResponseListener() {
                        @Override
                        public void onError(String message) {
                            Log.e("busResults Refreshing Error", message);
                        }
                        @Override
                        public void onResponse(ArrayList<BusResult> response) {
//                          resultsAdapter.updateBusResults(busResults);
                            ApplicationExecutors executors = new ApplicationExecutors();
                            executors.getMainThread().execute(() -> {
                                updateBusResults(response);
                            });
                            refreshResults.setRefreshing(false);
                        }
                    });
                }
            }
        });
    }

    //Replaces the old ResultsAdapter with a new one to update the results in RecyclerView
    private void updateSubwayResults(ArrayList<SubwayResult> response) {
        Bitmap selectedServiceIcon = getMainServiceIcon(selectedService);
        ResultsAdapter resultsAdapter = new ResultsAdapter(selectedServiceIcon, serviceDirection, "Subway");
        resultsAdapter.setSubwayResults(response);
        resultsList.setAdapter(resultsAdapter);
        resultsList.setLayoutManager(new LinearLayoutManager(this));
        resultsList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        resultsList.setHasFixedSize(true);
    }

    //Replaces the old ResultsAdapter with a new one to update the results in RecyclerView
    private void updateBusResults(ArrayList<BusResult> response) {
        Bitmap selectedServiceIcon = getMainServiceIcon(selectedService);
        ResultsAdapter resultsAdapter = new ResultsAdapter(selectedServiceIcon, serviceDirection, "Bus");
        resultsAdapter.setBusResults(response);
        resultsList.setAdapter(resultsAdapter);
        resultsList.setLayoutManager(new LinearLayoutManager(this));
        resultsList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        resultsList.setHasFixedSize(true);
    }

    //Gets programmatically created service icons
    //(Consisting of all services that passes by the selected stop)
    //before assigning them to the ImageViews found in the results layout
    public void setServiceIcons(ArrayList<String> services){
        for(int i = 0; i < services.size(); i++){
            ServiceIconCreator serviceIconCreator = new ServiceIconCreator(ResultsActivity.this);
            serviceIconCreator.setSize("Small");
            String service = services.get(i);
            if(service.equals("Rockaway Park Shuttle") || service.equals("Franklin Avenue Shuttle") || service.equals("42nd Street Shuttle")){
                service = "S";
            }
            Bitmap serviceIconBitmap = null;
            if(service.equals("N") || service.equals("Q") || service.equals("R") || service.equals("W")){
                serviceIconCreator.setTextColor("Black");
                serviceIconCreator.setBackgroundColor("Yellow");
                serviceIconCreator.setText(service);
                serviceIconCreator.setDimension(80);
                serviceIconBitmap = serviceIconCreator.createServiceIcon();
            }
            if(service.equals("1") || service.equals("2") || service.equals("3")){
                serviceIconCreator.setTextColor("White");
                serviceIconCreator.setBackgroundColor("Red");
                serviceIconCreator.setText(service);
                serviceIconCreator.setDimension(80);
                serviceIconBitmap = serviceIconCreator.createServiceIcon();
            }
            if(service.equals("4") || service.equals("5") || service.equals("6")){
                serviceIconCreator.setTextColor("White");
                serviceIconCreator.setBackgroundColor("Green");
                serviceIconCreator.setText(service);
                serviceIconCreator.setDimension(80);
                serviceIconBitmap = serviceIconCreator.createServiceIcon();
            }
            if(service.equals("A") || service.equals("C") || service.equals("E")){
                serviceIconCreator.setTextColor("White");
                serviceIconCreator.setBackgroundColor("Blue");
                serviceIconCreator.setText(service);
                serviceIconCreator.setDimension(80);
                serviceIconBitmap = serviceIconCreator.createServiceIcon();
            }
            if(service.equals("B") || service.equals("D") || service.equals("F")){
                serviceIconCreator.setTextColor("White");
                serviceIconCreator.setBackgroundColor("Orange");
                serviceIconCreator.setText(service);
                serviceIconCreator.setDimension(80);
                serviceIconBitmap = serviceIconCreator.createServiceIcon();
            }
            if(service.equals("J") || service.equals("M") || service.equals("Z")){
                serviceIconCreator.setTextColor("White");
                serviceIconCreator.setBackgroundColor("Brown");
                serviceIconCreator.setText(service);
                serviceIconCreator.setDimension(80);
                serviceIconBitmap = serviceIconCreator.createServiceIcon();
            }
            if(service.equals("L") || service.equals("S")){
                serviceIconCreator.setTextColor("White");
                serviceIconCreator.setBackgroundColor("Gray");
                serviceIconCreator.setText(service);
                serviceIconCreator.setDimension(80);
                serviceIconBitmap = serviceIconCreator.createServiceIcon();
            }
            if(service.equals("G")){
                serviceIconCreator.setTextColor("White");
                serviceIconCreator.setBackgroundColor("GreenYellow");
                serviceIconCreator.setText(service);
                serviceIconCreator.setDimension(80);
                serviceIconBitmap = serviceIconCreator.createServiceIcon();
            }
            if(service.equals("7")){
                serviceIconCreator.setTextColor("White");
                serviceIconCreator.setBackgroundColor("Purple");
                serviceIconCreator.setText(service);
                serviceIconCreator.setDimension(80);
                serviceIconBitmap = serviceIconCreator.createServiceIcon();
            }
            if(service.length() > 1){
                if(service.contains("-SBS")){
                    service = service.split("-")[0];
                }
                serviceIconCreator.setTextColor("White");
                serviceIconCreator.setBackgroundColor("Black");
                serviceIconCreator.setText(service);
                serviceIconCreator.setDimension(80);
                serviceIconBitmap = serviceIconCreator.createServiceIcon();
            }
            switch(i){
                case 0: serviceIcon1.setImageBitmap(serviceIconBitmap);
                        serviceIcon1.setVisibility(View.VISIBLE);
                        break;
                case 1: serviceIcon2.setImageBitmap(serviceIconBitmap);
                        serviceIcon2.setVisibility(View.VISIBLE);
                        break;
                case 2: serviceIcon3.setImageBitmap(serviceIconBitmap);
                        serviceIcon3.setVisibility(View.VISIBLE);
                        break;
                case 3: serviceIcon4.setImageBitmap(serviceIconBitmap);
                        serviceIcon4.setVisibility(View.VISIBLE);
                        break;
                case 4: serviceIcon5.setImageBitmap(serviceIconBitmap);
                        serviceIcon5.setVisibility(View.VISIBLE);
                        break;
                case 5: serviceIcon6.setImageBitmap(serviceIconBitmap);
                        serviceIcon6.setVisibility(View.VISIBLE);
                        break;
                case 6: serviceIcon7.setImageBitmap(serviceIconBitmap);
                        serviceIcon7.setVisibility(View.VISIBLE);
                        break;
                case 7: serviceIcon8.setImageBitmap(serviceIconBitmap);
                        serviceIcon8.setVisibility(View.VISIBLE);
                        break;
                case 8: serviceIcon9.setImageBitmap(serviceIconBitmap);
                        serviceIcon9.setVisibility(View.VISIBLE);
                        break;
                case 9: serviceIcon10.setImageBitmap(serviceIconBitmap);
                        serviceIcon10.setVisibility(View.VISIBLE);
                        break;
            }
        }
    }

    //Gets the service icon bitmap of the
    //selected service found in our selected stop
    public Bitmap getMainServiceIcon(String service){
        ServiceIconCreator serviceIconCreator = new ServiceIconCreator(ResultsActivity.this);
        serviceIconCreator.setSize("Large");
        Bitmap serviceIconBitmap = null;
        if(service.equals("N") || service.equals("Q") || service.equals("R") || service.equals("W")){
            serviceIconCreator.setTextColor("Black");
            serviceIconCreator.setBackgroundColor("Yellow");
            serviceIconCreator.setText(service);
            serviceIconCreator.setDimension(160);
            serviceIconBitmap = serviceIconCreator.createServiceIcon();
        }
        if(service.equals("1") || service.equals("2") || service.equals("3")){
            serviceIconCreator.setTextColor("White");
            serviceIconCreator.setBackgroundColor("Red");
            serviceIconCreator.setText(service);
            serviceIconCreator.setDimension(160);
            serviceIconBitmap = serviceIconCreator.createServiceIcon();
        }
        if(service.equals("4") || service.equals("5") || service.equals("6")){
            serviceIconCreator.setTextColor("White");
            serviceIconCreator.setBackgroundColor("Green");
            serviceIconCreator.setText(service);
            serviceIconCreator.setDimension(160);
            serviceIconBitmap = serviceIconCreator.createServiceIcon();
        }
        if(service.equals("A") || service.equals("C") || service.equals("E")){
            serviceIconCreator.setTextColor("White");
            serviceIconCreator.setBackgroundColor("Blue");
            serviceIconCreator.setText(service);
            serviceIconCreator.setDimension(160);
            serviceIconBitmap = serviceIconCreator.createServiceIcon();
        }
        if(service.equals("B") || service.equals("D") || service.equals("F")){
            serviceIconCreator.setTextColor("White");
            serviceIconCreator.setBackgroundColor("Orange");
            serviceIconCreator.setText(service);
            serviceIconCreator.setDimension(160);
            serviceIconBitmap = serviceIconCreator.createServiceIcon();
        }
        if(service.equals("J") || service.equals("M") || service.equals("Z")){
            serviceIconCreator.setTextColor("White");
            serviceIconCreator.setBackgroundColor("Brown");
            serviceIconCreator.setText(service);
            serviceIconCreator.setDimension(160);
            serviceIconBitmap = serviceIconCreator.createServiceIcon();
        }
        if(service.equals("L") || service.equals("S")){
            serviceIconCreator.setTextColor("White");
            serviceIconCreator.setBackgroundColor("Gray");
            serviceIconCreator.setText(service);
            serviceIconCreator.setDimension(160);
            serviceIconBitmap = serviceIconCreator.createServiceIcon();
        }
        if(service.equals("G")){
            serviceIconCreator.setTextColor("White");
            serviceIconCreator.setBackgroundColor("GreenYellow");
            serviceIconCreator.setText(service);
            serviceIconCreator.setDimension(160);
            serviceIconBitmap = serviceIconCreator.createServiceIcon();
        }
        if(service.equals("7")){
            serviceIconCreator.setTextColor("White");
            serviceIconCreator.setBackgroundColor("Purple");
            serviceIconCreator.setText(service);
            serviceIconCreator.setDimension(160);
            serviceIconBitmap = serviceIconCreator.createServiceIcon();
        }
        if(service.length() > 1){
            if(service.contains("-SBS")){
                service = service.split("-")[0];
            }
            serviceIconCreator.setTextColor("White");
            serviceIconCreator.setBackgroundColor("Black");
            serviceIconCreator.setText(service);
            serviceIconCreator.setDimension(160);
            serviceIconBitmap = serviceIconCreator.createServiceIcon();
        }
        return serviceIconBitmap;
    }
}