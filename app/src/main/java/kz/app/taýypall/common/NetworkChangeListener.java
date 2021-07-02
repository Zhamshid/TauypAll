package kz.app.taýypall.common;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.common.internal.service.Common;

import kz.app.taýypall.R;

public class NetworkChangeListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!BaseActivity.isConnectedToInternet(context)) {//Internet is not connected
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.no_connection_layout, null);
            builder.setView(view);

            Button btn_retry = view.findViewById(R.id.btn_retry);

            //show dialog
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCancelable(false);

            dialog.getWindow().setGravity(Gravity.CENTER);

            btn_retry.setOnClickListener(v -> {
                dialog.dismiss();
                onReceive(context, intent);
            });


        }

    }
}
