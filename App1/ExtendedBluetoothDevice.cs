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
    class ExtendedBluetoothDevice
    {
        /* package */
        static int NO_RSSI = -1000;
        public BluetoothDevice device;
	    /** The name is not parsed by some Android devices, f.e. Sony Xperia Z1 with Android 4.3 (C6903). It needs to be parsed manually. */
	    public String name;
        public int rssi;
        public Boolean isBonded;

        public ExtendedBluetoothDevice(ScanResult scanResult)
        {
            this.device = scanResult.getDevice();
            this.name = scanResult.getScanRecord() != null ? scanResult.getScanRecord().getDeviceName() : null;
            this.rssi = scanResult.getRssi();
            this.isBonded = false;
        }

        public ExtendedBluetoothDevice(BluetoothDevice device)
        {
            this.device = device;
            this.name = device.getName();
            this.rssi = NO_RSSI;
            this.isBonded = true;
        }

        public Boolean matches(ScanResult scanResult)
        {
            return device.getAddress().equals(scanResult.getDevice().getAddress());
        }
    }
}