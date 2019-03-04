package com.example.ordermeals;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class UI5_cook_page extends ActionBarActivity {
	public static String storeID;
	private ListView listView;
	public static ArrayList<C3_cook_meals> cook_meals = new ArrayList<C3_cook_meals>();
	public static ArrayList<C4_cook_meals_sort> cook_meals_sort = new ArrayList<C4_cook_meals_sort>();
	private MyAdapter myAdapter;
	private Handler myHandler = new Handler();
	public static int mealscount=1;
	final private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss");
	int preprepare = -1;
	int sortindex = 0;
	int groupmaxnumber=5,groupmaxtime=10;
	boolean isSetting = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui5_cook_page);
		FindView();
		GetPrePageInfo();
		myAdapter = new MyAdapter(this);
		listView.setAdapter(myAdapter);
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {

				if(preprepare == position){
					for(int i=0;i<cook_meals_sort.get(position).cook_meals.size();i++){
						for(int j=0;j<cook_meals.size();j++){
							if(cook_meals_sort.get(position).cook_meals.get(i).name.equals(cook_meals.get(j).name)&
									cook_meals_sort.get(position).cook_meals.get(i).time.getTime() == cook_meals.get(j).time.getTime()){
								cook_meals.remove(j);
								break;
							}
						}
					}
					cook_meals_sort.remove(position);
					myAdapter.change();
					listView.setAdapter(myAdapter);
					preprepare = -1;
					sortindex--;
				}else{
					preprepare = position;
					
				}
					
			}
			
		});
		GetMealsForDatabase();
	}

	private void GetMealsForDatabase(){
		new Thread(new Runnable(){

			@Override
			public void run() {

				try{
					Database.ConnectToCook();
					while(true){
						if(!isSetting){
							if(Database.GetMealsForDatabase()){
								Sort();
								ThreadCommunication();
							}
						}
						Thread.sleep(3000);
						System.out.println("found data");
					}
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}
			
		}).start();
	}
	private void ThreadCommunication(){
		myHandler.post(new Runnable() {
			@Override
				public void run() {
	
				myAdapter.change();
				System.out.println("change");
			}
		});
	}
	private void GetPrePageInfo(){
		storeID = this.getIntent().getExtras().getString("storeID");
	}
	private void FindView(){
		listView = (ListView)findViewById(R.id.cook_listView);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ui5_cook_page, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.sort) {
			System.out.println("1");
			SetSort();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private void SetSort(){
		LayoutInflater inflater = LayoutInflater.from(UI5_cook_page.this);
		View v = inflater.inflate(R.layout.ui9_meals_sort, null);
		final EditText meals_max_number = (EditText)v.findViewById(R.id.meals_max_number_et);
		final EditText max_m = (EditText)v.findViewById(R.id.max_m);
		final EditText max_s = (EditText)v.findViewById(R.id.max_s);
		meals_max_number.setText(groupmaxnumber+"");
		max_m.setText(groupmaxtime/60+"");
		max_s.setText(groupmaxtime%60+"");
		new AlertDialog.Builder(UI5_cook_page.this)
				.setTitle("群組設定").setView(v)
				.setPositiveButton("確定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO 自動產生的方法 Stub
						groupmaxnumber = Integer.parseInt(meals_max_number.getText().toString());
						groupmaxtime = Integer.parseInt(max_m.getText().toString())*60+Integer.parseInt(max_s.getText().toString());
						sortindex = 0;
						cook_meals_sort.clear();
						Sort();
						myAdapter.change();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {}
			
		}).show();
	}
	
	private void Sort(){

		for(;sortindex<cook_meals.size();sortindex++){
			boolean find = false;
			for(int j=0;j<cook_meals_sort.size();j++){
				System.out.println((cook_meals.get(sortindex).time.getTime() - cook_meals_sort.get(j).firsttime.getTime())/1000+"");
				if(cook_meals.get(sortindex).name.equals(cook_meals_sort.get(j).name)&
						(cook_meals.get(sortindex).time.getTime() - cook_meals_sort.get(j).firsttime.getTime())/1000 < groupmaxtime&
						cook_meals_sort.get(j).number + cook_meals.get(sortindex).number <=groupmaxnumber){
					System.out.println("in");
					cook_meals_sort.get(j).cook_meals.add(cook_meals.get(sortindex));
					cook_meals_sort.get(j).number += cook_meals.get(sortindex).number;
					find = true;
					break;
				}	
			}
			if(!find){
				System.out.println("in2");
				cook_meals_sort.add(new C4_cook_meals_sort(cook_meals.get(sortindex).name,cook_meals.get(sortindex).number,cook_meals.get(sortindex).time,cook_meals.get(sortindex)));
			}
		}
	}
	class MyAdapter extends BaseAdapter{
		private LayoutInflater myInflater;
		public MyAdapter(Context c) {
    		myInflater = LayoutInflater.from(c);
    	}
		public void removeItem(int position){
			
			change();
		}
		public void change(){
			this.notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			// TODO �۰ʲ��ͪ���k Stub
			return cook_meals_sort.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO �۰ʲ��ͪ���k Stub
			return cook_meals_sort.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO �۰ʲ��ͪ���k Stub
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {

			TagView tag;
			if(view == null){
				view = myInflater.inflate(R.layout.cook_item, null);
				tag = new TagView((TextView)view.findViewById(R.id.cook_name),(TextView)view.findViewById(R.id.cook_number)
						,(TextView)view.findViewById(R.id.cook_time));
				view.setTag(tag);
			}else{
				tag = (TagView) view.getTag();
			}
			tag.name.setText(cook_meals_sort.get(position).name);
			tag.number.setText(cook_meals_sort.get(position).number+"");
			tag.time.setText(sdf.format(cook_meals_sort.get(position).firsttime));
			return view;
		}
		
	}
	public void onBackPressed() {
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	class TagView{
		TextView name,number,time;
		public TagView(TextView name,TextView number,TextView time){
			this.name = name;
			this.number = number;
			this.time = time;
		}
	}
	public boolean onMenuOpened(int featureId, Menu menu) {  //overflow����Action���s�Ϥ�
	    if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {  
	        if (menu.getClass().getSimpleName().equals("MenuBuilder")) {  
	            try {  
	                Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);  
	                m.setAccessible(true);  
	                m.invoke(menu, true);  
	            } catch (Exception e) {  
	            }  
	        }  
	    }  
	    return super.onMenuOpened(featureId, menu);  
	}  
}
