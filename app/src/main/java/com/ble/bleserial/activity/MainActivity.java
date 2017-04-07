package com.ble.bleserial.activity;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ble.bleserial.adapter.DeviceListAdapter;
import com.ble.bleserial.modle.BleManagerImpl;
import com.ble.bleserial.R;
import com.clj.fastble.scan.ListScanCallback;
import com.dd.processbutton.iml.ActionProcessButton;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.zcw.togglebutton.ToggleButton;

import java.util.List;

public class MainActivity extends BaseActivity {
    private final static int TIME_OUT = 10000;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0x01;
    ;
    private TextView tvBleInfo;
    private ToggleButton tgb;
    private RecyclerView rvBondDevices, rvNewDevices;
    private ActionProcessButton btnScan;

    private boolean isOpenBle = false;
    private DeviceListAdapter bondAdapter, newAdapter;

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        tvBleInfo = (TextView) findViewById(R.id.tv_bluetooth_info);
        tgb = (ToggleButton) findViewById(R.id.tg_ble);

        rvBondDevices = (RecyclerView) findViewById(R.id.rv_bond_devices_list);
        bondAdapter = new DeviceListAdapter(this);
        rvBondDevices.setLayoutManager(new LinearLayoutManager(this));
        rvBondDevices.setAdapter(bondAdapter);

        rvNewDevices = (RecyclerView) findViewById(R.id.rv_new_devices_list);
        newAdapter = new DeviceListAdapter(this);
        rvNewDevices.setLayoutManager(new LinearLayoutManager(this));
        rvNewDevices.setAdapter(newAdapter);

        btnScan = (ActionProcessButton) findViewById(R.id.btn_scan);
        btnScan.setMode(ActionProcessButton.Mode.ENDLESS);
    }

    @Override
    protected void initListener() {
        tgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpenBle = !isOpenBle;
                BleManagerImpl.getInstant(MainActivity.this).enableBle(isOpenBle);
                updateView(isOpenBle);
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnScan.setProgress(10);
                BleManagerImpl.getInstant(MainActivity.this).scanDevice(new ListScanCallback(TIME_OUT) {
                    @Override
                    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                        super.onLeScan(device, rssi, scanRecord);
                        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                            bondAdapter.addDevice(device);
                        } else {
                            newAdapter.addDevice(device);
                        }
                    }

                    @Override
                    public void onDeviceFound(BluetoothDevice[] devices) {
                        btnScan.setProgress(100);
                    }

                });
            }
        });
    }

    @Override
    protected void initData() {
        AndPermission.with(this)
                .requestCode(100)
                .permission(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
                .send();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOpenBle = BleManagerImpl.getInstant(this).isBlueEnable();
        updateView(isOpenBle);
    }

    private void updateView(boolean isBleOpen) {
        if (isBleOpen) {
            btnScan.setClickable(true);
            tvBleInfo.setText(getString(R.string.ble_open));
            tgb.setToggleOn();
        } else {
            tvBleInfo.setText(getString(R.string.ble_close));
            tgb.setToggleOff();
            btnScan.setClickable(false);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。
            if (requestCode == 100) {
                // TODO 相应代码
                Log.e("Tag", "权限请求成功");
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。

            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, deniedPermissions)) {
                // 第一种：用默认的提示语。
                AndPermission.defaultSettingDialog(MainActivity.this, 100).show();

                // 第二种：用自定义的提示语。
                // AndPermission.defaultSettingDialog(this, REQUEST_CODE_SETTING)
                // .setTitle("权限申请失败")
                // .setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
                // .setPositiveButton("好，去设置")
                // .show();

                // 第三种：自定义dialog样式。
                // SettingService settingService =
                //    AndPermission.defineSettingDialog(this, REQUEST_CODE_SETTING);
                // 你的dialog点击了确定调用：
                // settingService.execute();
                // 你的dialog点击了取消调用：
                // settingService.cancel();
            }
        }
    };
}
