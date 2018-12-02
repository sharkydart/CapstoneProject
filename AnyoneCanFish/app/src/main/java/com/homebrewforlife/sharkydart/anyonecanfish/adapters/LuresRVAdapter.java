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
import com.homebrewforlife.sharkydart.anyonecanfish.models.Fire_Lure;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LuresRVAdapter extends RecyclerView.Adapter<LuresRVAdapter.ViewHolder> {
    private final ArrayList<Fire_Lure> mLureArrayList;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Fire_Lure theBox = (Fire_Lure) view.getTag();
            Log.d("fart", "clicked Lure: " + theBox.getUid());
        }
    };

    public LuresRVAdapter(AppCompatActivity parent, ArrayList<Fire_Lure> items) {
        mLureArrayList = items;
    }

    @Override @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_content_tackle_boxes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mName.setText(mLureArrayList.get(position).getName());
        holder.mDescription.setText(mLureArrayList.get(position).getDesc());

        if(mLureArrayList.get(position).getImage_url() != null)
            Picasso.get().load(mLureArrayList.get(position).getImage_url())
                    .into(holder.mLurePic);

        holder.itemView.setTag(mLureArrayList.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mLureArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mDescription;
        TextView mName;
        ImageView mLurePic;

        ViewHolder(View view) {
            super(view);
            mDescription = view.findViewById(R.id.tvDescription);
            mName = view.findViewById(R.id.tvName);
            mLurePic = view.findViewById(R.id.imgLurePic);
        }
    }
}
