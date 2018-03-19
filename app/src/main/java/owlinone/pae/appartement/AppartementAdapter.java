package owlinone.pae.appartement;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import owlinone.pae.R;

/**
 * Created by AnthonyCOPPIN on 19/03/2018.
 */

public class AppartementAdapter extends ArrayAdapter<Appart>  {

        ArrayList<Appart> appart;

        Context context;
        int resource;

    public AppartementAdapter(Context context, int resource, ArrayList<Appart> appart) {
            super(context, resource, appart);
            this.appart = appart;
            this.context = context;
            this.resource= resource;
            }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder = null;

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(resource, null, true);
        viewHolder = new ViewHolder();
        viewHolder.appart = appart.get(position);


        viewHolder.descriptAppart = (TextView) convertView.findViewById(R.id.descrip_appart);
        viewHolder.prixAppart = (TextView) convertView.findViewById(R.id.prix_appart);
        viewHolder.adresseAppart = (TextView) convertView.findViewById(R.id.adresse_apart);
        viewHolder.cpAppart = (TextView) convertView.findViewById(R.id.cp_appart);
        viewHolder.villeAppart = (TextView) convertView.findViewById(R.id.ville_apart);

        viewHolder.dispoAppart = (TextView) convertView.findViewById(R.id.dispo_appart);
        viewHolder.nomAppart = (TextView) convertView.findViewById(R.id.nom_prop);
        viewHolder.imagePrincipalAppart = (ImageView) convertView.findViewById(R.id.image_appart_princ);

        final Appart appart = getItem(position);

        //Récupère et décode les images en Base64 depuis la BDD
        String base64 = appart.getStrImagePrinc().substring(appart.getStrImagePrinc().indexOf(","));
        byte[] decodedBase64 = Base64.decode(base64, Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(decodedBase64, 0, decodedBase64.length);
        viewHolder.imagePrincipalAppart.setImageBitmap(image);

        convertView.setTag(viewHolder);
        setupItem(viewHolder);
        return convertView;
    }

            private void setupItem (ViewHolder viewHolder){
                viewHolder.descriptAppart.setText(viewHolder.appart.getStrDescript());
                viewHolder.prixAppart.setText(viewHolder.appart.getPrix());
                viewHolder.adresseAppart.setText(viewHolder.appart.getStrAdresse());
                viewHolder.cpAppart.setText(viewHolder.appart.getStrCp());
                viewHolder.villeAppart.setText(viewHolder.appart.getStrVille());
                viewHolder.dispoAppart.setText(viewHolder.appart.getStrDispo());
                viewHolder.nomAppart.setText(viewHolder.appart.getStrNom());

            }

            static class ViewHolder {
                Appart appart;
                TextView descriptAppart;
                TextView prixAppart;
                TextView adresseAppart;
                TextView cpAppart;
                TextView villeAppart;
                TextView dispoAppart;
                TextView nomAppart;
                ImageView imagePrincipalAppart;

            }
}

