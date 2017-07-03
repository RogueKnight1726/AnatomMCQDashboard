package eiv.rogueknight1726.com.anatomy;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class loginActivity extends AppCompatActivity {


    private FirebaseAuth auth;
    EditText usernameTV,passwordTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser()!=null){
            Intent intent = new Intent(loginActivity.this,homeActivity.class);
            startActivity(intent);
        }
        initiateTVs();
    }

    private void initiateTVs(){
        usernameTV = (EditText)findViewById(R.id.usernameTV);
        passwordTV = (EditText)findViewById(R.id.passwordTV);

    }

    public void validate(View v){
        if(usernameTV.getText().toString().equals("")||passwordTV.getText().toString().equals("")){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setCancelable(false);
            alert.setTitle("Please enter username and password");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alert.create();
            alert.show();
            return;
        }
        String username = usernameTV.getText().toString();
        String password = passwordTV.getText().toString();

        signIn(username,password);
    }
    private void signIn(String username,String password){
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.show();
        auth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                pDialog.hide();
                Log.e("Success", "" + task.isSuccessful());
                if (task.isSuccessful()) {
                    Log.e("Email", auth.getCurrentUser().getEmail());
                    Intent intent = new Intent(loginActivity.this, homeActivity.class);
                    startActivity(intent);
                }
                else
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(loginActivity.this);
                    alert.setCancelable(false);
                    alert.setTitle("Incorrect Username/Password");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alert.create();
                    alert.show();
                    return;
                }

            }
        });
    }
}
