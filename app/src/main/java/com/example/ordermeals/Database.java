package com.example.ordermeals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.os.Handler;

public class  Database{
	
	final static String driver = "com.mysql.jdbc.Driver";
	final static String url = "jdbc:mysql://192.168.56.1:3306/order_server";
	final static String url2 = "jdbc:mysql://192.168.56.1:3306/order_meals";
	final static String url3 = "jdbc:mysql://192.168.56.1:3306/order_cook";
	final static String url4 = "jdbc:mysql://192.168.56.1:3306/order_ctos";
	final static String username = "root3";
	final static String password = "1234";
	
	final static String selectSQL = "select * from account ";
	final static String selectSQL2 = "select * from ";
	final static String insertSQL =
			  "insert into account(account,password,address,phone,nfuYN,storeID,last_log_in,havemeals) " + "values (?,?,?,?,?,?,?,?)";
	private static Connection con = null;
	private static Connection con2 = null;
	private static Statement stat = null;
	private static ResultSet rs = null;
	private static PreparedStatement pst = null; 
	
	private static Connection C2Scon = null;
	private static Statement C2Sstat = null;
	private static ResultSet C2Srs = null;
	
	public static void aa(){
		try{
			Class.forName(driver);
			System.out.println("開始連線order_ctos");
			con = DriverManager.getConnection(url4,username,password);
			System.out.println("連線成功");
			
			pst = con.prepareStatement("insert into "+"dmaccount"+" (username,phone,address,meals,type) " + "values (?,?,?,?,?)");

    			pst.setString(1,"demo");
    			pst.setString(2,"0912345678");
        		pst.setString(3,"");
        		pst.setString(4,"1a1a5");
        		pst.setString(5,"T");
        		pst.executeUpdate();
		}catch (SQLException e) {

			e.printStackTrace();
		}catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
	}
 	public static void Connect(){
		try{
			Class.forName(driver);
			System.out.println("開始連線order_server");
			con = DriverManager.getConnection(url,username,password);
			System.out.println("連線成功");
		}catch (SQLException e) {

			e.printStackTrace();
		}catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
	}
	public static void ConnectToMeals(){
		try{
			Class.forName(driver);
			System.out.println("開始連線到order_meals");
			con = DriverManager.getConnection(url2,username,password);
			System.out.println("連線成功");
		}catch (SQLException e) {

			e.printStackTrace();
		}catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
	}
	public static void ConnectToCook(){
		try{
			Class.forName(driver);
			System.out.println("開始連線到order_cook");
			con = DriverManager.getConnection(url3,username,password);
			System.out.println("連線成功");
		}catch (SQLException e) {

			e.printStackTrace();
		}catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
	}
	public static void ConnectToC2S(){
		try{
			Class.forName(driver);
			System.out.println("開始連線到order_ctos");
			con2 = DriverManager.getConnection(url4,username,password);
			System.out.println("連線成功");
		}catch (SQLException e) {

			e.printStackTrace();
		}catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
	}
	public static void Close(){
		try{
			if(con!=null){
				con.close();
				con = null;
			}
			if(stat!=null){
				stat.close();
				stat = null;
			}
			if(rs!=null){
				rs.close();
				rs = null;
			}
			if(pst!=null){
				pst.close();
				pst = null;
			}
		}catch(SQLException e){ 
		      System.out.println("Close Exception :" + e.toString()); 
		} 
	}

	public static String VerifyAccount(String account,String password){
		try {
			stat = con.createStatement();
			rs = stat.executeQuery(selectSQL);
			while(rs.next()){
				if(rs.getString("account").toString().equals(account)
						&&rs.getString("password").toString().equals(password)){
					UI2_login_page.havemeals = rs.getString("havemeals");
					return rs.getString("storeID").toString();
					
				}
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return "non-Verify";
	}

	public static boolean Register(String account,String password,String address,String phone,String NFUS){
		try {
			pst = con.prepareStatement(insertSQL);
			pst.setString(1, account);
			pst.setString(2, password);
			pst.setNString(3, address);
			pst.setString(4, phone);
			pst.setString(5, NFUS);
			pst.setString(6, account);
			pst.setString(7, "first_time");
			pst.setString(8, "N");
			pst.executeUpdate();
			System.out.println("寫入結束");
			ConnectToMeals();
			stat = con.createStatement();
			//stat.executeUpdate("create table "+account+"(id int)");
			stat.executeUpdate("CREATE TABLE "+account+"(id INTEGER,name VARCHAR(10),money INTEGER,sort VARCHAR(10),Evaluation_score INTEGER,hot_level INTEGER,calories INTEGER,message VARCHAR(30),PRIMARY KEY(id))");
			//stat.executeUpdate("create table "+account+"(id int,name char(10),money int,sort char(10),Evaluation_score int,hot_level int,calories int,message char(30),primary key(id))");
			ConnectToC2S();
			stat = con2.createStatement();
			stat.executeUpdate("CREATE TABLE "+account+"(username VARCHAR(12),phone VARCHAR(10),address VARCHAR(30),meals VARCHAR(100),type VARCHAR(2))");
			ConnectToCook();
			stat = con.createStatement();
			stat.executeUpdate("CREATE TABLE "+account+"(id INTEGER,name VARCHAR(10),number INTEGER, time TIMESTAMP,PRIMARY KEY(id))");
		} catch (SQLException e) {

			System.out.println(e);
			System.out.println("此帳號註冊過!");
			return false;
		}con2 = null;
		return true;
	}
	
	public static void GetDatabaseMeals(String storeID){
		try {
			stat = con.createStatement();
			rs = stat.executeQuery(selectSQL2+storeID);
			String sort="",Psort="";
			int index=-1,id=0;
			while(rs.next()){
				sort = rs.getString("sort").toString();
				if(!sort.equals(Psort)){
					UI4_order_page.mealssort.add(sort);
					UI4_order_page.meals.add(new ArrayList<C2_meals>());
					index++;
				}
				System.out.println(++id+" "+rs.getString("name")+" "+rs.getInt("money"));
				Psort = sort;
				UI4_order_page.meals.get(index).add(new C2_meals(id,rs.getString("name"),rs.getInt("money")));
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}
	public static void WriteTimeBack(){
		try {
			stat = con.createStatement();
			stat.executeUpdate("update account set last_log_in='"+UI4_order_page.login_time+"' where account='"+UI4_order_page.account+"'");
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}
	public static void GetInfoFromDB(){
		try {
			stat = con.createStatement();
			rs = stat.executeQuery(selectSQL);
			while(rs.next()){
				String account = rs.getString("account").toString();
				if(account.equals(UI4_order_page.account)){
					UI8_user_page.phone = rs.getString("phone");
					UI8_user_page.address = rs.getString("address");
					UI8_user_page.login = rs.getString("last_log_in");
					UI8_user_page.nfuYN = rs.getString("nfuYN");	
				}	
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		
	}
	public static void EditUserInfo(String phone,String address,boolean NFUcheck){
		String uSQL = "update account set address=? where account='"+UI4_order_page.account+"'";
		try {
			pst = con.prepareStatement(uSQL);
			stat = con.createStatement();
			stat.executeUpdate("update account set phone='"+phone+"' where account='"+UI4_order_page.account+"'");
			pst.setNString(1, address);
			pst.executeUpdate();
			if(NFUcheck)
				stat.executeUpdate("update account set nfuYN='Y' where account='"+UI4_order_page.account+"'");
			else
				stat.executeUpdate("update account set nfuYN='N' where account='"+UI4_order_page.account+"'");
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}
	public static void TTraMealsToCook(int item){
		try{
			//SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
        	pst = con.prepareStatement("insert into "+UI4_order_page.storeID+" (id,name,number,time) " + "values (?,?,?,?)");
        	for(int i=0;i<UI6_takeaway_list.takeaway_list_array.get(item).meals.size();i++){
        		pst.setInt(1,++UI4_order_page.mealscount);
        		pst.setNString(2, UI6_takeaway_list.takeaway_list_array.get(item).meals.get(i).name);
            	pst.setInt(3, UI6_takeaway_list.takeaway_list_array.get(item).meals.get(i).number);
            	pst.setTimestamp(4, new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
            	pst.executeUpdate();
        	}
		}catch (SQLException e) {

			e.printStackTrace();
		}
	}
	public static void TAddHotLevel(int item){
		try {
			stat = con.createStatement();
			rs = stat.executeQuery("select * from "+UI4_order_page.storeID);
			Statement stat2 = con.createStatement();
			for(int i=0;i<UI6_takeaway_list.takeaway_list_array.get(item).meals.size();i++){
				System.out.println(UI6_takeaway_list.takeaway_list_array.get(item).meals.get(i).id);
				System.out.println(UI6_takeaway_list.takeaway_list_array.get(item).meals.get(i).name);
				
				while(rs.next()){
					int id = rs.getInt("id");
					if(id == UI6_takeaway_list.takeaway_list_array.get(item).meals.get(i).id){
						System.out.println(id);
						System.out.println(rs.getString("name"));
						int temp = Integer.parseInt(rs.getString("hot_level").toString());
						temp+=UI6_takeaway_list.takeaway_list_array.get(item).meals.get(i).number;
						System.out.println(temp);
						stat2.executeUpdate("Update "+UI4_order_page.storeID+" set hot_level='"+temp+""+"' where id="+id+"");
						break;
					}
				}
			}
			if(stat2!=null)stat2 = null;
		} catch (SQLException e) {

			e.printStackTrace();
		}
		
	}
	public static void DTraMealsToCook(int item){
		try{
			//SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
        	pst = con.prepareStatement("insert into "+UI4_order_page.storeID+" (id,name,number,time) " + "values (?,?,?,?)");
        	for(int i=0;i<UI6_delivery_list.delivery_list_array.get(item).meals.size();i++){
        		pst.setInt(1,++UI4_order_page.mealscount);
        		pst.setNString(2, UI6_delivery_list.delivery_list_array.get(item).meals.get(i).name);
            	pst.setInt(3, UI6_delivery_list.delivery_list_array.get(item).meals.get(i).number);
            	pst.setTimestamp(4, new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
            	pst.executeUpdate();
        	}
		}catch (SQLException e) {

			e.printStackTrace();
		}
	}
	public static void DAddHotLevel(int item){
		try {
			stat = con.createStatement();
			rs = stat.executeQuery("select * from "+UI4_order_page.storeID);
			Statement stat2 = con.createStatement();
			for(int i=0;i<UI6_delivery_list.delivery_list_array.get(item).meals.size();i++){
				while(rs.next()){
					if(rs.getInt("id") == UI6_delivery_list.delivery_list_array.get(item).meals.get(i).id){
						int temp = Integer.parseInt(rs.getString("hot_level").toString());
						temp+=UI6_delivery_list.delivery_list_array.get(item).meals.get(i).number;
						System.out.println(temp);
						stat2.executeUpdate("Update "+UI4_order_page.storeID+" set hot_level='"+temp+""+"' where id="+rs.getInt("id")+"");
						break;
					}
				}
			}
			if(stat2!=null)stat2 = null;
		} catch (SQLException e) {

			e.printStackTrace();
		}
		
	}
	public static void ITraMealsToCook(int item){
		try{
			pst = con.prepareStatement("insert into "+UI4_order_page.storeID+" (id,name,number,time) " + "values(?,?,?,?)");
			for(int i=0;i<UI6_inside_list.inside_list_array.get(item).meals.size();i++){
				pst.setInt(1,++UI4_order_page.mealscount);
				pst.setNString(2, UI6_inside_list.inside_list_array.get(item).meals.get(i).name);
				pst.setInt(3, UI6_inside_list.inside_list_array.get(item).meals.get(i).number);
				pst.setTimestamp(4, new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
				pst.executeUpdate();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	public static void IAddHotLevel(int item){
		try{
			stat = con.createStatement();
			rs = stat.executeQuery("select * from "+UI4_order_page.storeID);
			Statement stat2 = con.createStatement();
			for(int i=0;i<UI6_inside_list.inside_list_array.get(item).meals.size();i++){
				while(rs.next()){
					if(rs.getInt("id") == UI6_inside_list.inside_list_array.get(item).meals.get(i).id){
						int temp = Integer.parseInt(rs.getString("hot_level").toString());
						temp+=UI6_inside_list.inside_list_array.get(item).meals.get(i).number;
						stat2.executeUpdate("Update "+UI4_order_page.storeID+" set hot_level='"+temp+""+"' where id="+rs.getInt("id")+"");
						break;
					}
				}
			}
			if(stat2!=null)stat2 = null;
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	public static boolean GetMealsForDatabase(){
		boolean hasData = false;
		try{
 			Statement stmt = con.createStatement();
			stat = con.createStatement();
			rs = stat.executeQuery(selectSQL2+UI5_cook_page.storeID);
			while(rs.next()){
				if(rs.getInt("id")==UI5_cook_page.mealscount){
					hasData = true;
					String name = rs.getString("name");
					int number = rs.getInt("number");
					Date time = (Date)rs.getTimestamp("time");
					while(number>0){
						UI5_cook_page.cook_meals.add(new C3_cook_meals(name,1,time));
						number--;
					}
					stmt.executeUpdate("Delete from "+UI5_cook_page.storeID+" where id="+UI5_cook_page.mealscount+"");
					UI5_cook_page.mealscount++;
				}
			}
			stmt = null;
		}catch (SQLException e) {

			e.printStackTrace();
		}
		return hasData;
	}
	
	public static boolean GetListForDatabase(){
		boolean hasData = false;
		try{
			Statement stmt2 = con2.createStatement();
			C2Sstat = con2.createStatement();
			C2Srs = C2Sstat.executeQuery("select * from "+UI4_order_page.storeID);
			while(C2Srs.next()){
				hasData = true;
				String username = C2Srs.getString("username")
				 ,phone = C2Srs.getString("phone")
				 ,address = C2Srs.getString("address")
				 ,meals = C2Srs.getString("meals")
				 ,type = C2Srs.getString("type");
				System.out.println(username+" ");
				UI4_order_page.getusername = username;
				UI4_order_page.getphone = phone;
				UI4_order_page.getaddress = address;
				UI4_order_page.type = type;
				UI4_order_page.mealscode = meals;
				UI4_order_page.gettime = new Date();
				
				stmt2.executeUpdate("Delete from "+UI4_order_page.storeID+" where username='"+username+"' & meals='"+meals+"'");
				break;
			}
		}catch (SQLException e) {

			e.printStackTrace();
		}
		return hasData;
	}
	
	public static void UploadMeals(){
		try{
			int id = 0;
			pst = con.prepareStatement("insert into "+UI9_upload_meals.storeID+"(id,name,money,sort,Evaluation_score,hot_level,calories,message) " + "values (?,?,?,?,?,?,?,?)");
			for(int i=0;i<UI9_upload_meals.meals_sort.size();i++){
				for(int j=0;j<UI9_upload_meals.meals_sort.get(i).meals.size();j++){
					System.out.println("UI9_upload_meals.meals_sort.get(i).meals.get(j).name "+"UI9_upload_meals.meals_sort.get(i).meals.get(j).money "+"UI9_upload_meals.meals_sort.get(i).name");
					pst.setInt(1,++id);
					pst.setNString(2, UI9_upload_meals.meals_sort.get(i).meals.get(j).name);
					pst.setInt(3, UI9_upload_meals.meals_sort.get(i).meals.get(j).money);
					pst.setNString(4, UI9_upload_meals.meals_sort.get(i).name);
					pst.setInt(5, 0);
					pst.setInt(6, 0);
					pst.setInt(7, 0);
					pst.setString(8, "");
					pst.executeUpdate();
					System.out.println("寫入結束");
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	public static void ChangeAccountHaveMeals(){
		try {
			stat = con.createStatement();
			stat.executeUpdate("update account set havemeals='Y' where account='"+UI9_upload_meals.storeID+"'");
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}
}
