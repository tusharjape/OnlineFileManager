package com.tushar.jape.onlinefilemanager;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter {
    private final Activity context;
    private final ArrayList<Image> images;

    ListAdapter(@NonNull Activity context, ArrayList<Image> arr) {
        super(context, R.layout.row_view);
        this.context = context;
        this.images = arr;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = context.getLayoutInflater().inflate(R.layout.row_view, null);

        TextView txtName = (TextView)convertView.findViewById(R.id.fileNameTextView);
        txtName.setText(images.get(position).getFileName());

        return convertView;
    }
}
