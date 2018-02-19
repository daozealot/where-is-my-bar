package com.sample.mybar.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sample.mybar.R;
import com.sample.mybar.api.model.distance.Row;
import com.sample.mybar.api.model.places.Result;
import com.sample.mybar.ui.fragments.BarListFragment.OnListFragmentInteractionListener;

import java.util.List;

public class BarRecyclerViewAdapter extends RecyclerView.Adapter<BarRecyclerViewAdapter.ViewHolder> {

    private final List<Result> mBars;
    private final List<Row> mBarDistances;
    private final OnListFragmentInteractionListener mListener;

    public BarRecyclerViewAdapter(List<Result> bars, List<Row> barDistances, OnListFragmentInteractionListener listener) {
        mBars = bars;
        mBarDistances = barDistances;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_bar_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mBars.get(position);
        holder.mTitleView.setText(mBars.get(position).name);
        // TODO: add distance
        if ((mBarDistances.size() - 1) >= position) {
            holder.mDistanceView.setText(String.format("%s", mBarDistances.get(position).elements.get(0).distance.text));
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBars.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mTitleView;
        final TextView mDistanceView;
        Result mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = view.findViewById(R.id.title);
            mDistanceView = view.findViewById(R.id.distance);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
