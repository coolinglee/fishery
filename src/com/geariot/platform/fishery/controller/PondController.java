package com.geariot.platform.fishery.controller;

import com.geariot.platform.fishery.entities.Pond;
import com.geariot.platform.fishery.service.PondService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pond")
public class PondController {
	
	@Autowired
	private PondService pondService;
	
	@RequestMapping(value = "/addPond" , method = RequestMethod.POST)
	public Map<String,Object> addPond(@RequestBody Pond pond){
		return pondService.addPond(pond);
	}
	
	@RequestMapping(value = "/delPonds" , method = RequestMethod.GET)
	public Map<String,Object> delPonds(Integer... pondIds){
		return pondService.delPonds(pondIds);
	}
	
	@RequestMapping(value = "/modifyPond" , method = RequestMethod.POST)
	public Map<String,Object> modifyPond(@RequestBody Pond pond){
		return pondService.modifyPond(pond);
	}
	
	@RequestMapping(value = "/query" , method = RequestMethod.GET)
	public Map<String,Object> queryPond(String relation, String name, int page, int number){
		return pondService.queryPond(relation, name, page, number);
	}
	
	@RequestMapping(value = "/WXquery" , method = RequestMethod.GET)
	public Map<String,Object> queryPond(String relation){
		return pondService.WXqueryPond(relation);
	}
	
	@RequestMapping(value = "/pondEquipment" , method = RequestMethod.GET)
	public Map<String,Object> pondEquipment(int pondId, int page, int number){
		return pondService.pondEquipment(pondId, page, number);
	}
	
	@RequestMapping(value = "/fish" , method = RequestMethod.GET)
	public Map<String,Object> fishCateList(){
		return pondService.fishCateList();
	}
	
/*	@RequestMapping(value = "/apphomepage" , method = RequestMethod.GET)
	public Map<String,Object> appHomepage(String relation){
		return pondService.appHomepage(relation);
	}
	*/
	@RequestMapping(value = "/pondDetail" , method = RequestMethod.GET)
	public Map<String, Object> pondDetail(int pondId){
		return pondService.pondDetail(pondId);
	}
	
	@RequestMapping(value = "/relationEquipment" , method = RequestMethod.GET)
	public Map<String, Object> relationEquipment(String relation, int page, int number){
		return pondService.relationEquipment(relation, page, number);
	}
	
	@RequestMapping(value = "/pondHasSensor" , method = RequestMethod.GET)
	public int relationEquipment(String pondId){
		int pId = Integer.parseInt(pondId);
		//返回1表示已使用，返回0表示未使用
		return pondService.pondHasSensor(pId);
	}
	
	
}
