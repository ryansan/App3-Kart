package s326149.mappe3.rombestilling.models;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import s326149.mappe3.rombestilling.interfaces.AsyncPostBuildingResponse;
import s326149.mappe3.rombestilling.interfaces.AsyncPostOrderResponse;
import s326149.mappe3.rombestilling.interfaces.AsyncAddressResponse;
import s326149.mappe3.rombestilling.interfaces.AsyncPostRoomResponse;
import s326149.mappe3.rombestilling.interfaces.AsyncRepsonse;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class WebHandler {

    public AsyncRepsonse delegate = null;
    public AsyncAddressResponse addressDelegate = null;

    public AsyncPostOrderResponse postOrderDelegate = null;
    public AsyncPostRoomResponse postRoomDelegate = null;
    public AsyncPostBuildingResponse postBuildingDelegate = null;


    public static ArrayList<Building> buildings = new ArrayList<>();

    public ArrayList<Building> asyncBuildings = new ArrayList<>();
    public ArrayList<Room> asyncRooms = new ArrayList<>();
    public ArrayList<Order> asyncOrders = new ArrayList<>();

    public ArrayList<Building> nonAsyncBuildings = new ArrayList<>();
    public ArrayList<Room> nonAsyncRooms = new ArrayList<>();
    public ArrayList<Order> nonAsyncOrders= new ArrayList<>();

    public String address;

    String jsonString;

    public WebHandler(){
    }


   public String getNonAsyncBuildings(){
       GetJSON task = new GetJSON();
       try {
           String s = task.execute(new String[]{"http://student.cs.hioa.no/~s326149/getbuildings.php","http://student.cs.hioa.no/~s326149/getrooms.php","http://student.cs.hioa.no/~s326149/getorders.php"}).get();
           this.nonAsyncBuildings = asyncBuildings;
           this.nonAsyncRooms = asyncRooms;
           this.nonAsyncOrders = asyncOrders;

           for(Building b: nonAsyncBuildings){
               for(Room r: nonAsyncRooms){
                   if(b.getID() == r.getBuilding_ID()){
                       b.getRooms().add(r);
                   }
               }
           }

           for(Room r: nonAsyncRooms){
               for (Order o: nonAsyncOrders) {
                   if(r.getID() == o.getRoom_ID()) {
                       r.getOrders().add(o);
                   }
               }
           }

           return s;
       } catch (ExecutionException e) {
           e.printStackTrace();
       } catch (InterruptedException e) {
           e.printStackTrace();
       }
       return null;
   }

    public void getAsyncBuildings(){
        GetJSON task = new GetJSON();
        task.execute(new String[]{"http://student.cs.hioa.no/~s326149/getbuildings.php","http://student.cs.hioa.no/~s326149/getrooms.php","http://student.cs.hioa.no/~s326149/getorders.php"});
    }

    public void postRoom(Room room){
        PostRoomTask task = new PostRoomTask(room.getName(),room.getFloor(),room.getBuilding_ID(), room.getDescription());
        task.execute();
    }

    public void postBuilding(Building building){
        PostBuildingTask task = new PostBuildingTask(building.getName(),building.getAddress(),building.getLatLng());
        task.execute();
    }

    public void postOrder(Order order){
        PostOrderTask task = new PostOrderTask(order);
        task.execute();
    }

    public void deleteBuilding(Building b) {
        DeleteBuildingTask task = new DeleteBuildingTask(b);
        task.execute();
    }

    public void deleteRoom(Room room) {
        DeleteRoomTask task = new DeleteRoomTask(room);
        task.execute();
    }

    public void deleteOrder(Order order) {
        DeleteOrderTask task = new DeleteOrderTask(order);
        task.execute();
    }

    public void getAddress(LatLng latlng, Activity context){
        GetLocationTask task = new GetLocationTask(latlng, context);
        task.execute();
    }

    private class GetJSON extends AsyncTask<String, Void,String> {

        @Override
        protected String doInBackground(String... urls) {

            asyncBuildings.clear();
            asyncRooms.clear();
            asyncOrders.clear();

            String retur = "";
            String s = "";
            String output = "";

            for (int i = 0; i < urls.length; i++) {
                output = "";
                try {
                    URL urlen = new URL(urls[i]);
                    HttpURLConnection conn = (HttpURLConnection) urlen.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code :" + conn.getResponseCode());
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    Log.d("Task","Output from Server .... \n");

                    while ((s = br.readLine()) != null) {
                        output = output + s;
                    }
                    conn.disconnect();
                    try {

                        JSONArray mat = new JSONArray(output);

                        //Get buildings
                        if(i == 0){
                            for (int j = 0; j < mat.length(); j++) {
                                JSONObject jsonobject = mat.getJSONObject(j);
                                Building building = new Building();
                                building.setID(jsonobject.getInt("ID"));
                                building.setName(jsonobject.getString("Name"));
                                building.setLatLng(new LatLng(jsonobject.getDouble("Latitude"), jsonobject.getDouble("Longitude")));
                                building.setAddress(jsonobject.getString("address"));
                                asyncBuildings.add(building);
                                String name = jsonobject.getString("Name");
                                retur = retur + name + "\n";
                            }
                        }
                        if(i == 1){
                            for (int j = 0; j < mat.length(); j++) {
                                JSONObject jsonobject = mat.getJSONObject(j);

                                Room room = new Room();
                                room.setID(jsonobject.getInt("ID"));
                                room.setName(jsonobject.getString("Name"));
                                room.setFloor(jsonobject.getInt("Floor"));
                                room.setBuilding_ID(jsonobject.getInt("Building_ID"));
                                room.setDescription(jsonobject.getString("Description"));
                                asyncRooms.add(room);
                                String name = jsonobject.getString("Name");
                                retur = retur + name + "\n";
                            }
                        }
                        if(i == 2){
                            for (int j = 0; j < mat.length(); j++) {
                                JSONObject jsonobject = mat.getJSONObject(j);

                                Order order = new Order();
                                //OrderDate, FromHour, ToHour, Name, Description, Mail, Room_ID
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                String date = jsonobject.getString("OrderDate");
                                Date useThis = format.parse(date);
                                order.setDate(useThis);
                                order.setHourFrom(jsonobject.getInt("FromHour"));
                                order.setHourTo(jsonobject.getInt("ToHour"));

                                order.setID(jsonobject.getInt("ID"));
                                order.setName(jsonobject.getString("Name"));
                                order.setDescription(jsonobject.getString("Description"));
                                order.setMail(jsonobject.getString("Mail"));
                                order.setRoom_ID(jsonobject.getInt("Room_ID"));

                                asyncOrders.add(order);
                                String name = jsonobject.getString("Name");
                                retur = retur + name + "\n";
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                   // return retur;
                } catch (Exception e) {
                    Log.d("WH", e.getMessage());
                    e.printStackTrace();
                    return "Noe gikk feil";
                }
            }

            return retur;
        }

        @Override
        protected void onPostExecute(String ss) {
            //textView.setText(ss);

            for (Building b : asyncBuildings) {
                for (Room r: asyncRooms) {
                    if(b.getID() == r.getBuilding_ID()){
                        b.getRooms().add(r);
                    }
                }
            }

            for (Room r: asyncRooms) {
                for (Order o : asyncOrders) {
                    if (r.getID() == o.getRoom_ID()) {
                        r.getOrders().add(o);
                    }
                }
            }

            nonAsyncBuildings = asyncBuildings;

            jsonString = ss;
            Log.d("GET ALL", jsonString);
            if(delegate != null){
                delegate.processFinish(jsonString);
            }
        }
    }

    private class PostRoomTask extends AsyncTask<Void, Void, String> {
        String name;
        int floor;
        int building_ID;
        String description;

        public PostRoomTask(String name, int floor, int building_ID, String description) {
            this.name = name;
            this.floor = floor;
            this.building_ID = building_ID;
            this.description = description;
        }

        @Override
        protected String doInBackground(Void... params) {
            String s = "";
            String output = "";
            String query = "http://student.cs.hioa.no/~s326149/postroom.php?Name="+encode(name)+ "&Floor=" + floor + "&Description=" + encode(description) + "&Building_ID="+building_ID;

            try {
                URL urlen = new URL(query);
                HttpURLConnection conn = (HttpURLConnection) urlen.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                while ((s = br.readLine()) != null) {
                    output = output + s;
                }
                conn.disconnect();

                /*Double lon = Double.valueOf(0);
                Double lat = Double.valueOf(0);
                lon =((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                lokasjon = String.valueOf(lon) + " : " + String.valueOf(lat);*/

                return output;
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            return output;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("POST ROOM", "FINISHED");
            //resultat = "POSt ROOM";
            if(postRoomDelegate != null) {
                postRoomDelegate.postRoomFinish(result);
            }else{
                Log.d("postroom", "null");
            }
        }
    }

    private class PostBuildingTask extends AsyncTask<Void, Void, String> {
        JSONObject jsonObject;

        String lokasjon;

        String name;
        String address;
        LatLng latLng;

        public PostBuildingTask(String name, String address, LatLng latLng) {
            this.name = name;
            this.address = address;
            this.latLng = latLng;
        }

        @Override
        protected String doInBackground(Void... params) {
            String s = "";
            String output = "";
            String query = "http://student.cs.hioa.no/~s326149/postbuilding.php?Name="+encode(name)+"&Address="+encode(address)+"&Latitude="+latLng.latitude+"&Longitude="+latLng.longitude;

            try {
                URL urlen = new URL(query);
                HttpURLConnection conn = (HttpURLConnection) urlen.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                while ((s = br.readLine()) != null) {
                    output = output + s;
                }
                //jsonObject = new JSONObject("");
                conn.disconnect();

                /*Double lon = Double.valueOf(0);
                Double lat = Double.valueOf(0);
                lon =((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                lokasjon = String.valueOf(lon) + " : " + String.valueOf(lat);*/

                return output;
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            return output;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("POST BUILDING:", result);
            if(postBuildingDelegate!= null) {
                postBuildingDelegate.postBuildingFinish(result);
            }else{
                Log.d("postbuilding", "null");
            }
        }
    }

    private class PostOrderTask extends AsyncTask<Void, Void, String> {
        JSONObject jsonObject;

        String lokasjon;

        Order order;

        public PostOrderTask(Order order) {
            this.order = order;
        }

        @Override
        protected String doInBackground(Void... params) {
            String s = "";
            String output = "";
            String query = "http://student.cs.hioa.no/~s326149/postorder.php?OrderDate="+
                    order.getDate().getTime() +
                    "&FromHour="+
                    order.getHourFrom()+
                    "&ToHour="+
                    order.getHourTo()+
                    "&Name="+
                    encode(order.getName())+
                    "&Description="+
                    encode(order.getDescription())+
                    "&Mail="+
                    encode(order.getMail())+
                    "&Room_ID="+
                    order.getRoom_ID();
            Log.d("Query", query);
            try {
                URL urlen = new URL(query);
                HttpURLConnection conn = (HttpURLConnection) urlen.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                while ((s = br.readLine()) != null) {
                    output = output + s;
                }
                //jsonObject = new JSONObject("");
                conn.disconnect();

                /*Double lon = Double.valueOf(0);
                Double lat = Double.valueOf(0);
                lon =((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                lokasjon = String.valueOf(lon) + " : " + String.valueOf(lat);*/

                return output;
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            return output;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("POST ORDER:", "FINISHED");
            Log.d("Post order res", result);
            if(postOrderDelegate != null) {
                postOrderDelegate.postOrderFinish(result);
            }else{
                Log.d("Post order", "Null");
            }
        }
    }


    private class DeleteBuildingTask extends AsyncTask<Void, Void, String> {
        JSONObject jsonObject;

        String lokasjon;

        Building building;

        public DeleteBuildingTask(Building building) {
            this.building = building;
        }

        @Override
        protected String doInBackground(Void... params) {
            String s = "";
            String output = "";
            String query = "http://student.cs.hioa.no/~s326149/deletebuilding.php?ID="+building.getID();

            try {
                URL urlen = new URL(query);
                HttpURLConnection conn = (HttpURLConnection) urlen.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                while ((s = br.readLine()) != null) {
                    output = output + s;
                }
                jsonObject = new JSONObject("");
                conn.disconnect();

                /*Double lon = Double.valueOf(0);
                Double lat = Double.valueOf(0);
                lon =((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                lokasjon = String.valueOf(lon) + " : " + String.valueOf(lat);*/

                return output;
            }
            catch (IOException ex) {
                ex.printStackTrace();
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return output;
        }

        @Override
        protected void onPostExecute(String resultat) {
            Log.d("DELETE BUILDING:", "FINISHED");
            delegate.processFinish(resultat);
        }
    }

    private class DeleteRoomTask extends AsyncTask<Void, Void, String> {
        JSONObject jsonObject;

        String lokasjon;

        Room room;

        public DeleteRoomTask(Room room) {
            this.room = room;
        }

        @Override
        protected String doInBackground(Void... params) {
            String s = "";
            String output = "";
            String query = "http://student.cs.hioa.no/~s326149/deleteroom.php?ID="+ room.getID();

            try {
                URL urlen = new URL(query);
                HttpURLConnection conn = (HttpURLConnection) urlen.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                while ((s = br.readLine()) != null) {
                    output = output + s;
                }
                conn.disconnect();

                /*Double lon = Double.valueOf(0);
                Double lat = Double.valueOf(0);
                lon =((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                lokasjon = String.valueOf(lon) + " : " + String.valueOf(lat);*/

                return output;
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            return output;
        }

        @Override
        protected void onPostExecute(String resultat) {
            Log.d("DELETE ROOM:", "FINISHED");
            delegate.processFinish(resultat);
        }
    }

    private class DeleteOrderTask extends AsyncTask<Void, Void, String> {
        JSONObject jsonObject;

        String lokasjon;

        Order order;

        public DeleteOrderTask(Order order) {
            this.order = order;
        }

        @Override
        protected String doInBackground(Void... params) {
            String s = "";
            String output = "";
            String query = "http://student.cs.hioa.no/~s326149/deleteorder.php?ID="+ order.getID();

            try {
                URL urlen = new URL(query);
                HttpURLConnection conn = (HttpURLConnection) urlen.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                while ((s = br.readLine()) != null) {
                    output = output + s;
                }
                conn.disconnect();

                /*Double lon = Double.valueOf(0);
                Double lat = Double.valueOf(0);
                lon =((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                lokasjon = String.valueOf(lon) + " : " + String.valueOf(lat);*/

                return output;
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            return output;
        }

        @Override
        protected void onPostExecute(String resultat) {
            Log.d("DELETE Order:", "FINISHED");
            delegate.processFinish(resultat);
        }
    }

    private class GetLocationTask extends AsyncTask<Void, Void, String> { JSONObject jsonObject;
        String address;
        String lokasjon;

        LatLng latLng;
        Context context;

        public GetLocationTask(LatLng latLng, Context context) {
            this.latLng = latLng;
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {

            Geocoder geocoder = new Geocoder(context, new Locale("NO"));

            List<Address> adresses = new ArrayList<>();
            try {
                adresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (adresses.size() == 0) {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String address = adresses.get(0).getAddressLine(0);
            String useThisAddressForSavingAndDisplaying = adresses.get(0).getThoroughfare() + " " + adresses.get(0).getSubThoroughfare();

            return address;

        }
        @Override
        protected void onPostExecute(String resultat) {
         //   koordinater.setText(resultat);
            this.address = resultat;
            addressDelegate.processFinish(resultat);
        }
    }



    public String encode(String s){
        try{
            String encodedString = URLEncoder.encode(s,"UTF-8");
            return encodedString;
        }catch (UnsupportedEncodingException e){
            Log.d("Encoder:", e.getMessage());
            return s;
        }
    }

}
