package com.example.kart.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kart.MainActivity;
import com.example.kart.R;
import com.example.kart.models.DialogDismisser;
import com.example.kart.models.Order;
import com.example.kart.models.Room;
import com.example.kart.models.WebHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddOrderFragment extends DialogFragment {

    Button btnCancel, btnAddOrder, btnBack;
    Room room;
    EditText etOrderDescription, etOrderDate, etOrderName, etOrderMail, etOrderTimeFrom, etOrder;

    WebHandler webHandler;

    public Spinner spinner;
    public Spinner spinnerTo;

    public ArrayAdapter<String> adapter;
    public ArrayAdapter<String> adapterTo;

    //THE LIST POPULATED WITH 7-22
    public List<Integer> allTimesInlcudingBookedMarked = new ArrayList<>();

    //LIST FOR SPINNERFROM
    public List<String> availableTimesString = new ArrayList<>();

    //LIST FOR SPINNERTO
    public List<String> availableTimesStringTo = new ArrayList<>();

    //USING
    public List<String> onlyAvailableTimesList = new ArrayList<>();

    public Date chosenDate;
    private boolean roomHasOrdersToday;

    public AddOrderFragment(Room room, Date chosenDate){
        this.room = room;
        this.chosenDate = chosenDate;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_order, container,false);

//        etOrderDate = view.findViewById(R.id.etAddOrderDate);
//        etOrderTimeFrom = view.findViewById(R.id.etAddOrderTimeFrom);
        etOrderName = view.findViewById(R.id.etAddOrderName);
        etOrderDescription = view.findViewById(R.id.etAddOrderDescription);
        etOrderMail = view.findViewById(R.id.etAddOrderMail);

        spinner = view.findViewById(R.id.addOrderTimeSpinner);
        spinnerTo = view.findViewById(R.id.addOrderTimeSpinnerTo);

        btnCancel = view.findViewById(R.id.btnCancelAddOrder);
        btnBack = view.findViewById(R.id.btnAddOrderBack);
        btnAddOrder = view.findViewById(R.id.btnAddOrder);

        webHandler = ((MainActivity)getActivity()).webHandler;

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogDismisser.dismissAllDialogs(getFragmentManager());
            }
        });

        btnAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etOrderName.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Navn er obligatorisk", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(etOrderDescription.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Beskrivelse er obligatorisk", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(etOrderMail.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Mail er obligatorisk", Toast.LENGTH_SHORT).show();
                    return;
                }

                Order order = new Order();

                order.setDate(new Date(chosenDate.getTime()));

                order.setHourFrom(Integer.parseInt(spinner.getSelectedItem().toString()));
                order.setHourTo(Integer.parseInt(spinnerTo.getSelectedItem().toString()));

                order.setName(etOrderName.getText().toString());
                order.setDescription(etOrderDescription.getText().toString());
                order.setMail(etOrderMail.getText().toString());
                order.setRoom_ID(room.getID());
                webHandler.postOrder(order);
                //webHandler.getAsyncBuildings();

                room.getOrders().add(order);
                dismiss();

            }
        });



        for (int i = 7; i < 22; i++) {
            allTimesInlcudingBookedMarked.add(i);
            int temp = i;
            availableTimesString.add(i + "-" + ++temp);
        }
  /*
        Booked b1 = new Booked(8,10);
        Booked b2 = new Booked(12,14);

      List<Booked> bookeds = new ArrayList<>();
        bookeds.add(b1);
        bookeds.add(b2);
        */

        String myFormat = "dd.MM.yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        for (int i = 0; i < room.getOrders().size(); i++) {
            if(sdf.format(room.getOrders().get(i).getDate()).equals(sdf.format(chosenDate))){
                roomHasOrdersToday = true;
                for (int j = 0; j < allTimesInlcudingBookedMarked.size(); j++) {
                    if(room.getOrders().get(i).getHourFrom() == allTimesInlcudingBookedMarked.get(j)){
                        for (int k = room.getOrders().get(i).getHourFrom(); k < room.getOrders().get(i).getHourTo(); k++) {
                            String temp = availableTimesString.get(j);
                            temp += "N/A";
                            availableTimesString.set(j,temp);
                            allTimesInlcudingBookedMarked.set(j,-1);
                            j++;
                        }
                        break;
                    }
                }
            }
        }



        for (int i = 0; i < allTimesInlcudingBookedMarked.size(); i++) {
            if(allTimesInlcudingBookedMarked.get(i) != -1){
                onlyAvailableTimesList.add(String.valueOf(allTimesInlcudingBookedMarked.get(i)));
            }
        }



        //adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, availableTimesString);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, onlyAvailableTimesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        adapterTo = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, availableTimesStringTo);
        adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTo.setAdapter(adapterTo);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                availableTimesStringTo.clear();

                if(!roomHasOrdersToday){
                    if(i == onlyAvailableTimesList.size()-1){
                        availableTimesStringTo.add( String.valueOf(allTimesInlcudingBookedMarked.get(allTimesInlcudingBookedMarked.size()-1)+1));
                    }else{
                        for (int j = i+1; j < onlyAvailableTimesList.size(); j++) {
                            availableTimesStringTo.add((onlyAvailableTimesList.get(j)));
                        }
                    }
                    adapterTo.notifyDataSetChanged();
                    return;
                }

                Integer chosenValue = Integer.valueOf(onlyAvailableTimesList.get(i));

                int indexForChosenValueAllTimes = 0;

                for (int j = 0; j < allTimesInlcudingBookedMarked.size(); j++) {
                    if(allTimesInlcudingBookedMarked.get(j) == chosenValue){
                        indexForChosenValueAllTimes = j;
                    }
                }

                int temp = i+1;

                if(allTimesInlcudingBookedMarked.size() == indexForChosenValueAllTimes+1){
                    availableTimesStringTo.add( String.valueOf(allTimesInlcudingBookedMarked.get(allTimesInlcudingBookedMarked.size()-1)+1));
                }else{
                    if(allTimesInlcudingBookedMarked.get(indexForChosenValueAllTimes+1) == -1){
                        int index = allTimesInlcudingBookedMarked.indexOf(chosenValue);
                        availableTimesStringTo.add(String.valueOf(allTimesInlcudingBookedMarked.get(index)+1));
                    }
                    else {
                        int index = allTimesInlcudingBookedMarked.indexOf(chosenValue);
                        for (int j = index+1; j < allTimesInlcudingBookedMarked.size(); j++) {
                            if (allTimesInlcudingBookedMarked.get(j) == -1) {
                                break;
                            }
                            availableTimesStringTo.add(allTimesInlcudingBookedMarked.get(j).toString());
                        }
                    }
                }
                adapterTo.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    class Booked{
        public int from, to;
        Booked(int from, int to){
            this.from = from;
            this.to = to;
        }
    }
}
