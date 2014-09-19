package com.guodian.checkdevicetool.util;

import java.io.FileInputStream;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.dbstar.DbstarDVB.common.Configs;

public class XmlParser {
    
    public static Configs parseConfig(String path){
        GLog.getLogger("Futao").i("start parseConfig ");
        Configs config = null;
        try {
            InputStream in = new FileInputStream(path);
            XmlPullParser parser = Xml.newPullParser();  
            parser.setInput(in, "UTF-8");  
            
            int event = parser.getEventType();//
            while(event!=XmlPullParser.END_DOCUMENT){
                switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    config = new Configs();
                    break;

                case XmlPullParser.START_TAG:
                    if(Configs.SSID.equals(parser.getName())){
                        event = parser.next();
                        config.mWifiSSID =parser.getText();
                    }
                    if(Configs.PASSWORD.equals(parser.getName())){
                        event = parser.next();
                        config.mWifiPassword =parser.getText();
                    }
                    
                    if(Configs.SOCKET.equals(parser.getName())){
                        event = parser.next();
                        config.mScoketNumber = parser.getText();
                    }
                    
                    if(Configs.SMART_CARD_NUMBER.equals(parser.getName())){
                        event = parser.next();
                        config.mSmartCardNum = parser.getText();
                    }
                    if(Configs.VIDEO_PATH.equals(parser.getName())){
                        event = parser.next();
                        config.mVideoPath = parser.getText();
                    }
                    if(Configs.PLAY_TIME.equals(parser.getName())){
                        event = parser.next();
                        config.mPlayTime = parser.getText();
                    }
                    break;
                }
                
                event = parser.next();
            }
        } catch (Exception e) {
           GLog.getLogger("Futao").i("parser fail ");
            e.printStackTrace();
        }
        return  config;
    }
}
