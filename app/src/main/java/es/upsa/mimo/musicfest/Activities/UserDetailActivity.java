package es.upsa.mimo.musicfest.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.squareup.picasso.Picasso;

import es.upsa.mimo.musicfest.Model.Event;
import es.upsa.mimo.musicfest.R;

public class UserDetailActivity extends AppCompatActivity {

    private Event event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

    }
}
