package com.example.routinemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.icu.text.Normalizer2;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;
import java.util.zip.DataFormatException;

public class Home extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerview;
    private FloatingActionButton floatingActionButton;

    private DatabaseReference reference;
    private FirebaseAuth mAuth;

    private FirebaseUser muser;
    private String onlineUserId;
    private String key = "";
    private String task;
    private String description;
    private ProgressDialog loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        recyclerview = findViewById(R.id.recyclerview);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager.setReverseLayout(true);
        LinearLayoutManager.setStackFromEnd(true);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(LinearLayoutManager);


        mAuth = FirebaseAuth.getInstance();
        muser = mAuth.getCurrentUser();
        onlineUserId = muser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("task").child(onlineUserId);


        loader = new ProgressDialog(this);
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });
    }

    private void addTask() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View myview = inflater.inflate(R.layout.input_file, null);
        myDialog.setView(myview);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final EditText task = myview.findViewById(R.id.task);
        final EditText description = myview.findViewById(R.id.description);
        Button save = myview.findViewById(R.id.save);
        Button cancel=myview.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mtask = task.getText().toString().trim();
                String mdescription = description.getText().toString().trim();

                String id = reference.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());

                if (TextUtils.isEmpty(mtask)) {
                    task.setError("Task Required");
                    return;

                }
                if (TextUtils.isEmpty(mdescription)) {
                    description.setError("Description Required");
                    return;
                } else {


                    Model model = new Model(mtask, mdescription, id, date);

                    reference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Home.this, "Task added", Toast.LENGTH_SHORT).show();
                            } else {
                                String error = task.getException().toString();
                                Toast.makeText(Home.this, "Failed" + error, Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
                dialog.dismiss();


            }
        });
        dialog.show();
    }
    @Override
    protected void onStart(){
        super.onStart();
        FirebaseRecyclerOptions<Model> options=new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(reference,Model.class)
                .build();

        FirebaseRecyclerAdapter<Model,MyViewHolder> adapter=new FirebaseRecyclerAdapter<Model, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Model model) {
                holder.setTask(model.getTask());
                holder.setDescription(model.getDescription());
                holder.setData(model.getDate());

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.retrived_layout,parent,false);
                return new MyViewHolder(view);
            }
        };

        recyclerview.setAdapter(adapter);
        adapter.startListening();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTask(String task) {
            TextView taskTectView = mView.findViewById(R.id.taskTv);
            taskTectView.setText(task);
        }
        public void setDescription(String desc) {
            TextView descTextview = mView.findViewById(R.id.descriptionTv);
            descTextview.setText(desc);

        }
        public void setData(String date)
        {
            TextView Dateview=mView.findViewById(R.id.dataTv);
            Dateview.setText(date);
        }




    }
}
