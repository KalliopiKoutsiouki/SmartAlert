package unipi.exercise.smartalert.helper;

import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class AtticaMunicipalities {

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

        return municipalities;
    }
}
