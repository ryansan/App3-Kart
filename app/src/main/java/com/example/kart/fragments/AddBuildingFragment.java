package com.example.kart.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kart.MainActivity;
import com.example.kart.R;
import com.example.kart.models.Building;
import com.example.kart.models.WebHandler;
import com.example.kart.interfaces.AsyncAddressResponse;
import com.google.android.gms.maps.model.LatLng;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddBuildingFragment extends DialogFragment implements AsyncAddressResponse {

    private String address;
    private EditText buildingName;
    private Button btnCancel, btnAddBuilding;
    private LatLng latLng;
    private TextView txtAddBuildingAddress;

    public WebHandler webHandler;

    public AddBuildingFragment(String address, LatLng latLng){
        this.address = address;
        this.latLng = latLng;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_building, container,false);

        webHandler = ((MainActivity)getActivity()).webHandler;
        webHandler.addressDelegate = this;

        buildingName = view.findViewById(R.id.etAddBuildingName);
        btnCancel = view.findViewById(R.id.btnCancelAddBuilding);
        btnAddBuilding = view.findViewById(R.id.btnAddBuilding);
        txtAddBuildingAddress = view.findViewById(R.id.txtAddBuildingAddress);

        //address = getAddress();
        txtAddBuildingAddress.setText("Legger til et bygg i " + webHandler.address);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAdding();
            }
        });

        btnAddBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBuilding();
            }
        });



        return view;
    }

    public void addBuilding(){
        String name = buildingName.getText().toString();

        if(name.isEmpty()){
            name = address;
        }


        Building building = new Building();
        building.setName(name);
        building.setLatLng(latLng);
        building.setAddress(address);


        webHandler.postBuilding(building);
        webHandler.asyncBuildings.add(building);
        //WebHandler.getBuildings().add(building);

        getDialog().dismiss();
    }


    public void cancelAdding(){
        getDialog().dismiss();
    }

    /*
    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }*/

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }


    @Override
    public void processFinish(String output) {
        Log.d("PF:", "FINS");
        Log.d("Process Finsihed in add", output);
        this.address = output;
        txtAddBuildingAddress.setText("Legger til en bygning i " + this.address);
    }
}
