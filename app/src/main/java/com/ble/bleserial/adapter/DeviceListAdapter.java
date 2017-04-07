package com.ble.bleserial.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ble.bleserial.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/7.
 */

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHodler> {

    private List<BluetoothDevice> mList = new ArrayList<>();
    private LayoutInflater inflater = null;

    public DeviceListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHodler(inflater.inflate(R.layout.item_device, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHodler holder, int position) {
        BluetoothDevice device = mList.get(position);
        holder.tvMac.setText(device.getAddress());
        holder.tvName.setText(device.getName());
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public synchronized void addDevice(BluetoothDevice device) {
        if (device != null && !mList.contains(device)) {
            mList.add(device);
            notifyItemInserted(mList.size() - 1);
        }
    }

    public class ViewHodler extends RecyclerView.ViewHolder {
        private TextView tvName, tvMac;

        public ViewHodler(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvMac = (TextView) itemView.findViewById(R.id.tv_mac);
        }
    }
}
