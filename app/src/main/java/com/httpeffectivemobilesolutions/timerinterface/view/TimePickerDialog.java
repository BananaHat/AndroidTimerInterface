package com.httpeffectivemobilesolutions.timerinterface.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.httpeffectivemobilesolutions.timerinterface.R;
import com.httpeffectivemobilesolutions.timerinterface.controller.MainActivity;
import com.httpeffectivemobilesolutions.timerinterface.model.TimerModel;

/**
 * Created by Gabriel on 8/5/2015.
 */
public class TimePickerDialog extends DialogFragment {

    private static final String TAG = DialogFragment.class.getSimpleName();

    private TimerModel mTimer;
    private boolean mOnOff;
    private String mCommand;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int min;
        int sec;
        int tenth;

        if(mOnOff){
            mCommand = getString(R.string.c_set_on);
            min = mTimer.getOnMin();
            sec = mTimer.getOnSec()/10;
            tenth = mTimer.getOnSec()%10;
        }else{
            mCommand = getString(R.string.c_set_off);
            min = mTimer.getOffMin();
            sec = mTimer.getOffSec()/10;
            tenth = mTimer.getOffSec()%10;
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getTag());
        View view = inflater.inflate(R.layout.dialog_time_picker, null);
        final NumberPicker minPicker = (NumberPicker)view.findViewById(R.id.minPicker);
        minPicker.setMinValue(0);
        minPicker.setMaxValue(90);
        minPicker.setValue(min);
        final NumberPicker secPicker = (NumberPicker)view.findViewById(R.id.secPicker);
        secPicker.setMinValue(0);
        secPicker.setMaxValue(59);
        secPicker.setValue(sec);
        final NumberPicker tenthsPicker = (NumberPicker)view.findViewById(R.id.tenthsPicker);
        tenthsPicker.setMinValue(0);
        tenthsPicker.setMaxValue(9);
        tenthsPicker.setValue(tenth);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(minPicker.getValue() == 0 && secPicker.getValue() == 0){
                    Toast.makeText(getActivity(), R.string.time_Picker_error, Toast.LENGTH_LONG).show();
                    return;
                }
                mCommand = mCommand + minPicker.getValue() + "&"
                        + (secPicker.getValue()) +""+ tenthsPicker.getValue();
                ((MainActivity) getActivity()).getNetworkController().sendCommand(
                        mTimer.getAddress(), mCommand);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setView(view);
        return builder.create();
    }

    public TimePickerDialog setArgs(TimerModel timer, boolean onOff){

        mTimer = timer;
        mOnOff = onOff;
        return this;

    }

}
