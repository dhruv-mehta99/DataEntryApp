package in.astudentzone.dhruvmehta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.sql.ConnectionPoolDataSource;

public class NewApplication extends AppCompatActivity {

    DatabaseReference databaseReference;
    Button validate,submit;
    EditText client_number,propertyName,city,area,ownerName,preferredLanguage;
    private String phoneNumber;
    PopupWindow popupWindow;
    ValueEventListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_application);

        client_number = findViewById(R.id.edt_client_number);
        propertyName = findViewById(R.id.pg_name);
        city = findViewById(R.id.city_name);
        area = findViewById(R.id.area_name);
        ownerName = findViewById(R.id.owner_name);
        preferredLanguage = findViewById(R.id.preferred_language_name);
        validate = findViewById(R.id.btn_validate_number);
        submit = findViewById(R.id.btn_submit_entry);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("UserTable");



        propertyName.setVisibility(View.INVISIBLE);
        city.setVisibility(View.INVISIBLE);
        area.setVisibility(View.INVISIBLE);
        ownerName.setVisibility(View.INVISIBLE);
        preferredLanguage.setVisibility(View.INVISIBLE);
        submit.setVisibility(View.INVISIBLE);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout,null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber=client_number.getText().toString().trim();
                listener = databaseReference.child(MainActivity.phone).child(phoneNumber).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                            popupView.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    popupWindow.dismiss();
                                    return true;
                                }
                            });
                            client_number.setText("");
                        }
                        else {
                            propertyName.setVisibility(View.VISIBLE);
                            city.setVisibility(View.VISIBLE);
                            area.setVisibility(View.VISIBLE);
                            ownerName.setVisibility(View.VISIBLE);
                            preferredLanguage.setVisibility(View.VISIBLE);
                            submit.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseModel databaseModel = new DatabaseModel();
                databaseModel.setPhoneNumber(phoneNumber);
                databaseModel.setPropertyName(propertyName.getText().toString().trim());
                databaseModel.setCity(city.getText().toString().trim());
                databaseModel.setArea(area.getText().toString().trim());
                databaseModel.setStatus("pending");
                if(ownerName.getText().toString().trim().equals("")){
                    databaseModel.setOwnerName("null");
                }
                else {
                    databaseModel.setOwnerName(ownerName.getText().toString().trim());
                }
                if(preferredLanguage.getText().toString().trim().equals("")){
                    databaseModel.setPreferredLanguage("null");
                }
                else {
                    databaseModel.setPreferredLanguage(preferredLanguage.getText().toString().trim());
                }
                databaseReference.child(MainActivity.phone).child(phoneNumber).setValue(databaseModel, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if(error==null){
                            if(databaseModel.getOwnerName().equals("null") || databaseModel.getPreferredLanguage().equals("null")){

                                Toast.makeText(NewApplication.this,"NA",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(NewApplication.this,"Application Pending",Toast.LENGTH_SHORT).show();
                            }
                            propertyName.setVisibility(View.INVISIBLE);
                            city.setVisibility(View.INVISIBLE);
                            area.setVisibility(View.INVISIBLE);
                            ownerName.setVisibility(View.INVISIBLE);
                            preferredLanguage.setVisibility(View.INVISIBLE);
                            submit.setVisibility(View.INVISIBLE);

                            preferredLanguage.setText("");
                            city.setText("");
                            area.setText("");
                            ownerName.setText("");
                            preferredLanguage.setText("");
                        }
                        else{
                            Toast.makeText(NewApplication.this,"Error inserting data",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                client_number.setText("");
                databaseReference.child(MainActivity.phone).child(phoneNumber).removeEventListener(listener);
            }
        });


    }
}