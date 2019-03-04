package com.example.ordermeals;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class UI9_upload_meals extends Activity {
	private Handler myHandler = new Handler();
	private ProgressDialog progressdialog;
	public static String storeID;
	public static ArrayList<C5_upload_meals_sort> meals_sort = new ArrayList<C5_upload_meals_sort>();
	private ListView listview,listview2;
	private Myadapter myAdapter;
	private Myadapter2 myAdapter2;
	private EditText sort_name,meals_name,meals_money;
	private int meals_sort_item = -1;
	private TextView now_sort;
	TagView tag;
	TagView2 tag2;
	private EditText edit_sort_name,edit_meals_name,edit_meals_money;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui9_upload_meals);
		FindView();
		GetPrePageInfo();
		myAdapter = new Myadapter(this);
		myAdapter2 = new Myadapter2(this);
		listview.setAdapter(myAdapter);
		listview2.setAdapter(myAdapter2);
		listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {

				meals_sort_item = position;
				now_sort.setText(meals_sort.get(meals_sort_item).name);
				myAdapter2.Change();
			}
		});
	}
	private void GetPrePageInfo(){
		storeID = this.getIntent().getExtras().getString("storeID");
	}
	private void FindView(){
		now_sort = (TextView)findViewById(R.id.now_sort);
		listview = (ListView)findViewById(R.id.listView1);
		listview2 = (ListView)findViewById(R.id.listView2);
		sort_name = (EditText)findViewById(R.id.sort_name);
		meals_name = (EditText)findViewById(R.id.meals_name);
		meals_money = (EditText)findViewById(R.id.meals_price);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ui9_upload_meals, menu);
		return true;
	}
	public void AddmealsSort(View view){
		if(sort_name.getText().toString().isEmpty())
			Toast.makeText(this, "種類名稱不可以為空!",Toast.LENGTH_SHORT ).show();
		else{
			meals_sort.add(new C5_upload_meals_sort(sort_name.getText().toString()));
			myAdapter.Change();
			sort_name.setText("");
		}
	}
	public void Addmeals(View view){
		if(meals_sort.size()<0){
			Toast.makeText(this, "您還沒有任何餐點種類!",Toast.LENGTH_SHORT ).show();
		}else if(meals_name.getText().toString().isEmpty()){
			Toast.makeText(this, "餐點名稱不可以為空!",Toast.LENGTH_SHORT ).show();
		}else if(meals_money.getText().toString().isEmpty()){
			Toast.makeText(this, "餐點價格不可以為空!",Toast.LENGTH_SHORT ).show();
		}else if(meals_sort_item == -1){
			Toast.makeText(this, "請先選擇一個餐點種類!",Toast.LENGTH_SHORT ).show();
		}
		else{
			meals_sort.get(meals_sort_item).meals.add(new C6_upload_meals(meals_name.getText().toString(),
					Integer.parseInt(meals_money.getText().toString())));
			meals_name.setText("");
			meals_money.setText("");
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			UploadMeals();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private void UploadMeals(){
		if(isConnected()){
			boolean pass = false;
			if(meals_sort.size()<1){
				Toast.makeText(this, "至少需要一個餐點種類",Toast.LENGTH_SHORT ).show();
			}else{
				for(int i=0;i<meals_sort.size();i++){
					pass = true;
					if(meals_sort.get(i).meals.size()<1){
						pass = false;
						Toast.makeText(this, "每個種類至少需要一個餐點",Toast.LENGTH_SHORT ).show();
						break;
					}
				}
			}
			if(pass){
				message();
			}
		}else{
			Toast.makeText(this, "請檢查連線狀態!",Toast.LENGTH_SHORT ).show();
		}
	}


	private void message(){
		new AlertDialog.Builder(UI9_upload_meals.this)
				.setTitle("提醒")
				.setMessage("您日後使用的餐點格式將會依照您上傳的格式，確定依照此格式上傳?")
				.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						progressdialog = ProgressDialog.show(UI9_upload_meals.this, "上傳菜單中", "上傳中 , 請稍後...");
						new Thread(new Runnable(){

							@Override
							public void run() {

								Database.ConnectToMeals();
								Database.UploadMeals();
								Database.Close();
								Database.Connect();
								Database.ChangeAccountHaveMeals();
								Database.Close();
								progressdialog.dismiss();
								message2();
							}
						}).start();

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
					// TODO 自動產生的方法 Stub}
				}).show();
	}
	private void message2(){
		myHandler.post(new Runnable(){
			@Override
			public void run() {
				new AlertDialog.Builder(UI9_upload_meals.this)
						.setTitle("成功")
						.setMessage("上傳成功，請重新登入!")
						.setPositiveButton("確定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								android.os.Process.killProcess(android.os.Process.myPid());
							}
						}).show();
			}
		});
	}
	private void DeleteSort(int position){
		meals_sort.remove(position);
		listview.setAdapter(myAdapter);
		now_sort.setText("");
		meals_sort_item = -1;
		myAdapter2.Change();
	}
		
	private void DeleteMeals(int position){
		meals_sort.get(meals_sort_item).meals.remove(position);
		myAdapter2.Change();
	}
	class Myadapter2 extends BaseAdapter{
		private LayoutInflater myInflater;
		public Myadapter2(Context c) {
    		myInflater = LayoutInflater.from(c);
    	}
		@Override
		public int getCount() {

			return  meals_sort.size()>0&meals_sort_item!=-1? meals_sort.get(meals_sort_item).meals.size():0;
		}
		public void Change(){
			this.notifyDataSetChanged();
		}
		@Override
		public Object getItem(int position) {

			return meals_sort.size()>0&meals_sort_item!=-1? meals_sort.get(meals_sort_item).meals.get(position):null;
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {

			if(view == null){
				view = myInflater.inflate(R.layout.upload_item, null);
				tag2 = new TagView2((TextView)view.findViewById(R.id.meals_item_name),(TextView)view.findViewById(R.id.meals_money),
						(Button)view.findViewById(R.id.delete),(Button)view.findViewById(R.id.edit));
				view.setTag(tag2);
			}else{
				tag2 = (TagView2)view.getTag();
			}
			tag2.name.setText(meals_sort.get(meals_sort_item).meals.get(position).name);
			tag2.money.setText(meals_sort.get(meals_sort_item).meals.get(position).money+"元");
			tag2.delete.setOnClickListener(new ItemButton_Click2(position));
			tag2.edit.setOnClickListener(new ItemButton_Click2(position));
			return view;
		}
		
	}class ItemButton_Click2 implements OnClickListener{
		private int position;
		ItemButton_Click2(int position){
			this.position = position;
		}
		public void onClick(View view) {
			// TODO �۰ʲ��ͪ���k Stub
			int vid = view.getId();
			if(vid == tag2.edit.getId()){
				MealsEdit(position);
			}else if(vid == tag2.delete.getId()){
				DeleteMeals(position);
			}
		}	
	}
	class TagView2{ 
		TextView name,money;
		Button delete,edit;
		TagView2(TextView name,TextView money,Button delete,Button edit){
			this.name = name;
			this.money = money;
			this.edit = edit;
			this.delete = delete;
		}
	}
	private void MealsEdit(final int position){
		LayoutInflater inflater = LayoutInflater.from(UI9_upload_meals.this);
		final View v = inflater.inflate(R.layout.meals_edit, null);
		edit_meals_name = (EditText)v.findViewById(R.id.edit_meals_name);
		edit_meals_money = (EditText)v.findViewById(R.id.edit_meals_money);
		edit_meals_name.setText(meals_sort.get(meals_sort_item).meals.get(position).name);
		edit_meals_money.setText(meals_sort.get(meals_sort_item).meals.get(position).money+"");
		new AlertDialog.Builder(UI9_upload_meals.this)
				.setTitle(meals_sort.get(meals_sort_item).meals.get(position).name).setView(v)
				.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						meals_sort.get(meals_sort_item).meals.get(position).name= edit_meals_name.getText().toString();
						meals_sort.get(meals_sort_item).meals.get(position).money= Integer.parseInt(edit_meals_money.getText().toString());
						myAdapter2.Change();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
					// TODO 自動產生的方法 Stub}
				}).show();
	}
	class Myadapter extends BaseAdapter{
		private LayoutInflater myInflater;
		public Myadapter(Context c) {
    		myInflater = LayoutInflater.from(c);
    	}
		public void Change(){
			this.notifyDataSetChanged();
		}
		@Override
		public int getCount() {
			// TODO �۰ʲ��ͪ���k Stub
			return meals_sort.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO �۰ʲ��ͪ���k Stub
			return meals_sort.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO �۰ʲ��ͪ���k Stub
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO �۰ʲ��ͪ���k Stub
			
			if(view == null){
				view = myInflater.inflate(R.layout.upload_sort_item, null);
				tag = new TagView((TextView)view.findViewById(R.id.name),(Button)view.findViewById(R.id.delete_bt),
						(Button)view.findViewById(R.id.edit_bt));
				view.setTag(tag);
			}else{
				tag = (TagView)view.getTag();
			}
			tag.name.setText(meals_sort.get(position).name);
			tag.delete.setOnClickListener(new ItemButton_Click(position));
			tag.edit.setOnClickListener(new ItemButton_Click(position));
			return view;
		}
	}
	class ItemButton_Click implements OnClickListener{
		private int position;
		ItemButton_Click(int position){
			this.position = position;
		}
		@Override
		public void onClick(View view) {
			// TODO �۰ʲ��ͪ���k Stub
			int vid = view.getId();
			if(vid == tag.edit.getId()){
				SortEdit(position);
			}else if(vid == tag.delete.getId()){
				DeleteSort(position);
			}
		}	
	}
	class TagView{
		TextView name;
		Button edit,delete;
		public TagView(TextView name,Button delete,Button edit){
			this.name = name;
			this.edit = edit;
			this.delete = delete;
		}
	}
	private void SortEdit(final int position){
		LayoutInflater inflater = LayoutInflater.from(UI9_upload_meals.this);
		final View v = inflater.inflate(R.layout.sort_edit, null);
		edit_sort_name = (EditText)v.findViewById(R.id.edit_sort_name);
		edit_sort_name.setText(meals_sort.get(position).name);
		new AlertDialog.Builder(UI9_upload_meals.this)
				.setTitle("更改種類名稱").setView(v)
				.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						meals_sort.get(position).name = edit_sort_name.getText().toString();
						myAdapter.Change();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
					// TODO 自動產生的方法 Stub}
				}).show();
	}
	private boolean isConnected(){
		  ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		  NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		  if (networkInfo != null && networkInfo.isConnected()) {
		    return true;
		  }
		  return false;
	}
}
