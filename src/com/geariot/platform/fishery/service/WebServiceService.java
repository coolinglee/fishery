/**
 * 
 */
package com.geariot.platform.fishery.service;

import java.util.Map;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.geariot.platform.fishery.dao.WXUserDao;
import com.geariot.platform.fishery.entities.WXUser;
import com.geariot.platform.fishery.model.RESCODE;

/**
 * @author mxy940127
 *
 */
@Service
@Transactional
public class WebServiceService {

	private Logger logger = LogManager.getLogger(WebServiceService.class);
	
	@Autowired
	private WXUserDao wxUserDao;
	
	public Map<String, Object> bindUser(String phone, String openId){
		logger.debug("绑定用户的openId：" + openId);
		if(openId == null){
			return RESCODE.WECHAT_GET_PARAM_WRONG.getJSONRES();
		}
		WXUser userOpenId = wxUserDao.findUserByOpenId(openId);
		WXUser userPhone = wxUserDao.findUserByPhone(phone);
		//本地数据库没有openId的记录
		if(userOpenId == null){
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		//本地根据openId找到一条数据，且这条数据的phone字段不为空。
		else if(userOpenId.getPhone() != null && !userOpenId.getPhone().isEmpty()){
			if(!userOpenId.getPhone().equals(phone)){
				return RESCODE.BINDED_WITH_OTHER_PHONE.getJSONRES();
			}
		}
		if(userPhone == null){
			return RESCODE.NOT_FOUND.getJSONRES();
		}else if(!openId.equalsIgnoreCase(userPhone.getOpenId())){
			if(userPhone.getOpenId() != null && !userPhone.getOpenId().isEmpty()){
    			return RESCODE.PHONE_BINDED_BY_OTHER.getJSONRES();
    		}else{
    			userPhone.setOpenId(userOpenId.getOpenId());
    			userPhone.setName(userOpenId.getName());
    			wxUserDao.deleteUser(userOpenId.getId());
    		  	return RESCODE.SUCCESS.getJSONRES(userPhone);
    		}
		}
		return RESCODE.SUCCESS.getJSONRES(userOpenId);
	}

	public WXUser wechatLogin(String openId, String headimgurl) {
		WXUser wxUser = wxUserDao.findUserByOpenId(openId);
    	if(wxUser == null)
    	{
    		wxUser = new WXUser();
    		wxUser.setOpenId(openId);
    		wxUser.setHeadimgurl(headimgurl);
    		wxUserDao.save(wxUser);
    	}
    	return wxUser;
	}
}