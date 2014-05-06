package mx.gdgipn.parsetest.app;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;

/**
 * Created by thespianartist on 5/1/14.
 */
public class ParseTestApplication extends Application {

    public static final String TAG = "MyApp" ;

    @Override
    public void onCreate() {
        super.onCreate();

        //Parse Credenciales
        Parse.initialize(this, "YOUR APPLICATION ID", "YOUR CLIENT KEY");

        //El App ID de Facebook, se modifica en values -> strings ->
        ParseFacebookUtils.initialize(getString(R.string.app_id));

    }


}
