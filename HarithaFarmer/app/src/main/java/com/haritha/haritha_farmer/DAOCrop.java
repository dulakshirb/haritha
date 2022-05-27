package com.haritha.haritha_farmer;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOCrop {
    private DatabaseReference databaseRef;

    public DAOCrop(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseRef = db.getReference("Farmer").child(Crop.class.getSimpleName());
    }

    public Task<Void> insert(Crop crop){
        //if(crop==null) //throw exception
       return databaseRef.push().setValue(crop);
    }
}
