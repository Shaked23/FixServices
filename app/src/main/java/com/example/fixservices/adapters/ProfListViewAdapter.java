package com.example.fixservices.adapters;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fixservices.classes.Professional;
import com.example.fixservices.R;

import java.util.ArrayList;
import java.util.List;

public class ProfListViewAdapter extends BaseAdapter {

    private Context context;
    private List<Professional> professionalList;

    public ProfListViewAdapter(Context context, List<Professional> professionalList) {
        this.context = context;
        this.professionalList = professionalList;
    }

    @Override
    public int getCount() {
        return professionalList.size();
    }

    @Override
    public Professional getItem(int position) {
        return professionalList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.professional_list_card, parent, false);
        }

        TextView nameTextView = convertView.findViewById(R.id.textViewName);
        TextView phoneTextView = convertView.findViewById(R.id.textViewPhone);
        TextView domainTextView = convertView.findViewById(R.id.textViewDomain);
        TextView exprienceTextView = convertView.findViewById(R.id.textViewExperience);
        TextView scoreTextView = convertView.findViewById(R.id.textViewScore);
        TextView ratersTextView = convertView.findViewById(R.id.textViewRaters);
        TextView dateTextView = convertView.findViewById(R.id.textViewDate);
        TextView uidTextView = convertView.findViewById(R.id.textViewUID);

        Professional professional = getItem(position); // Get the professional at the current position
        if (professional != null) {
            nameTextView.setText("Name: " + professional.getName());
            phoneTextView.setText("Phone: " + professional.getPhone());
            domainTextView.setText("Domain: " + professional.getDomain());
            professional.calculateExperience();
            exprienceTextView.setText("Experience: " + professional.getExperience());
            scoreTextView.setText("Score: " + professional.getScore());
            ratersTextView.setText("Raters: " + professional.getRaters());
            dateTextView.setText("Register Date: " + professional.getDateCreate());
            uidTextView.setText("UID: " + professional.getUID());
        }

        uidTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = uidTextView.getText().toString().replace("UID: ", "");
                // Get the ClipboardManager
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                // Create a ClipData object to store the text
                ClipData clipData = ClipData.newPlainText("Copied Text", uid);
                // Set the ClipData to the clipboard
                clipboardManager.setPrimaryClip(clipData);
                // Notify the user that the text has been copied (optional)
                Toast.makeText(context,"Text copied to clipboard: " + uid, Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    public void setDataSet(ArrayList<Professional> professionalList) {
        this.professionalList = professionalList;
    }
}
