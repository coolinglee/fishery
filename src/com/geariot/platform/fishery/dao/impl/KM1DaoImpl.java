package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.KM1Dao;
import com.geariot.platform.fishery.entities.KM1;
@Repository
public class KM1DaoImpl implements KM1Dao {
	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	@Override
	public void save(KM1 km1) {
		// TODO Auto-generated method stub
		this.getSession().save(km1);
	}

}
