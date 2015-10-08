package cl.chihau.notifiwear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.widget.TextView;

/**
 * Created by chihau on 26-07-15.
 */
public class SecondActivity extends Activity {

    TextView txt;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        txt = (TextView) findViewById(R.id.txt);
        txt.setText(getMessageText(getIntent()));
    }

    /**
     * Obtain the intent that started this activity by calling
     * Activity.getIntent() and pass it into this method to
     * get the associated voice input string.
     */

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(MainActivity.EXTRA_VOICE_REPLY);
        }
        return null;
    }
}
