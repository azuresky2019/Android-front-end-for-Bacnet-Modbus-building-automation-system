package no.nordicsemi.android.nrftoolbox.proximity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.nordicsemi.android.log.LocalLogSession;
import no.nordicsemi.android.log.Logger;
import no.nordicsemi.android.nrftoolbox.R;
import no.nordicsemi.android.nrftoolbox.profile.BleManager;
import no.nordicsemi.android.nrftoolbox.profile.BleManagerCallbacks;
import no.nordicsemi.android.nrftoolbox.profile.BleProfileService;
import no.nordicsemi.android.nrftoolbox.scanner.DeviceListAdapter;
import no.nordicsemi.android.nrftoolbox.scanner.ExtendedBluetoothDevice;
import no.nordicsemi.android.nrftoolbox.scanner.ScannerFragment;

import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

import java.util.UUID;
import no.nordicsemi.android.nrftoolbox.profile.BleProfileServiceReadyActivity;
/**
 * Created by Administrator on 12/12/2016.
 */

public abstract class LightListActivity extends FragmentActivity {
//public class LightListActivity extends BleProfileServiceReadyActivity<ProximityService.ProximityBinder>{
    private final static String TAG = "LightListActivity";

    private final static String PARAM_UUID = "param_uuid";
    private final static long SCAN_DURATION = 20000;

    private final static int REQUEST_PERMISSION_REQ_CODE = 34; // any 8-bit number
    //private BleManager<? extends BleManagerCallbacks> mBleManager;
    private View mPermissionRationale;

    private BluetoothAdapter mBluetoothAdapter;
    private ScannerFragment.OnDeviceSelectedListener mListener;
    private DeviceListAdapter mAdapter;
    private final Handler mHandler = new Handler();
    private Button mScanButton;

    private ParcelUuid mUuid;

    private boolean mIsScanning = false;

    private SimpleAdapter adapter = null;
    private Map<String, Object> map = null;//new HashMap<String, Object>();
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private ListView lv;
    final ScannerFragment fragment = new ScannerFragment();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_device_selection);

        final BluetoothManager manager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();
       /*final Bundle args = getArguments();
        if (args.containsKey(PARAM_UUID)) {
            mUuid = args.getParcelable(PARAM_UUID);
        }*/

        //final BluetoothManager manager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        //mBluetoothAdapter = manager.getAdapter();

        lv = (ListView)findViewById(android.R.id.list);

        /*adapter = new SimpleAdapter(this,list,R.layout.device_list_row,
                new String[]{"img","name","address"},
                new int[]{R.id.rssi,R.id.name,R.id.address});*/

        //lv.setEmptyView(dialogView.findViewById(android.R.id.empty));*/
        //lv.setAdapter(mAdapter = new DeviceListAdapter(fragment.getActivity()));
        mAdapter = new DeviceListAdapter(this);

        //mListener = (ScannerFragment.OnDeviceSelectedListener) this;
        //mListener = new ScannerFragment.
        addBondedDevices();
        if (savedInstanceState == null)
            startScan();

        lv.setAdapter(mAdapter);
        stopScan();
        final ExtendedBluetoothDevice d = (ExtendedBluetoothDevice) mAdapter.getItem(0);
        mListener.onDeviceSelected(d.device, d.name);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                stopScan();
                final ExtendedBluetoothDevice d = (ExtendedBluetoothDevice) mAdapter.getItem(position);
                //mListener.onDeviceSelected(d.device, d.name);
                final Intent intent = new Intent(LightListActivity.this, ProximityActivity.class);
                //ProximityActivity.class.
                //intent.putExtra(ProximityActivity.EXTRAS_DEVICE_NAME,d.device);
                //intent.putExtra(ProximityActivity.EXTRAS_DEVICE_ADDRESS,d.name);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, final @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_REQ_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // We have been granted the Manifest.permission.ACCESS_COARSE_LOCATION permission. Now we may proceed with scanning.
                    startScan();
                } else {
                    mPermissionRationale.setVisibility(View.VISIBLE);
                    Toast.makeText(this, R.string.no_required_permission, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void startScan() {

      if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // When user pressed Deny and still wants to use this functionality, show the rationale
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) && mPermissionRationale.getVisibility() == View.GONE) {
                mPermissionRationale.setVisibility(View.VISIBLE);
                return;
            }

            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_REQ_CODE);
            return;
        }

        // Hide the rationale message, we don't need it anymore.
        if (mPermissionRationale != null)
            mPermissionRationale.setVisibility(View.GONE);

        mAdapter.clearDevices();
        //mScanButton.setText(R.string.scanner_action_cancel);

        final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        final ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(1000).setUseHardwareBatchingIfSupported(false).build();
        final List<ScanFilter> filters = new ArrayList<>();
        //filters.add(new ScanFilter.Builder().setServiceUuid(mUuid).build());
        scanner.startScan(filters, settings, scanCallback);

        mIsScanning = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mIsScanning) {
                    stopScan();
                }
            }
        }, SCAN_DURATION);
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(final int callbackType, final ScanResult result) {
            // do nothing
        }

        @Override
        public void onBatchScanResults(final List<ScanResult> results) {
            mAdapter.update(results);
        }

        @Override
        public void onScanFailed(final int errorCode) {
            // should never be called
        }
    };

    private void stopScan() {
        if (mIsScanning) {
           // mScanButton.setText(R.string.scanner_action_scan);

            final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
            scanner.stopScan(scanCallback);

            mIsScanning = false;
        }
    }

    private void addBondedDevices() {
        final Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        mAdapter.addBondedDevices(devices);
    }

    /*public void onDeviceSelected(final BluetoothDevice device, final String name) {

        mDeviceName = name;
        mDeviceNameView.setText(name != null ? name : getString(R.string.not_available));
        mConnectButton.setText(R.string.action_disconnect);

        //Logger.d(mLogSession, "Creating service...");
        final Intent service = new Intent(this, getServiceClass());
        service.putExtra(BleProfileService.EXTRA_DEVICE_ADDRESS, device.getAddress());
        //if (mLogSession != null)
        //    service.putExtra(BleProfileService.EXTRA_LOG_URI, mLogSession.getSessionUri());
        startService(service);
        //Logger.d(mLogSession, "Binding to the service...");
        bindService(service, mServiceConnection, 0);
    }*/
}
