package com.geariot.platform.fishery.service;

import cmcc.iot.onenet.javasdk.api.datapoints.GetDatapointsListApi;
import cmcc.iot.onenet.javasdk.api.device.GetLatesDeviceData;
import cmcc.iot.onenet.javasdk.api.triggers.AddTriggersApi;
import cmcc.iot.onenet.javasdk.api.triggers.DeleteTriggersApi;
import cmcc.iot.onenet.javasdk.response.BasicResponse;
import cmcc.iot.onenet.javasdk.response.datapoints.DatapointsList;
import cmcc.iot.onenet.javasdk.response.device.DeciceLatestDataPoint;
import cmcc.iot.onenet.javasdk.response.triggers.NewTriggersResponse;
import com.geariot.platform.fishery.dao.*;
import com.geariot.platform.fishery.entities.*;
import com.geariot.platform.fishery.entities.Timer;
import com.geariot.platform.fishery.model.*;
import com.geariot.platform.fishery.timer.CMDUtils;
import com.geariot.platform.fishery.utils.DataExportExcel;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

//import com.geariot.platform.fishery.socket.CMDUtils;

@Service
@Transactional
public class EquipmentService {

	private static Logger logger = LogManager.getLogger(EquipmentService.class);

	@Autowired
	private AIODao aioDao;

	@Autowired
	private SensorDao sensorDao;

	@Autowired
	private ControllerDao controllerDao;

	@Autowired
	private LimitDao limitDao;

	@Autowired
	private TimerDao timerDao;

	@Autowired
	private Sensor_DataDao sensor_DataDao;

	@Autowired
	private PondDao pondDao;

	@Autowired
	private CompanyDao companyDao;

	@Autowired
	private WXUserDao wxUserDao;

	@Autowired
	private AeratorStatusDao statusDao;

	@Autowired
	private Sensor_ControllerDao sensor_ControllerDao;

	@Autowired
	private DataAlarmDao daDao;

	@Autowired
	private Dev_TriggerDao dev_triggerDao;
	@Autowired
	private DeviceDao deviceDao;

	@Autowired
	private SocketSerivce socketService;

	@Autowired
	private BindService bindService;
	

	private String type = "";
	private String relation = "";
	private Company company = null;
	private AIO aio = null;
	private Sensor sensor = null;
	private Controller controller = null;
	private WXUser wxUser = null;
	private String key = "KMDJ=U3QacwRmoCdcVXrTW8D0V8=";

	public Map<String, Object> setLimit(String devicesn,int way,int lowlimit,int highlimit,int higherlimit) {
		Controller controller = controllerDao.findControllerByDeviceSnAndWay(devicesn,way);
		List<Sensor> sensors = sensorDao.findSensorsByPondId(controller.getPondId());
		String sensorsn= sensors.get(0).getDevice_sn();
		addTrigger("oxygen", sensorsn,  "<", lowlimit, 7);
		addTrigger("oxygen", sensorsn,  "<", highlimit, 8);
		addTrigger("oxygen", sensorsn,  "<", higherlimit, 9);
			return RESCODE.SUCCESS.getJSONRES();
		// return RESCODE.DEVICESNS_INVALID.getJSONRES();


	}
	//删除设备，简版，需添加触发器删除
	public Map<String, Object> delEquipment(String device_sn){
		Device  d = deviceDao.findDevice(device_sn);
		if(d!=null) {
			int type = d.getType();
			switch(type) {
				case 1://传感器
					sensorDao.delete(device_sn);
					deviceDao.delete(device_sn);
					break;
				case 2://一体机
					aioDao.delete(device_sn);
					deviceDao.delete(device_sn);
					break;
				case 3://控制器
					controllerDao.delete(device_sn);
					deviceDao.delete(device_sn);
					break;
			}
			return RESCODE.SUCCESS.getJSONRES();
		}else {
			return RESCODE.ACCOUNT_NOT_EXIST.getJSONRES();
		}		
	}
//	public Map<String, Object> delEquipment(String device_sn) {
//
//		String devices;
//		try {
//			devices = device_sn.trim().substring(0, 2);
//			device_sn = device_sn.substring(2);
//			deviceDao.delete(device_sn);
//			List<Dev_Trigger> trilist = dev_triggerDao.findDev_TriggerBydevsn(device_sn);
//			if (trilist !=null) {
//				for (Dev_Trigger dev_trigger : trilist) {
//					String tirggerid = dev_trigger.getTriger_id();
//					String key = "LTKhU=GLGsWmPrpHICwWOnzx=bA=";
//					/**
//					 * 触发器删除
//					 * @param tirggerid:触发器ID,String
//					 * @param key:masterkey 或者 设备apikey
//					 */
//					DeleteTriggersApi api = new DeleteTriggersApi(tirggerid, key);
//					BasicResponse<Void> response = api.executeApi();
//					System.out.println("errno:"+response.errno+" error:"+response.error);
//				}
//			}
//			dev_triggerDao.delete(device_sn);
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			return RESCODE.DEVICESNS_INVALID.getJSONRES();
//		}
//			if (devices.equals("02")) {
//				aioDao.delete(device_sn);
//				statusDao.delete(device_sn);
//			} else if (devices.equals("01")) {
//				int sensorId = sensorDao.findSensorByDeviceSns(device_sn).getId();
//				sensorDao.delete(device_sn);
//				List<Sensor_Controller> list = sensor_ControllerDao.list(sensorId);
//				for (Sensor_Controller sensor_Controller : list) {
//					controller = controllerDao.findControllerById(sensor_Controller.getControllerId());
//					if (controller == null) {
//						continue;
//					} else {
//						//changeControllerPortStatusClose(controller, sensor_Controller.getController_port());
//					}
//				}
//				sensor_ControllerDao.delete(sensorId);
//			} else if (devices.equals("03")) {
//				int controllerId = controllerDao.findControllerByDeviceSns(device_sn).getId();
//				controllerDao.delete(device_sn);
//			} else {
//				return RESCODE.DELETE_ERROR.getJSONRES();
//			}
//		return RESCODE.SUCCESS.getJSONRES();
//	}


	public void changeControllerWayOnoff(String divsn, int way ,int key) {
		String text = "KM"+way+":"+key;
		int results = CMDUtils.sendStrCmd(divsn,text);
	}


//	public boolean exportData(String device_sn, String startTime, String endTime, HttpServletResponse response) {
//		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		try {
//			List<ExcelData> list = sensor_DataDao.getExcelData(device_sn, sdf.parse(startTime), sdf.parse(endTime));
//			String[] fields = { "Id", "device_sn", "oxygen", "ph", "receiveTime", "waterTemperature" };
//			DataExportExcel dataExportExcel = new DataExportExcel();
//			HSSFWorkbook wb = dataExportExcel.generateExcel();
//			wb = dataExportExcel.generateSheet(wb, "DataTable", fields, list);
//			dataExportExcel.export(wb, response);
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}

	public Map<String, Object> addSensor(Sensor[] sensors) {
		for (Sensor sensor:sensors){
			if(deviceDao.findDevice(sensor.getDevice_sn())==null) {
				//	logger.info();					
					Device device = new Device();
					device.setDevice_sn(sensor.getDevice_sn());
					device.setType(1);
					deviceDao.save(device);
					System.out.println("添加了一个新的传感器");
					
				}
			sensorDao.save(sensor);
			System.out.println(sensor.getDevice_sn());
		}
		return RESCODE.SUCCESS.getJSONRES();

		/*String device_sn, String name, String relation,int pondId
		 * String deviceSn;
		try {
			deviceSn = device_sn.trim().substring(0, 2);
			device_sn = device_sn.substring(2);
            Device devexist = deviceDao.findDevice(device_sn);
            if (devexist != null) {
                return RESCODE.SENSOR_EXIST.getJSONRES();
            }
			Device device = new Device();
			device.setDevice_sn(device_sn);

			deviceDao.save(device);
		} catch (Exception e) {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}
		logger.debug("塘口Id:" + pondId + "尝试与传感器设备,设备编号为:" + device_sn + "进行绑定...");
		Pond pond = pondDao.findPondByPondId(pondId);
		if (pond == null) {
			logger.debug("塘口Id:" + pondId + "在数据库中无记录!!!");
			return RESCODE.NOT_FOUND.getJSONRES();
		} else {
                  if (deviceSn.equals("01")) {
                    Sensor exist = sensorDao.findSensorByDeviceSns(device_sn);
                    if (exist != null) {
                        return RESCODE.SENSOR_EXIST.getJSONRES();
                    }

                    Sensor sensor = new Sensor();
                    sensor.setDevice_sn(device_sn);
                    sensor.setName(name);
                    sensor.setRelation(relation);
                    sensor.setStatus(1);
                    sensor.setPondId(pondId);
                    sensor.setPort_status("00");
                    sensorDao.save(sensor);
                    Pond sensorbindpond=pondDao.findPondByPondId(pondId);
                    List<PondFish> senbindfish=pond.getPondFishs();
                    int pondfishtype;
                    if (senbindfish!=null){
                        PondFish senbinfs=senbindfish.get(0);
                        pondfishtype=senbinfs.getType();
                    }else return RESCODE.DEVICESNS_INVALID.getJSONRES();
                    int triggeraddresult=addTrigerbyFishtype(device_sn, pondfishtype);
                    if (triggeraddresult==0)
                        return RESCODE.SUCCESS.getJSONRES();
                    else return RESCODE.DEVICESNS_INVALID.getJSONRES();

                } else
                    return RESCODE.DEVICESNS_INVALID.getJSONRES();

                }*/
	}


	public Map<String, Object> addController(Controller[] controllers) {

		for (Controller controller : controllers ){
			if(deviceDao.findDevice(controller.getDevice_sn())==null) {
			//	logger.info("");
				
				Device device = new Device();
				device.setDevice_sn(controller.getDevice_sn());
				device.setType(3);
				deviceDao.save(device);
				System.out.println("添加了一个新的控制器");
				
			}
			controllerDao.save(controller);
			System.out.println(controller.getDevice_sn()); 
		}

		return RESCODE.SUCCESS.getJSONRES();

	}
	//测试用，具体实现后续完成
	public Map<String, Object> addAio(AIO[] aios) {
		for (AIO aio:aios){
			if(deviceDao.findDevice(aio.getDevice_sn())==null) {
				
				Device device = new Device();
				device.setDevice_sn(aio.getDevice_sn());
				device.setType(2);
				deviceDao.save(device);
				System.out.println("添加了一个一体机");
				
			}
			aioDao.save(aio);
			System.out.println(aio.getDevice_sn());
			 
		}
		return RESCODE.SUCCESS.getJSONRES();
	}


	public Map<String, Object> realTimeData(String device_sn, int way) {
		String deviceSn;
		Sensor_Data data = null;
		Map<String, Object> map = null;
		Date current_time=new Date();
		Date last_time;
		try {
			deviceSn = device_sn.trim().substring(0, 2);
		} catch (Exception e) {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}
		if (deviceSn.equals("01") || deviceSn.equals("02")) {
			if (aioDao.findAIOByDeviceSns(device_sn) == null) {
				return RESCODE.DEVICESNS_INVALID.getJSONRES();
			}
			data = sensor_DataDao.findDataByDeviceSnAndWay(device_sn, way);
			last_time = data.getReceiveTime();
			long diff = current_time.getTime() - last_time.getTime();
			long minutes = diff / (1000 * 60);
			if (minutes < 6) {
				AIO aio = aioDao.findAIOByDeviceSns(device_sn);
				map = RESCODE.SUCCESS.getJSONRES(data);
				Limit_Install install = limitDao.findLimitByDeviceSnsAndWay(device_sn, way);
				StringBuffer sb = new StringBuffer(aio.getStatus());
				if (install != null) {
					map.put("low_limit", install.getLow_limit());
					map.put("up_limit", install.getUp_limit());
					map.put("high_limit", install.getHigh_limit());
					map.put("status", String.valueOf(sb.charAt(way - 1)));
					map.put("name", aio.getName());
				} else {
					map.put("status", String.valueOf(sb.charAt(way - 1)));
					map.put("name", aio.getName());
					map.put("low_limit", 5);
					map.put("up_limit", 10);
					map.put("high_limit", 15);
				}
				return map;
			}else
				return RESCODE.OFF_LINE.getJSONRES();

		} else if (deviceSn.equals("03")) {
			if (sensorDao.findSensorByDeviceSns(device_sn) == null) {
				return RESCODE.DEVICESNS_INVALID.getJSONRES();
			}
			data = sensor_DataDao.findDataByDeviceSns(device_sn);
			last_time = data.getReceiveTime();
			long diff = current_time.getTime() - last_time.getTime();
			long minutes = diff / (1000 * 60);
			if (minutes < 6) {
				Sensor sensor = sensorDao.findSensorByDeviceSns(device_sn);
				map = RESCODE.SUCCESS.getJSONRES(data);
				Limit_Install install = limitDao.findLimitByDeviceSns(device_sn);
				if (install != null) {
					map.put("low_limit", install.getLow_limit());
					map.put("up_limit", install.getUp_limit());
					map.put("high_limit", install.getHigh_limit());
					map.put("status", sensor.getStatus());
					map.put("name", sensor.getName());
				} else {
					map.put("status", sensor.getStatus());
					map.put("name", sensor.getName());
					map.put("low_limit", 5);
					map.put("up_limit", 10);
					map.put("high_limit", 15);
				}
				return map;
			} else
				return RESCODE.OFF_LINE.getJSONRES();
		}else
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
	}

	public String realTimeData(String device_sn) {
		/*System.out.println(device_sn);
		String datastreamIds="temperature";
		String devId=device_sn;
		String sort = "ASC" ;
		String key ="BzFI2NGGcWgiMKODrBDiGkA7Psc=";
		Integer first = 1;
		
    	GetDatapointsListApi api = new GetDatapointsListApi(datastreamIds,devId,first,sort,key);
    	
    	JSONObject tempjson = JSONObject.fromObject(api.executeApi().getJson());
    	System.out.println(tempjson);
    	JSONObject temp = tempjson.getJSONObject("data");
    	JSONArray array= temp.getJSONArray("datastreams");
    	JSONObject obj1 = array.getJSONObject(0);
    	JSONArray obj2 =obj1.getJSONArray("datapoints");
    	JSONObject obj3=obj2.getJSONObject(0);
    	int obj4 =obj3.getInt("value");
    	//String obj5=obj3.getString("at");    	
     	System.out.println(obj4);
    	return temp;*/
		
        /*JSONObject tempjson = JSONObject.fromObject(response.getJson());    
        System.out.println(tempjson);
    	JSONObject temp = tempjson.getJSONObject("data");
    	JSONArray array= temp.getJSONArray("datastreams");
    	JSONObject obj1 = array.getJSONObject(0);
    	JSONArray obj2 =obj1.getJSONArray("datapoints");
    	JSONObject obj3=obj2.getJSONObject(0);
    	int obj4 =obj3.getInt("value");
    	//String obj5=obj3.getString("at");    	
     	System.out.println(obj4);*/
		
    	GetLatesDeviceData api = new GetLatesDeviceData(device_sn,key);
        BasicResponse<DeciceLatestDataPoint> response = api.executeApi();
		return response.getJson();
	}
	
	public Map<String, Object> myEquipment(String relation) {
		List<Sensor> sensors = sensorDao.querySensorByNameAndRelation(relation, null);
 		List<AIO> aios = aioDao.queryAIOByNameAndRelation(relation, null);
		List<Controller> controllers = controllerDao.queryControllerByNameAndRelation(relation, null);
		List<String> conDSN = new ArrayList<String>();
		List<String> aioDSN = new ArrayList<String>();
		//控制器
		//遍历获得设备编号
		if(controllers!=null) {
			for(Controller controller:controllers) {
				if(conDSN==null) {
					conDSN.add(controller.getDevice_sn());
				}else {
					boolean flag = true;
					for(String s:conDSN) {
						if(s.equals(controller.getDevice_sn())) {
							flag = false;
							break;
						}
					}
					if(flag) {
						conDSN.add(controller.getDevice_sn());
					}
				}
			}
		}
		
		if(aios!=null) {
			for(AIO aio:aios) {
				if(aioDSN==null) {
					aioDSN.add(aio.getDevice_sn());
				}else {
					boolean flag = true;
					for(String s:aioDSN) {
						if(s.equals(aio.getDevice_sn())) {
							flag = false;
							break;
						}
					}
					if(flag) {
						aioDSN.add(aio.getDevice_sn());
					}
				}
			}
		}
		
		//根据设备编号获得控制器各路的具体参数
		List<Object> ConResult = new ArrayList<>();
		
		for(String s:conDSN) {
			Map<String, Object> ConResultSE = new HashMap<>();
			List<Controller> cl = new ArrayList<Controller>();
			for(Controller con:controllers) {
				if(con.getDevice_sn().equals(s)) {
					cl.add(con);
				}
			}
			ConResultSE.put("id", s);
			ConResultSE.put("content", cl);
			ConResult.add(ConResultSE);
		}
		//一体机
		//遍历获得所有一体机设备编号
		
		
		//根据设备编号获得一体机各路的具体参数
		List<Object> AioResult = new ArrayList<>();
		
		for(String s:aioDSN) {
			Map<String, Object> AioResultSE = new HashMap<>();
			List<AIO> al = new ArrayList<AIO>();
			for(AIO aio:aios) {
				if(aio.getDevice_sn().equals(s)) {
					al.add(aio);
				}
			}
			AioResultSE.put("id", s);
			AioResultSE.put("content", al);
			logger.debug("id:"+s);
			AioResult.add(AioResultSE);
		}		
		
		List<Object> senResult = new ArrayList<>();
		
		for(Sensor s:sensors) {
			Map<String, Object> senResultSE = new HashMap<>();
			List<Sensor> sl = new ArrayList<Sensor>();
			sl.add(s);
			senResultSE.put("id", s.getDevice_sn());
			senResultSE.put("content", sl);
			senResult.add(senResultSE);
		}
	
		Map<String, Object> result = RESCODE.SUCCESS.getJSONRES();
		result.put("controller", ConResult);
		result.put("aio", AioResult);
		result.put("sensor", senResult);
		if (relation != null && relation.length() > 0) {
			if (relation.contains("WX")) {
				WXUser wxUser = wxUserDao.findUserByRelation(relation);
				result.put("user", wxUser == null ? "" : wxUser.getName());
			} else if (relation.contains("CO")) {
				Company company = companyDao.findCompanyByRelation(relation);
				result.put("user", company == null ? "" : company.getName());
			} else {
				result.put("user", "");
			}
		} else {
			result.put("user", "");
		}
		return result;
	}

	public Map<String, Object> adminFindEquipment(String device_sn, String userName, int page, int number) {
		int from = (page - 1) * number;
		if ((device_sn == null || device_sn.length() < 0) && (userName == null || userName.length() < 0)) {
			return noConditionsQuery(from, number);
		}
		if ((device_sn == null || device_sn.length() < 0)
				&& (userName != null && !userName.isEmpty() && !userName.trim().isEmpty())) {
			return nameConditionQuery(userName, from, number);
		}
		if ((device_sn != null && !device_sn.isEmpty() && !device_sn.trim().isEmpty())
				&& (userName == null || userName.length() < 0)) {
			return deviceSnConditionQuery(device_sn, from, number);
		}
		return doubleConditionQuery(device_sn, userName, from, number);
	}

	private Map<String, Object> doubleConditionQuery(String device_sn, String userName, int from, int number) {
		List<Equipment> equipments = new ArrayList<>();
		List<Company> companies = companyDao.companies(userName);
		List<WXUser> wxUsers = wxUserDao.wxUsers(userName);
		List<String> relations = new ArrayList<>();
		for (Company company : companies) {
			relations.add(company.getRelation());
		}
		for (WXUser wxUser : wxUsers) {
			relations.add(wxUser.getRelation());
		}
		if (relations.isEmpty()) {
			return RESCODE.SUCCESS.getJSONRES(equipments, 0, 0);
		} else {
			equipments = pondDao.adminFindEquipmentDouble(device_sn, relations, from, number);
			shareDealMethod(equipments);
			long count = pondDao.adminFindEquipmentCountDouble(device_sn, relations);
			int size = (int) Math.ceil(count / (double) number);
			return RESCODE.SUCCESS.getJSONRES(equipments, size, count);
		}
	}

	private Map<String, Object> deviceSnConditionQuery(String device_sn, int from, int number) {
		List<Equipment> equipments = pondDao.adminFindEquipmentBySn(device_sn);
		shareDealMethod(equipments);
		long count = pondDao.adminFindEquipmentCountSn(device_sn);
		int size = (int) Math.ceil(count / (double) number);
		return RESCODE.SUCCESS.getJSONRES(equipments, size, count);
	}

	private Map<String, Object> nameConditionQuery(String userName, int from, int number) {
		List<Equipment> equipments = new ArrayList<>();
		List<Company> companies = companyDao.companies(userName);
		List<WXUser> wxUsers = wxUserDao.wxUsers(userName);
		List<String> relations = new ArrayList<>();
		for (Company company : companies) {
			relations.add(company.getRelation());
		}
		for (WXUser wxUser : wxUsers) {
			relations.add(wxUser.getRelation());
		}
		if (relations.isEmpty()) {
			return RESCODE.SUCCESS.getJSONRES(equipments, 0, 0);
		} else {
			equipments = pondDao.adminFindEquipmentByName(relations, from, number);
			shareDealMethod(equipments);
			long count = pondDao.adminFindEquipmentCountName(relations);
			int size = (int) Math.ceil(count / (double) number);
			return RESCODE.SUCCESS.getJSONRES(equipments, size, count);
		}
	}

	private List<Equipment> shareDealMethod(List<Equipment> equipments) {
		for (Equipment equipment : equipments) {
			type = equipment.getDevice_sn().substring(0, 2);
			switch (type) {
				case "01":
					aio = aioDao.findAIOByDeviceSns(equipment.getDevice_sn());
					relation = aio.getRelation();
					if (relation == null) {
						equipment.setName("");
						equipment.setRelation("0");
					} else {
						if (relation.contains("CO")) {
							company = companyDao.findCompanyByRelation(relation);
							if (company == null) {
								equipment.setName("");
								equipment.setRelation("0");
							} else {
								equipment.setUserName(company.getName());
								equipment.setRelation(relation);
							}
						} else if (relation.contains("WX")) {
							wxUser = wxUserDao.findUserByRelation(relation);
							if (wxUser == null) {
								equipment.setName("");
								equipment.setRelation("0");
							} else {
								equipment.setUserName(wxUser.getName());
								equipment.setRelation(relation);
							}
						} else {
							equipment.setName("");
							equipment.setRelation("0");
						}
					}
					break;
				case "02":
					aio = aioDao.findAIOByDeviceSns(equipment.getDevice_sn());
					relation = aio.getRelation();
					if (relation == null) {
						equipment.setName("");
						equipment.setRelation("0");
					} else {
						if (relation.contains("CO")) {
							company = companyDao.findCompanyByRelation(relation);
							if (company == null) {
								equipment.setName("");
								equipment.setRelation("0");
							} else {
								equipment.setUserName(company.getName());
								equipment.setRelation(relation);
							}
						} else if (relation.contains("WX")) {
							wxUser = wxUserDao.findUserByRelation(relation);
							if (wxUser == null) {
								equipment.setName("");
								equipment.setRelation("0");
							} else {
								equipment.setUserName(wxUser.getName());
								equipment.setRelation(relation);
							}
						} else {
							equipment.setName("");
							equipment.setRelation("0");
						}
					}
					break;
				case "03":
					sensor = sensorDao.findSensorByDeviceSns(equipment.getDevice_sn());
					relation = sensor.getRelation();
					if (relation == null) {
						equipment.setName("");
						equipment.setRelation("0");
					} else {
						if (relation.contains("CO")) {
							company = companyDao.findCompanyByRelation(relation);
							if (company == null) {
								equipment.setName("");
								equipment.setRelation("0");
							} else {
								equipment.setUserName(company.getName());
								equipment.setRelation(relation);
							}
						} else if (relation.contains("WX")) {
							wxUser = wxUserDao.findUserByRelation(relation);
							if (wxUser == null) {
								equipment.setName("");
								equipment.setRelation("0");
							} else {
								equipment.setUserName(wxUser.getName());
								equipment.setRelation(relation);
							}
						} else {
							equipment.setName("");
							equipment.setRelation("0");
						}
					}
					break;
				case "04":
					controller = controllerDao.findControllerByDeviceSns(equipment.getDevice_sn());
					relation = controller.getRelation();
					if (relation == null) {
						equipment.setName("");
						equipment.setRelation("0");
					} else {
						if (relation.contains("CO")) {
							company = companyDao.findCompanyByRelation(relation);
							if (company == null) {
								equipment.setName("");
								equipment.setRelation("0");
							} else {
								equipment.setUserName(company.getName());
								equipment.setRelation(relation);
							}
						} else if (relation.contains("WX")) {
							wxUser = wxUserDao.findUserByRelation(relation);
							if (wxUser == null) {
								equipment.setName("");
								equipment.setRelation("0");
							} else {
								equipment.setUserName(wxUser.getName());
								equipment.setRelation(relation);
							}
						} else {
							equipment.setName("");
							equipment.setRelation("0");
						}
					}
					break;
				default:
					break;
			}
		}
		return equipments;
	}

	private Map<String, Object> noConditionsQuery(int from, int number) {
		List<Equipment> equipments = pondDao.adminFindEquipmentAll(from, number);
		shareDealMethod(equipments);
		long count = pondDao.adminFindEquipmentCountAll();
		int size = (int) Math.ceil(count / (double) number);
		return RESCODE.SUCCESS.getJSONRES(equipments, size, count);
	}

	public Map<String, Object> companyFindEquipment(String device_sn, String relation, int page, int number) {
		int from = (page - 1) * number;
		Sensor sensor = null;
		Company company = companyDao.findCompanyByRelation(relation);
		if (company == null) {
			return RESCODE.NOT_FOUND.getJSONRES();
		} else {
			List<String> relations = new ArrayList<>();
			relations.add(company.getRelation());
			if (device_sn == null || device_sn.length() < 0) {
				List<Equipment> equipments = pondDao.adminFindEquipmentByName(relations, from, number);
				for (Equipment equipment : equipments) {
					String type = equipment.getDevice_sn().substring(0, 2);
					if (type.equals("03")) {
						sensor = sensorDao.findSensorByDeviceSns(equipment.getDevice_sn());
						if (sensor == null) {
							equipment.setSensorId(0);
						} else {
							equipment.setSensorId(sensor.getId());
						}
					} else {
						equipment.setSensorId(0);
					}
				}
				long count = pondDao.adminFindEquipmentCountName(relations);
				int size = (int) Math.ceil(count / (double) number);
				Map<String, Object> map = RESCODE.SUCCESS.getJSONRES(equipments, size, count);
				map.put("user", company.getName());
				return map;
			} else {
				List<Equipment> equipments = pondDao.adminFindEquipmentDouble(device_sn, relations, from, number);
				long count = pondDao.adminFindEquipmentCountDouble(device_sn, relations);
				int size = (int) Math.ceil(count / (double) number);
				Map<String, Object> map = RESCODE.SUCCESS.getJSONRES(equipments, size, count);
				map.put("user", company.getName());
				return map;
			}
		}
	}

	public String dataToday(String device_sn, int way) {
		/*List<Sensor_Data> list = new ArrayList<>();
		if (way > 0) {
			list = sensor_DataDao.today(device_sn, way);
		} else {
			list = sensor_DataDao.today(device_sn);
		}
	//	addVirtualData(list);
		List<PH> phs = new ArrayList<>();
		List<Oxygen> oxygens = new ArrayList<>();
		List<Temperature> temperatures = new ArrayList<>();
		PH ph = null;
		Oxygen oxygen = null;
		Temperature temperature = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// for (Sensor_Data sensor_Data : list) {
		Sensor_Data sensor_Data = null;
		for (int i = RandomUtils.nextInt(6); i < 288; i = i + 6) {
			try {
				sensor_Data = list.get(i);
				ph = new PH(sensor_Data.getpH_value(), format.format(sensor_Data.getReceiveTime()));
				oxygen = new Oxygen(sensor_Data.getOxygen(), format.format(sensor_Data.getReceiveTime()));
				temperature = new Temperature(sensor_Data.getWater_temperature(),
						format.format(sensor_Data.getReceiveTime()));
				phs.add(ph);
				oxygens.add(oxygen);
				temperatures.add(temperature);
			} catch (Exception e) {
				break;
			}
		}
		Map<String, Object> map = RESCODE.SUCCESS.getJSONRES();
		map.put("phs", phs);
		map.put("oxygens", oxygens);
		map.put("temperatures", temperatures);
		return map;//
*/
		Date date = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm:ss");
		String dataFormat1 = sdf1.format(date);
		String dataFormat2  = sdf2.format(date);
		String dataFormatEnd = dataFormat1+"T"+dataFormat2;
		String dataFormatStart = dataFormat1+"T00:00:00";
		
		GetDatapointsListApi api = new GetDatapointsListApi(null, dataFormatStart, dataFormatEnd, device_sn, null, 6000, null, 137,
				null, null, null, key);
		BasicResponse<DatapointsList> response = api.executeApi();
		return response.getJson();
	}

	public String data3days(String device_sn, int way) {
		Date date = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm:ss");
		String dataFormat1 = sdf1.format(date);
		String dataFormat2  = sdf2.format(date);
		String dataFormatEnd = dataFormat1+"-"+date.getDate()+"T"+dataFormat2;
		String dataFormatStart = dataFormat1+"-"+(date.getDate()-3)+"T"+dataFormat2;
		GetDatapointsListApi api1 = new GetDatapointsListApi("temperature", dataFormatStart, null, device_sn,null,6000, "122320_31380529_1527020009053",null,
				null, null, null, key);
		BasicResponse<DatapointsList> response = api1.executeApi();
		return response.getJson();
	}
	
	public Map<String, Object> dataAll(String device_sn, int way,int day) {
		List<Sensor_Data> list = new ArrayList<>();
		if (way > 0) {
			list = sensor_DataDao.sevenData(device_sn, way,day);
		} else {
			list = sensor_DataDao.sevenData(device_sn,day);
		}
		List<PH> phs = new ArrayList<>();
		List<Oxygen> oxygens = new ArrayList<>();
		List<Temperature> temperatures = new ArrayList<>();
		PH ph = null;
		Oxygen oxygen = null;
		Temperature temperature = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//SimpleDateFormat isSameDay = new SimpleDateFormat("MM-dd");
		//String temp = "";
		List<Sensor_Data> splitlist = new ArrayList<>();
		int i = 0;
		while (i < 2016) {
			try {
				splitlist.add(list.get(i));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				break;
			}
			i = i + 8;
		}
		if (!list.isEmpty()) {
		//	addVirtualData(splitlist);
			for (Sensor_Data sensor_Data : splitlist) {
				/*if (!temp.equals(isSameDay.format(sensor_Data.getReceiveTime()))) {
					temp = isSameDay.format(sensor_Data.getReceiveTime());
					ph = new PH(sensor_Data.getpH_value(), isSameDay.format(sensor_Data.getReceiveTime()));
					oxygen = new Oxygen(sensor_Data.getOxygen(), isSameDay.format(sensor_Data.getReceiveTime()));
					temperature = new Temperature(sensor_Data.getWater_temperature(),
							isSameDay.format(sensor_Data.getReceiveTime()));
					phs.add(ph);
					oxygens.add(oxygen);
					temperatures.add(temperature);
				} else {*/
				ph = new PH(sensor_Data.getpH_value(), format.format(sensor_Data.getReceiveTime()));
				oxygen = new Oxygen(sensor_Data.getOxygen(), format.format(sensor_Data.getReceiveTime()));
				temperature = new Temperature(sensor_Data.getWater_temperature(),
						format.format(sensor_Data.getReceiveTime()));
				phs.add(ph);
				oxygens.add(oxygen);
				temperatures.add(temperature);
				//}
			}
		}

		Map<String, Object> map = RESCODE.SUCCESS.getJSONRES();
		map.put("phs", phs);
		map.put("oxygens", oxygens);
		map.put("temperatures", temperatures);
		return map;//
	}

	public Map<String, Object> pcDataToday(String device_sn, int way) {
		List<Sensor_Data> list = new ArrayList<>();
		if (way > 0) {
			list = sensor_DataDao.today(device_sn, way);
		} else {
			list = sensor_DataDao.today(device_sn);
		}
		//addVirtualData(list);
		List<PH> phs = new ArrayList<>();
		List<Oxygen> oxygens = new ArrayList<>();
		List<Temperature> temperatures = new ArrayList<>();
		PH ph = null;
		Oxygen oxygen = null;
		Temperature temperature = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Sensor_Data sensor_Data = null;
		for (int i = RandomUtils.nextInt(6); i < 288; i = i + 6) {
			try {
				sensor_Data = list.get(i);
				ph = new PH(sensor_Data.getpH_value(), format.format(sensor_Data.getReceiveTime()));
				oxygen = new Oxygen(sensor_Data.getOxygen(), format.format(sensor_Data.getReceiveTime()));
				temperature = new Temperature(sensor_Data.getWater_temperature(),
						format.format(sensor_Data.getReceiveTime()));
				phs.add(ph);
				oxygens.add(oxygen);
				temperatures.add(temperature);
			} catch (Exception e) {
				break;
			}
		}
		Map<String, Object> map = RESCODE.SUCCESS.getJSONRES();
		map.put("phs", phs);
		map.put("oxygens", oxygens);
		map.put("temperatures", temperatures);
		return map;//
	}

	public Map<String, Object> pcDataAll(String device_sn, int way,int day) {
		List<Sensor_Data> list = new ArrayList<>();
		if (way > 0) {
			list = sensor_DataDao.sevenData(device_sn, way,day);
		} else {
			list = sensor_DataDao.sevenData(device_sn,day);
		}
		List<PH> phs = new ArrayList<>();
		List<Oxygen> oxygens = new ArrayList<>();
		List<Temperature> temperatures = new ArrayList<>();
		PH ph = null;
		Oxygen oxygen = null;
		Temperature temperature = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//SimpleDateFormat isSameDay = new SimpleDateFormat("MM-dd");
		//String temp = "";
		List<Sensor_Data> splitlist = new ArrayList<>();
		int i = 0;
		while (i < 2016) {
			try {
				splitlist.add(list.get(i));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				break;
			}
			i = i + 8;
		}
		//addVirtualData(splitlist);
		for (Sensor_Data sensor_Data : splitlist) {
			/*if (!temp.equals(isSameDay.format(sensor_Data.getReceiveTime()))) {
				temp = isSameDay.format(sensor_Data.getReceiveTime());
				ph = new PH(sensor_Data.getpH_value(), isSameDay.format(sensor_Data.getReceiveTime()));
				oxygen = new Oxygen(sensor_Data.getOxygen(), isSameDay.format(sensor_Data.getReceiveTime()));
				temperature = new Temperature(sensor_Data.getWater_temperature(),
						isSameDay.format(sensor_Data.getReceiveTime()));
				phs.add(ph);
				oxygens.add(oxygen);
				temperatures.add(temperature);
			} else {*/
			ph = new PH(sensor_Data.getpH_value(), format.format(sensor_Data.getReceiveTime()));
			oxygen = new Oxygen(sensor_Data.getOxygen(), format.format(sensor_Data.getReceiveTime()));
			temperature = new Temperature(sensor_Data.getWater_temperature(),
					format.format(sensor_Data.getReceiveTime()));
			phs.add(ph);
			oxygens.add(oxygen);
			temperatures.add(temperature);
			/*}*/
		}

		Map<String, Object> map = RESCODE.SUCCESS.getJSONRES();
		map.put("phs", phs);
		map.put("oxygens", oxygens);
		map.put("temperatures", temperatures);
		return map;//
	}

	public Map<String, Object> setTimer(Timer timer) {
		Timer exist = timerDao.findTimerByDeviceSnAndWay(timer.getDevice_sn(),timer.getWay());
		if (exist == null) {
			timerDao.save(timer);
			return RESCODE.SUCCESS.getJSONRES();
		} else {
			timer.setId(exist.getId());
			timerDao.updateTimer(timer);
			return RESCODE.SUCCESS.getJSONRES();
			}

	}

	public Map<String, Object> modifyEquipment(String device_sn, Object newEquipment) {
		newEquipment.getClass().getName();
		/*String type = null;
		Map<String, Object> map = null;
		try {
			type = device_sn.substring(0, 2);
		} catch (Exception e) {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}
		if (type.equals("01") || type.equals("02")) {
			AIO aio = aioDao.findAIOByDeviceSns(device_sn);
			aio.setName(name);
			map = RESCODE.SUCCESS.getJSONRES();
			map.put("equipment", aio);
		} else if (type.equals("03")) {
			Sensor sensor = sensorDao.findSensorByDeviceSns(device_sn);
			sensor.setName(name);
			map = RESCODE.SUCCESS.getJSONRES();
			map.put("equipment", sensor);
		} else if (type.equals("04")) {
			Controller controller = controllerDao.findControllerByDeviceSns(device_sn);
			controller.setName(name);
			map = RESCODE.SUCCESS.getJSONRES();
			map.put("equipment", controller);
		}

		return map;*/
		return null;
	}
	
	public Map<String, Object> modifySensor(Sensor sensor){
		if(deviceDao.findDevice(sensor.getDevice_sn())==null) {
			return RESCODE.ACCOUNT_NOT_EXIST.getJSONRES();
		}else {
			sensorDao.updateSensor(sensor);
			return RESCODE.SUCCESS.getJSONRES();
		}
	}
	
	public Map<String, Object> modifyAio(AIO...aios){
		String DSN = aios[0].getDevice_sn();
		boolean flag = true;
		//判断一体机是否存在
		if(deviceDao.findDevice(DSN)==null) {
			flag = false;
		}
		/*if(aioDao.findAIOByDeviceSns(DSN)==null) {
			flag = false;
		}*/
		//判断是否为一个一体机的多路
		for(AIO aio:aios) {
			if(aio.getDevice_sn().equals(DSN)==false) {
				flag = false;
				break;
			}
		}
		if(flag) {
			for(AIO aio:aios) {
				logger.debug("进入aio循环");
			//	aioDao.updateByAioId(aio);
				aioDao.update(aio);
			}
			return RESCODE.SUCCESS.getJSONRES();
		}else {
			return RESCODE.WRONG_PARAM.getJSONRES();
		}
	}
	
	public Map<String, Object> modifyController(Controller...controller){
		String DSN = controller[0].getDevice_sn();
		boolean flag = true;
		//判断控制器是否存在
		if(deviceDao.findDevice(DSN)==null) {
			flag = false;
		}
		/*if(aioDao.findAIOByDeviceSns(DSN)==null) {
			flag = false;
		}*/
		//判断是否为一个控制器的多路
		for(Controller con:controller) {
			if(con.getDevice_sn().equals(DSN)==false) {
				flag = false;
				break;
			}
		}
		if(flag) {
			for(Controller con:controller) {
				logger.debug("进入控制器循环");
			//	aioDao.updateByAioId(aio);
				//aioDao.update(aio);
				controllerDao.updateController(con);
			}
			return RESCODE.SUCCESS.getJSONRES();
		}else {
			return RESCODE.WRONG_PARAM.getJSONRES();
		}
	} 

		
	public void triggeractive(String data){
		JSONObject tempjson = JSONObject.fromObject(data);

		JSONObject temp12 = tempjson.getJSONObject("trigger");
		int triggerid=temp12.getInt("id");
		String triggertype=temp12.getString("type");
		JSONArray currentdata=tempjson.getJSONArray("current_data");
		JSONObject obj1 = currentdata.getJSONObject(0);
		String dev_id= obj1.getString("dev_id");
		String ds_id= obj1.getString("ds_id");

		Device device=deviceDao.findDevice(dev_id);
		if (device!=null){
		   int type= device.getType();
		   if(type==1){

		   int pond_id=sensorDao.findSensorByDeviceSns(dev_id).getPondId();
            Pond pond = pondDao.findPondByPondId(pond_id);
            //Controller controller=

           }else if(type==2){


           }else if(type==3){



           }



        }


		System.out.println(dev_id);
		System.out.println(ds_id);
		System.out.println(""+triggerid);

		//String temp13 = temp12.getString("cmd_uuid");
	//	System.out.println(temp13);
//    	JSONArray array= temp.getJSONArray("datastreams");
//    	JSONObject obj1 = array.getJSONObject(0);
//    	JSONArray obj2 =obj1.getJSONArray("datapoints");
//    	JSONObject obj3=obj2.getJSONObject(0);
//    	int obj4 =obj3.getInt("value");
//    	//String obj5=obj3.getString("at");
//     	System.out.println(obj4);

	}


	public int addTrigerbyFishtype(String device_sn,int fishtype){
	    if (fishtype==0) {
            int trigger1 = addTrigger("pressure", device_sn, "==", 100, 3);
            if (trigger1 == 0) {
                int trigger2 = addTrigger("pressure", device_sn, "==", 99, 3);
                if (trigger2 == 0) {
                    int trigger3 = addTrigger("pressure", device_sn, "==", 98, 3);
                    if (trigger3 == 0) {
                        return 0;
                    } else return 1;
                } else return 1;
            } else return 1;
        }else if (fishtype==1) {
            int trigger1 = addTrigger("Battery", device_sn, "==", 100, 3);
            if (trigger1 == 0) {
                int trigger2 = addTrigger("Battery", device_sn, "==", 99, 3);
                if (trigger2 == 0) {
                    int trigger3 = addTrigger("Battery", device_sn, "==", 98, 3);
                    if (trigger3 == 0) {
                        return 0;
                    } else return 1;
                } else return 1;
            } else return 1;
        }else if (fishtype==3) {
            int trigger1 = addTrigger("Battery", device_sn, "==", 100, 3);
            if (trigger1 == 0) {
                int trigger2 = addTrigger("Battery", device_sn, "==", 99, 3);
                if (trigger2 == 0) {
                    int trigger3 = addTrigger("Battery", device_sn, "==", 98, 3);
                    if (trigger3 == 0) {
                        return 0;
                    } else return 1;
                } else return 1;
            } else return 1;
        } else return 1;
    }

	public int addTrigger(String dsid,String device_sn,String type,int threshold,int localtype){
		/**
		 * 触发器新增
		 * @param title:名称（可选）,String
		 * @param dsid:数据流名称（id）（可选）,String
		 * @param devids:设备ID（可选）,List<String>
		 * @param dsuuids:数据流uuid（可选）,List<String>
		 * @param desturl:url,String
		 * @param type:触发类型，String
		 * @param threshold:阙值，根据type不同，见以下说明,Integer
		 * @param key:masterkey 或者 设备apikey
		 */
		String url = "http://xx.bb.com";
		List<String> devids=new ArrayList<String>();
		devids.add(device_sn);
		String key = "LTKhU=GLGsWmPrpHICwWOnzx=bA=";
		int triggerid;

		AddTriggersApi api = new AddTriggersApi(null, dsid, devids, null, url, type, threshold, key);
		try{
			BasicResponse<NewTriggersResponse> response = api.executeApi();
			System.out.println(response.getJson());
			JSONObject tempjson = JSONObject.fromObject(response.getJson());
			int errnoint = tempjson.getInt("errno");
			if (errnoint==0){
				JSONObject triobj = tempjson.getJSONObject("data");
				triggerid = triobj.getInt("trigger_id");
				Dev_Trigger trigger = new Dev_Trigger();
				trigger.setDevice_sn(device_sn);
				trigger.setTriger_id(String.valueOf(triggerid));
				trigger.setTrigertype(localtype);
				dev_triggerDao.save(trigger);
				return 0;
			}else return 1;
		}catch(Exception e){
			System.out.println(e.getMessage());
			return 1;
		}
	}



}
