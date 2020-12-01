package com.example.routinemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegestrationActivity extends AppCompatActivity {
    private EditText RegEmail,RegPwd;
    private Toolbar toolbar;
    private Button RegBtn;
    private TextView RegQn;
    private FirebaseAuth mAuth;

    private ProgressDialog loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_regestration);

        mAuth=FirebaseAuth.getInstance();

        RegEmail=findViewById(R.id.Regestrationemail);
        RegPwd=findViewById(R.id.Regestrationpassword);
        RegBtn=findViewById(R.id.Regestrationbutton);


        RegQn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegestrationActivity.this, Login.class);
                startActivity(intent);
            }
        });

        RegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=RegEmail.getText().toString().trim();
                String password=RegPwd.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    RegEmail.setError("EMAIL IS REQUIRED");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    RegPwd.setError("PASSWORD IS REQUIRED");
                    return;
                }





                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(RegestrationActivity.this, Home.class);
                                startActivity(intent);
                                finish();
                                loader.dismiss();
                            }
                            else{
                                String error=task.getException().toString();
                                Toast.makeText(RegestrationActivity.this, "REGISTRATION FAILED" + error,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


            }
        });
    }
}
