package cl.chihau.notifiwear;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity {
    // Key for the string that's delivered in the action's intent
    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void notificacionSimple(View view) {
        int notificationId = 1;
        // Build intent for notification content
        Intent viewIntent = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notificación Simple")
                .setContentText("Esta es una notificación simple de prueba")
                .setSubText("Toque para abrir una actividad de prueba")
                // En el teléfono este intent se gatilla cuando se presiona la notificación
                // En el reloj este intent se gatilla cuando se presiona el botón "Abrir en teléfono"
                .setContentIntent(viewPendingIntent);


        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public void notificacionMapa(View view) {
        int notificationId = 2;

        Intent viewIntent = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);

        // Build an intent for an action to view a map
        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        Uri geoUri = Uri.parse("geo:0,0?q=" + Uri.encode("Valparaíso"));
        mapIntent.setData(geoUri);
        PendingIntent mapPendingIntent =
                PendingIntent.getActivity(this, 0, mapIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Notificación con Mapa")
                        .setContentText("Esta es una notificación con Mapa de prueba")
                        .setSubText("Toque la notificación para abrir una actividad de prueba o " +
                                "toque el botón VER MAPA para abrir Google Maps")
                        .setContentIntent(viewPendingIntent)
                        // En el teléfono aparecerá un botón en la notificación y en el reloj
                        // aparecerá un botón grande al deslizar hacia la izquierda
                        .addAction(R.drawable.ic_map_white_48dp, "Ver mapa", mapPendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public void notificacionAccionSoloReloj(View view) {
        int notificationId = 3;

        // Create an intent for the reply action
        Intent actionIntent = new Intent(this, MainActivity.class);
        PendingIntent actionPendingIntent =
                PendingIntent.getActivity(this, 0, actionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // Build an intent for an action to view a map
        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        Uri geoUri = Uri.parse("geo:0,0?q=" + Uri.encode("Valparaíso"));
        mapIntent.setData(geoUri);
        PendingIntent mapPendingIntent =
                PendingIntent.getActivity(this, 0, mapIntent, 0);

        // Create the action
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.mipmap.ic_launcher,
                        "Abrir Actividad", actionPendingIntent)
                        .build();

        // Build the notification and add the action via WearableExtender
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notificación sólo en el reloj")
                .setContentText("Esta notificación contiene una acción exclusiva sólo para relojes")
                .setSubText("Toque la notificación para abrir una actividad de prueba o " +
                        "toque el botón VER MAPA para abrir Google Maps o toque el botón Abrir Actividad" +
                        "en el reloj ejecutar la acción exclusiva para los relojes")
                .setContentIntent(actionPendingIntent)
                .addAction(R.mipmap.ic_launcher, "Ver mapa", mapPendingIntent)
                // El botón para ejecutar esta acción sólo se verá en el reloj
                .extend(new NotificationCompat.WearableExtender().addAction(action));
                // Cuando se usa .extend, las acciones especificadas con el método
                // NotificationCompat.Builder.addAction() no se muestran en el reloj, en éste ejemplo
                // el botón Ver Mapa no aparecerá en el reloj

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public void notificacionTextoLargo(View view) {
        int notificationId = 4;

        Intent actionIntent = new Intent(this, MainActivity.class);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(this, 0, actionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
        bigStyle.bigText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod " +
                "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
                "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat." +
                " Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu " +
                "fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in " +
                "culpa qui officia deserunt mollit anim id est laborum.");
        bigStyle.setBigContentTitle("Text extendido");

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        // Agrega una imagen como fondo de pantalla para el reloj
                        // En el caso del teléfono lo utiliza como ícono de la notificación
                        .setLargeIcon(BitmapFactory.decodeResource(
                                getResources(), R.mipmap.ic_launcher))
                        .setContentTitle("Notificación con texto extendido")
                        .setContentText("Deslice para ver el texto completo")
                        .setContentIntent(actionPendingIntent)
                        .setStyle(bigStyle);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public void notificacionImagenFondo(View view) {
        int notificationId = 5;

        // Se recomienda una imagen de tamaño mínimo de 400x400 px
        // Se puede utilizar una imagen de tamaño 640x400 px para dar un efecto paralax
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        // Create a WearableExtender to add functionality for wearables
        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true)
                        .setBackground(bitmap);

        // Create a NotificationCompat.Builder to build a standard notification
        // then extend it with the WearableExtender
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Notificación con imagen de fondo")
                .setContentText("Deslice hacia la izquierda para ver el efecto paralax")
                .setSmallIcon(R.mipmap.ic_launcher)
                .extend(wearableExtender);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public void notificacionSoloTelefono(View view) {
        int notificationId = 6;
        // Build intent for notification content
        Intent viewIntent = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notificación solo en el teléfono")
                .setContentText("Esta notificación sólo se muestra en teléfono/tablet")
                .setSubText("Toque para abrir una actividad de prueba")
                // En el teléfono este intent se gatilla cuando se presiona la notificación
                // En el reloj este intent se gatilla cuando se presiona el botón "Abrir en teléfono"
                .setContentIntent(viewPendingIntent)
                .setLocalOnly(true); // Este método hace que la notificación no se muestre en el reloj


        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public void notificacionRespuestaVoz(View view) {
        int notificationId = 6;

        String replyLabel = "¿A usted le gusta el futbol?";
        String[] replyChoices = getResources().getStringArray(R.array.reply_choices);

        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
                .setLabel(replyLabel)
                .setChoices(replyChoices)
                .build();

        // Create an intent for the reply action
        Intent replyIntent = new Intent(this, SecondActivity.class);
        PendingIntent replyPendingIntent =
                PendingIntent.getActivity(this, 0, replyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the reply action and add the remote input
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.mipmap.ic_launcher,
                        "Responder con la voz", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        // Build the notification and add the action via WearableExtender
        NotificationCompat.Builder notificationCompat =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Notificación con respuesta por voz")
                        .setContentText("Para responder esta notificación utiliza la voz")
                        .extend(new NotificationCompat.WearableExtender().addAction(action));

        // Issue the notification
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, notificationCompat.build());
    }

    public void NotificacionDosPaginas(View view) {
        int notificationId = 7;

        // Create builder for the main notification
        NotificationCompat.Builder notificationCompat =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Notificación 1")
                        .setContentText("Página 1");

        //.setContentIntent(viewPendingIntent);

        // Create second page notification
        Notification secondPageNotification =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Notificación 2")
                        .setContentText("Página 2")
                        .build();

        notificationCompat.extend(new NotificationCompat.WearableExtender()
                .addPage(secondPageNotification));

        // Issue the notification
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, notificationCompat.build());
    }

    public void NotificacionesApiladas(View view) {
        int notificationId = 8;

        final String GROUP_KEY = "grupo_notif";

        // Build the notification, setting the group appropriately
        Notification notif = new NotificationCompat.Builder(this)
                .setContentTitle("Notificación 1")
                .setContentText("Notificación 1")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setGroup(GROUP_KEY) // para crear un stack de notificaciones
                .build();

        // Issue the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, notif);

        Notification notif2 = new NotificationCompat.Builder(this)
                .setContentTitle("Notificación 2")
                .setContentText("Notificación 2")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setGroup(GROUP_KEY)
                .build();

        notificationManager.notify(notificationId + 1, notif2);
    }
}

