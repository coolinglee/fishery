package com.geariot.platform.fishery.wxutils;

import java.util.Date;

import org.apache.http.entity.StringEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.geariot.platform.fishery.utils.HttpRequest;
import com.geariot.platform.fishery.wxutils.WechatConfig;

public class WechatTemplateMessage {
	
	
	private static final String SELFTEST_BROKEN_TEMPLATE_ID="P9tXcCFGquvWFcqPyDD5OK7BZ6rFFfwZcGu54wrBBa8";
//	private static final Logger log = Logger.getLogger(WechatTemplateMessage.class);
	private static final Logger log = LogManager.getLogger(WechatTemplateMessage.class);
	private static final String ALARM_TEMPLATE_ID="rWbgpqTb6alKSu4Wusf7ItFq2FQRQrzk1CNQV0uyJ_4";
	
	//private static final String ALARM_TEMPLATE_ID=null;

	private static String invokeTemplateMessage(JSONObject params){
		StringEntity entity = new StringEntity(params.toString(),"utf-8"); //解决中文乱码问题   
		String result = HttpRequest.postCall(WechatConfig.WECHAT_TEMPLATE_MESSAGE_URL + 
				WechatConfig.getAccessTokenForInteface().getString("access_token"),
				entity, null);
		log.debug("微信模版消息结果：" + result);
		return result;
	}
	


//{{first.DATA}}
//类型：{{keyword1.DATA}}
//金额：{{keyword2.DATA}}
//状态：{{keyword3.DATA}}
//时间：{{keyword4.DATA}}
//备注：{{keyword5.DATA}}
//{{remark.DATA}}

	public static void sendBrokenMSG(StringBuilder sb,String openId,String deviceSn) {
		log.debug("给前台发送故障信息------");
		JSONObject params=new JSONObject();
		JSONObject data=new JSONObject();
		params.put("touser",openId);
		params.put("template_id", SELFTEST_BROKEN_TEMPLATE_ID);
		data.put("first", keywordFactory("故障信息","#173177"));
		data.put("keyword1", keywordFactory(deviceSn,"#173177"));
		data.put("keyword2", keywordFactory("设备故障","#173177"));
		data.put("keyword3", keywordFactory(new Date().toString(),"#173177"));
		params.put("remark", sb.toString());
		String result=invokeTemplateMessage(params);
		log.debug("故障消息结果:"+result);
		//data.put(key, value);
	}
	
	public static void sendOxygenOnoffMSG(String msg,String openId,String deviceSn,int onOff) {
		if(onOff==0) {
		log.debug("向微信用户发送增氧机关闭失败信息····");
		}else {
			log.debug("向微信用户发送增氧机打开失败信息");
		}
		JSONObject params=new JSONObject();
		JSONObject data=new JSONObject();
		params.put("touser", openId);
		params.put("template_id", SELFTEST_BROKEN_TEMPLATE_ID);
		data.put("first", keywordFactory("增氧机开闭信息","#173177"));
		data.put("keyword1", keywordFactory(deviceSn,"#173177"));
		if(onOff==1) {
		data.put("keyword2", keywordFactory("增氧机打开失败","#173177"));
		}else {
			data.put("keyword2", keywordFactory("增氧机关闭失败","#173177"));
		}
		data.put("keyword3", keywordFactory(new Date().toString(),"#173177"));
		params.put("remark", msg);
		params.put("data", data);
		String result=invokeTemplateMessage(params);
		if(onOff==1) {
		log.debug("增氧机打开失败信息结果："+result);
		}else {
			log.debug("增氧机关闭失败信息结果："+result);
		}
	}
	
	public static void alarmMSG(String msg,String openId,String deviceSn) {
		log.debug("向微信用户发送报警信息");
		JSONObject params=new JSONObject();
		JSONObject data=new JSONObject();
		params.put("touser", openId);
		params.put("template_id", ALARM_TEMPLATE_ID);
		data.put("first", keywordFactory("报警信息","#173177"));
		data.put("keyword1", keywordFactory(deviceSn,"#173177"));
		data.put("keyword2", keywordFactory("设备报警","#173177"));
		data.put("keyword3", keywordFactory(new Date().toString(),"#173177"));
		params.put("remark", msg);
		params.put("data", data);
		String result=invokeTemplateMessage(params);
		log.debug("报警信息结果："+result);
		
	}
	
	
	
	
	private static JSONObject keywordFactory(String value){
		JSONObject keyword = new JSONObject();
		keyword.put("value", value);
		return keyword;
	}
	
	private static JSONObject keywordFactory(String value, String color){
		JSONObject keyword = keywordFactory(value);
		keyword.put("color", color);
		return keyword;
	}
	
}
