package com.example.ordermeals;

import java.util.ArrayList;

public class C1_inside_list {
	public int tableid;	
	public int totalmoney=0;
	public int number=0;
	public boolean used = false;
	public boolean certain = false;
	public ArrayList<C2_meals> meals = new ArrayList<C2_meals>();
	C1_inside_list(int tableid){
		this.tableid = tableid;
	}
	public void Clear(){
		totalmoney = 0;
		number = 0;
		used = false;
		certain = false;
		meals.clear();
	}
}
