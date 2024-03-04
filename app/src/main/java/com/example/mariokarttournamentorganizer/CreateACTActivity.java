package com.example.mariokarttournamentorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.EventListener;

import java.util.HashMap;
import java.util.Map;

public class CreateACTActivity extends AppCompatActivity {
    private Button createButton;
    private EditText date;
    private String dateStr;
    private EditText time;
    private String timeStr;
    private EditText location;
    private String locationStr;
    private long count;
    private String docPath;

    //for firebase
    public static final String ID_FIELD = "ID";
    public static final String ADMIN_FIELD = "adminID";
    public static final String DATE_FIELD = "date";
    public static final String TIME_FIELD = "time";
    public static final String LOCATION_FIELD = "location";
    public static final String PLAYERS_FIELD = "players";
    public static final String TAG = "creatingACT";
    public static final String COMPLETED_FIELD = "completed";
    public static final String RESULT_FIELD = "result";
    public static final String TIMESTAMP_FIELD = "Date/Time";

    private FirebaseFirestore myFireStore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createact);

        //Setup input fields
        date = (EditText) findViewById(R.id.editTextDate);
        time = (EditText) findViewById(R.id.editTextTime);
        location = (EditText) findViewById(R.id.editTextLocation);

        //Set up create button
        createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(v->
        {
            dateStr = date.getText().toString();
            timeStr = time.getText().toString();
            locationStr = location.getText().toString();

//            myFireStore.collection("act_objects")
//                    .orderBy(TIMESTAMP_FIELD, Query.Direction.DESCENDING)
//                    .limit(1)
//                    .get();

            Query query = myFireStore.collection("act_objects");
            AggregateQuery countQuery = query.count();
            countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        // Count fetched successfully
                        AggregateQuerySnapshot snapshot = task.getResult();
                        count = snapshot.getCount();
                        //docPath = "" + count;
                        Log.d(TAG, "Count: " + count);
                    } else {
                        Log.d(TAG, "Count failed: ", task.getException());
                    }
                }
            });

            if(!dateStr.isEmpty() && !timeStr.isEmpty() && !locationStr.isEmpty()) {
                //TODO Setup button to store input activity into database

                //creating a new document in firebase
                Map<String, Object> act1 = new HashMap<>();
                act1.put(ID_FIELD, 1);
                act1.put(ADMIN_FIELD, 3);
                act1.put(TIMESTAMP_FIELD, Timestamp.now());
                act1.put(COMPLETED_FIELD, false); //default
                act1.put(DATE_FIELD, dateStr);
                act1.put(TIME_FIELD, timeStr);
                act1.put(LOCATION_FIELD, locationStr);
                act1.put(PLAYERS_FIELD,null); //this will be default, need to figure out inputting array
                act1.put(RESULT_FIELD, null);

                myFireStore.collection("act_objects").document("act1")
                        .set(act1)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });

                //Maybe also add checks or change input type for time to be valid times (1-12 hrs, 0-59 min)??

                //Go back to home screen after created
                Intent homeScreen = new Intent(this, homeScreenActivity.class);
                startActivity(homeScreen);
            }
            else {
                Toast.makeText(CreateACTActivity.this,
                        "Please fill out all necessary fields.", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    public void getMostRecent() {
//        //getting most recent act, to see which will be created next and to generate the ID
//        myFireStore.collection("act_objects")
//                .orderBy(TIMESTAMP_FIELD, Query.Direction.DESCENDING)
//                .limit(1)
//                .get();
//
//    }

//    public void getCount(){
//        Query query = myFireStore.collection("act_objects");
//        AggregateQuery countQuery = query.count();
//        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    // Count fetched successfully
//                    AggregateQuerySnapshot snapshot = task.getResult();
//                    Log.d(TAG, "Count: " + snapshot.getCount());
//                } else {
//                    Log.d(TAG, "Count failed: ", task.getException());
//                }
//            }
//        });
//    }
}
