package ace.cse.finalyrproject.spotter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import helper.Helper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email;
    private EditText password;
    private Button login;
    private Button register;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViews();
        getInstance();
        checkUserStatus();
    }

    private void checkUserStatus()
    {
        if(firebaseAuth.getCurrentUser()!=null ) {
            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void getInstance()
    {
        // Get Instance for firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // Get Instance
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void findViews() {

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);

        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == login) {
            // Handle clicks for login
            onLoginUser();
        } else if (v == register) {
            // Handle clicks for register
            registerActivity();
        }
    }

    private void registerActivity()
    {
        Intent nextIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(nextIntent);
    }


    private void onLoginUser() {
        if(getUserEmail().equals("") || getUserPassword().equals("")){
            showFieldsAreRequired();
        }else {
            logIn(getUserEmail(), getUserPassword());
        }
    }

    private void logIn(String email, String password) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Log in..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressDialog.dismiss();

                if(task.isSuccessful()){
                    setUserOnline();
                    goToMainActivity();
                }else {
                    showAlertDialog(task.getException().getMessage(),true);
                }
            }
        });
    }

    private void setUserOnline() {
        if(firebaseAuth.getCurrentUser()!=null ) {
            String userId = firebaseAuth.getCurrentUser().getUid();
            FirebaseDatabase.getInstance()
                    .getReference().
                    child("users").
                    child(userId).
                    child("connection").
                    setValue("online");
        }
    }

    private void showFieldsAreRequired() {
        showAlertDialog(getString(R.string.error_incorrect_email_pass),true);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private String getUserEmail() {
        return email.getText().toString().trim();
    }

    private String getUserPassword() {
        return password.getText().toString().trim();
    }

    private void showAlertDialog(String message, boolean isCancelable){
        alertDialog = Helper.buildAlertDialog(getString(R.string.login_error_title), message,isCancelable,LoginActivity.this);
        alertDialog.show();
    }

    private void dismissAlertDialog() {
        alertDialog.dismiss();
    }
}

