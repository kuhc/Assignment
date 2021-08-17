package com.utar.assignment.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.utar.assignment.Activity.AddFriend;
import com.utar.assignment.Activity.MainActivity;
import com.utar.assignment.Model.Amount;
import com.utar.assignment.Model.User;
import com.utar.assignment.R;
import com.utar.assignment.Util.FirebaseCallback;
import com.utar.assignment.Util.FirestoreHelper;
import com.utar.assignment.Util.GeneralHelper;
import com.utar.assignment.Util.friend_adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class FriendFragment extends Fragment {

    private static FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseAuth Auth;
    FirebaseFirestore db;
    List<String> amountListOwner = new ArrayList<>();
    List<String> listOwnerUsername = new ArrayList<>();
    List<Amount> amountList = new ArrayList<>();
    String uid;

    private User userInfo;
    TextView username;
    TextView friendNo;
    List<String> userList = new ArrayList<>();
    List<String> friendID = new ArrayList<>();
    View view;
    ListView listView;
    Iterator<Amount> ownerIterator;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend, container, false);

        username = view.findViewById(R.id.friend_frag_uname);
        friendNo = view.findViewById(R.id.friend_no);


        FirebaseUser user;
        user = Auth.getInstance().getCurrentUser();
        uid = user.getUid();

        //Get the username from firestore
        fStore.collection("Users").document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user1 = task.getResult().toObject(User.class);
                        String um = user1.getUsername();

                        username.setText(um);
                    }
                });

        Button button = view.findViewById(R.id.add_friend);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddFriend.class);
                startActivity(intent);
            }
        });

        db = FirebaseFirestore.getInstance();

        //get friend list from firestore
        DocumentReference documentReference = db.collection("Users").document(uid);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userInfo = documentSnapshot.toObject(User.class);

                if (userInfo.getFriendList() == null) {
                    GeneralHelper.showMessage(getContext(), "There is no existing friend!");
                } else if (userInfo.getFriendList() != null) {
                    if (userList == null) {
                        userList.add("-1");
                    }
                    userList.addAll(userInfo.getFriendList());
                    int fNo = userList.size();
                    String fNo1 = Integer.toString(fNo);
                    friendNo.setText(fNo1);

                    amountList = userInfo.getAmountList();
                    ownerIterator = amountList.iterator();

                    //interate to get the friends' username
                    getOwnerName(ownerIterator.next());
                }
            }

        });

        return view;
    }

    private void getOwnerName(Amount amt) {
        FirestoreHelper.getUser(amt.getOwnerId(), new FirebaseCallback() {
            @Override
            public void onResponse(Object object) {
                User user2 = (User) object;
                listOwnerUsername.add(user2.getUsername());

                if (!ownerIterator.hasNext()) {
                    initializeRecycleView(null, amountList, userList, uid, amountListOwner, listOwnerUsername, getActivity());
                    //GeneralHelper.showMessage(getContext(),listOwnerUsername.get(finalI));
                } else {
                    getOwnerName(ownerIterator.next());
                }

            }
        });
    }

    public void initializeRecycleView(List<User> user, List<Amount> amount, List<String> userFriendList, String uid, List<String> amountListOwner, List<String> listOwnerUsername, Context context) {
        RecyclerView recyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView = view.findViewById(R.id.friendList);
        recyclerView.setLayoutManager(layoutManager);

        friend_adapter adapter;
        adapter = new friend_adapter(user, amount, userFriendList, uid, amountListOwner, listOwnerUsername, context);
        recyclerView.setAdapter(adapter);
    }
}