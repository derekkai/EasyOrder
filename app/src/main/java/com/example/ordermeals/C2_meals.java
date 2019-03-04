package com.example.ordermeals;

public class C2_meals {
	public String sort;
	public int id;
	public String name;
	public int money;
	public int number=0;
	C2_meals(int id,String name,int money){
		this.id = id;
		this.name = name;
		this.money = money;
	}
	C2_meals(C2_meals meals,int number){
		this.id = meals.id;
		this.name = meals.name;
		this.money = meals.money;
		this.number = number;
	}
}
