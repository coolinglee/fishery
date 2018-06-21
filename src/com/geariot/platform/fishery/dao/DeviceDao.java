/**
 *
 */
package com.geariot.platform.fishery.dao;

import java.util.List;

import com.geariot.platform.fishery.entities.Fish_Category;
import com.geariot.platform.fishery.entities.Device;
import com.geariot.platform.fishery.model.Equipment;

/**
 * @author plong
 *
 */
public interface DeviceDao {

    void save(Device device);
    int delete(String devicesn);
    Device findDevice(String deviceSns);
}