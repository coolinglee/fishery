package com.geariot.platform.fishery.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.fishery.dao.AIODao;
import com.geariot.platform.fishery.dao.AeratorStatusDao;
import com.geariot.platform.fishery.dao.CompanyDao;
import com.geariot.platform.fishery.dao.ControllerDao;
import com.geariot.platform.fishery.dao.LimitDao;
import com.geariot.platform.fishery.dao.PondDao;
import com.geariot.platform.fishery.dao.PondFishDao;
import com.geariot.platform.fishery.dao.SensorDao;
import com.geariot.platform.fishery.dao.Sensor_ControllerDao;
import com.geariot.platform.fishery.dao.TimerDao;
import com.geariot.platform.fishery.dao.WXUserDao;
import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.Company;
import com.geariot.platform.fishery.entities.Controller;
import com.geariot.platform.fishery.entities.Pond;
import com.geariot.platform.fishery.entities.Sensor;
import com.geariot.platform.fishery.entities.WXUser;
import com.geariot.platform.fishery.model.Equipment;
import com.geariot.platform.fishery.model.RESCODE;

@Service
@Transactional
public class UserService {
    @Autowired
    private CompanyDao companyDao;
    
    @Autowired
    private WXUserDao wxuserDao;
    
    @Autowired
    private PondDao pondDao;
    
    @Autowired
    private AIODao aioDao;
    
    @Autowired
    private AeratorStatusDao aeratorStatusDao;
    
    @Autowired
    private LimitDao limitDao;
    
    @Autowired
    private SensorDao sensorDao;
    
    @Autowired
    private TimerDao timerDao;
    
    @Autowired
    private ControllerDao controllerDao;
    
    @Autowired
    private Sensor_ControllerDao sensor_ControllerDao;
    
    @Autowired
    private PondFishDao pondFishDao;
    
    @Autowired
    private PondService pondService;
    
	public Map<String, Object> addWXUser(WXUser wxuser) {
		WXUser exist = wxuserDao.findUserByOpenId(wxuser.getOpenId());
		if (exist != null) {
			return RESCODE.ACCOUNT_EXIST.getJSONRES();
		}
		wxuser.setCreateDate(new Date());
		wxuserDao.save(wxuser);
		wxuser.setRelation("WX"+wxuser.getId());
		return RESCODE.SUCCESS.getJSONRES(wxuser);
	}

	public Map<String, Object> addCompany(Company company) {
		Company exist = companyDao.findCompanyByName(company.getName());
		if (exist != null) {
			return RESCODE.ACCOUNT_EXIST.getJSONRES();
		}
		company.setCreateDate(new Date());
		company.setHasAccount(false);
		companyDao.save(company);
		company.setRelation("CO"+company.getId());
		return RESCODE.SUCCESS.getJSONRES(company);
	}

	public Map<String, Object> deleteCompany(Integer[] companyIds) {
		for (int companyId : companyIds) {
			Company exist = companyDao.findCompanyById(companyId);
			if (exist == null) {
				return RESCODE.DELETE_ERROR.getJSONRES();
			} else {
				List<AIO> aios = aioDao.queryAIOByNameAndRelation(exist.getRelation(), null);
				for(AIO aio : aios){
					aeratorStatusDao.delete(aio.getDevice_sn());
					limitDao.delete(aio.getDevice_sn());
					timerDao.delete(aio.getDevice_sn());
				}
				aioDao.deleteByRelation(exist.getRelation());
				//删除控制器和传感器前需要将之间的绑定关系删掉,再删两者
				List<Sensor> sensors = sensorDao.querySensorByNameAndRelation(exist.getRelation(), null);
				for(Sensor sensor : sensors){
					limitDao.delete(sensor.getDevice_sn());
					timerDao.delete(sensor.getDevice_sn());
					sensor_ControllerDao.delete(sensor.getId());
				}
				List<Controller> controllers = controllerDao.queryControllerByNameAndRelation(exist.getRelation(), null);
				for(Controller Controller : controllers){
					sensor_ControllerDao.deleteController(Controller.getId());
				}
				sensorDao.deleteByRelation(exist.getRelation());
				controllerDao.deleteByRelation(exist.getRelation());
				List<Pond> ponds = pondDao.queryPondByNameAndRelation(exist.getRelation(), null);
				for(Pond pond : ponds){
					pondFishDao.deleteByPondId(pond.getId());
				}
				pondDao.deleteByRelation(exist.getRelation());
				companyDao.deleteCompany(companyId);
			}
		}
		return RESCODE.SUCCESS.getJSONRES();
	}

	public Map<String, Object> deleteWXUser(Integer[] WXUserIds) {
		for (int WXUserId : WXUserIds) {
			WXUser exist = wxuserDao.findUserById(WXUserId);
			if (exist == null) {
				return RESCODE.DELETE_ERROR.getJSONRES();
			} else {
				//删名下一体机前需要将AeratorStatus记录删掉
				List<AIO> aios = aioDao.queryAIOByNameAndRelation(exist.getRelation(), null);
				for(AIO aio : aios){
					aeratorStatusDao.delete(aio.getDevice_sn());
					limitDao.delete(aio.getDevice_sn());
					timerDao.delete(aio.getDevice_sn());
				}
				aioDao.deleteByRelation(exist.getRelation());
				//删除控制器和传感器前需要将之间的绑定关系删掉,再删两者
				List<Sensor> sensors = sensorDao.querySensorByNameAndRelation(exist.getRelation(), null);
				for(Sensor sensor : sensors){
					limitDao.delete(sensor.getDevice_sn());
					timerDao.delete(sensor.getDevice_sn());
					sensor_ControllerDao.delete(sensor.getId());
				}
				List<Controller> controllers = controllerDao.queryControllerByNameAndRelation(exist.getRelation(), null);
				for(Controller Controller : controllers){
					sensor_ControllerDao.deleteController(Controller.getId());
				}
				sensorDao.deleteByRelation(exist.getRelation());
				controllerDao.deleteByRelation(exist.getRelation());
				List<Pond> ponds = pondDao.queryPondByNameAndRelation(exist.getRelation(), null);
				for(Pond pond : ponds){
					pondFishDao.deleteByPondId(pond.getId());
				}
				pondDao.deleteByRelation(exist.getRelation());
				wxuserDao.deleteUser(WXUserId);
			}
		}
		return RESCODE.SUCCESS.getJSONRES();
	}

	public Map<String, Object> modifyCompany(Company company) {
		Company exist = companyDao.findCompanyById(company.getId());
		if (exist == null) {
			return RESCODE.NOT_FOUND.getJSONRES();
		} 
		exist.setName(company.getName());
		exist.setPhone(company.getPhone());
		exist.setAddress(company.getAddress());
		exist.setLife(company.getLife());
		exist.setMail_address(company.getMail_address());
		companyDao.updateCompany(exist);
		
		return RESCODE.SUCCESS.getJSONRES(exist);
	}

	public Map<String, Object> modifyWXUser(WXUser wxuser) {
		WXUser exist = wxuserDao.findUserById(wxuser.getId());
		if (exist == null) {
			return RESCODE.NOT_FOUND.getJSONRES();
		} 
		
		exist.setName(wxuser.getName());
		exist.setPhone(wxuser.getPhone());
		exist.setAddress(wxuser.getAddress());
		exist.setLife(wxuser.getLife());
		exist.setSex(wxuser.getSex());
		wxuserDao.updateUser(exist);
		
		return RESCODE.SUCCESS.getJSONRES(exist);
	}

	public Map<String, Object> queryCompany(String name, int page, int number) {
		int from = (page - 1) * number;
		List<Company> list = companyDao.queryList(name, from, number);
		if (list == null || list.isEmpty()) {
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		long count = (long) companyDao.getQueryCount(name);
		int size = (int) Math.ceil(count / (double) number);
		return RESCODE.SUCCESS.getJSONRES(list,size,count);
	}

	public Map<String, Object> queryWXUser(String name, int page, int number) {
		int from = (page - 1) * number;
		List<WXUser> list = wxuserDao.queryList(name, from, number);
		if (list == null || list.isEmpty()) {
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		long count = (long) wxuserDao.getQueryCount(name);
		int size = (int) Math.ceil(count / (double) number);
		return RESCODE.SUCCESS.getJSONRES(list,size,count);
	}

	public Map<String, Object> WXUserDetail(int id) {
		WXUser wxUser = wxuserDao.findUserById(id);
		if (wxUser == null) {
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		return RESCODE.SUCCESS.getJSONRES(wxUser);
	}
	
	public Map<String, Object> CompanyDetail(int id) {
		Company company = companyDao.findCompanyById(id);
		if (company == null) {
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		int count = 0;
		List<String> relations = new ArrayList<>();
		relations.add(company.getRelation());
		Map<String, Object> map = RESCODE.SUCCESS.getJSONRES(company);
		List<Pond> ponds = pondDao.queryPondByNameAndRelation(company.getRelation(), null);
		List<Equipment> equipments = pondDao.adminFindEquipmentByName(relations, 0, 2000);
		for(Equipment equipment :equipments){
			if(equipment.getStatus().contains("0")){
				count++;
			}
		}
		map.put("pondCount", ponds.size());
		map.put("equip", count+"/"+equipments.size());
		return map;
	}

	public Map<String, Object> relationDetail(String relation) {
		String type = relation.substring(0, 2);
		switch(type){
			case "WX" : return WXUserDetail(Integer.parseInt(relation.substring(2)));
			case "CO" :	return CompanyDetail(Integer.parseInt(relation.substring(2)));
			default : return RESCODE.WRONG_PARAM.getJSONRES();
		}
	}
}
