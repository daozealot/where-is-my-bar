package com.sample.mybar.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sample.mybar.R;
import com.sample.mybar.ui.OnListBarClickedListener;
import com.sample.mybar.utils.common.BarPresentData;

import java.util.List;

public class BarRecyclerViewAdapter extends RecyclerView.Adapter<BarRecyclerViewAdapter.ViewHolder> {

    private final List<BarPresentData> mBars;
    private final OnListBarClickedListener mListener;
    private final Context mContext;

    public BarRecyclerViewAdapter(List<BarPresentData> bars, OnListBarClickedListener listener, Context context) {
        mBars = bars;
        mListener = listener;
        this.mContext = context;
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
        holder.mDistanceView.setText(
                String.format(
                        mContext.getString(R.string.distance_format),
                        mBars.get(position).distance));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListBarClick(holder.mItem);
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
        BarPresentData mItem;

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
