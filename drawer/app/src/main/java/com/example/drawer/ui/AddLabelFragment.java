package com.example.drawer.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.drawer.R;
import com.example.drawer.models.LabelModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class AddLabelFragment extends Fragment {

    EditText editTextLabel, editTextDescription;
    Button addButton;
    LinearLayout linearLayout;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference labelsRef = db.collection("labels");


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addlabel, container, false);

        editTextLabel = view.findViewById(R.id.editTextLabel);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        addButton = view.findViewById(R.id.add_button);
        linearLayout = view.findViewById(R.id.linear_layout);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLabelToFirestore();
            }
        });

        getLabelsFromFirestore();

        return view;
    }

    private void addLabelToFirestore(){
        String label = editTextLabel.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if(label.isEmpty()){
            Toast.makeText(getActivity(), "Label cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        LabelModel newLabel = new LabelModel(label, description);

        labelsRef.add(newLabel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(), "Label added succesfully", Toast.LENGTH_SHORT).show();
                            clearInputFields();
                            displayLabel(newLabel);
                        }else{
                            Toast.makeText(getActivity(), "Error adding label", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void getLabelsFromFirestore(){
        labelsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<LabelModel> labellist = task.getResult().toObjects(LabelModel.class);

                    for(LabelModel label : labellist){
                        displayLabel(label);
                    }
                }else{
                    Toast.makeText(getActivity(), "Error getting labels", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayLabel(LabelModel label){
        TextView labelTextView = new TextView(getActivity());
        labelTextView.setText(label.getLabel());

        linearLayout.addView(labelTextView);
    }

    private void clearInputFields(){
        editTextLabel.setText("");
        editTextDescription.setText("");
    }

}