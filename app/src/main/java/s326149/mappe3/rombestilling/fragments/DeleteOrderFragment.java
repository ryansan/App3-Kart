package s326149.mappe3.rombestilling.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import s326149.mappe3.rombestilling.MainActivity;
import s326149.mappe3.rombestilling.R;
import s326149.mappe3.rombestilling.models.Building;
import s326149.mappe3.rombestilling.models.Order;
import s326149.mappe3.rombestilling.models.Room;
import s326149.mappe3.rombestilling.models.WebHandler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DeleteOrderFragment extends DialogFragment {
    private Order order;
    private Room room;
    Building building;

    private Button btnCancelDeleteOrder, btnDeleteOrder;
    private TextView txtDeleteOrderLabel;

    public WebHandler webHandler;

    public DeleteOrderFragment(Building building, Room room, Order order){
        this.order = order;
        this.room = room;
        this.building = building;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_delete_order, container,false);

        webHandler = ((MainActivity)getActivity()).webHandler;

        btnCancelDeleteOrder = view.findViewById(R.id.btnCancelDeleteOrder);
        btnDeleteOrder = view.findViewById(R.id.btnDeleteOrder);
        txtDeleteOrderLabel = view.findViewById(R.id.txtDeleteOrderLabel);

        txtDeleteOrderLabel.setText("Er du sikker på at du vil slette bestilling for " + room.getName() + " fra kl " + order.getHourFrom() + " til " + order.getHourTo()+"?");

        btnCancelDeleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelDelete();
            }
        });

        btnDeleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteOrder();
            }
        });


        return view;
    }

    public void deleteOrder(){
        webHandler.deleteOrder(order);
        for (int i = 0; i < building.getRooms().size(); i++) {
            if(building.getRooms().get(i).equals(room)){
                room.getOrders().remove(order);
            }
        }
        order = null;
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
