package taufiq.locationwithmapbox.Model;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import barikoi.barikoilocation.Api;
import barikoi.barikoilocation.JsonUtils;
import barikoi.barikoilocation.PlaceModels.Place;
import barikoi.barikoilocation.RequestQueueSingleton;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class MainActivityInteractor {
    public interface OnFetchDataListener{
        void onDataFetchSuccess(Place reverseGeoPlaceModel);
        void onDataFetchError(String error);
    }
    public void getReverseAddress(LatLng point,final OnFetchDataListener listener){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String token = prefs.getString("token", "");
        RequestQueue queue = RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.GET,
                Api.url_base+"reverse?latitude="+point.getLatitude()+"&longitude="+point.getLongitude(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject place=new JSONArray(response).getJSONObject(0);
                            Place p=getPlace(place);
                            if(p!=null ) listener.onDataFetchSuccess(p);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //loading.setVisibility(View.GONE);

                        listener.onDataFetchError(JsonUtils.handleResponse(error));
                    }
                }
        ) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                //parameters.put("id", id.getText().toString());
                if(!token.equals("")) {
                    parameters.put("Authorization", "bearer " + token);
                }
                return parameters;
            }

        };
        queue.add(request);
    }
    /**
     * Gets a place from the server
     * @param jsonObject takes a json object and structures the data and return a place
     * @return
     */
    public static Place getPlace(JSONObject jsonObject){
        try{
            String lon = jsonObject.getString("longitude");
            String lat = jsonObject.getString("latitude");
            String address = jsonObject.getString("Address");
            String code = jsonObject.getString("uCode");
            String area=jsonObject.has("area")? jsonObject.getString("area"):"";
            String city=jsonObject.has("city")? jsonObject.getString("city"):"";
            String postal=jsonObject.has("postCode")? jsonObject.getString("postCode"):"";
            String pType=jsonObject.has("pType")? jsonObject.getString("pType"):"";
            String subType=jsonObject.has("subType")? jsonObject.getString("subType"):"";
            String route=jsonObject.has("route_description")? jsonObject.getString("route_description"):"";
            String ward=jsonObject.has("ward")? jsonObject.getString("ward"):"";
            String zone=jsonObject.has("zone")? jsonObject.getString("zone"):"";
            String phoneNumber=jsonObject.has("contact_person_phone")? jsonObject.getString("contact_person_phone"):"";
            JSONArray images=jsonObject.has("images")?  jsonObject.getJSONArray("images"): new JSONArray();

            Place newplace = new Place(address, lon, lat, code, city, area, postal, pType, subType,phoneNumber);

            if(ward.length()>0 && !ward.equals("null")){
                newplace.setWard(ward);
            }
            if(zone.length()>0 && !zone.equals("null")){
                newplace.setZone(zone);
            }

            if (images.length() > 0) {
                JSONObject image = images.getJSONObject(0);
                newplace.setImglink(image.getString("imageLink"));
            }
            if(route.length()>0 && !route.equals("null")){
                newplace.setRoute(route);
            }
            if(jsonObject.has("distance")){
                String distancestring=jsonObject.getString("distance_in_meters");
                newplace.setDistance(Float.parseFloat(distancestring));
            }
            if(phoneNumber.length()>4){
                newplace.setPhoneNumber(phoneNumber);
            }
            return newplace;

        }catch (JSONException e){
            Log.d("JsonUtils",e.getLocalizedMessage());
            return null;
        }
    }
}
