package com.yc.yechweather.model;


/**
 * 省份类
 * @author Administrator
 *
 */
public class Province {
	private int id;
	private String provinceName;//省份名称
	private String provinceCode;//省份代码
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public String getProvinceCode() {
		return provinceCode;
	}
	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}
	
}
