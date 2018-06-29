package com.avenuecode;

public class Route {

    private String source;
    private String target;
    private Integer distance;

	/**
	* Returns value of source
	* @return
	*/
	public String getSource() {
		return source;
	}

	/**
	* Sets new value of source
	* @param
	*/
	public void setSource(String source) {
		this.source = source;
	}

	/**
	* Returns value of target
	* @return
	*/
	public String getTarget() {
		return target;
	}

	/**
	* Sets new value of target
	* @param
	*/
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	* Returns value of distance
	* @return
	*/
	public Integer getDistance() {
		return distance;
	}

	/**
	* Sets new value of distance
	* @param
	*/
	public void setDistance(Integer distance) {
		this.distance = distance;
	}
}
