package com.geariot.platform.fishery.entities;

/**
 * @author mxy940127
 *
 */

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Pond {

	private int id;						//塘口Id,自增
	private String name;				//塘口名称
	private float area;					//面积,单位:亩
	private String address;				//塘口位置
	private float longitude;			//塘口经度
	private float latitude;				//塘口纬度
	private List<String> fish_category;	//塘口鱼种
	private String water_source;		//塘口水源
	private float sediment_thickness;	//底泥厚度
	private float depth;				//塘口深度
	private float density;				//塘口密度
	private String relation;			//普通用户或企业用户的relationId;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getArea() {
		return area;
	}
	public void setArea(float area) {
		this.area = area;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public float getLongitude() {
		return longitude;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	public float getLatitude() {
		return latitude;
	}
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	@ElementCollection
	public List<String> getFish_category() {
		return fish_category;
	}
	public void setFish_category(List<String> fish_category) {
		this.fish_category = fish_category;
	}
	public String getWater_source() {
		return water_source;
	}
	public void setWater_source(String water_source) {
		this.water_source = water_source;
	}
	public float getSediment_thickness() {
		return sediment_thickness;
	}
	public void setSediment_thickness(float sediment_thickness) {
		this.sediment_thickness = sediment_thickness;
	}
	public float getDepth() {
		return depth;
	}
	public void setDepth(float depth) {
		this.depth = depth;
	}
	public float getDensity() {
		return density;
	}
	public void setDensity(float density) {
		this.density = density;
	}
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}
}
