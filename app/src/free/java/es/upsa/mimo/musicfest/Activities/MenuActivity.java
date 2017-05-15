package es.upsa.mimo.musicfest.Activities;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import es.upsa.mimo.musicfest.Fragments.AroundEventsFragment;
import es.upsa.mimo.musicfest.Fragments.MyEventsFragment;
import es.upsa.mimo.musicfest.Fragments.SearchEventsFragment;
import es.upsa.mimo.musicfest.R;
import es.upsa.mimo.musicfest.services.SoonEventsNotifications;

public class MenuActivity extends AppCompatActivity {
    private static final String TAG = "MenuActivity";
    private static final Integer MYRESULT_CODE =1;
    private static final String ALARM_CANCEL = "es.upsa.mimo.musicfest.services.ALARM_CANCEL";
    private static final String ALARM_START = "es.upsa.mimo.musicfest.services.ALARM_START";
    private static final String APP_OPENED = "es.upsa.mimo.musicfest.services.APP_OPENED";
    private  FirebaseUser user;
    private FirebaseAuth mAuth;
    private int itemSelected=0;
    @BindView(R.id.drawer)
    DrawerLayout mDrawer;

    @BindView(R.id.nav_view)
    NavigationView nvDrawer;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private CircleImageView circleImageView;
    private TextView name, email;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();



        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            Log.d("aaa","toolabar not null");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(new DrawerArrowDrawable(toolbar.getContext()));
        }

        View headerview = nvDrawer.getHeaderView(0);
        circleImageView = (CircleImageView) headerview.findViewById(R.id.circle_image);
        name = (TextView) headerview.findViewById((R.id.username));
        email = (TextView) headerview.findViewById((R.id.email));

        user = FirebaseAuth.getInstance().getCurrentUser();

        name.setText(user.getDisplayName());
        email.setText(user.getEmail());
        mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        String img = dataSnapshot.child("img").getValue().toString();
                        Picasso.with(circleImageView.getContext()).load(img).into(circleImageView);
                        Log.d(TAG,"imagen url"+img);
                        // ...
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // ...
                    }
                });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserDetailActivity.class);
                startActivityForResult(intent, MYRESULT_CODE);
            }
        });

        setupNavDrawerContent(nvDrawer);
        int drawerSelection=0;
        if (savedInstanceState != null) //when the user rotates the screen
            drawerSelection = savedInstanceState.getInt("drawerSelection",0);
        //Select the first item
        selectDrawerItem(nvDrawer.getMenu().getItem(drawerSelection));

      /*  nvDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });*/
      if(savedInstanceState==null){
          Intent intent = new Intent(APP_OPENED);
          sendBroadcast(intent);
      }

    }

    private void setupNavDrawerContent(NavigationView nvDrawer) {
        nvDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });

        //Mirar a poner aqui los datos del nv
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isChecked = settings.getBoolean("notications", true);
        MenuItem item = menu.findItem(R.id.checkbox_notifications);
        item.setChecked(isChecked);
        return true;
    }


    private void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment =null;

        switch (menuItem.getItemId()){
            case R.id.my_events:
                fragment= MyEventsFragment.newInstance();
                itemSelected=0;
                break;
            case R.id.search_events:
                fragment = SearchEventsFragment.newInstance();
                itemSelected=1;
                break;
            case R.id.menu_cerrar_sesion:

                new AlertDialog.Builder(this)
                        .setTitle("Cerrando Sesion")
                        .setMessage("Esta seguro de que quieres cerrar sesion?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               /* Log.i("NavigationView", "Cerrar Sesion");
                                if(isServiceRunning()){
                                    stopService(new Intent(getApplicationContext(),Notifications.class));
                                }else{

                                }*/
                                mAuth.signOut();
                                Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                break;
        }
        if (fragment != null) {
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction().replace(R.id.content_layout, fragment).commit();
                // Highlight the selected item, update the title, and close the drawer

            menuItem.setChecked(true);
            setTitle(menuItem.getTitle());
        }

        mDrawer.closeDrawers();

    }
    private void signOut() {
        // Firebase sign out
        mAuth.signOut();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.checkbox_notifications:
                item.setChecked(!item.isChecked());
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("notications", item.isChecked());
                editor.apply();
                Intent intent = new Intent(item.isChecked()? ALARM_START : ALARM_CANCEL);
                Log.d("BROADCAST","BROADCAST");
                sendBroadcast(intent);
                return true;
            case R.id.user_profile:

                Intent intent1 = new Intent(this, UserDetailActivity.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"RESULT-- requestCOde: "+requestCode+" resultCOde: "+resultCode+ "user: "+user);
        if(requestCode== MYRESULT_CODE && resultCode==RESULT_OK && user!=null){
            Log.d(TAG,"DENTRO REQUEST");
            mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            String img = dataSnapshot.child("img").getValue().toString();
                            Picasso.with(circleImageView.getContext()).load(img).into(circleImageView);
                            Log.d(TAG,"imagen url"+img);
                            // ...
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            // ...
                        }
                    });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("drawerSelection", itemSelected);
    }
}
