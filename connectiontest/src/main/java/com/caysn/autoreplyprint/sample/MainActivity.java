package com.caysn.autoreplyprint.sample;

import com.caysn.autoreplyprint.AutoReplyPrint;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

public class MainActivity extends Activity implements OnClickListener {

    private MainActivity activity;
    private LinearLayout layoutMain;
    private GRadioGroup rgPort;
    private RadioButton rbBT2, rbBT4, rbNET, rbUSB, rbCOM, rbWiFiP2P;
    private ComboBox cbxListBT2, cbxListBT4, cbxListNET, cbxListUSB, cbxListCOMPort, cbxListCOMBaud, cbxListWiFiP2P;
    private Button btnEnumPort;
    private EditText editTestCount, editTestInterval;
    private CheckBox checkBoxPrintText, checkBoxPrintQRCode, checkBoxPrintImage;
    private Button btnStartTest, btnStopTest;
    private TextView tvInfo;
    private TestTask testTask = new TestTask();

    private static final int nBaudTable[] = {1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200, 230400, 256000, 500000, 750000, 1125000, 1500000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getResources().getString(R.string.app_name) + " " + AutoReplyPrint.INSTANCE.CP_Library_Version());

        activity = this;

        layoutMain = findViewById(R.id.layoutMain);
        rbBT2 = findViewById(R.id.rbBT2);
        rbBT4 = findViewById(R.id.rbBT4);
        rbNET = findViewById(R.id.rbNET);
        rbUSB = findViewById(R.id.rbUSB);
        rbCOM = findViewById(R.id.rbCOM);
        rbWiFiP2P = findViewById(R.id.rbWiFiP2P);
        cbxListBT2 = findViewById(R.id.cbxLisbBT2);
        cbxListBT4 = findViewById(R.id.cbxLisbBT4);
        cbxListNET = findViewById(R.id.cbxListNET);
        cbxListUSB = findViewById(R.id.cbxLisbUSB);
        cbxListCOMPort = findViewById(R.id.cbxListCOMPort);
        cbxListCOMBaud = findViewById(R.id.cbxListCOMBaud);
        cbxListWiFiP2P = findViewById(R.id.cbxListWiFiP2P);
        btnEnumPort = findViewById(R.id.btnEnumPort);
        editTestCount = findViewById(R.id.editTestCount);
        editTestInterval = findViewById(R.id.editTestInterval);
        checkBoxPrintText = findViewById(R.id.checkBoxPrintText);
        checkBoxPrintQRCode = findViewById(R.id.checkBoxPrintQRCode);
        checkBoxPrintImage = findViewById(R.id.checkBoxPrintImage);
        btnStartTest = findViewById(R.id.btnStartTest);
        btnStopTest = findViewById(R.id.btnStopTest);
        tvInfo = findViewById(R.id.tvInfo);

        for (int baud : nBaudTable) {
            cbxListCOMBaud.addString("" + baud);
        }
        cbxListCOMBaud.setText("115200");

        btnEnumPort.setOnClickListener(this);
        btnStartTest.setOnClickListener(this);
        btnStopTest.setOnClickListener(this);

        rgPort = new GRadioGroup(rbBT2, rbBT4, rbNET, rbUSB, rbCOM, rbWiFiP2P);
        rbBT2.performClick();
        btnEnumPort.performClick();
        EnableUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEnumPort:
                EnumPort();
                break;
            case R.id.btnStartTest:
                StartTest();
                break;
            case R.id.btnStopTest:
                StopTest();
                break;
            default:
                break;
        }
    }

    private void StartTest() {
        testTask.bBT2 = rbBT2.isChecked();
        testTask.bBT4 = rbBT4.isChecked();
        testTask.bNET = rbNET.isChecked();
        testTask.bUSB = rbUSB.isChecked();
        testTask.bCOM = rbCOM.isChecked();
        testTask.bWiFiP2P = rbWiFiP2P.isChecked();
        testTask.strBT2Address = cbxListBT2.getText();
        testTask.strBT4Address = cbxListBT4.getText();
        testTask.strNETAddress = cbxListNET.getText();
        testTask.strUSBPort = cbxListUSB.getText();
        testTask.strCOMPort = cbxListCOMPort.getText();
        testTask.nCOMBaudrate = Integer.parseInt(cbxListCOMBaud.getText());
        testTask.strWiFiP2PAddress = cbxListWiFiP2P.getText();
        testTask.mTestCount = Integer.parseInt(editTestCount.getText().toString());
        testTask.mTestInterval = Integer.parseInt(editTestInterval.getText().toString());
        testTask.bPrintText = checkBoxPrintText.isChecked();
        testTask.bPrintQRCode = checkBoxPrintQRCode.isChecked();
        testTask.bPrintImage = checkBoxPrintImage.isChecked();
        testTask.mBitmap = TestUtils.getImageFromAssetsFile(this, "yellowmen.png");//TestUtils.scaleBitmapToWidth(TestUtils.getImageFromAssetsFile(this, "yellowmen.png"), 384);
        testTask.mBitmap = TestUtils.scaleImageToWidth(testTask.mBitmap, 384);
        testTask.activity = this;
        testTask.tvInfo = tvInfo;
        DisableUI();
        new Thread(testTask).start();
    }

    private void StopTest() {
        testTask.mTaskEnabled = false;
    }

    private void EnumCom() {
        cbxListCOMPort.setText("");
        cbxListCOMPort.clear();
        String[] devicePaths = AutoReplyPrint.CP_Port_EnumCom_Helper.EnumCom();
        if (devicePaths != null) {
            for (int i = 0; i < devicePaths.length; ++i) {
                String name = devicePaths[i];
                cbxListCOMPort.addString(name);
                String text = cbxListCOMPort.getText();
                if (text.trim().equals("")) {
                    text = name;
                    cbxListCOMPort.setText(text);
                }
            }
        }
    }

    private void EnumUsb() {
        cbxListUSB.setText("");
        cbxListUSB.clear();
        String[] devicePaths = AutoReplyPrint.CP_Port_EnumUsb_Helper.EnumUsb();
        if (devicePaths != null) {
            for (int i = 0; i < devicePaths.length; ++i) {
                String name = devicePaths[i];
                cbxListUSB.addString(name);
                String text = cbxListUSB.getText();
                if (text.trim().equals("")) {
                    text = name;
                    cbxListUSB.setText(text);
                }
            }
        }
    }

    boolean inNetEnum = false;

    private void EnumNet() {
        if (inNetEnum)
            return;
        inNetEnum = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                IntByReference cancel = new IntByReference(0);
                AutoReplyPrint.CP_OnNetPrinterDiscovered_Callback callback = new AutoReplyPrint.CP_OnNetPrinterDiscovered_Callback() {
                    @Override
                    public void CP_OnNetPrinterDiscovered(String local_ip, String disconvered_mac, final String disconvered_ip, String discovered_name, Pointer private_data) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!cbxListNET.getData().contains(disconvered_ip))
                                    cbxListNET.addString(disconvered_ip);
                                if (cbxListNET.getText().trim().equals("")) {
                                    cbxListNET.setText(disconvered_ip);
                                }
                            }
                        });
                    }
                };
                AutoReplyPrint.INSTANCE.CP_Port_EnumNetPrinter(3000, cancel, callback, null);
                inNetEnum = false;
            }
        }).start();
    }

    boolean inBtEnum = false;

    private void EnumBt() {
        if (inBtEnum)
            return;
        inBtEnum = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                IntByReference cancel = new IntByReference(0);
                AutoReplyPrint.CP_OnBluetoothDeviceDiscovered_Callback callback = new AutoReplyPrint.CP_OnBluetoothDeviceDiscovered_Callback() {
                    @Override
                    public void CP_OnBluetoothDeviceDiscovered(String device_name, final String device_address, Pointer private_data) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!cbxListBT2.getData().contains(device_address))
                                    cbxListBT2.addString(device_address);
                                if (cbxListBT2.getText().trim().equals("")) {
                                    cbxListBT2.setText(device_address);
                                }
                            }
                        });
                    }
                };
                AutoReplyPrint.INSTANCE.CP_Port_EnumBtDevice(12000, cancel, callback, null);
                inBtEnum = false;
            }
        }).start();
    }

    boolean inBleEnum = false;

    private void EnumBle() {
        if (inBleEnum)
            return;
        inBleEnum = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                IntByReference cancel = new IntByReference(0);
                AutoReplyPrint.CP_OnBluetoothDeviceDiscovered_Callback callback = new AutoReplyPrint.CP_OnBluetoothDeviceDiscovered_Callback() {
                    @Override
                    public void CP_OnBluetoothDeviceDiscovered(String device_name, final String device_address, Pointer private_data) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!cbxListBT4.getData().contains(device_address))
                                    cbxListBT4.addString(device_address);
                                if (cbxListBT4.getText().trim().equals("")) {
                                    cbxListBT4.setText(device_address);
                                }
                            }
                        });
                    }
                };
                AutoReplyPrint.INSTANCE.CP_Port_EnumBleDevice(20000, cancel, callback, null);
                inBleEnum = false;
            }
        }).start();
    }

    boolean inWiFiP2PEnum = false;

    private void EnumWiFiP2P() {
        if (inWiFiP2PEnum)
            return;
        inWiFiP2PEnum = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                IntByReference cancel = new IntByReference(0);
                AutoReplyPrint.CP_OnWiFiP2PDeviceDiscovered_Callback callback = new AutoReplyPrint.CP_OnWiFiP2PDeviceDiscovered_Callback() {
                    @Override
                    public void CP_OnWiFiP2PDeviceDiscovered(String device_name, final String device_address, String device_type, Pointer private_data) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!cbxListWiFiP2P.getData().contains(device_address))
                                    cbxListWiFiP2P.addString(device_address);
                                if (cbxListWiFiP2P.getText().trim().equals("")) {
                                    cbxListWiFiP2P.setText(device_address);
                                }
                            }
                        });
                    }
                };
                AutoReplyPrint.INSTANCE.CP_Port_EnumWiFiP2PDevice(5000, cancel, callback, null);
                inWiFiP2PEnum = false;
            }
        }).start();
    }

    private void EnumPort() {
        EnumCom();
        EnumUsb();
        EnumNet();
        EnumBt();
        EnumBle();
        EnumWiFiP2P();
    }

    public void DisableUI() {
        rbBT2.setEnabled(false);
        rbBT4.setEnabled(false);
        rbNET.setEnabled(false);
        rbUSB.setEnabled(false);
        rbCOM.setEnabled(false);
        rbWiFiP2P.setEnabled(false);
        cbxListBT2.setEnabled(false);
        cbxListBT4.setEnabled(false);
        cbxListNET.setEnabled(false);
        cbxListUSB.setEnabled(false);
        cbxListCOMPort.setEnabled(false);
        cbxListCOMBaud.setEnabled(false);
        cbxListWiFiP2P.setEnabled(false);
        btnEnumPort.setEnabled(false);
        editTestCount.setEnabled(false);
        editTestInterval.setEnabled(false);
        btnStartTest.setEnabled(false);
    }

    public void EnableUI() {
        rbBT2.setEnabled(true);
        rbBT4.setEnabled(true);
        rbNET.setEnabled(true);
        rbUSB.setEnabled(true);
        rbCOM.setEnabled(true);
        rbWiFiP2P.setEnabled(true);
        cbxListBT2.setEnabled(true);
        cbxListBT4.setEnabled(true);
        cbxListNET.setEnabled(true);
        cbxListUSB.setEnabled(true);
        cbxListCOMPort.setEnabled(true);
        cbxListCOMBaud.setEnabled(true);
        cbxListWiFiP2P.setEnabled(true);
        btnEnumPort.setEnabled(true);
        editTestCount.setEnabled(true);
        editTestInterval.setEnabled(true);
        btnStartTest.setEnabled(true);
    }
}

