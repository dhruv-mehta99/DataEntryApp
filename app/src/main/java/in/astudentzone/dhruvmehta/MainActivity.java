 package in.astudentzone.dhruvmehta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

 public class MainActivity extends AppCompatActivity {

    Button otp,login;
    String verificationid;
    EditText edt_phone,edt_otp;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    public static String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        login=findViewById(R.id.btn_login);
        otp=findViewById(R.id.btn_getotp);
        edt_otp=findViewById(R.id.edt_otp);
        edt_phone=findViewById(R.id.edt_phone_number);
        login.setEnabled(false);
        edt_otp.setEnabled(false);
        progressBar = findViewById(R.id.progressbar);

        otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", "onCLicked for get otp");
                MainActivity.phone = edt_phone.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE);
                sendVerificationCode(phone);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = edt_otp.getText().toString().trim();

                if ((code.isEmpty() || code.length() < 6)){

                    edt_otp.setError("Enter code...");
                    edt_otp.requestFocus();
                    return;
                }
                verifyCode(code);

            }
        });
    }
    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationid, code);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(MainActivity.this, HomePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void sendVerificationCode (String number){

        PhoneAuthProvider.getInstance(mAuth).verifyPhoneNumber("+91" + number,
                60,
                TimeUnit.SECONDS,
                MainActivity.this,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken)
        {
            super.onCodeSent(s, forceResendingToken);
            verificationid = s;
            progressBar.setVisibility(View.INVISIBLE);
            edt_otp.setEnabled(true);
            login.setEnabled(true);

        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(MainActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
        }
    };
}