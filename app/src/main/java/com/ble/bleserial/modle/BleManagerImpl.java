package com.ble.bleserial.modle;

import android.content.Context;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.scan.ListScanCallback;

/**
 * Created by Administrator on 2017/4/7.
 */

public class BleManagerImpl {


    private static BleManagerImpl instant = null;
    private BleManager bleManager = null;

    public BleManagerImpl(Context context) {
        this.bleManager = new BleManager(context);
    }

    public static BleManagerImpl getInstant(Context context) {
        if (instant == null) {
            instant = new BleManagerImpl(context);
        }
        return instant;
    }

    public boolean enableBle(boolean is) {
        if (bleManager == null && is == isBlueEnable())
            return is;
        if (is) {
            bleManager.enableBluetooth();
            return true;
        } else {
            bleManager.disableBluetooth();
            return false;
        }
    }

    public boolean isBlueEnable() {
        return bleManager != null && bleManager.isBlueEnable();
    }


    public void scanDevice(ListScanCallback listScanCallback) {
        if (bleManager == null)
            return;
        if (!bleManager.isInScanning()) {
            Log.e("TAG", "Start scan");
            bleManager.scanDevice(listScanCallback);
        }
    }

}
