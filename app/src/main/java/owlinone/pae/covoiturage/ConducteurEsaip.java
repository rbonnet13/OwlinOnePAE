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

public class ConducteurEsaip extends Fragment {
    private ListView lvEsaip;
    private ArrayList<HashMap<String, String>> notifEsaipList;
    NotificationEsaip notifEsaip;

    public static ConducteurEsaip newInstance(NotificationEsaip notifEsaip) {
        ConducteurEsaip fragment = new ConducteurEsaip();
        Bundle bundle = new Bundle();
        bundle.putSerializable("esaip", notifEsaip);
        fragment.setArguments(bundle);

        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,  @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View PageEsaip = inflater.inflate(R.layout.conducteur_esaip, container, false);

        notifEsaip = (NotificationEsaip) getArguments().getSerializable(
                "esaip");

        Log.e("Test", "FragmentEsaip: " + notifEsaip.getNotif().toString());



        notifEsaipList = new ArrayList<>();
        notifEsaipList = notifEsaip.getNotif();


        lvEsaip = (ListView) PageEsaip.findViewById(R.id.listNotificationEsaip);

        ListAdapter adapterEsaip = new SimpleAdapter(getContext(), notifEsaipList,
                R.layout.list_notif, new String[]{"NOM_NOTIF","PRENOM_NOTIF","ADRESSE_NOTIF","TELEPHONE_NOTIF","LOGO","DATE_NOTIF"},
                new int[]{R.id.nom_notif, R.id.prenom_notif,R.id.adresse_notif,R.id.tel_notif,R.id.destination_notif,R.id.date_notif});
        lvEsaip.setAdapter(adapterEsaip);

        return PageEsaip;
    }
}