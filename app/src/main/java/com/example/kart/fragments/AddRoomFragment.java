package com.example.kart.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kart.MainActivity;
import com.example.kart.R;
import com.example.kart.models.Building;
import com.example.kart.models.DialogDismisser;
import com.example.kart.models.Room;
import com.example.kart.models.WebHandler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddRoomFragment extends DialogFragment {

    String buildingName;
    Button btnCancel, btnAddRoom, btnBack;
    Building building;
    EditText etAddRoomName, etAddRoomFloor, etAddRoomDescription;


    public WebHandler webHandler;

    public AddRoomFragment(Building building){
        this.building = building;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_room, container,false);

        webHandler = ((MainActivity)getActivity()).webHandler;


        etAddRoomName = view.findViewById(R.id.etAddRoomName);
        etAddRoomFloor = view.findViewById(R.id.etAddRoomFloor);
        etAddRoomDescription = view.findViewById(R.id.etAddRoomDescription);
        btnCancel = view.findViewById(R.id.btnCancelAddRoom);
        btnAddRoom = view.findViewById(R.id.btnAddRoom);
        btnBack = view.findViewById(R.id.btnAddRoomBack);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cancelAdding();
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogDismisser.dismissAllDialogs(getFragmentManager());
            }
        });

        btnAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etAddRoomName.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Navn er obligatorisk", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(etAddRoomDescription.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Beskrivelse er obligatorisk", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(etAddRoomFloor.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Etasje er obligatorisk", Toast.LENGTH_SHORT).show();
                    return;
                }


                Room room = new Room();
                room.setBuilding_ID(building.getID());
                room.setName(etAddRoomName.getText().toString());
                room.setFloor(Integer.parseInt(etAddRoomFloor.getText().toString()));
                room.setDescription(etAddRoomDescription.getText().toString());

                webHandler.postRoom(room);
                //webHandler.getAsyncBuildings();
                building.getRooms().add(room);

                dismiss();
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
}
