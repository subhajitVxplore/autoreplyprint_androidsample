package com.caysn.autoreplyprint.sample;

import com.caysn.autoreplyprint.AutoReplyPrint;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

public class MainActivity extends Activity implements OnClickListener {

    private MainActivity activity;
    private LinearLayout layoutMain;
    private GRadioGroup rgPort;
    private RadioButton rbBT2, rbBT4, rbNET, rbUSB, rbCOM, rbWiFiP2P;
    private ComboBox cbxListBT2, cbxListBT4, cbxListNET, cbxListUSB, cbxListCOMPort, cbxListCOMBaud, cbxListWiFiP2P;
    private Button btnEnumPort, btnOpenPort, btnClosePort;
    private Button buttonCopyRecv, buttonClearRecv, buttonCopySend, buttonClearSend, buttonSendCmd, buttonSelectFile, buttonSendFile;
    private TextView textViewRecv;
    private ScrollView scrollViewRecv;
    private EditText editTextSendCmd, editTextFileName;

    private static final String TAG = "MainActivity";
    private static final int nBaudTable[] = {1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200, 230400, 256000, 500000, 750000, 1125000, 1500000};
    private static final int REQUEST_CHOOSEFILE = 1;
    private Uri selectedFileUri;

    private Pointer h = Pointer.NULL;

    AutoReplyPrint.CP_OnPortOpenedEvent_Callback opened_callback = new AutoReplyPrint.CP_OnPortOpenedEvent_Callback() {
        @Override
        public void CP_OnPortOpenedEvent(Pointer handle, String name, Pointer private_data) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Open Success", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
    AutoReplyPrint.CP_OnPortOpenFailedEvent_Callback openfailed_callback = new AutoReplyPrint.CP_OnPortOpenFailedEvent_Callback() {
        @Override
        public void CP_OnPortOpenFailedEvent(Pointer handle, String name, Pointer private_data) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Open Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
    AutoReplyPrint.CP_OnPortClosedEvent_Callback closed_callback = new AutoReplyPrint.CP_OnPortClosedEvent_Callback() {
        @Override
        public void CP_OnPortClosedEvent(Pointer h, Pointer private_data) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ClosePort();
                }
            });
        }
    };
    AutoReplyPrint.CP_OnPortWrittenEvent_Callback port_written_callback = new AutoReplyPrint.CP_OnPortWrittenEvent_Callback() {
        @Override
        public void CP_OnPortWrittenEvent(Pointer handle, Pointer buffer, int count, Pointer private_data) {
            byte[] data = buffer.getByteArray(0, count);
            System.out.println("Written " + count + " Bytes");
            String str = "";
            for (int i = 0; i < count; ++i) {
                str += String.format("%02X ", data[i]);
                if (i >= 15) {
                    str += "...";
                    break;
                }
            }
            System.out.println(str);
        }
    };
    AutoReplyPrint.CP_OnPortReceivedEvent_Callback port_received_callback = new AutoReplyPrint.CP_OnPortReceivedEvent_Callback() {
        @Override
        public void CP_OnPortReceivedEvent(Pointer handle, Pointer buffer, int count, Pointer private_data) {
            byte[] data = buffer.getByteArray(0, count);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < count; ++i) {
                if (i % 16 == 0)
                    sb.append("\r\n");
                sb.append(String.format("%02X ", data[i]));
            }
            final String str = sb.toString();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textViewRecv.append(str);
                    scrollViewRecv.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollViewRecv.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            });
        }
    };

    private void AddCallback() {
        AutoReplyPrint.INSTANCE.CP_Port_AddOnPortOpenedEvent(opened_callback, Pointer.NULL);
        AutoReplyPrint.INSTANCE.CP_Port_AddOnPortOpenFailedEvent(openfailed_callback, Pointer.NULL);
        AutoReplyPrint.INSTANCE.CP_Port_AddOnPortClosedEvent(closed_callback, Pointer.NULL);
        AutoReplyPrint.INSTANCE.CP_Port_AddOnPortWrittenEvent(port_written_callback, Pointer.NULL);
        AutoReplyPrint.INSTANCE.CP_Port_AddOnPortReceivedEvent(port_received_callback, Pointer.NULL);
    }

    private void RemoveCallback() {
        AutoReplyPrint.INSTANCE.CP_Port_RemoveOnPortOpenedEvent(opened_callback);
        AutoReplyPrint.INSTANCE.CP_Port_RemoveOnPortOpenFailedEvent(openfailed_callback);
        AutoReplyPrint.INSTANCE.CP_Port_RemoveOnPortClosedEvent(closed_callback);
        AutoReplyPrint.INSTANCE.CP_Port_RemoveOnPortWrittenEvent(port_written_callback);
        AutoReplyPrint.INSTANCE.CP_Port_RemoveOnPortReceivedEvent(port_received_callback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getResources().getString(R.string.app_name) + " " + AutoReplyPrint.INSTANCE.CP_Library_Version());

        activity = this;

        layoutMain = (LinearLayout) findViewById(R.id.layoutMain);
        rbBT2 = (RadioButton) findViewById(R.id.rbBT2);
        rbBT4 = (RadioButton) findViewById(R.id.rbBT4);
        rbNET = (RadioButton) findViewById(R.id.rbNET);
        rbUSB = (RadioButton) findViewById(R.id.rbUSB);
        rbCOM = (RadioButton) findViewById(R.id.rbCOM);
        rbWiFiP2P = (RadioButton) findViewById(R.id.rbWiFiP2P);
        cbxListBT2 = (ComboBox) findViewById(R.id.cbxLisbBT2);
        cbxListBT4 = (ComboBox) findViewById(R.id.cbxLisbBT4);
        cbxListNET = (ComboBox) findViewById(R.id.cbxListNET);
        cbxListUSB = (ComboBox) findViewById(R.id.cbxLisbUSB);
        cbxListCOMPort = (ComboBox) findViewById(R.id.cbxListCOMPort);
        cbxListCOMBaud = (ComboBox) findViewById(R.id.cbxListCOMBaud);
        cbxListWiFiP2P = (ComboBox) findViewById(R.id.cbxListWiFiP2P);
        btnEnumPort = (Button) findViewById(R.id.btnEnumPort);
        btnOpenPort = (Button) findViewById(R.id.btnOpenPort);
        btnClosePort = (Button) findViewById(R.id.btnClosePort);

        buttonCopyRecv = findViewById(R.id.buttonCopyRecv);
        buttonClearRecv = findViewById(R.id.buttonClearRecv);
        buttonCopySend = findViewById(R.id.buttonCopySend);
        buttonClearSend = findViewById(R.id.buttonClearSend);
        buttonSendCmd = findViewById(R.id.buttonSendCmd);
        buttonSelectFile = findViewById(R.id.buttonSelectFile);
        buttonSendFile = findViewById(R.id.buttonSendFile);
        textViewRecv = findViewById(R.id.textViewRecv);
        scrollViewRecv = findViewById(R.id.scrollViewRecv);
        editTextSendCmd = findViewById(R.id.editTextSendCmd);
        editTextFileName = findViewById(R.id.editTextFileName);

        for (int baud : nBaudTable) {
            cbxListCOMBaud.addString("" + baud);
        }
        cbxListCOMBaud.setText("115200");

        btnEnumPort.setOnClickListener(this);
        btnOpenPort.setOnClickListener(this);
        btnClosePort.setOnClickListener(this);
        buttonCopyRecv.setOnClickListener(this);
        buttonClearRecv.setOnClickListener(this);
        buttonCopySend.setOnClickListener(this);
        buttonClearSend.setOnClickListener(this);
        buttonSendCmd.setOnClickListener(this);
        buttonSelectFile.setOnClickListener(this);
        buttonSendFile.setOnClickListener(this);

        rgPort = new GRadioGroup(rbBT2, rbBT4, rbNET, rbUSB, rbCOM, rbWiFiP2P);
        rbBT2.performClick();
        btnEnumPort.performClick();
        RefreshUI();
        HandleIntent(getIntent());

        AddCallback();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        HandleIntent(intent);
    }

    @Override
    protected void onDestroy() {
        RemoveCallback();

        btnClosePort.performClick();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if ((requestCode == REQUEST_CHOOSEFILE) && (resultCode == RESULT_OK)) {
            if (resultData != null) {
                selectedFileUri = resultData.getData();
                editTextFileName.setText(TestUtils.GetUriName(selectedFileUri));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnEnumPort:
                EnumPort();
                break;

            case R.id.btnOpenPort:
                OpenPort();
                break;

            case R.id.btnClosePort:
                ClosePort();
                break;

            case R.id.buttonCopyRecv:
                CopyText(textViewRecv.getText().toString());
                break;

            case R.id.buttonClearRecv:
                textViewRecv.setText("");
                break;

            case R.id.buttonCopySend:
                CopyText(editTextSendCmd.getText().toString());
                break;

            case R.id.buttonClearSend:
                editTextSendCmd.setText("");
                break;

            case R.id.buttonSendCmd:
                SendCmd();
                break;

            case R.id.buttonSelectFile:
                SelectFile();
                break;

            case R.id.buttonSendFile:
                SendFile();
                break;
        }
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

    private void OpenPort() {
        DisableUI();
        final boolean rbBT2Checked = rbBT2.isChecked();
        final boolean rbBT4Checked = rbBT4.isChecked();
        final boolean rbNETChecked = rbNET.isChecked();
        final boolean rbUSBChecked = rbUSB.isChecked();
        final boolean rbCOMChecked = rbCOM.isChecked();
        final boolean rbWiFiP2PChecked = rbWiFiP2P.isChecked();
        final String strBT2Address = cbxListBT2.getText();
        final String strBT4Address = cbxListBT4.getText();
        final String strNETAddress = cbxListNET.getText();
        final String strUSBPort = cbxListUSB.getText();
        final String strCOMPort = cbxListCOMPort.getText();
        final int nComBaudrate = Integer.parseInt(cbxListCOMBaud.getText());
        final String strWiFiP2PAddress = cbxListWiFiP2P.getText();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (rbBT2Checked) {
                    h = AutoReplyPrint.INSTANCE.CP_Port_OpenBtSpp(strBT2Address, 0);
                } else if (rbBT4Checked) {
                    h = AutoReplyPrint.INSTANCE.CP_Port_OpenBtBle(strBT4Address, 0);
                } else if (rbNETChecked) {
                    h = AutoReplyPrint.INSTANCE.CP_Port_OpenTcp(null, strNETAddress, (short) 9100, 5000, 0);
                } else if (rbUSBChecked) {
                    h = AutoReplyPrint.INSTANCE.CP_Port_OpenUsb(strUSBPort, 0);
                } else if (rbCOMChecked) {
                    h = AutoReplyPrint.INSTANCE.CP_Port_OpenCom(strCOMPort, nComBaudrate, AutoReplyPrint.CP_ComDataBits_8, AutoReplyPrint.CP_ComParity_NoParity, AutoReplyPrint.CP_ComStopBits_One, AutoReplyPrint.CP_ComFlowControl_XonXoff, 0);
                } else if (rbWiFiP2PChecked) {
                    //if (AutoReplyPrint.INSTANCE.CP_Port_WiFiP2P_IsConnected())
                    //    AutoReplyPrint.INSTANCE.CP_Port_WiFiP2P_Disconnect();
                    int host_address = AutoReplyPrint.INSTANCE.CP_Port_WiFiP2P_Connect(strWiFiP2PAddress, 20000);
                    if (host_address != 0) {
                        String host_address_string = String.format("%d.%d.%d.%d", host_address&0x000000ffl, (host_address&0x0000ff00l)>>8, (host_address&0x00ff0000l)>>16, (host_address&0xff000000l)>>24);
                        h = AutoReplyPrint.INSTANCE.CP_Port_OpenTcp(null, host_address_string, (short) 9100, 5000, 0);
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RefreshUI();
                    }
                });
            }
        }).start();
    }

    private void ClosePort() {
        if (h != Pointer.NULL) {
            AutoReplyPrint.INSTANCE.CP_Port_Close(h);
            h = Pointer.NULL;
        }
        //if (AutoReplyPrint.INSTANCE.CP_Port_WiFiP2P_IsConnected())
        //    AutoReplyPrint.INSTANCE.CP_Port_WiFiP2P_Disconnect();
        RefreshUI();
    }

    private void DisableUI() {
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
        btnOpenPort.setEnabled(false);
        btnClosePort.setEnabled(false);
    }

    private void RefreshUI() {
        rbBT2.setEnabled(h == Pointer.NULL);
        rbBT4.setEnabled(h == Pointer.NULL);
        rbNET.setEnabled(h == Pointer.NULL);
        rbUSB.setEnabled(h == Pointer.NULL);
        rbCOM.setEnabled(h == Pointer.NULL);
        rbWiFiP2P.setEnabled(h == Pointer.NULL);
        cbxListBT2.setEnabled(h == Pointer.NULL);
        cbxListBT4.setEnabled(h == Pointer.NULL);
        cbxListNET.setEnabled(h == Pointer.NULL);
        cbxListUSB.setEnabled(h == Pointer.NULL);
        cbxListCOMPort.setEnabled(h == Pointer.NULL);
        cbxListCOMBaud.setEnabled(h == Pointer.NULL);
        cbxListWiFiP2P.setEnabled(h == Pointer.NULL);
        btnEnumPort.setEnabled(h == Pointer.NULL);
        btnOpenPort.setEnabled(h == Pointer.NULL);
        btnClosePort.setEnabled(h != Pointer.NULL);
        buttonSendCmd.setEnabled(h != Pointer.NULL);
        buttonSendFile.setEnabled(h != Pointer.NULL);

        int visibility = (h == Pointer.NULL) ? View.VISIBLE : View.GONE;
        if (!rbBT2.isChecked()) {
            rbBT2.setVisibility(visibility);
            cbxListBT2.setVisibility(visibility);
        }
        if (!rbBT4.isChecked()) {
            rbBT4.setVisibility(visibility);
            cbxListBT4.setVisibility(visibility);
        }
        if (!rbNET.isChecked()) {
            rbNET.setVisibility(visibility);
            cbxListNET.setVisibility(visibility);
        }
        if (!rbUSB.isChecked()) {
            rbUSB.setVisibility(visibility);
            cbxListUSB.setVisibility(visibility);
        }
        if (!rbCOM.isChecked()) {
            rbCOM.setVisibility(visibility);
            cbxListCOMPort.setVisibility(visibility);
            cbxListCOMBaud.setVisibility(visibility);
        }
        if (!rbWiFiP2P.isChecked()) {
            rbWiFiP2P.setVisibility(visibility);
            cbxListWiFiP2P.setVisibility(visibility);
        }
    }

    private void SendCmd() {
        String srcstr = editTextSendCmd.getText().toString();
        int srcstrlen = srcstr.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < srcstrlen; ++i) {
            char ch = srcstr.charAt(i);
            if (((ch >= '0') && (ch <= '9')) ||
                    ((ch >= 'a') && (ch <= 'f')) ||
                    ((ch >= 'A') && (ch <= 'F'))) {
                sb.append(ch);
            }
        }
        int sblen = sb.length();
        if ((sblen % 2 == 1) || (sblen < 2)) {
            Toast.makeText(this, "Invalid Cmd", Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] data = new byte[sblen / 2];
        for (int i = 0; i < sblen; i += 2) {
            String hexstr = sb.substring(i, i + 2);
            data[i / 2] = (byte) Integer.parseInt(hexstr, 16);
        }
        AutoReplyPrint.INSTANCE.CP_Port_Write(h, data, data.length, 10000);
    }

    private void CopyText(String text) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText(text, text);
        cm.setPrimaryClip(mClipData);
    }

    private void SelectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//无类型限制
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CHOOSEFILE);
    }

    private void SendFile() {
        final byte[] data = TestUtils.ReadFromFile(this, selectedFileUri);
        if ((data != null) && (data.length > 0)) {
            buttonSendFile.setEnabled(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    AutoReplyPrint.INSTANCE.CP_Port_Write(h, data, data.length, 100000);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RefreshUI();
                        }
                    });
                }
            }).start();
        }
    }

    private void HandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            String type = intent.getType();
            Log.i(TAG, "action: " + action + " type: " + type);
            selectedFileUri = intent.getData();
            editTextFileName.setText(TestUtils.GetUriName(selectedFileUri));
        }
    }
}
