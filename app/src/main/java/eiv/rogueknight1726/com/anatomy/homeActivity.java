package eiv.rogueknight1726.com.anatomy;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

public class homeActivity extends AppCompatActivity {
    Spinner spinner;
    TextView questionET,optionOneET,optionTwoET,optionThreeET,optionFourET,descriptionET;
    private int STORAGE_PERMISSION_CODE = 23;
    int RESULT_LOAD_IMG_FOR_QUESTION = 1;
    int RESULT_LOAD_IMG_FOR_ANSWER = 2;
    ImageView questionImage,descriptiveAnswerImage;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        initiateViews();
        initiateSpinner();
    }


    private void initiateViews(){
        pDialog = new ProgressDialog(homeActivity.this);
        pDialog.setCancelable(false);
        questionET = (TextView)findViewById(R.id.questionET);
        optionOneET = (TextView)findViewById(R.id.optionOneET);
        optionTwoET = (TextView)findViewById(R.id.optionTwoET);
        optionThreeET = (TextView)findViewById(R.id.optionThreeET);
        optionFourET = (TextView)findViewById(R.id.optionFourET);
        descriptionET = (TextView)findViewById(R.id.descriptionTextET);
        questionImage = (ImageView)findViewById(R.id.questionImage);
        descriptiveAnswerImage = (ImageView)findViewById(R.id.descriptiveAnswerImage);
    }

    private void initiateSpinner(){
        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    public void validateQuestion(View v){
        String error = "";
        if (questionET.getText().toString().equals("")){
            error = "Please fill the question";
            questionET.requestFocus();
            showError(error);
            return;
        }
        else if(optionOneET.getText().toString().equals("")){
            error = "Please fill the Option One";
            optionOneET.requestFocus();
            showError(error);
            return;
        }
        else if(optionTwoET.getText().toString().equals("")){
            error = "Please fill the Option Two";
            optionTwoET.requestFocus();
            showError(error);
            return;
        }
        else if(optionThreeET.getText().toString().equals("")){
            error = "Please fill the Option Three";
            optionThreeET.requestFocus();
            showError(error);
            return;
        }
        else if(optionFourET.getText().toString().equals("")){
            error = "Please fill the Option Four";
            optionFourET.requestFocus();
            showError(error);
            return;
        }
        else if (spinner.getSelectedItemPosition()==0){
            error = "Select teh correct answer";
            spinner.requestFocus();
            showError(error);
            return;
        }
        sendQuestionToFirebase();
    }

    private void sendQuestionToFirebase(){
        pDialog.show();
        final String question = questionET.getText().toString();
        final String optionOne = optionOneET.getText().toString();
        final String optionTwo = optionTwoET.getText().toString();
        final String optionThree = optionThreeET.getText().toString();
        final String optionFour = optionFourET.getText().toString();

        final String questionID = UUID.randomUUID().toString();
        final String optionOneID = UUID.randomUUID().toString();
        final String optionTwoID = UUID.randomUUID().toString();
        final String optionThreeID = UUID.randomUUID().toString();
        final String optionFourID = UUID.randomUUID().toString();

        StorageReference storageReference = storage.getReference().child("Q"+questionID);

        byte[] dataQuestionImage = getImageOutputs(questionImage);
        final byte[] dataAnswerImage = getImageOutputs(descriptiveAnswerImage);



        UploadTask uploadTask = storageReference.putBytes(dataQuestionImage);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                pDialog.hide();
                new AlertDialog.Builder(homeActivity.this)
                        .setTitle("An upload error occured")
                        .setMessage("Please make sure you are connected to the internet")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                StorageReference storageReference = storage.getReference().child("A"+questionID);
                UploadTask uploadTask = storageReference.putBytes(dataAnswerImage);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        pDialog.hide();
                        new AlertDialog.Builder(homeActivity.this)
                                .setTitle("An upload error occured")
                                .setMessage("Please make sure you are connected to the internet")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        FirebaseDatabase database = FirebaseDatabase.getInstance();

                        //Add the question to the database
                        DatabaseReference questionIDReference = database.getReference().child("Questions").child(""+questionID);
                        questionIDReference.child("Question").setValue(question);
                        questionIDReference.child("Options").child(optionOneID).setValue(optionOne);
                        questionIDReference.child("Options").child(optionTwoID).setValue(optionTwo);
                        questionIDReference.child("Options").child(optionThreeID).setValue(optionThree);
                        questionIDReference.child("Options").child(optionFourID).setValue(optionFour);
                        String answerID = "";
                        switch(spinner.getSelectedItemPosition()){
                            case 1:
                                answerID = optionOneID;
                                break;
                            case 2:
                                answerID = optionTwoID;
                                break;
                            case 3:
                                answerID = optionThreeID;
                                break;
                            case 4:
                                answerID = optionFourID;
                                break;
                            default:
                                answerID = "Critical error occured";
                                break;
                        }
                        questionIDReference.child("Answer").setValue(answerID);
                        questionIDReference.child("DescriptiveAnswer").setValue(descriptionET.getText().toString());
                        pDialog.hide();



                        new AlertDialog.Builder(homeActivity.this)
                                .setTitle("Question Added")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();

                    }
                });

            }
        });




    }

    private byte[] getImageOutputs(ImageView imageV){
        if (imageV.getDrawingCache()==null){
            imageV.setDrawingCacheEnabled(true);
            imageV.buildDrawingCache();
            Bitmap bitmap = imageV.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return baos.toByteArray();
        }
        else{
            byte[] empty = new byte[0];
            return empty;
        }
    }

    private void showError(String error){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(error);
        alert.setCancelable(false);
        alert.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.create();
        alert.show();
    }
    
    public void uploadImageForQuestion(View v){
        requestStoragePermission();
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG_FOR_QUESTION);
    }
    public void uploadImageForAnswer(View v){
        Toast.makeText(this, "Upload Image for Answer", Toast.LENGTH_SHORT).show();
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG_FOR_ANSWER);
    }

    private void requestStoragePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){

        }
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);

    }


    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                if (reqCode==RESULT_LOAD_IMG_FOR_ANSWER){
                    descriptiveAnswerImage.setImageBitmap(selectedImage);
                }
                else if (reqCode==RESULT_LOAD_IMG_FOR_QUESTION){
                    questionImage.setImageBitmap(selectedImage);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(homeActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(homeActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}
