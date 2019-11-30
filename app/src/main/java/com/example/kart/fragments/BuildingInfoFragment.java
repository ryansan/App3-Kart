package com.example.kart.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kart.MainActivity;
import com.example.kart.R;
import com.example.kart.fragments.models.Building;
import com.example.kart.fragments.models.Room;
import com.example.kart.fragments.models.WebHandler;
import com.example.kart.fragments.models.interfaces.AsyncPostResponse;
import com.example.kart.fragments.models.interfaces.AsyncRepsonse;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class BuildingInfoFragment extends DialogFragment implements AsyncPostResponse {

    TextView buildingName;
    ListView lvRooms;
    Button btnCancel, btnAddRoom;

    ArrayList<String> roomNames = new ArrayList<>();
    ArrayAdapter<String> listAdapter;

    List<Building> buildings;
    Building building;
    public WebHandler webHandler;
    public BuildingInfoFragment(Building building, List<Building> buildings){
        this.building = building;
        this.buildings = buildings;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_building_info, container,false);

        webHandler = ((MainActivity)getActivity()).webHandler;
        webHandler.postDelegate = this;

        buildingName = view.findViewById(R.id.tvBuildingName);
        btnCancel = view.findViewById(R.id.btnCancelViewRooms);
        btnAddRoom = view.findViewById(R.id.btnAddRoom);
        lvRooms = view.findViewById(R.id.lvRooms);

        buildingName.setText("Rom i bygg: " + building.getName());

        getRoomNames();



        lvRooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getActivity(), "Clicked " + roomNames.get(i), Toast.LENGTH_SHORT).show();

                RoomOrdersFragment roomOrdersFragment = new RoomOrdersFragment(building, building.getRooms().get(i));
                roomOrdersFragment.show(getFragmentManager(),"RoomOrdersFragment");
            }
        });

        lvRooms.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                DeleteRoomFragment deleteRoomFragment = new DeleteRoomFragment(building, building.getRooms().get(i));

                FragmentManager fm = getFragmentManager();
                deleteRoomFragment.show(fm,"DeleteRoomFragment");

                fm.executePendingTransactions();
                deleteRoomFragment.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {

                        //Toast.makeText(getActivity(), "Delete ended", Toast.LENGTH_SHORT).show();
                        getRoomNames();
                    }
                });

                return true;
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cancelAdding();
                dismiss();
            }
        });

        btnAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddRoomFragment addRoomFragment = new AddRoomFragment(building);
                //addRoomFragment.show(getFragmentManager(),"AddRoomFragment");



                FragmentManager fm = getFragmentManager();
                
                addRoomFragment.show(fm,"AddRoomDialog");

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

                        getRoomNames();
//                        listAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        return view;
    }

    public void getRoomNames(){
        //List<Building> buildings = WebHandler.getBuildings();

        buildings = webHandler.asyncBuildings;

        roomNames.clear();

        for(Room r : building.getRooms()){
            roomNames.add(r.getName());
        }
/*

        for (Building b: webHandler.asyncBuildings) {
            if(b.getID()==building.getID()){
                for (Room r: b.getRooms()) {
                    roomNames.add(r.getName());
                }
            }
        }
*/


        listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, roomNames);

        lvRooms.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void postFinish(String output) {
        Log.d("PF:", "FINS");
        Log.d("Process Finsihed in pst", output);

        if(output.length() < 4) {

            this.building.getRooms().get(this.building.getRooms().size() - 1).setID(Integer.parseInt(output));
        }else{
            Log.d("post error", output);
        }
        this.buildings = webHandler.asyncBuildings;
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
}
