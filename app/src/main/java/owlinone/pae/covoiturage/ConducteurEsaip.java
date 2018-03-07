package owlinone.pae.covoiturage;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import owlinone.pae.R;
import owlinone.pae.configuration.AddressUrl;
import owlinone.pae.configuration.HttpHandler;

/**
 * Created by haade on 13/09/2017.
 */

public class ConducteurEsaip extends Fragment {
    private ListView lvEsaip;
    String strPrenom = "",strLogo = "", strClicked = "", strNameUser = "", strNom = "", strAdresse = "", strDate = "", strTel = "", strDestination = "" ,strPseudo="",strPrenomUser="",strNomUser="", strIdNotif ="";
    HashMap<String, String> obj = new HashMap();
    private ArrayList<HashMap<String, String>> notifEsaipList;
    NotificationEsaip notifEsaip;
    private ProgressDialog dialogChargement;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,  @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View PageEsaip = inflater.inflate(R.layout.conducteur_esaip, container, false);

        //On récupère toutes les notifications pour aller vers l'esaip
        notifEsaip = (NotificationEsaip) getArguments().getSerializable(
                "esaip");

        Log.e("Test", "FragmentEsaip: " + notifEsaip.getNotif().toString());


        notifEsaipList = new ArrayList<>();
        notifEsaipList = notifEsaip.getNotif();

        Log.e("Test", "notifEsaipList: " + notifEsaipList.toString());


        lvEsaip = (ListView) PageEsaip.findViewById(R.id.listNotificationEsaip);

        lvEsaip.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                obj = (HashMap) lvEsaip.getItemAtPosition(position);
                strIdNotif = obj.get("ID_NOTIF");
                strPseudo = obj.get("PSEUDO_CONDUCTEUR_NOTIF");
                strNom = obj.get("NOM_NOTIF");
                strPrenom = obj.get("PRENOM_NOTIF");
                strAdresse = obj.get("ADRESSE_NOTIF");
                strTel = obj.get("TELEPHONE_NOTIF");
                strDate = obj.get("DATE_NOTIF");
                strDestination = obj.get("DESTINATION_NOTIF");
                strLogo = obj.get("LOGO");
                strNameUser = obj.get("USER_NAME");
                strPrenomUser = obj.get("USER_PRENOM");
                strClicked = obj.get("CLICKED_NOTIF");

                //Si l'item n'a jamais été clické
                if("FALSE".equals(strClicked)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.titreCovoiturage);
                    builder.setIcon(R.drawable.owl_in_one_logo);
                    builder.setMessage(R.string.accepterCovoiturage);

                    builder.setNegativeButton(R.string.oui, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getContext(), R.string.toastNotifAccepterCovoiturage, Toast.LENGTH_LONG).show();
                            new sendGCMRetour().execute();
                            dialog.dismiss();
                            dialogChargement = ProgressDialog.show(getContext(), "",
                                    "Chargement...", true);
                            timerDelayRemoveDialog(8000, dialogChargement);

                        }
                    });

                    builder.setPositiveButton(R.string.non, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                            Toast.makeText(getContext(), R.string.toastNotifRefuserCovoiturage, Toast.LENGTH_LONG).show();
                            new sendGCMRefus().execute();
                            dialog.dismiss();
                            dialogChargement = ProgressDialog.show(getContext(), "",
                                    "Chargement...", true);
                            timerDelayRemoveDialog(8000, dialogChargement);
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    Toast.makeText(getContext(), R.string.toastNotifSpamCovoiturage, Toast.LENGTH_LONG).show();
                }
            }
        });

        // Appui long sur un item
        lvEsaip.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                obj = (HashMap) lvEsaip.getItemAtPosition(pos);
                strIdNotif = obj.get("ID_NOTIF");
                strPseudo = obj.get("PSEUDO_CONDUCTEUR_NOTIF");
                strNom = obj.get("NOM_NOTIF");
                strPrenom = obj.get("PRENOM_NOTIF");
                strAdresse = obj.get("ADRESSE_NOTIF");
                strTel = obj.get("TELEPHONE_NOTIF");
                strDate = obj.get("DATE_NOTIF");
                strDestination = obj.get("DESTINATION_NOTIF");
                strLogo = obj.get("LOGO");
                strNameUser = obj.get("USER_NAME");
                strPrenomUser = obj.get("USER_PRENOM");
                strClicked = obj.get("CLICKED_NOTIF");

                // on ouvre la fenêtre pour envoyer un sms
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:" + strTel));
                startActivity(sendIntent);
                return true;
            }
        });

        ListAdapter adapterEsaip = new SimpleAdapter(getContext(), notifEsaipList,
                R.layout.list_notif, new String[]{"NOM_NOTIF","PRENOM_NOTIF","ADRESSE_NOTIF","TELEPHONE_NOTIF","LOGO","DATE_NOTIF"},
                new int[]{R.id.nom_notif, R.id.prenom_notif,R.id.adresse_notif,R.id.tel_notif,R.id.destination_notif,R.id.date_notif});
        lvEsaip.setAdapter(adapterEsaip);

        return PageEsaip;
    }
    private class sendGCMRetour extends AsyncTask<Void, Void, Void> {
        Exception exception;
        protected Void doInBackground(Void... arg0) {
            try {
                HttpHandler sh = new HttpHandler();
                HashMap<String, String> parametersConducteur = new HashMap<>();
                String urlNotification = AddressUrl.strNotifAccepte;
                parametersConducteur.put("prenom", strPrenomUser);
                parametersConducteur.put("nom", strNameUser);
                parametersConducteur.put("PRENOM_NOTIF",strPrenom);
                parametersConducteur.put("NOM_NOTIF",strNom);
                parametersConducteur.put("DESTINATION_NOTIF",strDestination);
                Log.e("parametersConducteur", "parametersConducteur: " + parametersConducteur.toString());

                sh.performPostCall(urlNotification, parametersConducteur);
                return null;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            new sendClickedRetour().execute();

        }
    }
    private class sendGCMRefus extends AsyncTask<Void, Void, Void> {
        Exception exception;
        protected Void doInBackground(Void... arg0) {
            try {
                HttpHandler sh = new HttpHandler();
                HashMap<String, String> parametersConducteur = new HashMap<>();

                String urlNotification = AddressUrl.strNotifRefuse;
                parametersConducteur.put("ID_NOTIF", strIdNotif);
                parametersConducteur.put("prenom", strPrenomUser);
                parametersConducteur.put("nom", strNameUser);
                parametersConducteur.put("PRENOM_NOTIF",strPrenom);
                parametersConducteur.put("NOM_NOTIF",strNom);
                parametersConducteur.put("DESTINATION_NOTIF",strDestination);
                Log.e("parametersConducteur", "parametersConducteur: " + parametersConducteur.toString());

                sh.performPostCall(urlNotification, parametersConducteur);
                return null;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            new sendClickedRetour().execute();

        }
    }
    private class sendClickedRetour extends AsyncTask<Void, Void, Void> {
        Exception exception;
        protected Void doInBackground(Void... arg0) {
            try {
                strClicked = "TRUE";
                HttpHandler sh = new HttpHandler();
                HashMap<String, String> parametersConducteur = new HashMap<>();
                String urlNotification = AddressUrl.strNotifClicked;
                parametersConducteur.put("ID_NOTIF",strIdNotif);
                parametersConducteur.put("CLICKED_NOTIF",strClicked);

                Log.e("strClicked", "strClicked: " + strClicked);
                Log.e("strIdNotif", "strIdNotif: " + strIdNotif);


                sh.performPostCall(urlNotification, parametersConducteur);
                return null;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dialogChargement.dismiss();
            Intent intent = new Intent(getContext(), ConducteurTab.class);
            startActivity(intent);
        }
    }
    public void timerDelayRemoveDialog(long time, final ProgressDialog d){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                d.dismiss();
            }
        }, time);
    }
}