package com.example.Smart_Dustbin.collector;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DirectionsFetcher extends AsyncTask<String, Void, List<LatLng>> {

    private static final long REQUEST_DELAY = 60000; // 1 minute
    private DirectionsFetchListener listener;

    public interface DirectionsFetchListener {
        void onDirectionsFetched(List<LatLng> path);
        void onDirectionsFetchFailed(String errorMessage);
    }

    public void fetchDirections(String requestUrl, DirectionsFetchListener listener) {
        this.listener = listener;
        execute(requestUrl);
    }

    @Override
    protected List<LatLng> doInBackground(String... strings) {
        String requestUrl = strings[0];
        try {
            Thread.sleep(REQUEST_DELAY); // Introduce delay
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            String jsonData = stringBuilder.toString();
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray routesArray = jsonObject.getJSONArray("routes");
            JSONObject routeObject = routesArray.getJSONObject(0);
            JSONObject overviewPolyline = routeObject.getJSONObject("overview_polyline");
            String encodedPolyline = overviewPolyline.getString("points");

            return decodePolyline(encodedPolyline);

        } catch (IOException | JSONException | InterruptedException e) {
            Log.e("DirectionsFetcher", "Error fetching directions: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<LatLng> path) {
        if (path != null) {
            listener.onDirectionsFetched(path);
        } else {
            listener.onDirectionsFetchFailed("Failed to fetch directions");
        }
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> points = new ArrayList<>();
        int index = 0;
        int len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng point = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            points.add(point);
        }
        return points;
    }
}
