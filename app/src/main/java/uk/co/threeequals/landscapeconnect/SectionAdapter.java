package uk.co.threeequals.landscapeconnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Section Adapter is used to display Sections in a list
 * Created by michaelwaterworth on 03/11/2015. Copyright Michael Waterworth
 */
class SectionAdapter extends ArrayAdapter<SectionResponseLink> {
    public SectionAdapter(Context context, ArrayList<SectionResponseLink> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_sections_row, parent, false);
        }

        SectionResponseLink srl = getItem(position);

        TextView title = (TextView) convertView.findViewById(R.id.section_row_title);
        title.setText(srl.section.getTitle());

        TextView required = (TextView) convertView.findViewById(R.id.section_row_description);

        String isRequired = getContext().getString(R.string.optional);
        if (srl.section.hasRequiredQuestions()) {
            isRequired = getContext().getString(R.string.required);
        }
        required.setText(isRequired);

        ImageView isCompleted = (ImageView) convertView.findViewById(R.id.section_row_done);
        if (srl.sectionResponse.isCompleted()) {
            isCompleted.setVisibility(View.VISIBLE);
        } else {
            isCompleted.setVisibility(View.GONE);
        }

        return convertView;
    }
}