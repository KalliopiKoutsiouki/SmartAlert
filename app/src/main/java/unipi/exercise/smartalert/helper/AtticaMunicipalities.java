package unipi.exercise.smartalert.helper;

import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A utility class that provides data and operations related to municipalities in Attica and other areas.
 * This includes geolocation data, translation between English and Greek, and checks for the system's language setting.
 */
public class AtticaMunicipalities {

     /**
     * Retrieves a map of municipality names to their average corresponding geolocation points.
     *
     * @return A {@link Map} where the key is the municipality name (in English) and the value is a {@link GeoPoint}.
     */
    public static Map<String, GeoPoint> getMunicipalitiesMap() {
        Map<String, GeoPoint> municipalities = new HashMap<>();

        municipalities.put("Athens", new GeoPoint(37.9838, 23.7275));
        municipalities.put("Piraeus", new GeoPoint(37.9470, 23.6412));
        municipalities.put("Kifisia", new GeoPoint(38.0720, 23.8110));
        municipalities.put("Marousi", new GeoPoint(38.0565, 23.8066));
        municipalities.put("Glyfada", new GeoPoint(37.8622, 23.7548));
        municipalities.put("Chalandri", new GeoPoint(38.0214, 23.7981));
        municipalities.put("Peristeri", new GeoPoint(38.0154, 23.6910));
        municipalities.put("Nea Smyrni", new GeoPoint(37.9444, 23.7142));
        municipalities.put("Voula", new GeoPoint(37.8481, 23.7634));
        municipalities.put("Moschato-Tavros", new GeoPoint(37.9581, 23.6788));
        municipalities.put("Ilioupoli", new GeoPoint(37.9306, 23.7678));
        municipalities.put("Kallithea", new GeoPoint(37.9560, 23.7024));
        municipalities.put("Agios Dimitrios", new GeoPoint(37.9339, 23.7347));
        municipalities.put("Agia Paraskevi", new GeoPoint(38.0100, 23.8284));
        municipalities.put("Aigaleo", new GeoPoint(37.9927, 23.6822));
        municipalities.put("Keratsini-Drapetsona", new GeoPoint(37.9645, 23.6181));
        municipalities.put("Elliniko-Argyroupoli", new GeoPoint(37.9009, 23.7488));
        municipalities.put("Palaio Faliro", new GeoPoint(37.9290, 23.7024));
        municipalities.put("Alimos", new GeoPoint(37.9130, 23.7219));
        municipalities.put("Zografou", new GeoPoint(37.9686, 23.7714));
        municipalities.put("Penteli", new GeoPoint(38.0555, 23.8578));
        municipalities.put("Agioi Anargyroi-Kamatero", new GeoPoint(38.0360, 23.7103));
        municipalities.put("Megara", new GeoPoint(37.9948, 23.3435));
        municipalities.put("Spata-Artemida", new GeoPoint(37.9723, 23.9654));
        municipalities.put("Lavreotiki", new GeoPoint(37.7116, 24.0580));
        municipalities.put("Saronikos", new GeoPoint(37.7804, 23.9274));
        municipalities.put("Rafina-Pikermi", new GeoPoint(38.0199, 23.9885));
        municipalities.put("Markopoulo Mesogaias", new GeoPoint(37.8756, 23.9530));
        municipalities.put("Kropia", new GeoPoint(37.8631, 23.8496));
        municipalities.put("Elefsina", new GeoPoint(38.0417, 23.5445));
        municipalities.put("Vari-Voula-Vouliagmeni", new GeoPoint(37.8355, 23.7915));
        municipalities.put("Fyli", new GeoPoint(38.1164, 23.6917));
        municipalities.put("Acharnes", new GeoPoint(38.0838, 23.7536));
        municipalities.put("Dionysos", new GeoPoint(38.1258, 23.8640));
        municipalities.put("Mandra-Eidyllia", new GeoPoint(38.1219, 23.4994));
        municipalities.put("Oropos", new GeoPoint(38.2975, 23.7485));
        municipalities.put("Kalamata", new GeoPoint(30.7178, -64.006));
        municipalities.put("Pyrgos", new GeoPoint(22.6234403, 114.019));

        return municipalities;
    }

    public static Map<String, String> getEnglishToGreekMap() {
        Map<String, String> greekTranslations = new HashMap<>();

        greekTranslations.put("Athens", "Αθήνα");
        greekTranslations.put("Piraeus", "Πειραιάς");
        greekTranslations.put("Kifisia", "Κηφισιά");
        greekTranslations.put("Marousi", "Μαρούσι");
        greekTranslations.put("Glyfada", "Γλυφάδα");
        greekTranslations.put("Chalandri", "Χαλάνδρι");
        greekTranslations.put("Peristeri", "Περιστέρι");
        greekTranslations.put("Nea Smyrni", "Νέα Σμύρνη");
        greekTranslations.put("Voula", "Βούλα");
        greekTranslations.put("Moschato-Tavros", "Μοσχάτο-Ταύρος");
        greekTranslations.put("Ilioupoli", "Ηλιούπολη");
        greekTranslations.put("Kallithea", "Καλλιθέα");
        greekTranslations.put("Agios Dimitrios", "Άγιος Δημήτριος");
        greekTranslations.put("Agia Paraskevi", "Αγία Παρασκευή");
        greekTranslations.put("Aigaleo", "Αιγάλεω");
        greekTranslations.put("Keratsini-Drapetsona", "Κερατσίνι-Δραπετσώνα");
        greekTranslations.put("Elliniko-Argyroupoli", "Ελληνικό-Αργυρούπολη");
        greekTranslations.put("Palaio Faliro", "Παλαιό Φάληρο");
        greekTranslations.put("Alimos", "Άλιμος");
        greekTranslations.put("Zografou", "Ζωγράφου");
        greekTranslations.put("Penteli", "Πεντέλη");
        greekTranslations.put("Agioi Anargyroi-Kamatero", "Άγιοι Ανάργυροι-Καματερό");
        greekTranslations.put("Megara", "Μέγαρα");
        greekTranslations.put("Spata-Artemida", "Σπάτα-Αρτέμιδα");
        greekTranslations.put("Lavreotiki", "Λαυρεωτική");
        greekTranslations.put("Saronikos", "Σαρωνικός");
        greekTranslations.put("Rafina-Pikermi", "Ραφήνα-Πικέρμι");
        greekTranslations.put("Markopoulo Mesogaias", "Μαρκόπουλο Μεσογαίας");
        greekTranslations.put("Kropia", "Κρωπία");
        greekTranslations.put("Elefsina", "Ελευσίνα");
        greekTranslations.put("Vari-Voula-Vouliagmeni", "Βάρη-Βούλα-Βουλιαγμένη");
        greekTranslations.put("Fyli", "Φυλή");
        greekTranslations.put("Acharnes", "Αχαρνές");
        greekTranslations.put("Dionysos", "Διόνυσος");
        greekTranslations.put("Mandra-Eidyllia", "Μάνδρα-Ειδυλλία");
        greekTranslations.put("Oropos", "Ωρωπός");
        greekTranslations.put("Kalamata", "Καλαμάτα");
        greekTranslations.put("Pyrgos", "Πύργος");
        greekTranslations.put("Woodland Area", "Δασική Περιοχή");

        return greekTranslations;
    }

    public static Map<String, String> getGreekToEnglishMap() {
        Map<String, String> englishTranslations = new HashMap<>();

        getEnglishToGreekMap().forEach((english, greek) -> englishTranslations.put(greek, english));

        return englishTranslations;
    }

    public static String translateToGreek(String englishName) {
        return getEnglishToGreekMap().getOrDefault(englishName, englishName); // Return original if no translation exists
    }

    public static String translateToEnglish(String greekName) {
        return getGreekToEnglishMap().getOrDefault(greekName, greekName); // Return original if no translation exists
    }

    public static boolean isSystemLanguageGreek() {
        return Locale.getDefault().getLanguage().equals("el");
    }
}
