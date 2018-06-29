package com.avenuecode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;

@Entity
public class Graph {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition = "text")
    private String data;

	/**
	* Returns value of id
	* @return
	*/
	public Long getId() {
		return id;
	}

	/**
	* Sets new value of id
	* @param
	*/
	public void setId(Long id) {
		this.id = id;
	}

	/**
	* Returns value of data
	* @return
	*/
	public String getData() {
		return data;
	}

	/**
	* Sets new value of data
	* @param
	*/
	public void setData(String data) {
		this.data = data;
	}
}
