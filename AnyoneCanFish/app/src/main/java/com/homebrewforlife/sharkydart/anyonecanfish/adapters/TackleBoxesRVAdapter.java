package com.homebrewforlife.sharkydart.anyonecanfish.adapters;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.homebrewforlife.sharkydart.anyonecanfish.R;
import com.homebrewforlife.sharkydart.anyonecanfish.models.Fire_TackleBox;

import java.util.ArrayList;

public class TackleBoxesRVAdapter extends RecyclerView.Adapter<TackleBoxesRVAdapter.ViewHolder> {
    private final ArrayList<Fire_TackleBox> mTackleBoxArrayList;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Fire_TackleBox theBox = (Fire_TackleBox) view.getTag();
            Log.d("fart", "clicked tacklebox: " + theBox.getUid());
        }
    };

    public TackleBoxesRVAdapter(AppCompatActivity parent, ArrayList<Fire_TackleBox> items) {
        mTackleBoxArrayList = items;
    }

    @Override @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_content_tackle_boxes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mName.setText(mTackleBoxArrayList.get(position).getName());
        holder.mDescription.setText(mTackleBoxArrayList.get(position).getDesc());

        holder.itemView.setTag(mTackleBoxArrayList.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mTackleBoxArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mDescription;
        TextView mName;
        TextView mLureCount;

        ViewHolder(View view) {
            super(view);
            mDescription = view.findViewById(R.id.tvDescription);
            mName = view.findViewById(R.id.tvName);
            mLureCount = view.findViewById(R.id.tvLureCount);
        }
    }
}
