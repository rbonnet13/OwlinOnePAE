package owlinone.pae.covoiturage;

/**
 * Created by AnthonyCOPPIN on 03/03/2018.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Belal on 2/3/2016.
 */
//Extending FragmentStatePagerAdapter
public class Pager extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;
    NotificationEsaip notifEsaip;
    NotificationHome notifHome;



    //Constructor to the class
    public Pager(FragmentManager fm, int tabCount, NotificationEsaip notifEsaip, NotificationHome notifHome) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
        this.notifEsaip= notifEsaip;
        this.notifHome= notifHome;

    }



    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                ConducteurEsaip esaip = new ConducteurEsaip();
                Bundle bundleEsaip = new Bundle();
                bundleEsaip.putSerializable("esaip", notifEsaip);

                esaip.setArguments(bundleEsaip);

                return esaip;
            case 1:
                ConducteurHome home = new ConducteurHome();
                Bundle bundleHome = new Bundle();
                bundleHome.putSerializable("home", notifHome);

                home.setArguments(bundleHome);
                return home;

            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}