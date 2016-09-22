package com.appunite.debughelper;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.appunite.debughelper.adapter.CountAdapter;
import com.appunite.debughelper.interceptor.DebugInterceptor;


public class InfoListFragment extends DialogFragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    Button clearButton;

    public static Fragment newInstance() {
        return new InfoListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.debug_fragment_layout, container, true);


        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.info_recyclerview);
        recyclerView.setLayoutManager(mLayoutManager);

        final CountAdapter adapter = new CountAdapter(DebugInterceptor.getRequestCounter());
        recyclerView.setAdapter(adapter);

        clearButton = (Button) rootView.findViewById(R.id.clear_counter_button);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugInterceptor.cleanRequestLogs();
                adapter.updateData(DebugInterceptor.getRequestCounter());
            }
        });

        return rootView;
    }

}