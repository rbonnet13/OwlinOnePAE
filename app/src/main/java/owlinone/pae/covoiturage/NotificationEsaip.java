package owlinone.pae.covoiturage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by AnthonyCOPPIN on 02/03/2018.
 */

public class NotificationEsaip implements Serializable {

    ArrayList<HashMap<String, String>> notif;

    public ArrayList<HashMap<String, String>> getNotif() {
        return notif;
    }

    public void setNotif(ArrayList<HashMap<String, String>> notif) {
        this.notif = notif;
    }

}
