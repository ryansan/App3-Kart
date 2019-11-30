package com.example.kart.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.kart.MainActivity;
import com.example.kart.R;
import com.example.kart.fragments.models.Building;
import com.example.kart.fragments.models.Room;
import com.example.kart.fragments.models.WebHandler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DeleteRoomFragment extends DialogFragment {
    private Room room;
    Building building;

    private Button btnCancelDeleteRoom, btnDeleteRoom;
    private TextView txtDeleteRoomLabel;

    public WebHandler webHandler;

    public DeleteRoomFragment(Building building, Room room){
        this.room = room;
        this.building = building;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_delete_room, container,false);

        webHandler = ((MainActivity)getActivity()).webHandler;

        btnCancelDeleteRoom = view.findViewById(R.id.btnCancelDeleteRoom);
        btnDeleteRoom = view.findViewById(R.id.btnDeleteRoom);
        txtDeleteRoomLabel = view.findViewById(R.id.txtDeleteRoomLabel);

        txtDeleteRoomLabel.setText("Er du sikker på at du vil slette rom " + room.getName() + "?");

        btnCancelDeleteRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelDelete();
            }
        });

        btnDeleteRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRoom();
            }
        });


        return view;
    }

    public void deleteRoom(){
        webHandler.deleteRoom(room);
        building.getRooms().remove(room);
        room = null;
        getDialog().dismiss();
    }


    public void cancelDelete(){
        getDialog().dismiss();
    }


    /*@Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }*/

/*
    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }*/
}
