package com.dyaco.spirit_commercial.settings;

import static com.dyaco.spirit_commercial.App.getApp;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyaco.spirit_commercial.databinding.PopupWifiQrcodeBinding;
import com.dyaco.spirit_commercial.model.repository.WifiXmlData;
import com.dyaco.spirit_commercial.support.CommandUtil;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.GlideApp;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * WIFI:S:CoreStarloT_5G;T:WPA;P:core2295star0350;H:false;;
 */

public class WifiQrCodePopupWindow extends BasePopupWindow<PopupWifiQrcodeBinding> {
    private final List<WifiXmlData> wifiXmlDataList = new ArrayList<>();
    private WifiXmlData myWifiXmlData;

    String currentSSID = "";
    public WifiQrCodePopupWindow(Context context) {
        super(context, 500, 0, 795, GENERAL.TRANSLATION_X,false,true,true,true);

        initView();

    }

    private void initView() {
        Looper.myQueue().addIdleHandler(() -> {

            getBinding().btnClose.setOnClickListener(v -> dismiss());


            getWifiConfig();

            currentSSID =  new CommonUtils().getSSID(mContext);
            Log.d("OOOIIIEEE", "WifiQrCodePopupWindow: " + wifiXmlDataList);

            getBinding().tvSSID.setText(currentSSID);

            for (WifiXmlData wifiXmlData : wifiXmlDataList) {
             //   Log.d("OOOIIIEEE", "#####: " + currentSSID +","+ wifiXmlData.getSSID() +","+ wifiXmlData.getPreSharedKey() +","+(currentSSID.equals(wifiXmlData.getSSID())));

                if ((currentSSID).equals(wifiXmlData.getSSID())) {
                    myWifiXmlData = wifiXmlData;
                }
            }

            //WIFI:S:CoreStarloT_5G;T:WPA;P:core2295star0350;H:false;;

            String sharedWifiQrcode = "WIFI:S:" + myWifiXmlData.getSSID() + ";T:WPA;P:" + myWifiXmlData.getPreSharedKey() + ";H:" + myWifiXmlData.isHiddenSSID() + ";;";

            Log.d("WIFI_SHARED", "sharedWifiQrcode: " + sharedWifiQrcode);
            if (myWifiXmlData != null) {

                getBinding().tvPWD.setText(sharedWifiQrcode);

                GlideApp.with(getApp())
                        .load(new CommonUtils().createQRCode(sharedWifiQrcode, 384, 8))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(getBinding().actionImage);
            }

            return false;
        });

    }

    private void getWifiConfig() {

        String x = "cat /data/misc/apexdata/com.android.wifi/WifiConfigStore.xml";

        String wifiConfigXml = CommandUtil.execute(x);


        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;
            doc = dBuilder.parse(new InputSource(new StringReader(wifiConfigXml)));
            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nodeList1 = doc.getElementsByTagName("Network");
            for (int i = 0; i < nodeList1.getLength(); i++) {
                WifiXmlData wifiXmlData = new WifiXmlData();
                Node node1 = nodeList1.item(i);
                NodeList nodeList2 = node1.getChildNodes();
                for (int i2 = 0; i2 < nodeList2.getLength(); i2++) {
                    Node node2 = nodeList2.item(i2);
                    if ("WifiConfiguration".equals(node2.getNodeName())) {
                        NodeList nodeList3 = node2.getChildNodes();

                        for (int i3 = 0; i3 < nodeList3.getLength(); i3++) {
                            Node node3 = nodeList3.item(i3);
                            NamedNodeMap nnm = node3.getAttributes();

                            Node n = nnm.item(0); //<string name="SSID">"CoreStarCo"</string>
                            Node n2 = null; //<boolean name="HiddenSSID" value="false" />

                            if ("SSID".equals(n.getTextContent()) ||
                                    "PreSharedKey".equals(n.getTextContent()) ||
                                    "HiddenSSID".equals(n.getTextContent()) ||
                                    "ConfigKey".equals(n.getTextContent())) {

                                if ("HiddenSSID".equals(n.getTextContent()) && nnm.getLength() >= 2) {
                                    n2 = nnm.item(1);
                                    Log.d("getWifiConfig", "##" + i + "## " + n.getTextContent() +","+ (n2 != null ? n2.getNodeValue(): ""));
                                    assert n2 != null;
                                    wifiXmlData.setHiddenSSID(strReplace(n2.getNodeValue()));
                                }

                                NodeList nodeList4 = node3.getChildNodes();
                                for (int i4 = 0; i4 < nodeList4.getLength(); i4++) {
                                    Node node4 = nodeList4.item(i4);
                                    if ("SSID".equals(n.getTextContent())) {
                                        wifiXmlData.setSSID(strReplace(node4.getNodeValue()));
                                    }

                                    if ("PreSharedKey".equals(n.getTextContent())) {
                                        wifiXmlData.setPreSharedKey(strReplace(node4.getNodeValue()));
                                    }

                                    if ("ConfigKey".equals(n.getTextContent())) {
                                        wifiXmlData.setType(strReplace(node4.getNodeValue()));
                                    }
                                    Log.d("getWifiConfig", "##" + i + "## " + n.getTextContent() + "," + node4.getNodeValue() +","+ (n2 != null ? n2.getNodeValue(): ""));
                                }

                            }
                        }

                        Log.d("getWifiConfig", "============");
                    }
                }
                wifiXmlDataList.add(wifiXmlData);
            }

        } catch (Exception e) {
            Log.d("getWifiConfig", "#######Exception: " + e.getLocalizedMessage());
            Log.d("getWifiConfig", "\n#######Exception:######### ");
        }
    }

    private String strReplace(String str) {
        return str.replace("\"","");
    }
}
