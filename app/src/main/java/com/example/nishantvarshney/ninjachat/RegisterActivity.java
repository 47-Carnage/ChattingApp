package com.example.nishantvarshney.ninjachat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;

    private Button mCreateBtn;

    private Toolbar mToolbar;

    private DatabaseReference mDatabase;

    private ProgressDialog mRegProgressBar;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mDisplayName = (TextInputLayout) findViewById(R.id.reg_displayName);
        mEmail = (TextInputLayout) findViewById(R.id.reg_emailName);
        mPassword = (TextInputLayout) findViewById(R.id.reg_password);
        mCreateBtn = (Button) findViewById(R.id.reg_createAc);

        mRegProgressBar = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.reg_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String displayName = mDisplayName.getEditText().getText().toString();
                String emailName = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(displayName) || TextUtils.isEmpty(emailName) || TextUtils.isEmpty(password))
                {
                    mRegProgressBar.setTitle("Registering User");
                    mRegProgressBar.setMessage("Please wait while we create your account!");
                    mRegProgressBar.setCanceledOnTouchOutside(false);
                    mRegProgressBar.show();

                    register_user(displayName,emailName,password);
                }

            }
        });
    }

    private void register_user(final String displayName, String emailName, String password)
    {
        mAuth.createUserWithEmailAndPassword(emailName,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    String device_token = FirebaseInstanceId.getInstance().getToken();

                    HashMap<String,String> userMap = new HashMap<>();
                    userMap.put("name",displayName);
                    userMap.put("status","Hi there from ninja");
                    userMap.put("image","default");
                    userMap.put("thumb_image","default");
                    userMap.put("device_token",device_token);

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {

                                mRegProgressBar.dismiss();

                                Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();
                            }
                        }
                    });

                }

                else
                {
                    mRegProgressBar.hide();

                    Toast.makeText(RegisterActivity.this,"Cannot sign in. Please revert back and check again!",Toast.LENGTH_LONG).show();
                }
            }

        });
    }
}
