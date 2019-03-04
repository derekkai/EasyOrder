package com.example.ordermeals;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class UI7_delivery_meals extends Fragment {
	public interface DAlertListener{
		public void EditMeals(int position,int number);
		public void ChangeState();
		public void ClearBill();
	}
	private Button certain_bt,clear_bt;
	private int item = 0;
	private TagView tag;
	public TextView username,phone,time,totalmoney;
	public EditText address;
	private MyAdapter myadapter;
	private ListView listview;
	public DAlertListener dal;
	final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
	private SoundPool soundPool;
	private int pay_bill;
	public void onAttach(Activity activity){
		super.onAttach(activity);
		dal = (DAlertListener) activity;
	}
	
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		ViewGroup rootview = (ViewGroup) inflater.inflate(
				R.layout.ui7_delivery_meals, container, false);
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
		pay_bill = soundPool.load(this.getActivity(), R.raw.pay_bill, 1); 
		FindView(rootview);
		certain_bt.setOnClickListener( new OnClickListener(){
			@Override
			public void onClick(View arg0) {

				UI6_delivery_list.delivery_list_array.get(item).certain = true;
				certain_bt.setVisibility(View.INVISIBLE);
		    	clear_bt.setVisibility(View.VISIBLE);
		    	dal.ChangeState();
		    	myadapter.change();
		    	new Thread(new Runnable() {

					public void run() {
						Database.ConnectToCook();
						Database.DTraMealsToCook(item);
						Database.Close();
						Database.ConnectToMeals();
						Database.DAddHotLevel(item);
						Database.Close();
					}
				}).start();
			}
    	});
		clear_bt.setOnClickListener( new OnClickListener(){
			@Override
			public void onClick(View arg0) {

				soundPool.play(pay_bill, 1.0F, 1.0F, 0, 0, 1.0F);
				dal.ClearBill();
				clear_bt.setVisibility(View.INVISIBLE);
			}
    	});
		myadapter = new MyAdapter(this.getActivity());
		return rootview;
	}
	public void FindView(View view){
		clear_bt = (Button)view.findViewById(R.id.D_clear);
    	certain_bt = (Button) view.findViewById(R.id.D_certain);
		address = (EditText)view.findViewById(R.id.D_address_et);
		username = (TextView)view.findViewById(R.id.D_name_tv);
		phone = (TextView)view.findViewById(R.id.D_phone_tv);
		time = (TextView)view.findViewById(R.id.D_time_tv);
		totalmoney = (TextView)view.findViewById(R.id.D_total_money);
		listview = (ListView)view.findViewById(R.id.Dmeals_list);
	}
	public void RemoveList(){
		clear_bt.setVisibility(View.INVISIBLE);
		certain_bt.setVisibility(View.INVISIBLE);
		listview.setAdapter(null);
		address.setText("");
		username.setText("");
		phone.setText("");
		time.setText("");
		totalmoney.setText(0+"");
	}
	public void GetItem(int position){
		this.item = position;
		listview.setAdapter(myadapter);
		address.setText(UI6_delivery_list.delivery_list_array.get(item).address);
		username.setText(UI6_delivery_list.delivery_list_array.get(item).username);
		phone.setText(UI6_delivery_list.delivery_list_array.get(item).phone);
		time.setText(sdf.format(UI6_delivery_list.delivery_list_array.get(item).time));
		ChangeList();
		show();
	}
	 public void show(){
	    	if(UI6_delivery_list.delivery_list_array.get(item).meals.size()<1 ){
	    		certain_bt.setVisibility(View.INVISIBLE);
	    		clear_bt.setVisibility(View.INVISIBLE);
	    	}else if(UI6_delivery_list.delivery_list_array.get(item).meals.size()>=1){
	    		if(UI6_delivery_list.delivery_list_array.get(item).certain){
	    			certain_bt.setVisibility(View.INVISIBLE);
	        		clear_bt.setVisibility(View.VISIBLE);
	    		}else{
	    			clear_bt.setVisibility(View.INVISIBLE);
	    			certain_bt.setVisibility(View.VISIBLE);
	    		}
	    	}
	    }
	public void ChangeList(){
		certain_bt.setVisibility(View.VISIBLE);
		 myadapter.change();
		 Caltotal();
	}
	public void Caltotal(){
		int total = 0;
		for(int i=0;i<UI6_delivery_list.delivery_list_array.get(item).meals.size();i++){
			total += UI6_delivery_list.delivery_list_array.get(item).meals.get(i).money
					*UI6_delivery_list.delivery_list_array.get(item).meals.get(i).number;
		}
		UI6_delivery_list.delivery_list_array.get(item).totalmoney = total;
		totalmoney.setText(UI6_delivery_list.delivery_list_array.get(item).totalmoney+"");
	} 
	class MyAdapter extends BaseAdapter{
		private LayoutInflater myInflater;
		public MyAdapter(Context c){
			myInflater = LayoutInflater.from(c);
		}
		public void change(){
			this.notifyDataSetChanged();
		}
		public void removeItem(int position){
			UI6_delivery_list.delivery_list_array.get(item).meals.remove(position);
			this.notifyDataSetChanged();
		}
		@Override
		public int getCount() {

			return UI6_delivery_list.delivery_list_array.get(item).meals.size();
		}

		@Override
		public Object getItem(int position) {

			return UI6_delivery_list.delivery_list_array.get(item).meals.get(item);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {

			
			if(view == null){
				view = myInflater.inflate(R.layout.meals_item, null);
				 tag = new TagView((TextView)view.findViewById(R.id.name),(TextView)view.findViewById(R.id.money),(TextView)view.findViewById(R.id.number),
			        		(ImageButton)view.findViewById(R.id.editbt),(ImageButton)view.findViewById(R.id.deletbt));
				 view.setTag(tag);
			}else {
		         tag = (TagView) view.getTag();
		    }
			tag.mealsname.setText(UI6_delivery_list.delivery_list_array.get(item).meals.get(position).name);
			tag.number.setText(UI6_delivery_list.delivery_list_array.get(item).meals.get(position).number+"");
			tag.money.setText(UI6_delivery_list.delivery_list_array.get(item).meals.get(position).money+"");
			tag.edit.setOnClickListener(new ItemButton_Click(position));
			tag.delete.setOnClickListener(new ItemButton_Click(position));
			if(UI6_delivery_list.delivery_list_array.get(item).certain){
				tag.delete.setVisibility(View.INVISIBLE);
		    	tag.edit.setVisibility(View.INVISIBLE);
			}else{
				tag.delete.setVisibility(View.VISIBLE);
		    	tag.edit.setVisibility(View.VISIBLE);
			}
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

			int vid = view.getId();
			if(vid == tag.edit.getId()){
				dal.EditMeals(position, UI6_delivery_list.delivery_list_array.get(item).meals.get(position).number);
			}else if(vid == tag.delete.getId()){
				myadapter.removeItem(position);
				Caltotal();
			}
		}
		
	}
	class TagView{
		ImageButton edit,delete;
		TextView mealsname,number,money;
		public TagView(TextView mealsname,TextView money,TextView number,ImageButton edit,ImageButton delete){
			this.mealsname = mealsname;
			this.money = money;
			this.number = number;
			this.edit = edit;
			this.delete = delete;
		}
	}
}
