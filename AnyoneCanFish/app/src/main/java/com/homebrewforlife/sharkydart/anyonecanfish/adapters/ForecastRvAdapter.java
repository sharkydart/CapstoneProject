package com.homebrewforlife.sharkydart.anyonecanfish.adapters;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.homebrewforlife.sharkydart.anyonecanfish.R;
import com.homebrewforlife.sharkydart.anyonecanfish.models.Fire_GameFish;
import com.homebrewforlife.sharkydart.anyonecanfish.models.ForecastPeriod;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ForecastRvAdapter extends RecyclerView.Adapter<ForecastRvAdapter.ViewHolder> {
    private final AppCompatActivity mParentActivity;
    private final ArrayList<ForecastPeriod> mForecastPeriodArrayList;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ForecastPeriod thePeriod = (ForecastPeriod) view.getTag();
            Log.d("fart", "clicked day: " + thePeriod.getName());
        }
    };

    public ForecastRvAdapter(AppCompatActivity parent, ArrayList<ForecastPeriod> items) {
        mForecastPeriodArrayList = items;
        mParentActivity = parent;
    }

    @Override @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String temperature = mForecastPeriodArrayList.get(position).getTemperature() + " "
                + mForecastPeriodArrayList.get(position).getTemperatureUnit();
        holder.mTvTemperature.setText(temperature);
        holder.mTvWindSpeed.setText(mForecastPeriodArrayList.get(position).getWindSpeed());
        holder.mTvWindDirection.setText(mForecastPeriodArrayList.get(position).getWindDirection());
        holder.mTvShortForecast.setText(mForecastPeriodArrayList.get(position).getShortForecast());
        holder.mTvPeriodName.setText(mForecastPeriodArrayList.get(position).getName());
        Picasso.get().load(mForecastPeriodArrayList.get(position).getIconURL()).into(holder.mImgWeatherIcon);

        holder.itemView.setTag(mForecastPeriodArrayList.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        Log.d("fart", "count: " + mForecastPeriodArrayList.size());
        return mForecastPeriodArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvTemperature;
        TextView mTvWindSpeed;
        TextView mTvWindDirection;
        TextView mTvPeriodName;
        TextView mTvShortForecast;
        ImageView mImgWeatherIcon;

        ViewHolder(View view) {
            super(view);
            mTvTemperature = view.findViewById(R.id.tvTemperature);
            mTvWindSpeed = view.findViewById(R.id.tvWindSpeed);
            mTvWindDirection = view.findViewById(R.id.tvWindDirection);
            mImgWeatherIcon = view.findViewById(R.id.imgWeatherIcon);
            mTvPeriodName = view.findViewById(R.id.tvPeriodName);
            mTvShortForecast = view.findViewById(R.id.tvShortForecast);
        }
    }
}
