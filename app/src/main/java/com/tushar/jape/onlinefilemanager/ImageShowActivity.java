package com.tushar.jape.onlinefilemanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class ImageShowActivity extends AppCompatActivity {

    private String imageName;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String imageKey = getIntent().getStringExtra("key");
        imageName = getIntent().getStringExtra("name");
        imageView = (ImageView)findViewById(R.id.imageView);
        storageRef = FirebaseStorage.getInstance().getReference("image").child(imageKey +".jpg");
        databaseRef = FirebaseDatabase.getInstance().getReference("image").child(imageKey);

        TextView fileNameTextView = (TextView) findViewById(R.id.fileNameTextView);
        fileNameTextView.setText(imageName);

        try {
            final File localFile = File.createTempFile("images", "jpg");

            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getPath());
                    imageView.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(ImageShowActivity.this, exception.toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        catch(Exception e){
            e.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFile(storageRef, databaseRef);
            }
        });
    }

    private void deleteFile(StorageReference storageRef, DatabaseReference databaseRef){
        databaseRef.setValue(null);
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ImageShowActivity.this, "File deleted!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ImageShowActivity.this, ListActivity.class));
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ImageShowActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
