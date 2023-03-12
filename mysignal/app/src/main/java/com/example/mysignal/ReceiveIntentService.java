package com.example.mysignal;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ReceiveIntentService extends IntentService {

    private static final String ACTION_RECV = "android.intent.dtn.SEND_DATA";


    public ReceiveIntentService() {
        super("ReceiveIntentService");
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    /*public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ReceiveIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }*/

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_RECV.equals(action)) {
                final String param1 = intent.getStringExtra(Intent.EXTRA_TEXT);
                handleActionFoo(param1);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String message) {

        Log.d("deepak","data received in service: "+message);

        ContentValues values=new ContentValues();
        values.put("data", message.getBytes());
        values.put("appName", getApplicationContext().getPackageName());

        getContentResolver().insert(MainActivity.CONTENT_URL, values);
    }
}