package com.example.Smart_Dustbin.collector;
// DustbinListAdapter.java

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.Smart_Dustbin.R;

import java.util.ArrayList;

public class DustbinListAdapter extends ArrayAdapter<Dustbin2> {

    private Context mContext;
    private ArrayList<Dustbin2> mDustbinList;

    public DustbinListAdapter(@NonNull Context context, ArrayList<Dustbin2> dustbinList) {
        super(context, 0, dustbinList);
        mContext = context;
        mDustbinList = dustbinList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.dustbin_list_item, parent, false);
        }

        Dustbin2 currentDustbin = mDustbinList.get(position);

        TextView idTextView = listItem.findViewById(R.id.dustbin_id_text_view);
        idTextView.setText("Dustbin ID: " + currentDustbin.getId());

        TextView locationTextView = listItem.findViewById(R.id.dustbin_location_text_view);
        locationTextView.setText("Location: " + currentDustbin.getLocation());

        return listItem;
    }
}
