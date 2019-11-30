package com.example.kart.fragments;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kart.MainActivity;
import com.example.kart.R;
import com.example.kart.fragments.models.Building;
import com.example.kart.fragments.models.DialogDismisser;
import com.example.kart.fragments.models.Order;
import com.example.kart.fragments.models.Room;
import com.example.kart.fragments.models.WebHandler;
import com.example.kart.fragments.models.interfaces.AsyncPostResponse;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class RoomOrdersFragment extends DialogFragment implements AsyncPostResponse {

    private  Building building;
    TextView tvRoomName;
    TextView txtNoAddButton;
    ListView lvRoomOrders;
    Button btnAddRoomOrder;
    Button btnCancelShowRoomOrders;
    Room room;
    Button btnDate;
    Button btnBack;

    Calendar calendar;

    public WebHandler webHandler;

    ArrayList<String> orders = new ArrayList<>();

    ArrayList<Order> currentOrdersInList = new ArrayList<>();

    ArrayAdapter<String> listAdapter;

    Date chosenDate;



    public RoomOrdersFragment(Building building, Room room){
        this.building = building;
        this.room = room;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_show_room_orders, container,false);

        webHandler = ((MainActivity)getActivity()).webHandler;
        webHandler.postDelegate = null;
        webHandler.postDelegate = this;

        tvRoomName = view.findViewById(R.id.tvRoomName);
        txtNoAddButton = view.findViewById(R.id.txtNoAddButton);
        btnCancelShowRoomOrders = view.findViewById(R.id.btnCancelShowRoomOrders);
        btnAddRoomOrder = view.findViewById(R.id.btnAddRoomOrder);
        lvRoomOrders = view.findViewById(R.id.lvRoomOrders);
        tvRoomName.setText(room.getName() + ", " + room.getDescription());

        btnDate = view.findViewById(R.id.etAddOrderShowForDate);

        btnBack = view.findViewById(R.id.btnShowRoomOrdersrBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        btnCancelShowRoomOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cancelAdding();
                webHandler.postDelegate = null;
                Log.d("close","c)");
                //dismissAllDialogs(getFragmentManager());
                DialogDismisser.dismissAllDialogs(getFragmentManager());
                //popBackStackTillEntry(1);
            }
        });

        btnAddRoomOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddOrderFragment addRoomFragment = new AddOrderFragment(room, chosenDate);
                FragmentManager fm = getFragmentManager();
                addRoomFragment.show(fm,"AddOrderFragment");
                fm.executePendingTransactions();

                addRoomFragment.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if(getFragmentManager() == null){
                            return;
                        }
                        List<Fragment> allFragments = getFragmentManager().getFragments();

                        if (allFragments == null) {
                            return;
                        }

                        setOrders();
                        listAdapter.notifyDataSetChanged();
                    }
                });
            }
        });



        calendar = Calendar.getInstance();
        chosenDate = Calendar.getInstance().getTime();
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDate = df.format(c);

        btnDate.setText(formattedDate);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateLabel();
                //listAdapter.clear();
                setOrders();
                listAdapter.notifyDataSetChanged();
            }

        };



        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        setOrders();


        listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, orders);

        lvRoomOrders.setAdapter(listAdapter);

        lvRoomOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OrderInfoFragment orderInfoFragment = new OrderInfoFragment(currentOrdersInList.get(i));
                orderInfoFragment.show(getFragmentManager(),"OrderInfoFragment");
            }
        });

        //DELETING
        lvRoomOrders.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                DeleteOrderFragment deleteOrderFragment = new DeleteOrderFragment(building, room, currentOrdersInList.get(i));

                FragmentManager fm = getFragmentManager();
                deleteOrderFragment.show(fm,"DeleteOrderFragment");

                fm.executePendingTransactions();
                deleteOrderFragment.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {

                        setOrders();
                        listAdapter.notifyDataSetChanged();
                    }
                });

                return true;
            }
        });

        return view;
    }

    private void updateLabel() {
        String myFormat = "dd.MM.yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        btnDate.setText(sdf.format(calendar.getTime()));

        String sqliteFormat = "yyyy-MM-dd";
        sdf = new SimpleDateFormat(sqliteFormat);
        chosenDate.setTime(calendar.getTimeInMillis());

        Date today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);


        if(today.after(chosenDate)){
            txtNoAddButton.setVisibility(View.VISIBLE);
            txtNoAddButton.setText("Kan ikke legge til bestilling tilbake i tid");
            btnAddRoomOrder.setEnabled(false);
        }else{
            btnAddRoomOrder.setEnabled(true);
            txtNoAddButton.setVisibility(View.INVISIBLE);
            txtNoAddButton.setText("");
        }

        //date = sdf.format(calendar.getTime());
    }

    public void setOrders(){

        String myFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        List<Room> rooms = webHandler.asyncRooms;


        orders.clear();
        currentOrdersInList.clear();


        for (Room r: building.getRooms()){
            if(r.equals(room)){
                for (Order o : room.getOrders()) {

                    String dateChosenByUser = btnDate.getText().toString();
                    String oDate = sdf.format(o.getDate());

                    if(dateChosenByUser.equals(oDate)){
                        orders.add(o.getHourFrom() + "-" + o.getHourTo() + ", " + o.getName());
                        currentOrdersInList.add(o);
                    }
                }
            }
        }

        /*
        for (Room r: rooms){
            if(r.getID() == room.getID()){
                for (Order o : r.getOrders()) {

                    String dateChosenByUser = btnDate.getText().toString();
                    String oDate = sdf.format(o.getDate());

                    if(dateChosenByUser.equals(oDate)){
                        orders.add(o.getHourFrom() + "-" + o.getHourTo() + ", " + o.getName());
                        currentOrdersInList.add(o);
                    }
                }
            }
        }
         */

        //SORT THE LIST OF ORDERS BASED ON HOURFROM
      /*  Collections.sort(orders, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                int s1int = Integer.parseInt(s.substring(0, s.indexOf("-")));
                int s2int = Integer.parseInt(t1.substring(0, t1.indexOf("-")));
                return s1int - s2int;
            }
        });*/

        for (int i = 0; i < orders.size(); i++) {
            for (int j = 0; j < orders.size() - i - 1; j++) {

                int s1int = Integer.parseInt(orders.get(j).substring(0, orders.get(j).indexOf("-")));
                int s2int = Integer.parseInt(orders.get(j+1).substring(0, orders.get(j+1).indexOf("-")));

                if (s1int > s2int) {
                    // swap arr[j+1] and arr[i]
                    String temp = orders.get(j);
                    orders.set(j, orders.get(j + 1));
                    orders.set(j + 1, temp);
                }
            }
        }

        for (int i = 0; i < currentOrdersInList.size(); i++) {
            for (int j = 0; j < currentOrdersInList.size() - i - 1; j++) {
                if (currentOrdersInList.get(j).getHourFrom() > currentOrdersInList.get(j + 1).getHourFrom()) {
                    // swap arr[j+1] and arr[i]
                    Order temp = currentOrdersInList.get(j);
                    currentOrdersInList.set(j, currentOrdersInList.get(j + 1));
                    currentOrdersInList.set(j + 1, temp);
                }
            }
        }


    }

    @Override
    public void postFinish(String output) {
        Log.d("PF:", "FINS");
        Log.d("POST FIN IN ORDER", output);

        if(output.length() < 4) {
            //this.building.getRooms().get(this.building.getRooms().size() - 1).setID(Integer.parseInt(output));
            this.room.getOrders().get(this.room.getOrders().size()-1).setID(Integer.parseInt(output));
        }else{
            Log.d("post error", output);
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        this.webHandler.postDelegate = null;
        super.onDismiss(dialog);
    }
}
