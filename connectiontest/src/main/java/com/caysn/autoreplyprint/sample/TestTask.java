package com.caysn.autoreplyprint.sample;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.TextView;

import com.caysn.autoreplyprint.AutoReplyPrint;
import com.sun.jna.Pointer;

import java.util.ArrayList;
import java.util.List;

public class TestTask implements Runnable {

    private static final String TAG = "TestTask";

    // 端口部分参数
    public boolean bBT2;
    public boolean bBT4;
    public boolean bNET;
    public boolean bUSB;
    public boolean bCOM;
    public boolean bWiFiP2P;
    public String strBT2Address;
    public String strBT4Address;
    public String strNETAddress;
    public String strUSBPort;
    public String strCOMPort;
    public int nCOMBaudrate;
    public String strWiFiP2PAddress;

    // 界面部分参数
    public MainActivity activity;
    public TextView tvInfo;

    public int mTestCount;
    public int mTestInterval;

    public boolean bPrintText;
    public boolean bPrintQRCode;
    public boolean bPrintImage;
    public Bitmap mBitmap;

    public boolean mTaskEnabled;

    @Override
    public void run() {
        mTaskEnabled = true;
        ShowMessage("测试开始");
        List<SimpleTestResult> results = new ArrayList<SimpleTestResult>();
        for (int nTestIndex = 0; nTestIndex < mTestCount; ++nTestIndex) {
            if (!mTaskEnabled)
                break;
            Log.i(TAG, "正在执行第" + (nTestIndex + 1) + "次测试 ");
            ShowMessage("正在执行第" + (nTestIndex + 1) + "次测试 ");
            SimpleTestResult result = null;
            try {
                result = SimpleTest(nTestIndex + 1);
            } catch (Throwable tr) {
                tr.printStackTrace();
            }
            results.add(result);
            Log.i(TAG, "第" + (nTestIndex + 1) + "次测试 " + ((result != null) && (result.result_code == SimpleTestResult.Result_OK) ? "通过" : "失败"));
            ShowMessage("第" + (nTestIndex + 1) + "次测试 " + ((result != null) && (result.result_code == SimpleTestResult.Result_OK) ? "通过" : "失败"));
            if (mTestInterval > 0) {
                try {
                    Thread.sleep(mTestInterval);
                } catch (Throwable tr) {
                    tr.printStackTrace();
                }
            }
        }
        ShowMessage("测试结束" + "\r\n" + CaculateResults(results));
        EnableUI();
    }

    private Pointer OpenPort() {
        Pointer h = Pointer.NULL;
        if (bBT2) {
            h = AutoReplyPrint.INSTANCE.CP_Port_OpenBtSpp(strBT2Address, 0);
        } else if (bBT4) {
            h = AutoReplyPrint.INSTANCE.CP_Port_OpenBtBle(strBT4Address, 0);
        } else if (bNET) {
            h = AutoReplyPrint.INSTANCE.CP_Port_OpenTcp(null, strNETAddress, (short) 9100, 5000, 0);
        } else if (bUSB) {
            h = AutoReplyPrint.INSTANCE.CP_Port_OpenUsb(strUSBPort, 0);
        } else if (bCOM) {
            h = AutoReplyPrint.INSTANCE.CP_Port_OpenCom(strCOMPort, nCOMBaudrate, AutoReplyPrint.CP_ComDataBits_8, AutoReplyPrint.CP_ComParity_NoParity, AutoReplyPrint.CP_ComStopBits_One, AutoReplyPrint.CP_ComFlowControl_None, 0);
        } else if (bWiFiP2P) {
            int host_address = AutoReplyPrint.INSTANCE.CP_Port_WiFiP2P_Connect(strWiFiP2PAddress, 20000);
            if (host_address != 0) {
                String host_address_string = String.format("%d.%d.%d.%d", host_address & 0x000000ffl, (host_address & 0x0000ff00l) >> 8, (host_address & 0x00ff0000l) >> 16, (host_address & 0xff000000l) >> 24);
                h = AutoReplyPrint.INSTANCE.CP_Port_OpenTcp(null, host_address_string, (short) 9100, 5000, 0);
            }
        }
        Log.i(TAG, h == Pointer.NULL ? "OpenPort Failed" : "OpenPort Success");
        return h;
    }

    private String CaculateResults(List<SimpleTestResult> results) {
        long test_ok_count = 0;
        long test_ok_open_ms = 0;
        String open_failed_list = "";
        String begin_query_failed_list = "";
        String end_query_failed_list = "";
        for (int i = 0; i < results.size(); ++i) {
            SimpleTestResult result = results.get(i);
            if (result != null) {
                switch (result.result_code) {
                    case SimpleTestResult.Result_OK:
                        test_ok_count++;
                        test_ok_open_ms += result.open_ms;
                        break;
                    case SimpleTestResult.Result_OpenFailed:
                        open_failed_list += "" + (i + 1) + ",";
                        break;
                    case SimpleTestResult.Result_BeginQueryFailed:
                        begin_query_failed_list += "" + (i + 1) + ",";
                        break;
                    case SimpleTestResult.Result_EndQueryFailed:
                        end_query_failed_list += "" + (i + 1) + ",";
                        break;
                }
            } else {
                open_failed_list += "" + (i + 1) + ",";
            }
        }
        String msg = "";
        msg += "测试通过:" + test_ok_count + "\r\n";
        if (test_ok_count > 0)
            msg += "打开端口平均耗时:" + test_ok_open_ms / test_ok_count + "ms\r\n";
        if (test_ok_count < results.size()) {
            msg += "打开失败:" + open_failed_list + "\r\n";
            msg += "打印前查询失败:" + begin_query_failed_list + "\r\n";
            msg += "打印后查询失败:" + end_query_failed_list + "\r\n";
        }
        return msg;
    }

    class SimpleTestResult {

        public static final int Result_OK = 0;
        public static final int Result_OpenFailed = -1;
        public static final int Result_BeginQueryFailed = -2;
        public static final int Result_EndQueryFailed = -3;

        public int result_code = 0;
        public long open_ms = 0;
    }

    private SimpleTestResult SimpleTest(int nTestIndex) {

        SimpleTestResult result = new SimpleTestResult();

        long beginTime = System.currentTimeMillis();
        Pointer h = OpenPort();
        long endTime = System.currentTimeMillis();
        result.open_ms = endTime - beginTime;

        if (AutoReplyPrint.INSTANCE.CP_Port_IsOpened(h)) {
            if (AutoReplyPrint.INSTANCE.CP_Pos_QueryRTStatus(h, 3000) != 0) {
                AutoReplyPrint.INSTANCE.CP_Pos_SetAlignment(h, 0);
                AutoReplyPrint.INSTANCE.CP_Pos_PrintText(h, nTestIndex + " " + "Open   UsedTime:" + result.open_ms + "\r\n");
                AutoReplyPrint.INSTANCE.CP_Pos_SetAlignment(h, 1);
                if (bPrintText) {
                    for (int i = 0; i < 10; ++i) {
                        AutoReplyPrint.INSTANCE.CP_Pos_PrintText(h, "ABCDEFGHIJKLMNOPQRSTUVWXYZ\r\n");
                    }
                }
                if (bPrintQRCode) {
                    AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeUnitWidth(h, 6);
                    AutoReplyPrint.INSTANCE.CP_Pos_PrintQRCode(h, 10, AutoReplyPrint.CP_QRCodeECC_L, "http://www.caysn.com/");
                }
                if (bPrintImage) {
                    if (mBitmap != null) {
                        AutoReplyPrint.CP_Pos_PrintRasterImageFromData_Helper.PrintRasterImageFromBitmap(h,
                                mBitmap.getWidth(), mBitmap.getHeight(), mBitmap,
                                AutoReplyPrint.CP_ImageBinarizationMethod_ErrorDiffusion,
                                AutoReplyPrint.CP_ImageCompressionMethod_None);
                    }
                }
                if (AutoReplyPrint.INSTANCE.CP_Pos_QueryPrintResult(h, 10000)) {
                    result.result_code = SimpleTestResult.Result_OK;
                } else {
                    result.result_code = SimpleTestResult.Result_EndQueryFailed;
                }
            } else {
                result.result_code = SimpleTestResult.Result_BeginQueryFailed;
            }
            AutoReplyPrint.INSTANCE.CP_Port_Close(h);
        } else {
            result.result_code = SimpleTestResult.Result_OpenFailed;
        }

        return result;
    }

    private void ShowMessage(final String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvInfo.setText(msg);
            }
        });
    }

    private void EnableUI() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.EnableUI();
            }
        });
    }
}
