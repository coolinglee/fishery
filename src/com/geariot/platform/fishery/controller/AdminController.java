/**
 * 
 */
package com.geariot.platform.fishery.controller;

import com.geariot.platform.fishery.entities.Admin;
import com.geariot.platform.fishery.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author mxy940127
 *
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;
	
	@RequestMapping(value = "/add" , method = RequestMethod.POST)
	public Map<String,Object> addAdmin(@RequestBody Admin admin){
		return adminService.add(admin);
	}
	
	@RequestMapping(value = "/login" , method = RequestMethod.GET)
	public Map<String,Object> login(String account, String password){
		return adminService.login(account, password);
	}
	
	@RequestMapping(value = "/logout" , method = RequestMethod.GET)
	public Map<String,Object> logout(){
		return adminService.logout();
	}
	
	@RequestMapping(value = "/modify" , method = RequestMethod.GET)
	public Map<String,Object> modifyPSW(int adminId, String password){
		return adminService.modifyPSW(adminId, password);
	}
	
	@RequestMapping(value = "/modifyCompany" , method = RequestMethod.GET)
	public Map<String,Object> modifyCompanyPSW(int companyId, String password){
		return adminService.modifyCompanyPSW(companyId, password);
	}
}
