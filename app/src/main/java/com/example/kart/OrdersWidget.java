package com.example.kart;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.kart.models.Building;
import com.example.kart.models.Order;
import com.example.kart.models.Room;
import com.example.kart.models.WebHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OrdersWidget extends AppWidgetProvider {

    static int i = 0;
    static List<String> reminders = new ArrayList<>();
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        i++;
        RemoteViews updateViews = new RemoteViews(context.getApplicationContext().getPackageName(), R.layout.orders_widget);


        String myFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        WebHandler webHandler = new WebHandler();
        String t = webHandler.getNonAsyncBuildings();
        List<Building> buildings = webHandler.nonAsyncBuildings;
        List<Room> rooms = webHandler.nonAsyncRooms;
        List<Order> orders = webHandler.nonAsyncOrders;
        Log.d("TESTT rom", String.valueOf(rooms.size()));
        Log.d("TESTTT ord", String.valueOf(orders.size()));


        if(reminders.isEmpty()) {
            reminders.add("Trykk for å se neste bestilling");
            for (Building b : buildings) {
                for (Room r : b.getRooms()) {
                    for (Order o : r.getOrders()) {

                        String dateChosenByUser = sdf.format(Calendar.getInstance().getTime());
                        String oDate = sdf.format(o.getDate());

                        Log.d("Idag sys:", dateChosenByUser);
                        Log.d("Idag order:", oDate);

                        if (dateChosenByUser.equals(oDate)) {
                            Log.d("Idag sys:", dateChosenByUser);
                            Log.d("Idag order:", oDate);

                            int iend = b.getAddress().indexOf(",");
                            Log.d("AD", b.getAddress());
                            Log.d("AD", "size: "+ iend);
                            String address  = b.getAddress();
                            String subString;
                            if(iend != -1){
                                subString = address.substring(0 , iend);
                            }else{
                                subString = address;
                            }

                            reminders.add("Rombestilling i dag, " + dateChosenByUser +
                                    "\n\nRom: " + r.getName() +
                                    "\nFra klokken: " + o.getHourFrom() + "-" + o.getHourTo() +
                                    "\nAdresse: " + subString +
                                    "\nBestillings ID: " + o.getID()+
                                    "\n\n\nTrykk for å se neste bestilling");
                        }
                    }
                }
            }
        }

        Log.e("Sjekk i ", String.valueOf(i));
        if(i == reminders.size()){
            Log.e("Inni == ", String.valueOf(i));
            i = 0;
            orders.clear();
        }else{
            Log.d("Reminders: ", reminders.size()+"");

            if(reminders.size()>0){
                updateViews.setTextViewText(R.id.widgettekst, reminders.get(i));
            }else{
                updateViews.setTextViewText(R.id.widgettekst, "Ingen rombestillinger i dag.");
            }
        }

        Intent clickIntent = new Intent(context, OrdersWidget.class);

        clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        updateViews.setOnClickPendingIntent(R.id.widgettekst, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, updateViews);
    }
}
