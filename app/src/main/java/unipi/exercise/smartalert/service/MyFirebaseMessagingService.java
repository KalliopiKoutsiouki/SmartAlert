package unipi.exercise.smartalert.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Locale;

import unipi.exercise.smartalert.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private NotificationManager mManager;
    private static final String CHANNEL_ID = "default";

    public MyFirebaseMessagingService() {

    }

    // This method will be called when a new message is received
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Check if the message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String messageBody = remoteMessage.getNotification().getBody();

            title = translateText(title);
            messageBody = translateMessageBody(messageBody);
            // Handle the notification payload
            Log.e(TAG, "Message EventNotification Body: " + messageBody);

            sendNotification(title, messageBody);
        }

        // Check if the message contains data (optional)
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message Data: " + remoteMessage.getData());

            // If notification payload is absent, use data payload for the notification
            if (remoteMessage.getNotification() == null) {
                String title = remoteMessage.getNotification().getTitle();
                String messageBody = remoteMessage.getNotification().getBody();

                title = translateText(title);
                messageBody = translateMessageBody(messageBody);

                sendNotification(title, messageBody);
            }
        }
    }

    // This method is used to handle the token generation process
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // Send the token to your server so you can target this device with notifications
        Log.e(TAG, "New Token: " + token);
    }

    private String translateText(String text) {
        // Check the system language
        if (Locale.getDefault().getLanguage().equals("el")) {
            // Translate specific phrases to Greek
            switch (text) {
                case "Danger!!!":
                    return "Προσοχή!!!";
                case "There is a ":
                    return "Υπάρχει ";
                case " at ":
                    return " στην περιοχή ";
                default:
                    return text; // Return original if no translation is found
            }
        }
        return text; // Return original text if language is not Greek
    }

    private String translateMessageBody(String body) {
        if (Locale.getDefault().getLanguage().equals("el")) {
            // Replace the dynamic parts of the message
            return body
                    .replace("There is a ", translateText("There is a "))
                    .replace(" at ", translateText(" at "));
        }
        return body;
    }

    private void sendNotification(String title, String messageBody) {

        createNotificationChannel();

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .bigText(messageBody) // Set the full body text here
                .setBigContentTitle(title); // Optional: Set an expanded title

        // Create the notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setStyle(bigTextStyle) // Set the expanded style
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Ensure high priority for visibility
                .setAutoCancel(true) // Automatically dismiss when tapped
                .build();

        // Display the notification with a unique ID
        getNotificationManager().notify((int) System.currentTimeMillis(), notification);
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "General Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for general event notifications");

            channel.enableLights(true);
            channel.setLightColor(Color.RED); // You can customize the light color
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 1000});

            getNotificationManager().createNotificationChannel(channel);
        }
    }

    private NotificationManager getNotificationManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

}