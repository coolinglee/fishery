package com.geariot.platform.fishery.entities;

/**
 * @author plong
 *
 */

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="javaClassName")
public class Dev_Trigger {
	private int id;						//触发器本地Id,自增
	private String device_sn;            //设备id
	private String trigger_id;               //触发器id
	private int triggertype;                //触发器类型
	private int way;                //触发器属于哪一路
	/*
	 * 0.预警
	 * 1.危险
	 * 2.增氧机上下限触发
	 * 3.控制器缺相/断电
	 * 4.正常
	 * 5.增氧机高限
	 */
	

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDevice_sn() {
		return device_sn;
	}
	public void setDevice_sn(String device_sn) {
		this.device_sn = device_sn;
	}
	public String getTriger_id() {
		return trigger_id;
	}

	public void setTriger_id(String triger_id) {
		this.trigger_id = triger_id;
	}
	public int getTrigertype() {
		return triggertype;
	}
	public void setTrigertype(int trigertype) {
		this.triggertype = trigertype;
	}
	public int getWay() {
		return way;
	}
	public void setWay(int way) {
		this.way = way;
	}
	
}
