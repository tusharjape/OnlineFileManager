package com.tushar.jape.onlinefilemanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddActivity extends AppCompatActivity {

    private static final int FILE_SELECTOR = 0;

    Toolbar toolbar;
    FloatingActionButton fab;
    Uri uri;
    EditText fileNameEditText;

    Button btnChoose, btnUpload;
    TextView txtView;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        initialize();

        fab.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) {startActivity(new Intent(AddActivity.this, ListActivity.class));}});

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == FILE_SELECTOR && resultCode == RESULT_OK && data!=null && data.getData() != null) {
            uri = data.getData();
            txtView.setText(uri.getPath());
            fileNameEditText.setText(uri.getPath());
            btnChoose.setText(R.string.choose_new_file);
            btnUpload.setVisibility(View.VISIBLE);
        }
    }

    void initialize(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnChoose = (Button)findViewById(R.id.btnChoose);
        btnUpload = (Button)findViewById(R.id.btnUpload);
        btnUpload.setVisibility(View.INVISIBLE);

        txtView = (TextView)findViewById(R.id.txtView);

        mStorageRef = FirebaseStorage.getInstance().getReference("image");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("image");

        fileNameEditText = (EditText)findViewById(R.id.fileEditText);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        fab = (FloatingActionButton)findViewById(R.id.fab);
    }

    private void chooseFile() {
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("image/*");
        startActivityForResult(fileIntent, FILE_SELECTOR);
    }

    private void uploadFile() {
        String fileName = fileNameEditText.getText().toString(), key = mDatabaseRef.push().getKey();

        uploadDetailsOnDatabase(new Image(fileName, key));
    }

    private void uploadDetailsOnDatabase(final Image image) {
        mDatabaseRef.child(image.getKey()).setValue(image).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        })
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                uploadFileOnStorage(image);
            }
        });
    }

    private void uploadFileOnStorage(final Image image){
        progressBar.setVisibility(View.VISIBLE);
        btnUpload.setText(R.string.uploading);
        btnChoose.setText(R.string.uploading);

        mStorageRef.child(image.getKey()+".jpg").putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(AddActivity.this, "File uploaded", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);

                        Intent intent = new Intent(AddActivity.this, ListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(AddActivity.this, exception.toString(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

}
