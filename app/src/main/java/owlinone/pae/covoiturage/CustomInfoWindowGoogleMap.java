package owlinone.pae.covoiturage;

/**
 * Created by rudyb on 14/02/2018.
 */

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

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

        TextView hotel_tv = (TextView) view.findViewById(R.id.hotels);
        TextView food_tv = (TextView) view.findViewById(R.id.food);
        TextView transport_tv = (TextView) view.findViewById(R.id.transport);

        name_tv.setText(marker.getTitle());
        details_tv.setText(marker.getSnippet());

        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();

        hotel_tv.setText(infoWindowData.getMail());
        food_tv.setText(infoWindowData.getTel());
        transport_tv.setText(infoWindowData.getAdresse());

        return view;
    }
}