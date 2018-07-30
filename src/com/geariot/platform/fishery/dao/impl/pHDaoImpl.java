package com.geariot.platform.fishery.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.geariot.platform.fishery.dao.pHDao;
import com.geariot.platform.fishery.entities.pH;
@Repository
public class pHDaoImpl implements pHDao {
	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	@Override
	public void save(pH ph) {
		this.getSession().save(ph);
	}

}
