package es.upsa.mimo.musicfest.Activities;

import android.content.res.Configuration;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.upsa.mimo.musicfest.Fragments.EventsFragment;
import es.upsa.mimo.musicfest.R;

public class MenuActivity extends AppCompatActivity {
    private static final String TAG = "MenuActivity";
    @BindView(R.id.drawer)
    DrawerLayout mDrawer;

    @BindView(R.id.nav_view)
    NavigationView nvDrawer;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(new DrawerArrowDrawable(toolbar.getContext()));
        }

        setupNavDrawerContent(nvDrawer);
      /*  nvDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });*/

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

    private void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment =null;

        switch (menuItem.getItemId()){
            case R.id.menu_seccion_1:
                Toast.makeText(getApplicationContext(),"secccion1",Toast.LENGTH_SHORT).show();
                fragment= EventsFragment.newInstance();
                break;
            case R.id.amigos:
                Toast.makeText(getApplicationContext(),"secccion2",Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_seccion_3:
                Toast.makeText(getApplicationContext(),"secccion3",Toast.LENGTH_SHORT).show();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
