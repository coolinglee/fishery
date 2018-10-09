import request from '../utils/request';
import { stringify } from 'qs';

export async function queryEquipment(params) {
    return request(`/api/equipment/query?${stringify(params)}`);
}

export async function getAllEquipment(params) {
    return request(`/api/equipment/myEquipment?${stringify(params)}`);
}

export async function myEquipment(params){
    return request(`/api/pond/relationEquipment?${stringify(params)}`);
}


export async function companyFindEquipment(params) {
    return request(`/api/equipment/companyFindEquipment?${stringify(params)}`);
}


export async function queryAdminEquipment(params) {
    return request(`/api/equipment/adminFindEquipment?${stringify(params)}`);
}

export async function getPond(params) {
  return request(`/api/equipment/adminFindEquipment?${stringify(params)}`);
}

export async function getDataToday(params) {          //获得设备数据汇总  （曲线图数据）
    return request(`api/equipment/dataToday?${stringify(params)}`);
}

export async function getDataAll(params) {          //获得设备数据汇总  （曲线图数据）
    return request(`api/equipment/data3days?${stringify(params)}`);
}

export async function getRealTimeData(params) {          //获得设备实时数据
    return request(`api/equipment/realTimeData?${stringify(params)}`);
}


export async function addEquipment(params) {
    return request(`/api/equipment/add?${stringify(params)}`);
}

export async function CompanyDelEquipments(params) {
    return request(`/api/equipment/CompanyDelEquipments?${stringify(params)}`);
}

export async function modifyEquipment(params) {
  return request(`/api/equipment/modifyEquipment?${stringify(params)}`);
}

export async function delEquipments(params) {
  console.log(params)
    let str = ''
    for (let item of params.device_sn) {
        str = 'device_sn=' + item + '&' + str
    }
    str = str.slice(0, -1)
    return request(`/api/equipment/CompanyDelEquipments?${str}`)
}

export async function realTimeData(params) {
    return request(`/api/equipment/realTimeData?${stringify(params)}`);
}

export async function aeratorOnOff(params) {        //开关增氧机
    return request(`/api/equipment/aeratorOnOff?${stringify(params)}`);
}

export async function autoSet(params) {             //设置定时增氧
    return request('/api/equipment/autoSet', {
        method: 'POST',
        body: params
    });
}
