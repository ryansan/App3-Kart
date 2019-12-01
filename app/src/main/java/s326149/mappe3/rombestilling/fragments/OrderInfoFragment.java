package s326149.mappe3.rombestilling.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import s326149.mappe3.rombestilling.R;
import s326149.mappe3.rombestilling.models.DialogDismisser;
import s326149.mappe3.rombestilling.models.Order;

import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class OrderInfoFragment extends DialogFragment {

    TextView txtOrderID, txtOrderName, txtOrderDescription, txtOrderDate, txtOrderMail, txtOrderFromAndTo;
    Button btnCancelShowOrder, btnBack;
    Order order;

    public OrderInfoFragment(Order order){
        this.order = order;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_show_order_info, container,false);

        txtOrderID = view.findViewById(R.id.txtOrderID);
        txtOrderName = view.findViewById(R.id.txtOrderName);
        txtOrderDescription = view.findViewById(R.id.txtOrderDescription);
        txtOrderDate = view.findViewById(R.id.txtOrderDate);
        txtOrderMail = view.findViewById(R.id.txtOrderMail);
        txtOrderFromAndTo = view.findViewById(R.id.txtOrderFromAndTo);

        txtOrderID.setText("ID: " + order.getID());
        txtOrderName.setText("Booket av: " + order.getName());
        txtOrderDescription.setText("Beskrivelse: " + order.getDescription());
        txtOrderMail.setText("Mail: " + order.getMail());
        txtOrderFromAndTo.setText("Kl "+order.getHourFrom() + " - "+ order.getHourTo());

        String myFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        txtOrderDate.setText("Dato: " + sdf.format(order.getDate()));

        btnCancelShowOrder = view.findViewById(R.id.btnCancelShowOrder);
        btnBack = view.findViewById(R.id.btnShowOrderrBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnCancelShowOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogDismisser.dismissAllDialogs(getFragmentManager());
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
