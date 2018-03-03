package owlinone.pae.covoiturage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import owlinone.pae.R;

/**
 * Created by haade on 13/09/2017.
 */

public class ConducteurHome extends Fragment {
    private ListView lvHome;
    private ArrayList<HashMap<String, String>> notifHomeList;
    NotificationHome notifHome;

    public static ConducteurHome newInstance(NotificationHome notifHome) {
        ConducteurHome fragment = new ConducteurHome();
        Bundle bundle = new Bundle();
        bundle.putSerializable("home", notifHome);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View PageHome = inflater.inflate(R.layout.conducteur_home, container, false);
        notifHome = (NotificationHome) getArguments().getSerializable(
                "home");

        Log.e("Test", "FragmentHome: " + notifHome.getNotif().toString());

        notifHomeList = new ArrayList<>();
        notifHomeList = notifHome.getNotif();

        Log.e("Test", "notifHomeList: " + notifHomeList.toString());

        lvHome = (ListView) PageHome.findViewById(R.id.listNotificationHome);
        ListAdapter adapterHome = new SimpleAdapter(getContext(), notifHomeList,
                R.layout.list_notif, new String[]{"NOM_NOTIF","PRENOM_NOTIF","ADRESSE_NOTIF","TELEPHONE_NOTIF","LOGO","DATE_NOTIF"},
                new int[]{R.id.nom_notif, R.id.prenom_notif,R.id.adresse_notif,R.id.tel_notif,R.id.destination_notif,R.id.date_notif});
        lvHome.setAdapter(adapterHome);
        return PageHome;
    }
}