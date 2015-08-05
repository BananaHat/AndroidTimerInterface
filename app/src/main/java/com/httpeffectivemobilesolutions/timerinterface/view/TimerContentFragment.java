package com.httpeffectivemobilesolutions.timerinterface.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.httpeffectivemobilesolutions.timerinterface.R;
import com.httpeffectivemobilesolutions.timerinterface.controller.MainActivity;
import com.httpeffectivemobilesolutions.timerinterface.model.TimerModel;

import java.io.Serializable;

/**
 * Created by Gabriel on 7/27/2015.
 */
public class TimerContentFragment extends Fragment implements Serializable {

    private static final String TAG = TimerContentFragment.class.getSimpleName();
    private static final String TIMER_KEY = "TIMER_KEY";

    private MainActivity mActivity;
    private TimerModel mTimer;
    private View mView;
    private Context mContext;
    private Handler mHandler = new Handler();
    private TextView relay;
    private TextView left;
    private Runnable mTimeUpdater = new Runnable() {
        @Override
        public void run() {
            left.setText(mTimer.getLeftTime());
            relay.setText(mTimer.getRelayState());
            mHandler.postDelayed(this, 50);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mActivity = (MainActivity)getActivity();
        if(null != savedInstanceState && null == mTimer){
            mTimer = (TimerModel)savedInstanceState.getSerializable(TIMER_KEY);
            mActivity.setTimerContentFragment(this);
        }
        mView = inflater.inflate(R.layout.fragment_content, null);
        mContext = mView.getContext();

        if(null != mTimer){
            populateView();
        }

        return mView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(TIMER_KEY, mTimer);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mTimeUpdater);
        super.onDestroy();
    }

    /**
     * Called when the view is created or new timer is selected.
     */
    private void populateView(){
        if(null != mView && null != mTimer){
            TextView name = (TextView)mView.findViewById(R.id.name);
            relay = (TextView)mView.findViewById(R.id.relay_state);
            left = (TextView)mView.findViewById(R.id.time_left);
            TextView onTime = (TextView)mView.findViewById(R.id.on_time_value);
            TextView offTime = (TextView)mView.findViewById(R.id.off_time_value);

            ImageButton editName = (ImageButton)mView.findViewById(R.id.edit_name);
            ImageButton editOn = (ImageButton)mView.findViewById(R.id.edit_on_time);
            ImageButton editOff = (ImageButton)mView.findViewById(R.id.edit_off_time);

            name.setText(mTimer.getName());
            relay.setText(mTimer.getRelayState());
            left.setText(mTimer.getLeftTime());
            onTime.setText(mTimer.getOnTime());
            offTime.setText(mTimer.getOffTime());

            editName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditDialog();
                }
            });

            editOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment dialog = new TimePickerDialog().setArgs(mTimer, true);
                    dialog.show(getActivity().getSupportFragmentManager(), getString(R.string.set_on));
                }
            });

            editOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment dialog = new TimePickerDialog().setArgs(mTimer, false);
                    dialog.show(getActivity().getSupportFragmentManager(), getString(R.string.set_off));
                }
            });

            mHandler.postDelayed(mTimeUpdater, 50);
        }
    }

    /**
     * Sets what timer model will be displayed on th content view because I don't like passing args
     * as a bundle
     * @param timer
     */
    public void setTimer(TimerModel timer){
        mTimer = timer;
        if(null != mView){
            populateView();
        }
    }

    private void showEditDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.set_name);
        final EditText input = new EditText(mContext);
        builder.setView(input);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                mActivity.getNetworkController().sendCommand(mTimer.getAddress(),
                        getString(R.string.c_set_name) + text);            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

}
