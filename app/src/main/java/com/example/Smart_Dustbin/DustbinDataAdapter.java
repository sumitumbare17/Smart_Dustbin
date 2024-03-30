package com.example.Smart_Dustbin;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DustbinDataAdapter extends RecyclerView.Adapter<DustbinDataAdapter.ViewHolder> {

    private Context mContext;
    private List<DustbinData> mDataList;

    public DustbinDataAdapter(Context context, List<DustbinData> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_dustbin_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DustbinData data = mDataList.get(position);
        holder.dustbinIdTextView.setText(" Dustbin ID : "+data.getDustbinId());
        holder.totalWasteTextView.setText("Total Waste (kg) : "+String.valueOf(data.getTotalWaste()));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dustbinIdTextView;
        TextView totalWasteTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dustbinIdTextView = itemView.findViewById(R.id.dustbin_id_text_view);
            totalWasteTextView = itemView.findViewById(R.id.total_waste_text_view);
        }
    }
}
