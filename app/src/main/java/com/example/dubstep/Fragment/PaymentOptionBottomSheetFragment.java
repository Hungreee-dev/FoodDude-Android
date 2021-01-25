package com.example.dubstep.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.dubstep.R;
import com.example.dubstep.ReferralActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.radiobutton.MaterialRadioButton;

public class PaymentOptionBottomSheetFragment extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    MaterialRadioButton materialRadioButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout_payment_mode,container,false);
        RadioGroup radioGroup = v.findViewById(R.id.radio_group_payment_option);
        v.findViewById(R.id.cod_radio_option).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialRadioButton = v.findViewById(R.id.cod_radio_option);
            }
        });

        v.findViewById(R.id.online_radio_option).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialRadioButton = v.findViewById(R.id.online_radio_option);
            }
        });

        v.findViewById(R.id.continue_order_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (materialRadioButton == null){
                    Toast.makeText(getActivity(), "No payment option selected", Toast.LENGTH_SHORT).show();
                }else if (materialRadioButton.getText().toString().contains("delivery")){
//                    cod option selected
                    mListener.optionSelected(0);
                    dismiss();
                } else if (materialRadioButton.getText().toString().contains("online")){
//                    online option selected
                    mListener.optionSelected(1);
                    dismiss();

                }

            }
        });
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;

        } catch (ClassCastException e){
            throw new ClassCastException(context.toString()+" must implement bottom sheet listener");
        }
    }

    public interface BottomSheetListener{
        void optionSelected(int option);
    }
}
