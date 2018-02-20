package com.sample.mybar.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sample.mybar.R;
import com.sample.mybar.events.BarsReceivedEvent;
import com.sample.mybar.events.DistanceReceivedEvent;
import com.sample.mybar.ui.OnListBarClickedListener;
import com.sample.mybar.ui.adapters.BarRecyclerViewAdapter;
import com.sample.mybar.utils.common.BarPresentData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Bars.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListBarClickedListener}
 * interface.
 */
public class BarListFragment extends Fragment {

    private OnListBarClickedListener mListener;
    private List<BarPresentData> mBars;
    private BarRecyclerViewAdapter mBarRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BarListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBars = new ArrayList<>();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BarsReceivedEvent e) {
        if (e.barsData != null && mBars != null) {
            mBars.clear();
            mBars.addAll(e.barsData);
            if (mBarRecyclerViewAdapter != null) {
                mBarRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DistanceReceivedEvent e) {
        if (mBarRecyclerViewAdapter != null) {
            mBarRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bar_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mBarRecyclerViewAdapter = new BarRecyclerViewAdapter(mBars, mListener, context);
            recyclerView.setAdapter(mBarRecyclerViewAdapter);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListBarClickedListener) {
            mListener = (OnListBarClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListBarClickedListener");
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
