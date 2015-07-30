package com.httpeffectivemobilesolutions.timerinterface.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.httpeffectivemobilesolutions.timerinterface.R;
import com.httpeffectivemobilesolutions.timerinterface.model.TimerModel;

/**
 * Created by Gabriel on 7/27/2015.
 */
public class TimerContentFragment extends Fragment {

    private View mView;
    private Context mContext;
    private TimerModel mTimer;
    private Handler mHandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_content, null);
        mContext = mView.getContext();

        if(null != mTimer){
            populateView();
        }

        return mView;
    }

    /**
     * Called when the view is created or new timer is selected.
     */
    private void populateView(){
        if(null != mView && null != mTimer){
            TextView name = (TextView)mView.findViewById(R.id.name);
            final TextView relay = (TextView)mView.findViewById(R.id.relay_state);
            final TextView left = (TextView)mView.findViewById(R.id.time_left);
            TextView onTime = (TextView)mView.findViewById(R.id.on_time_value);
            TextView offTime = (TextView)mView.findViewById(R.id.off_time_value);

            ImageButton editName = (ImageButton)mView.findViewById(R.id.edit_name);
            ImageButton editOn = (ImageButton)mView.findViewById(R.id.edit_on_time);
            ImageButton editOff = (ImageButton)mView.findViewById(R.id.edit_off_time);

            name.setText(mTimer.getName());
            relay.setText(mTimer.getRealyState());
            left.setText(mTimer.getLeftTime());
            onTime.setText(mTimer.getOnTime());
            offTime.setText(mTimer.getOffTime());


            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    left.setText(mTimer.getLeftTime());
                    relay.setText(mTimer.getRealyState());
                    mHandler.postDelayed(this, 50);
                }
            }, 50);
        }
    }

    /**
     * Sets what timer model will be displayed on th content view
     * @param timer
     */
    public void setTimer(TimerModel timer){
        mTimer = timer;
        if(null != mView){
            populateView();
        }
    }

}
