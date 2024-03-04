package com.example.mariokarttournamentorganizer;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class homeScreenActivity extends AppCompatActivity {

    RecyclerView upcomingRecyclerView;
    RecyclerView pastRecyclerView;
    Button createACTButton;
    TextView infoTextView;
    String [] upcomingArray = new String[10];
    public String [] pastArray = new String[10];

//    ArrayList<String> upcomingArray = new ArrayList<>();
//    ArrayList<String> pastArray = new ArrayList<>();

    public static final String ACTNAME_KEY = "name";
    public static final String DATE_KEY = "Date/Time";
    //private CollectionReference colllectionRef = FirebaseFirestore.getInstance().collection("act_objects");
    private CollectionReference collection = FirebaseFirestore.getInstance().collection("act_objects");
    //private CollectionReference collectionRef = dataBase.collection("act_objects");

    //Not sure how to utilize this yet.
    @Override
    protected void onStart() {
        super.onStart();

        collection
                //.whereEqualTo("state", "CA")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "Listen failed.", e);
                            return;
                        }

                        for (QueryDocumentSnapshot doc : value) {
                            //do something
                            Log.i("TAG", "Something updated");
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        createACTButton = findViewById(R.id.createACTButton);
        upcomingRecyclerView = (RecyclerView) findViewById(R.id.upcomingRecyclerView);
        pastRecyclerView = (RecyclerView) findViewById(R.id.pastRecyclerView);
        infoTextView = findViewById(R.id.infoTextView);

        fillACTS();

          createACTButton.setOnClickListener(v->
          {
              Intent CreateACT = new Intent(this, CreateACTActivity.class);
              startActivity(CreateACT);
          });
    }

    public void firebaseDemo(View v) { // Delete when the demo is gone
        Intent startDemo = new Intent(this, FirebaseDemoActivity.class);
        startActivity(startDemo);
    }

    //Will iterate through all documents within the main ACT collection
    //on Firebase, and fill in the recycleViews according to ACT
    //information.
    private void fillACTS(){

        //Nested hashmap that will store the name of the ACT as the key,
        //and another hashmap as the value.
        //Reasoning for this:
        //   - The getData() method returns a map.
        //   - Need a way to differentiate between active/inactive ACTs.
        //     This means we need the value associated with the "completed"
        //     boolean field within the collection that is within the document.
        //     To get this, we need the data within the document, which I am
        //     using getData() for. You could easily just pull the specific
        //     field, but why not pull everything for now
        //     in case we want to display more info on recycleViews.
        //   - We could use the information gained from the
        //     ACT/ACT_Info hashmap to send over data through intents.
        //     This is just spit-balling ideas though.

        Map<String, Map<String,Object>> hashMap = new HashMap<>();

            collection
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d("TAG", document.getId() + " => " + document.getData());

                                //Add each document information to the hashmap.
                                hashMap.put(document.getId().toString(), document.getData());
                            }
                            //This function MUST be after the for loop due to syncing
                            //errors.
                            setUpRecycleViews(hashMap);
                        } else {
                            Log.d("TagError", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void setUpRecycleViews(Map<String, Map<String,Object>> hashMap){

        //Visualization of this nested loop:
        //              ACT == {                                       }
        //         ACT_Info ==            {                            }
        //                      < ACT Name < Field , Value in field > >

        for (Map.Entry<String, Map<String, Object>> ACT : hashMap.entrySet()) {
            //Log.i("TRACKER", "Key: " + ACT.getKey() +". Value: " + ACT.getValue());
            Map<String, Object> innerMap = ACT.getValue();
            for (Map.Entry<String, Object> ACT_Info : innerMap.entrySet()) {
                //Log.i("TRACKER", "Key: " + ACT_Info.getKey() +". Value: " + ACT_Info.getValue());
                if(ACT_Info.getKey().equals("completed") && ACT_Info.getValue() instanceof Boolean){
                    //Log.i("HIT", ACT_Info.getValue().toString());
                    boolean completed = (boolean) ACT_Info.getValue();
                    if(completed){
                        //Log.i("HIT_TRUE", ACT_Info.getValue().toString());
                        for(int i = 0; i < pastArray.length; i++){
                            if(pastArray[i] == null){
                                pastArray[i] = ACT.getKey().toString();
                                break;
                            }
                        }
                    }
                    else {
                        //Log.i("HIT_FALSE", ACT_Info.getValue().toString());
                        for(int i = 0; i < upcomingArray.length; i++){
                            if(upcomingArray[i] == null){
                                upcomingArray[i] = ACT.getKey().toString();
                                break;
                            }
                        }
                    }
                } else if (ACT_Info.getKey() == "completed") {
                    Log.i("Incorrect Declaration: boolean",
                            "'Completed' field not filled out correctly in Firebase.");
                }

                //System.out.println("Inner Key: " + innerKey + ", Inner Value: " + innerValue);
            }
        }
        Intent pastACT = new Intent(this, adminActViewActivity.class);
        Intent currACT = new Intent(this, UserACTActivity.class);

        RecyclerViewAdapter pastCustomAdapter = new RecyclerViewAdapter(pastArray);
        RecyclerViewAdapter upcomingCustomAdapter = new RecyclerViewAdapter(upcomingArray);

        pastRecyclerView.setAdapter(pastCustomAdapter);
        upcomingRecyclerView.setAdapter(upcomingCustomAdapter);

        pastRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        upcomingRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemClickSupport.addTo(pastRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                infoTextView.setText(pastArray[position]);
                startActivity(pastACT);
            }
        });

        //Handle the clicking of certain rows, where position is the item clicked.
        ItemClickSupport.addTo(upcomingRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                infoTextView.setText(upcomingArray[position]);
                startActivity(currACT);
            }
        });
    }
}
