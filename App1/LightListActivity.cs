using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Android.Bluetooth;

namespace LightSwitch
{
    class LightListActivity: FragmentActivity
    {
        private static String TAG = "LightListActivity";

        private static String PARAM_UUID = "param_uuid";
        private static long SCAN_DURATION = 50000;

        private static int REQUEST_PERMISSION_REQ_CODE = 34; // any 8-bit number

        private View mPermissionRationale;

        private BluetoothAdapter mBluetoothAdapter;
        private ScannerFragment.OnDeviceSelectedListener mListener;
        private DeviceListAdapter mAdapter;
        private Handler mHandler = new Handler();
        private Button mScanButton;

        private ParcelUuid mUuid;

        private Boolean mIsScanning = false;

        private SimpleAdapter adapter = null;
        private Map<String, Object> map = null;//new HashMap<String, Object>();
        private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        private ListView lv;
         ScannerFragment fragment = new ScannerFragment();

        
        protected void onCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            SetContentView(Resource.Layout.fragment_device_selection);

            BluetoothManager manager = (BluetoothManager)this.GetSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = manager.getAdapter();
            /*final Bundle args = getArguments();
             if (args.containsKey(PARAM_UUID)) {
                 mUuid = args.getParcelable(PARAM_UUID);
             }*/

            //final BluetoothManager manager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
            //mBluetoothAdapter = manager.getAdapter();

            lv = (ListView)FindViewById<ListView>(android.R.id.list);

            /*adapter = new SimpleAdapter(this,list,R.layout.device_list_row,
                    new String[]{"img","name","address"},
                    new int[]{R.id.rssi,R.id.name,R.id.address});*/

            //lv.setEmptyView(dialogView.findViewById(android.R.id.empty));*/
            //lv.setAdapter(mAdapter = new DeviceListAdapter(fragment.getActivity()));
            mAdapter = new DeviceListAdapter(this);

            //mListener = (ScannerFragment.OnDeviceSelectedListener) this;
            addBondedDevices();
            if (savedInstanceState == null)
                startScan();

            lv.setAdapter(mAdapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id)
            {
                stopScan();
                final ExtendedBluetoothDevice d = (ExtendedBluetoothDevice)mAdapter.getItem(position);
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
    public void onRequestPermissionsResult( int requestCode,  @NonNull String[] permissions,  @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_PERMISSION_REQ_CODE:
                {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    {
                        // We have been granted the Manifest.permission.ACCESS_COARSE_LOCATION permission. Now we may proceed with scanning.
                        startScan();
                    }
                    else {
                        mPermissionRationale.setVisibility(View.VISIBLE);
                        Toast.makeText(this, R.string.no_required_permission, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
        }
    }

private void startScan()
{

    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
    {
        // When user pressed Deny and still wants to use this functionality, show the rationale
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) && mPermissionRationale.getVisibility() == View.GONE)
        {
            mPermissionRationale.setVisibility(View.VISIBLE);
            return;
        }

        requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, REQUEST_PERMISSION_REQ_CODE);
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
    final List< ScanFilter > filters = new ArrayList<>();
    //filters.add(new ScanFilter.Builder().setServiceUuid(mUuid).build());
    scanner.startScan(filters, settings, scanCallback);

    mIsScanning = true;
    mHandler.postDelayed(new Runnable() {
            @Override
            public void run()
{
    if (mIsScanning)
    {
        stopScan();
    }
}
        }, SCAN_DURATION);
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(final int callbackType, final ScanResult result)
{
    // do nothing
}

@Override
        public void onBatchScanResults(final List<ScanResult> results)
{
    mAdapter.update(results);
}

@Override
        public void onScanFailed(final int errorCode)
{
    // should never be called
}
    };

    private void stopScan()
{
    if (mIsScanning)
    {
        // mScanButton.setText(R.string.scanner_action_scan);

        final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        scanner.stopScan(scanCallback);

        mIsScanning = false;
    }
}

private void addBondedDevices()
{
    final Set< BluetoothDevice > devices = mBluetoothAdapter.getBondedDevices();
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
}