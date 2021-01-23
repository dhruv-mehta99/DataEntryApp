package in.astudentzone.dhruvmehta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DataEntryActivity extends AppCompatActivity {

    Button submit;
    TextView applicationSubmitted,pendingApproval;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_entry);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("UserTable");

        applicationSubmitted = findViewById(R.id.edt_application_submitted);
        pendingApproval = findViewById(R.id.edt_pending_approval);

        submit = findViewById(R.id.btn_submit_new_data_entry);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DataEntryActivity.this,NewApplication.class);
                startActivity(i);
            }
        });
        databaseReference.child(MainActivity.phone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("TAG", String.valueOf(snapshot.getChildrenCount()));
                applicationSubmitted.setText(String.valueOf(snapshot.getChildrenCount()));
                int count = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if(snapshot1.child("status").getValue().toString().equals("pending")){
                        count++;
                    }
                }
                pendingApproval.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}