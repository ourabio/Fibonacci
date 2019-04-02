package com.braineet.fibonacci;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import java.math.BigInteger;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<String> rowsArrayList = new ArrayList<>();
    private Handler mHandler;
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        mHandler = new Handler();
        populateData();
        initAdapter();
        initScrollListener();


    }

    // Populate first 20 items
    private void populateData() {

        for(int i =0 ; i<20 ; i++  ){

            rowsArrayList.add(calculateFibonacci(i).toString());
        }
        rowsArrayList.add(null);
    }

    private void initAdapter() {

        recyclerViewAdapter = new RecyclerViewAdapter(rowsArrayList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    // Calculate new Fibonnaci
    private BigInteger calculateFibonacci(int n){

        if (n < 2) return((BigInteger.valueOf(n)));

        return(  new BigInteger(rowsArrayList.get(n-2)).add(new BigInteger(rowsArrayList.get(n-1)))  );
    }

    //Scroll listener to detect bottom of list reached

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() >= rowsArrayList.size() - 2) {

                        //reached bottom of list!

                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });


    }

    private void loadMore() {

        //      New thread to perform background operation to avoid blocking the mainUI thread

        new Thread(new Runnable() {

            @Override

            public void run() {

                //remove loading item
                rowsArrayList.remove(rowsArrayList.size() - 1);

                // Prepare the new Page Limits
                int currentSize = rowsArrayList.size();
                int nextLimit = currentSize + 20;

                //Add new Page to DataList
                for(int i = currentSize ; i< nextLimit ; i++){

                    rowsArrayList.add(calculateFibonacci(i).toString());
                }

                //Add new loadingBar to the end of the List
                rowsArrayList.add(null);

                 //Update the value background thread to UI thread with delay to show loading

                mHandler.postDelayed(new Runnable() {

                        @Override

                        public void run() {

                            // Notify Adapter of the new Datalist
                            recyclerViewAdapter.notifyDataSetChanged();
                            isLoading = false;

                        }

                    }, 500);

                }

        }).start();

    }
}
