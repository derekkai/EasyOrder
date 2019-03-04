package com.example.ordermeals;

import java.util.ArrayList;
import java.util.Date;

public class C4_cook_meals_sort {
	public String name;
	public int number;
	public Date firsttime ;
	public ArrayList<C3_cook_meals> cook_meals = new ArrayList<C3_cook_meals>();
	C4_cook_meals_sort(String name,int number,Date firsttime,C3_cook_meals c3_cook_meals){
		this.name = name;
		this.number = number;
		this.firsttime = firsttime;
		this.cook_meals.add(c3_cook_meals);
	}
}
