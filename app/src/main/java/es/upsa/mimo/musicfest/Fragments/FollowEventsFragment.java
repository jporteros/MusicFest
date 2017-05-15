package es.upsa.mimo.musicfest.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.upsa.mimo.musicfest.Activities.EventDetailActivity;
import es.upsa.mimo.musicfest.Adapters.CardAdapterEvent;
import es.upsa.mimo.musicfest.Model.Event;
import es.upsa.mimo.musicfest.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowEventsFragment extends Fragment {
    private DatabaseReference mDatabase;
    //@BindView(R.id.recycler)
    private RecyclerView mRecyclerView;
    CardAdapterEvent mAdapter;
    private final static String TAG = "FollwoEventsFragment";

    private ArrayList<Event> events= new ArrayList<>();
    public FollowEventsFragment() {
        // Required empty public constructor
    }

    public static FollowEventsFragment newInstance() {


        FollowEventsFragment fragment = new FollowEventsFragment();

        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       final View v= inflater.inflate(R.layout.fragment_follow_events, container, false);
        //ButterKnife.bind(getActivity(),v);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference myRefEvents = mDatabase.child("user-eventsFollow").child(userId);

        ValueEventListener eventsListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       events.clear();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {

                            Event e= child.getValue(Event.class);
                            // e.setImg(R.drawable.sp);
                            events.add(e);
                        }
                        if(events!=null){
                            mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler);

                            Log.d("aaa","recycler"+mRecyclerView);
                            mAdapter = new CardAdapterEvent(events);
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent= new Intent(getContext(), EventDetailActivity.class);
                                    intent.putExtra("event",events.get(mRecyclerView.getChildAdapterPosition(view)));
                                    startActivity(intent);
                                }
                            });
                            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
        };


        myRefEvents.addValueEventListener(eventsListener);

        return v;
    }

}
