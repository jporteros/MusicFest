package es.upsa.mimo.musicfest.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.upsa.mimo.musicfest.Adapters.CardAdapterComment;
import es.upsa.mimo.musicfest.Model.Comment;
import es.upsa.mimo.musicfest.Model.Event;
import es.upsa.mimo.musicfest.R;

public class EventDetailActivity extends AppCompatActivity {

    @BindView(R.id.comment_body)
    EditText comment_body;
    @BindView(R.id.img_collapsing)
    ImageView img;
    @BindView(R.id.eventtittle)
    TextView title;
    @BindView(R.id.eventCity)
    TextView city;
    @BindView(R.id.eventdateEnd)
    TextView dateEnd;
    @BindView(R.id.eventdateStart)
    TextView dateStart;
    @BindView(R.id.eventLatLng)
    TextView latLng;
    @BindView(R.id.eventtype)
    TextView type;
    @BindView(R.id.eventVenue)
    TextView venue;
    @BindView(R.id.btn_comment)
    Button btn_comment;
    @BindView(R.id.fab_map)
    FloatingActionButton fab_map;
    @BindView(R.id.recyclerview_comments)
    RecyclerView mRecyclerView;
    @BindView(R.id.btn_follow)
    Button btn_follow;

    private Event event;
    private FirebaseAuth auth;
    CardAdapterComment mAdapter;
    private SimpleDateFormat dateFormatter,dateFormatter2;
    private ArrayList<Comment> comments= new ArrayList<>();
    private DatabaseReference mDatabase;
    CollapsingToolbarLayout collapsingToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        ButterKnife.bind(this);
        event=getIntent().getParcelableExtra("event");
        Picasso.with(img.getContext()).load(event.getImg()).into(img);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Poner titulo");
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        title.setText("Titulo: "+event.getTitle());
        city.setText("Ciudad: "+event.getCity_name());
        type.setText("Tipo: "+event.getType());
        latLng.setText("Latitud y Longitud: "+event.getLat()+", "+event.getLng());
        venue.setText("Lugar: "+event.getVenue_name());
        dateStart.setText("Fecha de comienzo: "+event.getStartDate());
        dateEnd.setText("Fecha de finalización: "+event.getEndDate());


        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference myRefEvents2 = mDatabase.child("/user-eventsFollow/").child(userId);

        ValueEventListener eventsListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Event object and use the values to update the UI
                int flag=0;


                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Event e= child.getValue(Event.class);
                    if(e.getId().equals(event.getId())){
                        flag=1;
                    }
                }
                if(flag==1){
                    btn_follow.setText("Siguiendo");
                }else{
                    btn_follow.setText("Seguir");
                }


                // ..
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w( "loadEvent:onCancelled", databaseError.toException());
                // ...
            }
        };


        myRefEvents2.addValueEventListener(eventsListener2);

        final DatabaseReference myRefEvents = mDatabase.child("/event-Comments/").child(event.getId());
        ValueEventListener eventsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comments.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Comment comment = child.getValue(Comment.class);
                    comments.add(comment);
                }
                if(comments!=null){

                    mRecyclerView= (RecyclerView) findViewById(R.id.recyclerview_comments);
                    mAdapter = new CardAdapterComment(comments,event.getId());
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL,false));

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myRefEvents.addValueEventListener(eventsListener);


        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String body= comment_body.getText().toString();
                if (TextUtils.isEmpty(body)||body.equals(" ")) {
                    comment_body.setError("Esta vacio");
                    return;
                }
                writeNewEventComment(body);
                comment_body.setText(null);
            }
        });
        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_follow.getText().equals("Siguiendo")){
                    mDatabase.child("/user-eventsFollow/").child(userId).child(event.getId()).removeValue();

                    btn_follow.setText("Seguir");
                }else{
                    btn_follow.setText("Siguiendo");
                    writeNewEventFollow(userId);

                }
            }
        });

        fab_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }
        });

    }

    private void writeNewEventFollow(String userId) {
        Map<String, Object> eventValues = event.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/user-eventsFollow/" + userId + "/" + event.getId(), eventValues);


        //Con update children no sobrescribimos otros nodos secundarios.
        mDatabase.updateChildren(childUpdates);

    }

    private void writeNewEventComment(String body) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        Log.d("aaaa","fuera del if en comment");
        if(user!=null){
            Log.d("aaaa","dentro del if en comment");
            dateFormatter2 = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());

            String dateprueba = dateFormatter2.format(Calendar.getInstance().getTime());
            String key = mDatabase.child("event-Comments").child(event.getId()).push().getKey();
            Comment comment = new Comment(user.getUid(),key, user.getDisplayName(), body,dateprueba);
            //mDatabase.child("event-Comments").child(event.eid).child(key).setValue(comment);

            mDatabase.child("event-Comments").child(event.getId()).child(comment.cid).setValue(comment);


        }




    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
