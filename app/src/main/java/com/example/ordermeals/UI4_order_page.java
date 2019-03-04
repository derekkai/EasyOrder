package com.example.ordermeals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.ordermeals.UI6_delivery_list.DListener;
import com.example.ordermeals.UI6_inside_list.IListener;
import com.example.ordermeals.UI6_takeaway_list.TListener;
import com.example.ordermeals.UI7_delivery_meals.DAlertListener;
import com.example.ordermeals.UI7_inside_meals.IAlertListener;
import com.example.ordermeals.UI7_takeaway_meals.TAlertListener;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;

public class UI4_order_page extends FragmentActivity implements DListener,IListener,TListener,DAlertListener,TAlertListener,IAlertListener{
	
	private Handler myHandler = new Handler();
	public static Context mContext; 
	private PagerAdapter myPagerAdapter;
	private ViewPager viewpager;
	private PagerTabStrip pagerTabStrip;
	private EditText tablenumber;
	public static UI6_takeaway_list takeaway_list = new UI6_takeaway_list();
	public static UI6_delivery_list delivery_list = new UI6_delivery_list();
	public static UI6_inside_list inside_list = new UI6_inside_list();
	private UI7_takeaway_meals takeaway_meals = new UI7_takeaway_meals();
	private UI7_delivery_meals delivery_meals = new UI7_delivery_meals();
	private UI7_inside_meals inside_meals = new UI7_inside_meals();
	private LinearLayout lyout;
	private List<String> titleList = new ArrayList<String>();
	private List<Fragment> fragmentList = new ArrayList<Fragment>();
	private SoundPool soundPool;
	public static ArrayList<String> mealssort = new ArrayList<String>();
	public static ArrayList<ArrayList<C2_meals>> meals = new ArrayList<ArrayList<C2_meals>>();
	private boolean wait = true;
	public static String storeID,account,login_time;
	private int nowpage = 0,nowTitem = -1,nowDitem = -1,nowIitem=-1;	
	private int whichlist;
	private Spinner spinner,spinner2;
	private EditText number,edit_number;
	private ImageButton add,sub,t_add_ibt,d_add_ibt,i_add_ibt,delete_list_ibt,edit_add,edit_sub,add_list_bt;
	private ArrayAdapter<String> mealssortlist;
	private Spinneradapter spinneradapter;
	final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
	private int spinner1item = 0,spinner2item = 0;
	private ProgressDialog progressdialog;
	public static int mealscount = 0;
	private int removelist_voice;
	private boolean getmealsready = false;
	private EditText tablepeoplenumber;
	public static String mealscode;
	public static String getusername,getphone,getaddress;
	public static Date gettime;
	public static String type; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui4_order_page);
		FindView();
		SetContext();
		GetPrePageInfo();
		GetMeals();
		removelist_voice = soundPool.load(this, R.raw.romove_list, 1);
		myPagerAdapter = new PagerAdapter(getSupportFragmentManager(),fragmentList,titleList);
		viewpager.setAdapter(myPagerAdapter);
		viewpager.setOnPageChangeListener(new OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int arg0) {}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			@Override
			public void onPageSelected(int page) {
				delete_list_ibt.setVisibility(View.VISIBLE);
				add_list_bt.setVisibility(View.VISIBLE);
				t_add_ibt.setVisibility(View.INVISIBLE);
				d_add_ibt.setVisibility(View.INVISIBLE);
				i_add_ibt.setVisibility(View.INVISIBLE);
				nowpage = page;
				System.out.println(nowTitem+" "+nowDitem+" "+nowIitem+" "+page);
				
				if(page == 0){
					
					getSupportFragmentManager().beginTransaction().hide(inside_meals).hide(delivery_meals).show(takeaway_meals).commit();
					if(nowTitem != -1){
						if(!takeaway_list.takeaway_list_array.get(nowTitem).certain){
							t_add_ibt.setVisibility(View.VISIBLE);
							if(UI6_takeaway_list.takeaway_list_array.size()==0)
								delete_list_ibt.setVisibility(View.INVISIBLE);
						}
						else
							delete_list_ibt.setVisibility(View.INVISIBLE);
					}else
						delete_list_ibt.setVisibility(View.INVISIBLE);
				}else if(page == 1){
					
					getSupportFragmentManager().beginTransaction().hide(inside_meals).hide(takeaway_meals).show(delivery_meals).commit();
					if(nowDitem != -1){
						if(!delivery_list.delivery_list_array.get(nowDitem).certain){
							d_add_ibt.setVisibility(View.VISIBLE);
							if(UI6_delivery_list.delivery_list_array.size()==0)
								delete_list_ibt.setVisibility(View.INVISIBLE);
						}
						else
							delete_list_ibt.setVisibility(View.INVISIBLE);
					}else
						delete_list_ibt.setVisibility(View.INVISIBLE);
				}else if(page == 2){
					getSupportFragmentManager().beginTransaction().hide(delivery_meals).hide(takeaway_meals).commit();
					add_list_bt.setVisibility(View.INVISIBLE);
					
					if(nowIitem != -1){
						if(inside_list.inside_list_array.get(nowIitem).used){
							getSupportFragmentManager().beginTransaction().show(inside_meals).commit();
							i_add_ibt.setVisibility(View.VISIBLE);
						}else{
							delete_list_ibt.setVisibility(View.INVISIBLE);
							i_add_ibt.setVisibility(View.INVISIBLE);
						}
						if(!inside_list.inside_list_array.get(nowIitem).certain){
							if(UI6_inside_list.inside_list_array.size()==0){
								delete_list_ibt.setVisibility(View.INVISIBLE);
							}
						}else{
							getSupportFragmentManager().beginTransaction().show(inside_meals).commit();
							delete_list_ibt.setVisibility(View.INVISIBLE);
						}
					}else{
						delete_list_ibt.setVisibility(View.INVISIBLE);
					}
				}
			}
			
		});
		//GetListFromDB();
	}
	private void GetListFromDB(){
		new Thread(new Runnable(){

			@Override
			public void run() {

				while(!getmealsready);
				Database.ConnectToC2S();
				while(true){
					
						if(Database.GetListForDatabase()){
							if(type.equals("T"))
								ThreadCommunication(0,getusername,getphone,gettime,getaddress);
							else if(type.equals("D"))
								ThreadCommunication(1,getusername,getphone,gettime,getaddress);
						}
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							// TODO �۰ʲ��ͪ� catch �϶�
							e.printStackTrace();
						}
						System.out.println("found data");
					
				}
			}
			
		}).start();
	}
	private  void ThreadCommunication(final int i,final String username,final String phone,final Date time,final String address){
		myHandler.post(new Runnable(){

			@Override
			public void run() {
				// TODO �۰ʲ��ͪ���k Stub
				if(i == 0 ){
					UI4_order_page.takeaway_list.AddList(username,phone,time);
				}else if(i==1){
					System.out.print("***********");
					UI4_order_page.delivery_list.AddList(username, phone, address, time);
				}
				HandleCode();
			}
			
		});
	}
	private void HandleCode(){
		System.out.println(mealscode);
		String[] str = mealscode.split("b");
		for(int i=0;i<str.length;i++){
			String[] str2 = str[i].split("a");
			System.out.println(str[i]);
			if(type.equals("T")){
				takeaway_list.takeaway_list_array.get(takeaway_list.takeaway_list_array.size()-1).meals.add(
						new C2_meals(meals.get(Integer.parseInt(str2[0])-1).get(Integer.parseInt(str2[1])-1),Integer.parseInt(str2[2])));
			}else if(type.equals("D")){
				delivery_list.delivery_list_array.get(delivery_list.delivery_list_array.size()-1).meals.add(
						new C2_meals(meals.get(Integer.parseInt(str2[0])-1).get(Integer.parseInt(str2[1])-1),Integer.parseInt(str2[2])));
			}
		}
	}
	private void GetMeals(){
		progressdialog = ProgressDialog.show(UI4_order_page.this, "下載菜單", "下載餐點中 , 請稍後...");
		new Thread(new Runnable(){

			@Override
			public void run() {
				System.out.println("開始抓取餐點");
				Database.ConnectToMeals();
				Database.GetDatabaseMeals(storeID);
				Database.Close();
				progressdialog.dismiss();
				getmealsready =true;
			}

		}).start();
	}
	private void GetPrePageInfo(){
		storeID = this.getIntent().getExtras().getString("storeID");
		account = this.getIntent().getExtras().getString("account");
		login_time = this.getIntent().getExtras().getString("login_time");
	}
	private void SetContext(){
		mContext = this.getApplicationContext();
		pagerTabStrip.setTabIndicatorColor(Color.GREEN);
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    	fragmentTransaction.add(R.id.frameLayout1, takeaway_meals);
    	fragmentTransaction.add(R.id.frameLayout1, delivery_meals).hide(delivery_meals);
    	fragmentTransaction.add(R.id.frameLayout1, inside_meals).hide(inside_meals);
    	fragmentTransaction.commit();
    	soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);

		fragmentList.add(takeaway_list);
		fragmentList.add(delivery_list);
		fragmentList.add(inside_list);
		titleList.add("外帶");
		titleList.add("外送");
		titleList.add("內用");
	}
	private void FindView(){
		i_add_ibt = (ImageButton)findViewById(R.id.i_add_meals_ibt);
		add_list_bt = (ImageButton)findViewById(R.id.add_list_bt);
		delete_list_ibt = (ImageButton)findViewById(R.id.deletelist);
		t_add_ibt = (ImageButton)findViewById(R.id.t_add_meals_ibt);
		d_add_ibt = (ImageButton)findViewById(R.id.d_add_meals_ibt);
		viewpager = (ViewPager)findViewById(R.id.viewpager);
		pagerTabStrip = (PagerTabStrip)findViewById(R.id.viewpagertab);
	}
	public void AddNewList(View view){
		LayoutInflater inflater = LayoutInflater.from(UI4_order_page.this);
		View v = inflater.inflate(R.layout.ui7_add_new_list, null);
		RadioGroup rg = (RadioGroup)v.findViewById(R.id.whichlist_rg);
		final LinearLayout ly = (LinearLayout)v.findViewById(R.id.address_ly);
		final RadioButton trb = (RadioButton)v.findViewById(R.id.takeaway_rd);
		final RadioButton drb = (RadioButton)v.findViewById(R.id.delivery_rd);
		
		final EditText username = (EditText)v.findViewById(R.id.username_et);
		final EditText phone = (EditText)v.findViewById(R.id.phone_et);
		final EditText address = (EditText)v.findViewById(R.id.address_et);
		whichlist=0;
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener(){
		
			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {

				if(checkedId == trb.getId()){
					whichlist = 0;
					ly.setVisibility(View.INVISIBLE);
				}else if(checkedId == drb.getId()){
					whichlist = 1;
					ly.setVisibility(View.VISIBLE);
				}
			}	
		});
		new AlertDialog.Builder(UI4_order_page.this)
				.setTitle("新增訂單").setView(v)
				.setPositiveButton("確定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO 自動產生的方法 Stub
						Date time = new Date();
						AddList(whichlist,username.getText().toString(),phone.getText().toString(),address.getText().toString(),time);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1) {}

				}).show();
	}
	private void AddList(int whichlist,String username,String phone,String address,Date time){
		if(whichlist==0){
			takeaway_list.AddList(username,phone,time);
		}
		else if(whichlist==1){
			delivery_list.AddList(username, phone, address, time);
		}
	}
	public void RemoveList(View view){
		if(nowpage < 2){
			new AlertDialog.Builder(UI4_order_page.this)
					.setTitle("刪除訂單")
					.setMessage("您確定要刪除訂單嗎?")
					.setPositiveButton("確定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO 自動產生的方法 Stub
							soundPool.play(removelist_voice, 1.0F, 1.0F, 0, 0, 1.0F);
							removelistitem();
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {}
					}).show();
		}else if(nowpage == 2){
			new AlertDialog.Builder(UI4_order_page.this)
					.setTitle("清空此桌?")
					.setMessage("您確定要清空此桌嗎?")
					.setPositiveButton("確定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO 自動產生的方法 Stub
							soundPool.play(removelist_voice, 1.0F, 1.0F, 0, 0, 1.0F);
							removelistitem();
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {}
					}).show();
		}

	}
	private void removelistitem(){
		delete_list_ibt.setVisibility(View.INVISIBLE);
		if(nowpage == 0){
			t_add_ibt.setVisibility(View.INVISIBLE);
			UI6_takeaway_list.takeaway_list_array.remove(nowTitem);
			takeaway_meals.RemoveList();
			takeaway_list.RemoveList();
			nowTitem = -1;
			
		}else if(nowpage == 1){
			d_add_ibt.setVisibility(View.INVISIBLE);
			UI6_delivery_list.delivery_list_array.remove(nowDitem);
			delivery_meals.RemoveList();
			delivery_list.RemoveList();
			nowDitem = -1;
		}else if(nowpage == 2){
			getSupportFragmentManager().beginTransaction().hide(inside_meals).commit();
			d_add_ibt.setVisibility(View.INVISIBLE);
			UI6_inside_list.inside_list_array.get(nowIitem).Clear();
			inside_list.RemoveList();
		}
	}
	public void AddNewMeals(View view){
		LayoutInflater inflater = LayoutInflater.from(UI4_order_page.this);
		final View v = inflater.inflate(R.layout.ui8_add_new_meals, null);
		spinner = (Spinner)v.findViewById(R.id.spinner1);
		spinner2 = (Spinner)v.findViewById(R.id.spinner2);
		number = (EditText)v.findViewById(R.id.number);
		add = (ImageButton)v.findViewById(R.id.add);
		sub = (ImageButton)v.findViewById(R.id.sub);
		mealssortlist = new ArrayAdapter<String>(UI4_order_page.this,R.layout.spinner,mealssort);
		spinner.setAdapter(mealssortlist);
		spinneradapter = new Spinneradapter(0,this);
		spinner2.setAdapter(spinneradapter);
		add.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {

				int temp = Integer.parseInt(number.getText().toString());
				temp++;
				number.setText(temp+"");
			}
			
		});
		sub.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {

				int temp = Integer.parseInt(number.getText().toString());
				if(temp>1)
					temp--;
				number.setText(temp+"");
			}
		});	
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				spinner1item = position;
				spinneradapter = new Spinneradapter(position,mContext);
				spinner2.setAdapter(spinneradapter);	
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
			
		});
		spinner2.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				spinner2item = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}

		});
		new AlertDialog.Builder(UI4_order_page.this)
				.setTitle("新增餐點").setView(v)
				.setPositiveButton("確定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO 自動產生的方法 Stub
						int num = Integer.parseInt(number.getText().toString());
						if(nowpage == 0){
							takeaway_list.takeaway_list_array.get(nowTitem).meals.add(new C2_meals(meals.get(spinner1item).get(spinner2item),num));
							takeaway_meals.ChangeList();
						}else if(nowpage == 1){
							delivery_list.delivery_list_array.get(nowDitem).meals.add(new C2_meals(meals.get(spinner1item).get(spinner2item),num));
							delivery_meals.ChangeList();
						}else if(nowpage == 2){
							inside_list.inside_list_array.get(nowIitem).meals.add(new C2_meals(meals.get(spinner1item).get(spinner2item),num));
							inside_meals.ChangeList();
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {}
				}).show();


	}
	private void WriteTimeBackDB(){
		new Thread(new Runnable() {

			public void run() {
				Database.Connect();
				Database.WriteTimeBack();
				Database.Close();
				wait = false;
			}
		}).start();
		
	}
	protected void onDestroy(){
		super.onDestroy();
		WriteTimeBackDB();
	}
	public void onBackPressed() {
		Logout();
    }
	public void Exit(View view){
		Logout();
	}
	public void Logout(){
		new AlertDialog.Builder( UI4_order_page.this)
				.setTitle("登出")
				.setMessage("您確定要登出嗎?")
				.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						WriteTimeBackDB();
						while(wait);
						android.os.Process.killProcess(android.os.Process.myPid());
						System.out.println("finish");
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO 自動產生的方法 Stub
					}
				}).show();
	}
	public void toUser_page(View view){
		Intent intent;
		intent = new Intent(UI4_order_page.this,UI8_user_page.class);
		this.startActivity(intent);
	} 
	private class PagerAdapter extends FragmentPagerAdapter{
		private List<Fragment> fragmentList;
		private List<String> titleList;
		public PagerAdapter(FragmentManager fm,List<Fragment> fragmentList,List<String> titleList) {
			super(fm);
			this.fragmentList = fragmentList;
			this.titleList = titleList;
			// TODO �۰ʲ��ͪ��غc�l Stub
		}
		public CharSequence getPageTitle(int position){
			return titleList.get(position);
		}
		@Override
		public Fragment getItem(int position) {
			// TODO �۰ʲ��ͪ���k Stub
			return (fragmentList == null || fragmentList.size() == 0)? null
					: fragmentList.get(position);
		}

		@Override
		public int getCount() {
			// TODO �۰ʲ��ͪ���k Stub
			return fragmentList == null ? 0:fragmentList.size();
		}
	}
	class Spinneradapter extends BaseAdapter{
		private LayoutInflater myInflater;
		int position;
		public Spinneradapter(int position,Context c) {
    		myInflater = LayoutInflater.from(c);
    		this.position = position;
    	} 
		@Override
		public int getCount() {
			// TODO �۰ʲ��ͪ���k Stub
			return meals.get(position).size();
		}

		@Override
		public Object getItem(int position) {
			// TODO �۰ʲ��ͪ���k Stub
			return meals.get(this.position).get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO �۰ʲ��ͪ���k Stub
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO �۰ʲ��ͪ���k Stub
			TagView tag;
			if(view == null){
				view = myInflater.inflate(R.layout.spinner_item, null);
				tag = new TagView((TextView)view.findViewById(R.id.S_name),
						(TextView)view.findViewById(R.id.S_money));
				view.setTag(tag);
			}else{
				tag = (TagView)view.getTag();
			}
			tag.name_tv.setText(meals.get(this.position).get(position).name);
			tag.money_tv.setText(meals.get(this.position).get(position).money+"");
			return view;
		}	
	}
	class TagView{
		TextView name_tv,money_tv;
		public TagView(TextView name_tv,TextView money_tv){
			this.name_tv = name_tv;
			this.money_tv = money_tv;
		}
	}
	@Override
	public void ReturnItem(int position) {
		// TODO �۰ʲ��ͪ���k Stub
		delete_list_ibt.setVisibility(View.VISIBLE);
		if(nowpage == 0){
			nowTitem = position;
			takeaway_meals.GetItem(position);
			t_add_ibt.setVisibility(View.VISIBLE);
			if(UI6_takeaway_list.takeaway_list_array.get(position).certain){
				delete_list_ibt.setVisibility(View.INVISIBLE);
				t_add_ibt.setVisibility(View.INVISIBLE);
			}
		}else if(nowpage == 1){
			nowDitem = position;
			delivery_meals.GetItem(position);
			d_add_ibt.setVisibility(View.VISIBLE);
			if(UI6_delivery_list.delivery_list_array.get(position).certain){
				delete_list_ibt.setVisibility(View.INVISIBLE);
				d_add_ibt.setVisibility(View.INVISIBLE);
			}
		}else if(nowpage == 2){
			getSupportFragmentManager().beginTransaction().show(inside_meals).commit();
			nowIitem = position;
			inside_meals.GetItem(position);
			i_add_ibt.setVisibility(View.VISIBLE);
			if(UI6_inside_list.inside_list_array.get(position).certain){
				delete_list_ibt.setVisibility(View.INVISIBLE);
				i_add_ibt.setVisibility(View.INVISIBLE);
			}
		}
	}
	@Override
	public void EditMeals(final int position, int number) {
		// TODO �۰ʲ��ͪ���k Stub
		LayoutInflater inflater = LayoutInflater.from(UI4_order_page.this);
		final View v = inflater.inflate(R.layout.edit_number, null);
		edit_number = (EditText)v.findViewById(R.id.edit_number);
		edit_add = (ImageButton)v.findViewById(R.id.edit_add);
		edit_sub = (ImageButton)v.findViewById(R.id.edit_sub);
		edit_number.setText(number+"");
		edit_add.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO �۰ʲ��ͪ���k Stub
				int temp = Integer.parseInt(edit_number.getText().toString());
				temp++;
				edit_number.setText(temp+"");
			}
			
		});
		edit_sub.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO �۰ʲ��ͪ���k Stub
				int temp = Integer.parseInt(edit_number.getText().toString());
				if(temp>1)
					temp--;
				edit_number.setText(temp+"");
			}
			
		});
		new AlertDialog.Builder(UI4_order_page.this)
				.setTitle("更改數量").setView(v)
				.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int num = Integer.parseInt(edit_number.getText().toString());
						if(nowpage == 0){
							UI6_takeaway_list.takeaway_list_array.get(nowTitem).meals.get(position).number = num;
							takeaway_meals.ChangeList();

						}
						else if(nowpage == 1){
							UI6_delivery_list.delivery_list_array.get(nowDitem).meals.get(position).number = num;
							delivery_meals.ChangeList();
						}else if(nowpage == 2){
							UI6_inside_list.inside_list_array.get(nowIitem).meals.get(position).number = num;
							inside_meals.ChangeList();
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
					// TODO 自動產生的方法 Stub}
				}).show();
	}
	@Override
	public void ChangeState() {
		if(nowpage==0)
			t_add_ibt.setVisibility(View.INVISIBLE);
		else if(nowpage == 1)
			d_add_ibt.setVisibility(View.INVISIBLE);
		else if(nowpage == 2)
			i_add_ibt.setVisibility(View.INVISIBLE);
		delete_list_ibt.setVisibility(View.INVISIBLE);
	}
	@Override
	public void ClearBill() {
		// TODO �۰ʲ��ͪ���k Stub
		removelistitem();
	}
	public void SetTable(){
		LayoutInflater inflater = LayoutInflater.from(UI4_order_page.this);
		View v = inflater.inflate(R.layout.table_number, null);
		tablenumber = (EditText)v.findViewById(R.id.tablenumber);
		new AlertDialog.Builder(UI4_order_page.this)
				.setTitle("設定餐桌數量").setView(v)
				.setPositiveButton("確定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO 自動產生的方法 Stub
						UI6_inside_list.inside_list_array.clear();
						for(int i=0;i<Integer.parseInt(tablenumber.getText().toString());){
							UI6_inside_list.inside_list_array.add(new C1_inside_list(++i));
						}
						UI6_inside_list.myadapter.Change();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1) {}

				}).show();
	}
	@Override
	public void CreatInside(final int position) {
		nowIitem = position;
		getSupportFragmentManager().beginTransaction().hide(inside_meals).commit();
		i_add_ibt.setVisibility(View.INVISIBLE);
		delete_list_ibt.setVisibility(View.INVISIBLE);
		LayoutInflater inflater = LayoutInflater.from(UI4_order_page.this);
		View v = inflater.inflate(R.layout.creat_table_people_number, null);
		tablepeoplenumber = (EditText)v.findViewById(R.id.people_number);
		new AlertDialog.Builder(UI4_order_page.this)
				.setTitle((position+1)+"號桌").setView(v)
				.setPositiveButton("確定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO 自動產生的方法 Stub
						getSupportFragmentManager().beginTransaction().show(inside_meals).commit();
						UI6_inside_list.inside_list_array.get(position).used = true;
						UI6_inside_list.inside_list_array.get(position).number = Integer.parseInt(tablepeoplenumber.getText().toString());
						inside_list.myadapter.Change();
						inside_meals.GetItem(position);
						i_add_ibt.setVisibility(View.VISIBLE);
						delete_list_ibt.setVisibility(View.VISIBLE);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1) {}

				}).show();
	}
}

