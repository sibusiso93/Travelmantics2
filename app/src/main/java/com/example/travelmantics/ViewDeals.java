package com.example.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class ViewDeals extends AppCompatActivity
{


    Toolbar toolbar;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private static final int PICTURE_RESULT = 42; //the answer to everything
    EditText Title;
    EditText Description;
    EditText Price;
    ImageView imageView;
    Button select;
    Deals deal;
    Uri Image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_deals);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        Title = findViewById(R.id.edit_Title);
        Description =  findViewById(R.id.edit_Descr);
        Price =  findViewById(R.id.edit_Price);
        imageView =  findViewById(R.id.image2);
        select = findViewById(R.id.button_Image);
        if(!FirebaseUtil.isAdmin)
        {
            select.setVisibility(View.GONE);

        }
        else
        {
            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPickImageDialog();
                }
            });
        }
        Intent intent = getIntent();
        Deals deal = (Deals) intent.getSerializableExtra("Deal");
        if (deal==null)
        {
            deal = new Deals();
        }
        this.deal = deal;
        Title.setText(deal.getTitle());
        Description.setText(deal.getDescription());
        Price.setText(deal.getPrice());
        showImage(deal.getImageurl());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.del).setVisible(true);
            menu.findItem(R.id.Save).setVisible(true);
            enableEditTexts(true);
        }
        else {
            menu.findItem(R.id.del).setVisible(false);
            menu.findItem(R.id.Save).setVisible(false);
            enableEditTexts(false);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())

        {
            case R.id.del:
                deleteDeal();
                Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show();
                backToList();
                return true;



                case R.id.Save:


                    if(Image != null)
                    {
                        if(deal.getId() != null)
                        {
                            StorageReference picRef = FirebaseUtil.mStorage.getReference().child(deal.getImageurl());
                            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Delete Image", "Image Successfully Deleted");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Delete Image", e.getMessage());
                                }
                            });
                        }
                        StorageReference ref = FirebaseUtil.mStorageRef.child(Image.getLastPathSegment());
                        deal.setName(Image.getLastPathSegment());
                        ref.putFile(Image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                            {
                                final String name  = taskSnapshot.getMetadata().getName();
                                final Task<Uri> url = FirebaseUtil.mStorageRef.child(name).getDownloadUrl();
                                url.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri)
                                    {   deal.setImageurl(uri.toString());
                                       saveDeal();
                                        clean();

                                        backToList();

                                    }

                                });


                            }

                        });
                        Toast.makeText(this, "Deal Saved", Toast.LENGTH_LONG).show();

                    }
                    else
                    {
                           saveDeal();
                         clean();
                         Toast.makeText(this, "Deal Saved", Toast.LENGTH_LONG).show();
                          backToList();
                    }




                return true;

            default:
                return true;

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 42 && resultCode == RESULT_OK)
        {
            Image = data.getData();


            Glide.with(this)
                    .load(Image)
                    .into(imageView);
        }

    }


    private void showPickImageDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);


        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Gallery");


        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, PICTURE_RESULT);





                    }
                });
        builderSingle.show();
    }

    private void backToList()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void clean()
    {
        Title.setText("");
        Price.setText("");
        Description.setText("");
        Title.requestFocus();
    }

    private void enableEditTexts(boolean isEnabled)
    {
        Title.setEnabled(isEnabled);
        Description.setEnabled(isEnabled);
        Price.setEnabled(isEnabled);
    }

    private void showImage(String url)
    {
        if (url != null && url.isEmpty() == false)
        {

            Glide.with(this)
                    .load(url)
                    .into(imageView);
        }
    }

    private void deleteDeal() {
        if (deal == null)
        {
            Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_SHORT).show();
            return;
        }
        mDatabaseReference.child(deal.getId()).removeValue();

        if(deal.getName() != null && deal.getName().isEmpty() == false) {
            StorageReference picRef = FirebaseUtil.mStorage.getReference().child(deal.getImageurl());
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Delete Image", "Image Successfully Deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Delete Image", e.getMessage());
                }
            });
        }

    }

    private void saveDeal()
    {
        deal.setTitle(Title.getText().toString());
        deal.setDescription(Description.getText().toString());
        deal.setPrice(Price.getText().toString());
        if(deal.getId()==null)
        {
            mDatabaseReference.push().setValue(deal);
        }
        else
            {
            mDatabaseReference.child(deal.getId()).setValue(deal);
        }
    }

}
