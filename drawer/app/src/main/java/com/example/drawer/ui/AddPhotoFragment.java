package com.example.drawer.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.drawer.R;
import com.example.drawer.models.LabelModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddPhotoFragment extends Fragment {

    public static  final int CAMERA_REQUEST_CODE = 1;

    ImageView p_image_view;
    ScrollView p_scrollView;

    Button save_button, camera_button;
    LinearLayout linearLayout;
    List<String> selectedLabels;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference labelsref = db.collection("labels");



    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addphoto, container, false);

        p_image_view = view.findViewById(R.id.p_imageView);
        p_scrollView = view.findViewById(R.id.p_scroll_view);
        save_button = view.findViewById(R.id.save_button);
        camera_button = view.findViewById(R.id.camera_button);
        linearLayout = view.findViewById(R.id.p_linear_layout);

        selectedLabels = new ArrayList<>();

        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePhotoToFirestore();
            }
        });

        loadLabelsFromFirestore();

        return view;
    }

    private void loadLabelsFromFirestore(){
        labelsref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<LabelModel> labelList = task.getResult().toObjects(LabelModel.class);

                    for(LabelModel label: labelList){
                        displayLabel(label);
                    }
                }else{
                    Toast.makeText(getActivity(), "Error getting labels", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayLabel(LabelModel label){
        LinearLayout horizontalLayout = new LinearLayout((getActivity()));
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

        CheckBox checkBox = new CheckBox(getActivity());
        checkBox.setText(label.getLabel());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                selectedLabels.add(label.getLabel());
            }else{
                selectedLabels.remove(label.getLabel());
            }
        });

        TextView labelTextView = new TextView(getActivity());

        horizontalLayout.addView(checkBox);
        horizontalLayout.addView(labelTextView);

        linearLayout.addView(horizontalLayout);
    }

    private void openCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            p_image_view.setImageBitmap(photo);
        }
    }

    private  void savePhotoToFirestore(){
        Bitmap photoBitmap = getBitmapFromImageView(p_image_view);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] photoData = baos.toByteArray();

        // Fotoğrafı Base64 formatına dönüştür
        String base64Photo = android.util.Base64.encodeToString(photoData, Base64.DEFAULT);

        // Firestore'a kaydedilecek veri
        Map<String, Object> data = new HashMap<>();
        data.put("photo", base64Photo);
        data.put("labels", selectedLabels);

        FirebaseFirestore.getInstance().collection("photos")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getActivity(), "Fotoğraf başarıyla Firestore'a kaydedildi.", Toast.LENGTH_SHORT).show();

                    String documentId = documentReference.getId();

                    StorageReference storegaRef = FirebaseStorage.getInstance().getReference().child("photos").child(documentId + ".jpg");

                    storegaRef.putBytes(photoData)
                            .addOnSuccessListener(taskSnapshot -> {
                                Toast.makeText(getActivity(), "Fotoğraf başarıyla Storage'a yüklemdi.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getActivity(), "Storage'a yükleme hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }).addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Firestore'a ekleme hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private  Bitmap getBitmapFromImageView(ImageView imageView){
        if(imageView.getDrawable() instanceof BitmapDrawable){
            return  ((BitmapDrawable) ((BitmapDrawable) imageView.getDrawable())).getBitmap();
        }else{
            return  null;
        }
    }

}