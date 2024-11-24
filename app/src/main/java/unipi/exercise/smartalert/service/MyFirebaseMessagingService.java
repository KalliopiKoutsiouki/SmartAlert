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
import unipi.exercise.smartalert.helper.AtticaMunicipalities;

/**
 * Service for handling Firebase Cloud Messaging (FCM) notifications.
 * This service processes incoming messages, translates content dynamically based on locale,
 * and displays notifications to the user.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private NotificationManager mManager;
    private static final String CHANNEL_ID = "default";

    public MyFirebaseMessagingService() {

    }

    /**
     * Called when a new FCM message is received.
     *
     * @param remoteMessage The message received from FCM.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = null;
        String messageBody = null;

        // Check if the message contains data
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message Data: " + remoteMessage.getData());

            // Extract the data fields
            title = remoteMessage.getData().get("title");
            String eventType = remoteMessage.getData().get("eventType");
            String municipalities = remoteMessage.getData().get("municipalities");

            if (title == null) title = "Danger!!!"; // Fallback if title is missing

            // Translate title if necessary
            title = translateText(title);

            // Translate the message body dynamically
            messageBody = buildMessageBody(eventType, municipalities);
        }

        // Check if the message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            String notificationTitle = remoteMessage.getNotification().getTitle();
            String notificationBody = remoteMessage.getNotification().getBody();

            // Fallback to notification title/body if data payload is absent
            if (title == null) title = notificationTitle;
            if (messageBody == null) messageBody = notificationBody;
        }

        // Send the notification
        if (title != null && messageBody != null) {
            sendNotification(title, messageBody);
        }
    }
    /**
     * Builds the message body dynamically based on the event type and municipalities.
     *
     * @param eventType      The type of the event (e.g., Fire, Earthquake).
     * @param municipalities The affected municipalities as a comma-separated string.
     * @return The formatted message body.
     */
    private String buildMessageBody(String eventType, String municipalities) {
        if (eventType == null || municipalities == null) return null;

        // Split municipalities and translate each
        String[] municipalityArray = municipalities.split(",\\s*");
        StringBuilder translatedMunicipalities = new StringBuilder();
        if (Locale.getDefault().getLanguage().equals("el")) {
            for (String municipality : municipalityArray) {
                translatedMunicipalities.append(AtticaMunicipalities.translateToGreek(municipality.trim())).append(", ");
            }
            // Remove trailing comma and space
            if (translatedMunicipalities.length() > 0) {
                translatedMunicipalities.setLength(translatedMunicipalities.length() - 2);
            }
        } else {
            // If not Greek, use municipalities as is
            translatedMunicipalities.append(String.join(", ", municipalityArray));
        }
        String localizedEventType = translateText(eventType);
        // Build the message dynamically
        return translateText("There is a ") + localizedEventType + translateText(" at ") + translatedMunicipalities;
    }

    /**
     * Called when a new FCM token is generated for the device.
     *
     * @param token The new FCM token.
     */
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // Send the token to your server so you can target this device with notifications
        Log.e(TAG, "New Token: " + token);
    }

    /**
     * Translates the given text based on the system's language setting.
     *
     * @param text The text to translate.
     * @return The translated text, or the original text if no translation is required.
     */
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
                case "Fire":
                    return "Φωτιά";
                case "Earthquake":
                    return "Σεισμός";
                case "Tornado":
                    return "Ανεμοστρόβιλος";
                case "Flood":
                    return "Πλημμύρα";
                default:
                    return text;
            }
        } else {
            switch (text) {
                case "Φωτιά":
                    return "Fire";
                case "Σεισμός":
                    return "Earthquake";
                case "Ανεμοστρόβιλος":
                    return "Tornado";
                case "Πλημμύρα":
                    return "Flood";
            }
        }
        return text; // Return original text if language is not Greek
    }

    /**
     * Sends a notification to the user.
     *
     * @param title       The notification title.
     * @param messageBody The notification message body.
     */
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


    /**
     * Creates a notification channel for Android O and above.
     */
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


    /**
     * Retrieves the NotificationManager instance.
     *
     * @return The NotificationManager.
     */
    private NotificationManager getNotificationManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

}