package s326149.mappe3.rombestilling.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import s326149.mappe3.rombestilling.MainActivity;
import s326149.mappe3.rombestilling.R;
import s326149.mappe3.rombestilling.models.Building;
import s326149.mappe3.rombestilling.models.WebHandler;
import com.google.android.gms.maps.model.LatLng;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DeleteBuildingFragment extends DialogFragment {
    private Building building;
    private EditText buildingName;
    private Button btnCancelDeleteBuilding, btnDeleteBuilding;
    private LatLng latLng;
    private TextView txtDeleteBuildingLabel;

    public WebHandler webHandler;

    public DeleteBuildingFragment(Building building){
        this.building = building;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_delete_building, container,false);

        webHandler = ((MainActivity)getActivity()).webHandler;

        btnCancelDeleteBuilding = view.findViewById(R.id.btnCancelDeleteBuilding);
        btnDeleteBuilding = view.findViewById(R.id.btnDeleteBuilding);
        txtDeleteBuildingLabel = view.findViewById(R.id.txtDeleteBuildingLabel);

        txtDeleteBuildingLabel.setText("Er du ikker på at du vil slette bygning i " + building.getAddress() + "?");

        btnCancelDeleteBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelDelete();
            }
        });

        btnDeleteBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBuilding();
            }
        });


        return view;
    }

    public void deleteBuilding(){
        webHandler.deleteBuilding(building);
        getDialog().dismiss();
    }


    public void cancelDelete(){
        getDialog().dismiss();
    }


    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

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
