package owlinone.pae.covoiturage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import owlinone.pae.main.MainActivity;
import owlinone.pae.R;

public class Covoiturage extends AppCompatActivity {

   @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.covoiturage);
       // Toolbar
       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar8);
       setSupportActionBar(toolbar);
       if (getSupportActionBar() != null) {
           getSupportActionBar().setDisplayHomeAsUpEnabled(true);
           getSupportActionBar().setDisplayShowHomeEnabled(true);
       }
       toolbar.setNavigationOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(Covoiturage.this, MainActivity.class);
               startActivity(intent);             }
       });

       Button mDriver = (Button) findViewById(R.id.conducteur);
       Button mCustomer = (Button) findViewById(R.id.client);


       mDriver.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(Covoiturage.this, Conducteur.class);
               startActivity(intent);
               finish();
               return;
           }
       });

       mCustomer.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(Covoiturage.this, Client.class);
               startActivity(intent);
               finish();
               return;
           }
       });
   }
    /*
    @SuppressWarnings("deprecation")
    private void initActionBar() {
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.addTab(actionBar.newTab()
                    .setText("ListView")
                    .setTabListener(new ActionBar.TabListener() {
                        @Override
                        public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
                            ft.replace(android.R.id.content, new ListViewFragment());

                        }

                        @Override
                        public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

                        }

                        @Override
                        public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

                        }
                    }));
            actionBar.addTab(actionBar.newTab()
                    .setText("RecyclerView")
                    .setTabListener(new ActionBar.TabListener() {
                        @Override
                        public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
                            ft.replace(android.R.id.content, new RecyclerViewFragment());

                        }

                        @Override
                        public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

                        }

                        @Override
                        public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

                        }
                    }));
            actionBar.addTab(actionBar.newTab()
                    .setText("ScrollView")
                    .setTabListener(new ActionBar.TabListener() {
                        @Override
                        public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
                            ft.replace(android.R.id.content, new ScrollViewFragment());

                        }

                        @Override
                        public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

                        }

                        @Override
                        public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

                        }
                    }));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            TextView content = (TextView) getLayoutInflater().inflate(R.layout.about_view, null);
            content.setMovementMethod(LinkMovementMethod.getInstance());
            content.setText(Html.fromHtml(getString(R.string.about_body)));
            new AlertDialog.Builder(this)
                    .setTitle(R.string.about)
                    .setView(content)
                    .setInverseBackgroundForced(true)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class ListViewFragment extends Fragment {

        @SuppressLint("InflateParams")
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_listview, container, false);

            return root;
        }
    }

    public static class RecyclerViewFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_recyclerview, container, false);

            return root;
        }
    }

    public static class ScrollViewFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_scrollview, container, false);


            return root;
        }
    }*/
}

