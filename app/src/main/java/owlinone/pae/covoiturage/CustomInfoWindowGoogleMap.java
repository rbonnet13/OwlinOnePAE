package owlinone.pae.covoiturage;

/**
 * Created by rudyb on 14/02/2018.
 */

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import owlinone.pae.R;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindowGoogleMap(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.fragment_conducteur_affichage, null);

        TextView name_tv = (TextView) view.findViewById(R.id.name);
        TextView details_tv = (TextView) view.findViewById(R.id.details);
        ImageView img = (ImageView) view.findViewById(R.id.image_conducteur);

        TextView hotel_tv = (TextView) view.findViewById(R.id.hotels);
        TextView food_tv = (TextView) view.findViewById(R.id.food);
        TextView transport_tv = (TextView) view.findViewById(R.id.transport);

        name_tv.setText(marker.getTitle());
        details_tv.setText(marker.getSnippet());

        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();

        String url_image = infoWindowData.getImage();
        url_image = url_image.replace(" ","%20");
        try {
            Log.i("RESPUESTA IMAGE: ",""+url_image);
            Glide.with(this.context).load(url_image).into(img);
        } catch (Exception e) {
            e.printStackTrace();
        }

        hotel_tv.setText(infoWindowData.getHotel());
        food_tv.setText(infoWindowData.getFood());
        transport_tv.setText(infoWindowData.getTransport());

        return view;
    }
}