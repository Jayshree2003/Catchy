package com.marsad.catchy.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.marsad.catchy.R;
import com.marsad.catchy.adapter.ChatUserAdapter;
import com.marsad.catchy.adapter.ViewPagerAdapter;
import com.marsad.catchy.chat.ChatActivity;
import com.marsad.catchy.chat.ChatUsersActivity;
import com.marsad.catchy.model.ChatUserModel;

import java.util.ArrayList;
import java.util.List;

public class chat extends Fragment {

    ChatUserAdapter adapter;
    List<ChatUserModel> list;
    FirebaseUser user;

    public chat() {
        // Required empty public constructor
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat);

        init();

        fetchUserData();

        clickListener();
    }

    private void setContentView(int fragmentChat) {

    }

    void init() {
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
        list = new ArrayList<>();

        adapter = new ChatUserAdapter((ChatUsersActivity) requireActivity(), list);


        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        user = FirebaseAuth.getInstance().getCurrentUser();
    }



    void fetchUserData() {

        CollectionReference reference = FirebaseFirestore.getInstance().collection("Messages");
        reference.whereArrayContains("uid", user.getUid())
                .addSnapshotListener((value, error) -> {

                    if (error != null)
                        return;

                    if (value == null)
                        return;



                    if (value.isEmpty())
                        return;


                    list.clear();
                    for (QueryDocumentSnapshot snapshot : value) {

                        if (snapshot.exists()) {
                            ChatUserModel model = snapshot.toObject(ChatUserModel.class);
                            list.add(model);
                        }

                    }

                    adapter.notifyDataSetChanged();

                });


    }

    void clickListener() {

        adapter.OnStartChat((position, uids, chatID) -> {

            String oppositeUID;
            if (!uids.get(0).equalsIgnoreCase(user.getUid())) {
                oppositeUID = uids.get(0);
            } else {
                oppositeUID = uids.get(1);
            }

            Intent intent = new Intent(getActivity(),ChatActivity.class);
            intent.putExtra("uid", oppositeUID);
            intent.putExtra("id", chatID);
            startActivity(intent);


        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
}