package bw.ub.cs.smarthealth;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment implements View.OnClickListener {


    private View view;
    private FirebaseAuth mAuth;
    private ArrayList<MessageModel> messages;
    private MessageAdapter messageAdapter;

    TextInputEditText inputEditText;

    private String message;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    public ChatFragment() {
        // Required empty public constructor
        messages = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);

        initMessages(view);

        return view;
    }

    void initMessages(View view){
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messageAdapter = new MessageAdapter(getContext(), messages);
        recyclerView.setAdapter(messageAdapter);
        Glide.with(getContext())
                .load(R.drawable.gbot_bg)
                .into((ImageView) view.findViewById(R.id.bg));

        inputEditText = (TextInputEditText) view.findViewById(R.id.messageTextInputEditText);
        view.findViewById(R.id.sendFloatingActionButton).setOnClickListener(this);

        loadData();
    }

    void loadData(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = firebaseDatabase.getReference().child(Constants.CHATS);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                messages.add(0, messageModel);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void saveData(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = firebaseDatabase.getReference().child(Constants.CHATS);
        DatabaseReference databaseReference = mRef.push();
        String key = databaseReference.getKey();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String guid = FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(0).getEmail().split("@")[0];
//        String message =  getEditTextString();
        String profileImg = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
        MessageModel messageModel = new MessageModel(new Date().toString(), username, uid, key, message, guid, profileImg);
        databaseReference.setValue(messageModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    recyclerView.scrollToPosition(0);
                    closeKeyboard();
                } else {
                    inputEditText.setText(message);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sendFloatingActionButton:
                if (!getEditTextString().trim().isEmpty()) {
                    saveData();
                } else {
                    Toast.makeText(getContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    String getEditTextString(){
        message = inputEditText.getText().toString();
        inputEditText.setText("");
        view.findViewById(R.id.root_view).requestFocus();
        return message;
    }
}
