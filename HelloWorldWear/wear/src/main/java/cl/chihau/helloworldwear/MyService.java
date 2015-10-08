package cl.chihau.helloworldwear;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by chihau on 31-07-15.
 */
public class MyService extends WearableListenerService {
    private static final String START_ACTIVITY_PATH = "/start-activity";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("TEST", "onMessageReceived: " + messageEvent);

        // Check to see if the message is to start an activity
        if (messageEvent.getPath().equals(START_ACTIVITY_PATH)) {
            Intent startIntent = new Intent(this, MainActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
        }
    }
}
