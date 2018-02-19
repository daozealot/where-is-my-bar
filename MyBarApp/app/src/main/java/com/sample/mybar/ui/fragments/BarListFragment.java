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
import com.sample.mybar.api.model.distance.Row;
import com.sample.mybar.api.model.places.Result;
import com.sample.mybar.events.BarsReceivedEvent;
import com.sample.mybar.events.DistanceReceivedEvent;
import com.sample.mybar.ui.adapters.BarRecyclerViewAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Bars.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class BarListFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private List<Result> mBars;
    private List<Row> mBarDistances;
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
        mBarDistances = new ArrayList<>();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BarsReceivedEvent event) {
        if (event.bars != null && mBars != null) {
            mBars.clear();
            mBars.addAll(event.bars);
            if (mBarRecyclerViewAdapter != null) {
                mBarRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DistanceReceivedEvent event) {
        if (event.barDistanceData != null && mBarDistances != null) {
            // FIXME is this the right placeId?
            mBarDistances.addAll(event.barDistanceData);
            if (mBarRecyclerViewAdapter != null) {
                mBarRecyclerViewAdapter.notifyDataSetChanged();
            }
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
            mBarRecyclerViewAdapter = new BarRecyclerViewAdapter(mBars, mBarDistances, mListener);
            recyclerView.setAdapter(mBarRecyclerViewAdapter);
        }
        return view;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
//    }


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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // FIXME: Update argument type and name
        void onListFragmentInteraction(Result item);
    }
}
