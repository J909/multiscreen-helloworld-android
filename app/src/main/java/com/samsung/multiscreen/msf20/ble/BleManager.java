package com.samsung.multiscreen.msf20.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * Scans for ble devices.
 * @author jonas
 */
public class BleManager {

    private static BleManager mInstance;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean isScanning;
    private Handler mHandler;
    private boolean mBtOn;
    private List<BluetoothDevice> mDevices = new ArrayList<BluetoothDevice>();

    private BluetoothAdapter.LeScanCallback mScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int i, byte[] bytes) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mDevices.add(device);
                }
            });
        }
    };

    private BroadcastReceiver mBtChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            if(btState == BluetoothAdapter.STATE_ON) {
                mBtOn = true;
                startScan();
            } else if (btState == BluetoothAdapter.STATE_OFF){
                mBtOn = false;
                stopScan();
            }
        }
    };

    public static synchronized BleManager getInstance(Context context) {
        if (mInstance != null) {
            return mInstance;
        }
        mInstance = new BleManager(context);
        return mInstance;
    }

    private BleManager(Context context) {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    private void startScan() {
        if(mBtOn && !isScanning) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isScanning = false;
                    mBluetoothAdapter.stopLeScan(mScanCallback);
                }
            }, 3000);
            isScanning = true;
            mBluetoothAdapter.startLeScan(mScanCallback);
        }
    }

    private void stopScan() {
        mHandler.removeCallbacksAndMessages(null);
        isScanning = false;
        mBluetoothAdapter.stopLeScan(mScanCallback);
    }

    public boolean init(final Context context) {
        if(mBtChangeReceiver == null && mBluetoothAdapter != null) {
            mHandler = new Handler(context.getMainLooper());
            context.registerReceiver(mBtChangeReceiver,
                    new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
            if (mBluetoothAdapter.isEnabled()) {
                mBtOn = true;
                startScan();
            }
            return true;
        }
        return false;
    }
}
