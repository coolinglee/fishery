package com.geariot.platform.fishery.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="javaClassName")
public class PondFish {

	private int id;
	private String fish_name;
	private int type;
	/*
	 * 1.鱼
	 * 2.虾
	 * 3.蟹
	 */
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getFish_name() {
		return fish_name;
	}
	public void setFish_name(String fish_name) {
		this.fish_name = fish_name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
}
