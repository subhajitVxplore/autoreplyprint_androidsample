package com.caysn.autoreplyprint.sample;

import android.app.Activity;
import android.graphics.Bitmap;

import com.caysn.autoreplyprint.AutoReplyPrint;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;

import java.text.SimpleDateFormat;
import java.util.Date;

class TestFunction {

    public static String[] testFunctionOrderedList = new String[]{
            "Test_Page_SampleTicket_58MM_1",
            "Test_Page_SampleTicket_80MM_1",

            "Test_Pos_QueryRTStatus",
            "Test_Pos_QueryPrintResult",

            "Test_Page_SetPageDrawDirection",
            "Test_Page_DrawRect",
            "Test_Page_DrawText",
            "Test_Page_DrawTextInUTF8",
            "Test_Page_DrawTextInGBK",
            "Test_Page_DrawTextInBIG5",
            "Test_Page_DrawTextInShiftJIS",
            "Test_Page_DrawTextInEUCKR",
            "Test_Page_DrawBarcode",
            "Test_Page_DrawBarcode_CODE128",
            "Test_Page_DrawQRCode",
            "Test_Page_DrawRasterImageFromBitmap",
    };

    public Activity ctx = null;

    void Test_Page_SampleTicket_58MM_1(Pointer h)
    {
        int paperWidth = 384;
        int paperHeight = 800;

        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, paperWidth, paperHeight);
        AutoReplyPrint.INSTANCE.CP_Pos_SetMultiByteMode(h);
        AutoReplyPrint.INSTANCE.CP_Pos_SetMultiByteEncoding(h, AutoReplyPrint.CP_MultiByteEncoding_UTF8);

        AutoReplyPrint.INSTANCE.CP_Pos_SetTextScale(h, 1, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 0, 160, new WString("CASH RECEIPT"));
        AutoReplyPrint.INSTANCE.CP_Pos_SetTextScale(h, 0, 0);
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 0, 60, new WString("销售期2015033"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, AutoReplyPrint.CP_Page_DrawAlignment_Right, 60, new WString("兑奖期2015033"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 0, 90, new WString("站号230902001"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, AutoReplyPrint.CP_Page_DrawAlignment_Right, 90, new WString("7639-A"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 0, 120, new WString("注数5"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, AutoReplyPrint.CP_Page_DrawAlignment_Right, 120, new WString("金额10.00"));

        AutoReplyPrint.INSTANCE.CP_Pos_SetTextLineHeight(h, 60);
        AutoReplyPrint.INSTANCE.CP_Pos_SetTextScale(h, 0, 1);
        AutoReplyPrint.INSTANCE.CP_Pos_SetTextUnderline(h, 2);
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 0, 160, new WString(" A: 02  07  10  17  20  21  25\r\n A: 02  07  10  17  20  21  25\r\n A: 02  07  10  17  20  21  25\r\n A: 02  07  10  17  20  21  25\r\n A: 02  07  10  17  20  21  25\r\n"));
        AutoReplyPrint.INSTANCE.CP_Pos_SetTextScale(h, 0, 0);
        AutoReplyPrint.INSTANCE.CP_Pos_SetTextUnderline(h, 0);
        AutoReplyPrint.INSTANCE.CP_Pos_SetTextLineHeight(h, 30);

        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeHeight(h, 60);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeUnitWidth(h, 3);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeReadableTextPosition(h, AutoReplyPrint.CP_Pos_BarcodeTextPrintPosition_BelowBarcode);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, 0, 460, AutoReplyPrint.CP_Pos_BarcodeType_CODE128, "1234567890");
        AutoReplyPrint.INSTANCE.CP_Page_DrawQRCode(h, 284, 460, 0, AutoReplyPrint.CP_QRCodeECC_L, "1234567890");

        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);

        AutoReplyPrint.INSTANCE.CP_Pos_FeedAndHalfCutPaper(h);
        AutoReplyPrint.INSTANCE.CP_Pos_KickOutDrawer(h, 0, 100, 100);
        AutoReplyPrint.INSTANCE.CP_Pos_KickOutDrawer(h, 1, 100, 100);
        AutoReplyPrint.INSTANCE.CP_Pos_Beep(h, 1, 500);

        {
            Test_Pos_QueryPrintResult(h);
        }
    }

    void Test_Page_SampleTicket_80MM_1(Pointer h)
    {
        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 576, 200);
        AutoReplyPrint.INSTANCE.CP_Pos_SetMultiByteMode(h);
        AutoReplyPrint.INSTANCE.CP_Pos_SetMultiByteEncoding(h, AutoReplyPrint.CP_MultiByteEncoding_UTF8);

        AutoReplyPrint.INSTANCE.CP_Pos_SetTextScale(h, 1, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawText(h, AutoReplyPrint.CP_Page_DrawAlignment_HCenter, 0, "Print Store");
        AutoReplyPrint.INSTANCE.CP_Pos_SetTextScale(h, 7, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawText(h, AutoReplyPrint.CP_Page_DrawAlignment_HCenter, 48,"______");
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, AutoReplyPrint.CP_Page_DrawAlignment_HCenter, 100, AutoReplyPrint.CP_Pos_BarcodeType_CODE128, "No.201804190001");

        AutoReplyPrint.INSTANCE.CP_Page_SetPageArea(h, 0, 200, 576, 1400);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 576, 1400, 1, 1);
        AutoReplyPrint.INSTANCE.CP_Page_SetPageDrawDirection(h, AutoReplyPrint.CP_Page_DrawDirection_TopToBottom);

        int y = 0;
        AutoReplyPrint.INSTANCE.CP_Pos_SetTextScale(h, 1, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, AutoReplyPrint.CP_Page_DrawAlignment_HCenter, y, new WString("Print物流（测试）托运单"));

        y += 64;
        AutoReplyPrint.INSTANCE.CP_Pos_SetTextScale(h, 0, 0);
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 0,   y, new WString("发站：厦门总部"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 300, y, new WString("到站：广州 0539-7825336"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 600, y, new WString("托运日期：2016-05-24"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 900,y, new WString("运单号：601052400032"));

        y += 32;
        AutoReplyPrint.INSTANCE.CP_Page_DrawRect(h, 0, y - 5, 1400, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 0,   y, new WString("收货人"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 300, y, new WString("电话：15000353189"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 600, y, new WString("运费：提付10"));

        y += 32;
        AutoReplyPrint.INSTANCE.CP_Page_DrawRect(h, 0, y - 5, 1400, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 0,   y, new WString("发货人"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 300, y, new WString("电话：15000353189"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 600, y, new WString("会员号"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 900, y, new WString("代收款：1000"));

        y += 32;
        AutoReplyPrint.INSTANCE.CP_Page_DrawRect(h, 0, y - 5, 1400, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 0,   y, new WString("货物名称"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 150, y, new WString("件数"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 300, y, new WString("重量"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 450, y, new WString("体积"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 600, y, new WString("保价额"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 750, y, new WString("保价费"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 900, y, new WString("交货方式"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 1050, y, new WString("自提"));

        y += 32;
        AutoReplyPrint.INSTANCE.CP_Page_DrawRect(h, 0, y - 5, 1400, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 0,   y, new WString("配件"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 900, y, new WString("送货费"));

        y += 32;
        AutoReplyPrint.INSTANCE.CP_Page_DrawRect(h, 0, y - 5, 1400, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 0,   y, new WString("托运地址"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 150, y, new WString("运河路高架桥南张营中心街东首"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 900, y, new WString("预付运费"));

        y += 32;
        AutoReplyPrint.INSTANCE.CP_Page_DrawRect(h, 0, y - 5, 1400, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 0,   y, new WString("到站地址"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 150, y, new WString("金兰物流E7区11号"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 900, y, new WString("提付合计"));

        y += 32;
        AutoReplyPrint.INSTANCE.CP_Page_DrawRect(h, 0, y - 5, 1400, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 0,   y, new WString("备注"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 900, y, new WString("返款"));

        y += 32;
        AutoReplyPrint.INSTANCE.CP_Page_DrawRect(h, 0, y - 5, 1400, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 0,   y, new WString("声明：1、不得虚假报货名 2、不得虚假报货名 3、不得虚假报货名 4、不得虚假报货名 5、不得虚假报货名 6、不得虚假报货名 7、不得虚假报货名 8、不得虚假报货名 1、不得虚假报货名 2、不得虚假报货名 3、不得虚假报货名 4、不得虚假报货名 5、不得虚假报货名 6、不得虚假报货名 7、不得虚假报货名 8、不得虚假报货名 1、不得虚假报货名 2、不得虚假报货名 3、不得虚假报货名 4、不得虚假报货名 5、不得虚假报货名 6、不得虚假报货名 7、不得虚假报货名 8、不得虚假报货名 1、不得虚假报货名 2、不得虚假报货名 3、不得虚假报货名 4、不得虚假报货名 5、不得虚假报货名 6、不得虚假报货名 7、不得虚假报货名 8、不得虚假报货名"));

        y = 530;
        AutoReplyPrint.INSTANCE.CP_Page_DrawRect(h, 0, y - 5, 1400, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 0,   y, new WString("服务查询：2379911"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 300, y, new WString("发货人签名"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 600, y, new WString("第一联"));
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 900, y, new WString("制单"));

        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);

        AutoReplyPrint.INSTANCE.CP_Pos_KickOutDrawer(h, 0, 100, 100);
        AutoReplyPrint.INSTANCE.CP_Pos_KickOutDrawer(h, 1, 100, 100);
        AutoReplyPrint.INSTANCE.CP_Pos_Beep(h, 1, 500);
        AutoReplyPrint.INSTANCE.CP_Pos_FeedAndHalfCutPaper(h);

        {
            Test_Pos_QueryPrintResult(h);
        }
    }

    void Test_Pos_QueryRTStatus(Pointer h)
    {
        int status = AutoReplyPrint.INSTANCE.CP_Pos_QueryRTStatus(h, 3000);
        if (status != 0) {
            String s = "";
            s += String.format("RTStatus: %02x %02x %02x %02x\r\n", status & 0xff, (status >> 8) & 0xff, (status >> 16) & 0xff, (status >> 24) & 0xff);
            if (AutoReplyPrint.CP_RTSTATUS_Helper.CP_RTSTATUS_COVERUP(status))
                s += "[Cover Up]";
            if (AutoReplyPrint.CP_RTSTATUS_Helper.CP_RTSTATUS_NOPAPER(status))
                s += "[No Paper]";
            TestUtils.showMessageOnUiThread(ctx, s);
        } else {
            TestUtils.showMessageOnUiThread(ctx, "Query failed");
        }
    }

    void Test_Pos_QueryPrintResult(Pointer h)
    {
        boolean result = AutoReplyPrint.INSTANCE.CP_Pos_QueryPrintResult(h, 30000);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Print failed");
        else
            TestUtils.showMessageOnUiThread(ctx, "Print Success");
    }

    void Test_Page_SetPageDrawDirection(Pointer h)
    {
        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_SetPageDrawDirection(h, AutoReplyPrint.CP_Page_DrawDirection_LeftToRight);
        AutoReplyPrint.INSTANCE.CP_Page_DrawText(h, 0, 0, "LeftToRight");
        AutoReplyPrint.INSTANCE.CP_Page_SetPageDrawDirection(h, AutoReplyPrint.CP_Page_DrawDirection_RightToLeft);
        AutoReplyPrint.INSTANCE.CP_Page_DrawText(h, 0, 0, "RightToLeft");
        AutoReplyPrint.INSTANCE.CP_Page_SetPageDrawDirection(h, AutoReplyPrint.CP_Page_DrawDirection_TopToBottom);
        AutoReplyPrint.INSTANCE.CP_Page_DrawText(h, 0, 0, "TopToBottom");
        AutoReplyPrint.INSTANCE.CP_Page_SetPageDrawDirection(h, AutoReplyPrint.CP_Page_DrawDirection_BottomToTop);
        AutoReplyPrint.INSTANCE.CP_Page_DrawText(h, 0, 0, "BottomToTop");
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        boolean result = AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Page_DrawRect(Pointer h)
    {
        // 10,10
        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawRect(h, 10, 10, 100, 100, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawRect(h, 20, 20, 80, 80, 0);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);

        // left,top
        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawRect(h, -1, -1, 100, 20, 1);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);

        // left,vcenter
        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawRect(h, -1, -2, 100, 20, 1);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);

        // left,bottom
        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawRect(h, -1, -3, 100, 20, 1);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);

        // hcenter,top
        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawRect(h, -2, -1, 100, 20, 1);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);

        // hcenter,vcenter
        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawRect(h, -2, -2, 100, 20, 1);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);

        // hcenter,bottom
        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawRect(h, -2, -3, 100, 20, 1);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);

        // right,top
        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawRect(h, -3, -1, 100, 20, 1);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);

        // right,vcenter
        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawRect(h, -3, -2, 100, 20, 1);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);

        // right,bottom
        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawRect(h, -3, -3, 100, 20, 1);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);
    }

    void Test_Page_DrawText(Pointer h)
    {
        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawText(h, 0, 0, "12345678901234567890");
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        boolean result = AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Page_DrawTextInUTF8(Pointer h)
    {
        WString str = new WString(
                "1234567890\r\n" +
                        "abcdefghijklmnopqrstuvwxyz\r\n" +
                        "ΑΒΓΔΕΖΗΘΙΚ∧ΜΝΞΟ∏Ρ∑ΤΥΦΧΨΩ\r\n" +
                        "αβγδεζηθικλμνξοπρστυφχψω\r\n" +
                        "你好，欢迎使用！\r\n" +
                        "你號，歡迎使用！\r\n" +
                        "梦を见る事が出来なければ\r\n未来を変える事は出来ません\r\n" +
                        "왕관을 쓰려는자\r\n그 무게를 견뎌라\r\n");

        AutoReplyPrint.INSTANCE.CP_Pos_SetMultiByteMode(h);
        AutoReplyPrint.INSTANCE.CP_Pos_SetMultiByteEncoding(h, AutoReplyPrint.CP_MultiByteEncoding_UTF8);

        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInUTF8(h, 0, 0, str);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        boolean result = AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Page_DrawTextInGBK(Pointer h)
    {
        WString str = new WString("1234567890\r\nabcdefghijklmnopqrstuvwxyz\r\n你好，欢迎使用！\r\n你號，歡迎使用！\r\n");

        AutoReplyPrint.INSTANCE.CP_Pos_SetMultiByteMode(h);
        AutoReplyPrint.INSTANCE.CP_Pos_SetMultiByteEncoding(h, AutoReplyPrint.CP_MultiByteEncoding_GBK);

        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInGBK(h, 0, 0, str);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        boolean result = AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Page_DrawTextInBIG5(Pointer h)
    {
        WString str = new WString("1234567890\r\nabcdefghijklmnopqrstuvwxyz\r\n你號，歡迎使用！\r\n");

        AutoReplyPrint.INSTANCE.CP_Pos_SetMultiByteMode(h);
        AutoReplyPrint.INSTANCE.CP_Pos_SetMultiByteEncoding(h, AutoReplyPrint.CP_MultiByteEncoding_BIG5);

        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInBIG5(h, 0, 0, str);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        boolean result = AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Page_DrawTextInShiftJIS(Pointer h)
    {
        WString str = new WString(
                "1234567890\r\n" +
                        "abcdefghijklmnopqrstuvwxyz\r\n" +
                        "こんにちは\r\n");

        AutoReplyPrint.INSTANCE.CP_Pos_SetMultiByteMode(h);
        AutoReplyPrint.INSTANCE.CP_Pos_SetMultiByteEncoding(h, AutoReplyPrint.CP_MultiByteEncoding_ShiftJIS);

        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInShiftJIS(h, 0, 0, str);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        boolean result = AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Page_DrawTextInEUCKR(Pointer h)
    {
        WString str = new WString(
                "1234567890\r\n" +
                        "abcdefghijklmnopqrstuvwxyz\r\n" +
                        "왕관을 쓰려는자\r\n" +
                        "그 무게를 견뎌라\r\n");

        AutoReplyPrint.INSTANCE.CP_Pos_SetMultiByteMode(h);
        AutoReplyPrint.INSTANCE.CP_Pos_SetMultiByteEncoding(h, AutoReplyPrint.CP_MultiByteEncoding_EUCKR);

        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Page_DrawTextInEUCKR(h, 0, 0, str);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        boolean result = AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Page_DrawBarcode(Pointer h)
    {
        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 1000);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeUnitWidth(h, 2);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeHeight(h, 60);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeReadableTextFontType(h, 0);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeReadableTextPosition(h, AutoReplyPrint.CP_Pos_BarcodeTextPrintPosition_BelowBarcode);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, 0, 0, AutoReplyPrint.CP_Pos_BarcodeType_UPCA, "01234567890");
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, 0, 100, AutoReplyPrint.CP_Pos_BarcodeType_UPCE, "123456");
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, 0, 200, AutoReplyPrint.CP_Pos_BarcodeType_EAN13, "123456789012");
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, 0, 300, AutoReplyPrint.CP_Pos_BarcodeType_EAN8, "1234567");
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, 0, 400, AutoReplyPrint.CP_Pos_BarcodeType_CODE39, "123456");
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, 0, 500, AutoReplyPrint.CP_Pos_BarcodeType_ITF, "123456");
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, 0, 600, AutoReplyPrint.CP_Pos_BarcodeType_CODEBAR, "A123456A");
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, 0, 700, AutoReplyPrint.CP_Pos_BarcodeType_CODE93, "123456");
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, 0, 800, AutoReplyPrint.CP_Pos_BarcodeType_CODE128, "No.123456");
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);
    }

    void Test_Page_DrawBarcode_CODE128(Pointer h)
    {
        String str = "No.123456";

        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeUnitWidth(h, 2);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeHeight(h, 60);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeReadableTextFontType(h, 0);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeReadableTextPosition(h, AutoReplyPrint.CP_Pos_BarcodeTextPrintPosition_BelowBarcode);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, 10, 100, AutoReplyPrint.CP_Pos_BarcodeType_CODE128, str);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, 10, 200, AutoReplyPrint.CP_Pos_BarcodeType_CODE128, str);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, 10, 300, AutoReplyPrint.CP_Pos_BarcodeType_CODE128, str);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);

        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeUnitWidth(h, 2);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeHeight(h, 60);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeReadableTextFontType(h, 0);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeReadableTextPosition(h, AutoReplyPrint.CP_Pos_BarcodeTextPrintPosition_None);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, -1, -1, AutoReplyPrint.CP_Pos_BarcodeType_CODE128, str);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, -2, -2, AutoReplyPrint.CP_Pos_BarcodeType_CODE128, str);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, -3, -3, AutoReplyPrint.CP_Pos_BarcodeType_CODE128, str);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);

        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeUnitWidth(h, 2);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeHeight(h, 60);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeReadableTextFontType(h, 0);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeReadableTextPosition(h, AutoReplyPrint.CP_Pos_BarcodeTextPrintPosition_AboveAndBelowBarcode);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, -1, -1, AutoReplyPrint.CP_Pos_BarcodeType_CODE128, str);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, -2, -2, AutoReplyPrint.CP_Pos_BarcodeType_CODE128, str);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, -3, -3, AutoReplyPrint.CP_Pos_BarcodeType_CODE128, str);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);

        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeUnitWidth(h, 2);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeHeight(h, 60);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeReadableTextFontType(h, 0);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeReadableTextPosition(h, AutoReplyPrint.CP_Pos_BarcodeTextPrintPosition_AboveBarcode);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, -1, -1, AutoReplyPrint.CP_Pos_BarcodeType_CODE128, str);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, -2, -2, AutoReplyPrint.CP_Pos_BarcodeType_CODE128, str);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, -3, -3, AutoReplyPrint.CP_Pos_BarcodeType_CODE128, str);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);

        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeUnitWidth(h, 2);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeHeight(h, 60);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeReadableTextFontType(h, 0);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeReadableTextPosition(h, AutoReplyPrint.CP_Pos_BarcodeTextPrintPosition_BelowBarcode);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, -1, -1, AutoReplyPrint.CP_Pos_BarcodeType_CODE128, str);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, -2, -2, AutoReplyPrint.CP_Pos_BarcodeType_CODE128, str);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBarcode(h, -3, -3, AutoReplyPrint.CP_Pos_BarcodeType_CODE128, str);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        boolean result = AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Page_DrawQRCode(Pointer h)
    {
        String str = "Hello";

        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeUnitWidth(h, 4);
        AutoReplyPrint.INSTANCE.CP_Page_DrawQRCode(h, -1, -1, 0, AutoReplyPrint.CP_QRCodeECC_L, str);
        AutoReplyPrint.INSTANCE.CP_Page_DrawQRCode(h, -2, -2, 0, AutoReplyPrint.CP_QRCodeECC_L, str);
        AutoReplyPrint.INSTANCE.CP_Page_DrawQRCode(h, -3, -3, 0, AutoReplyPrint.CP_QRCodeECC_L, str);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);

        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 1000);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 1000, 2, 1);
        AutoReplyPrint.INSTANCE.CP_Pos_SetBarcodeUnitWidth(h, 4);
        AutoReplyPrint.INSTANCE.CP_Page_DrawQRCode(h, 10, 10, 10, AutoReplyPrint.CP_QRCodeECC_L, str);
        AutoReplyPrint.INSTANCE.CP_Page_DrawQRCode(h, 10, 300, 10, AutoReplyPrint.CP_QRCodeECC_L, str);
        AutoReplyPrint.INSTANCE.CP_Page_DrawQRCode(h, 10, 600, 10, AutoReplyPrint.CP_QRCodeECC_L, str);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);
    }

    void Test_Page_DrawRasterImageFromBitmap(Pointer h)
    {
        Bitmap bitmap = TestUtils.getImageFromAssetsFile(ctx, "RasterImage/test_pdf.pdf");
        if ((bitmap == null) || (bitmap.getWidth() == 0) || (bitmap.getHeight() == 0))
            return;

        int srcw = bitmap.getWidth();
        int srch = bitmap.getHeight();
        int maxw = 384;
        int maxh = 600;
        int dstw = srcw;
        int dsth = srch;
        if (dstw > maxw) {
            dstw = maxw;
            dsth = maxw * srch / srcw;
        }
        if (dsth > maxh) {
            dsth = maxh;
            dstw = maxh * srcw / srch;
        }

        AutoReplyPrint.INSTANCE.CP_Page_SelectPageModeEx(h, 200, 200, 0, 0, 384, 600);
        AutoReplyPrint.INSTANCE.CP_Page_DrawBox(h, 0, 0, 384, 600, 2, 1);
        AutoReplyPrint.CP_Page_DrawRasterImageFromData_Helper.DrawRasterImageFromBitmap(h, 0, 0, dstw, dsth, bitmap, AutoReplyPrint.CP_ImageBinarizationMethod_ErrorDiffusion);
        AutoReplyPrint.INSTANCE.CP_Page_PrintPage(h);
        AutoReplyPrint.INSTANCE.CP_Page_ExitPageMode(h);

        Test_Pos_QueryPrintResult(h);
    }

}
