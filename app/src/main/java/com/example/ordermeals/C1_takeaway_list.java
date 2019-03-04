package com.example.ordermeals;

import java.util.ArrayList;
import java.util.Date;

public class C1_takeaway_list {
	public String username;
	public String phone;
	public Date time;
	public int totalmoney=0;
	public boolean certain = false;
	public ArrayList<C2_meals> meals = new ArrayList<C2_meals>();
	C1_takeaway_list(){
		
	}
	C1_takeaway_list(String username,String phone,Date time){
		this.username = username;
		this.phone = phone;
		this.time = time;
	}
}
