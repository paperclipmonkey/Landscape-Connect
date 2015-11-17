package com.example.michaelwaterworth.testqdownloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by michaelwaterworth on 03/11/2015. Copyright Michael Waterworth
 */
public class SectionAdapter extends ArrayAdapter<SectionResponseLink> {
    public SectionAdapter(Context context, ArrayList<SectionResponseLink> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.sections_row, parent, false);
        }

        SectionResponseLink srl = getItem(position);

        TextView title = (TextView) convertView.findViewById(R.id.section_row_title);
        title.setText(srl.section.getTitle());

        TextView required = (TextView) convertView.findViewById(R.id.section_row_description);

        String isRequired = "";
        if(srl.section.isRequired()){
            isRequired = "Required";
        }
        required.setText(isRequired);

        ImageView isCompleted = (ImageView) convertView.findViewById(R.id.section_row_done);
        if(srl.sectionResponse.isCompleted()){
            isCompleted.setVisibility(View.VISIBLE);
        } else {
            isCompleted.setVisibility(View.GONE);
        }

        return convertView;
    }
}