package com.example.ordermeals;

import java.util.ArrayList;
import java.util.Date;

public class C1_delivery_list {
	public String username;
	public String phone;
	public String address;
	public Date time;
	public int totalmoney = 0;
	public boolean certain = false;
	public ArrayList<C2_meals> meals = new ArrayList<C2_meals>();
	
	C1_delivery_list(String username,String phone,String address,Date time){
		this.username = username;
		this.phone = phone;
		this.address = address;
		this.time = time;
	}
}
