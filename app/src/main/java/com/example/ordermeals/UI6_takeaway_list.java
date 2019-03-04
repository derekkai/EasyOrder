package com.example.ordermeals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class UI6_takeaway_list extends Fragment {
	
	public interface TListener{
		public void ReturnItem(int position);
	}
	
	public static ArrayList<C1_takeaway_list> takeaway_list_array = new ArrayList<C1_takeaway_list>();
	private ListView listview;
	final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
	private MyAdapter myAdapter;
	private TListener listener;
	
	public void onAttach(Activity activity){
		super.onAttach(activity);
		listener = (TListener) activity;
	}
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		ViewGroup rootview = (ViewGroup) inflater.inflate(
				R.layout.ui6_takeaway_list, container, false);
		FindView(rootview);
		listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {

				listener.ReturnItem(position);
			}
			
		});
		myAdapter = new MyAdapter(this.getActivity());
		listview.setAdapter(myAdapter);
		return rootview;
	}
	private void FindView(View view){
		listview = (ListView)view.findViewById(R.id.takeaway_listView);
	}
	public void AddList(String username,String phone,Date time){
		takeaway_list_array.add(new C1_takeaway_list(username,phone,time));
		myAdapter.Change();
	}
	public void RemoveList(){
		listview.setAdapter(myAdapter);
	}
	private class MyAdapter extends BaseAdapter{
		private LayoutInflater myInflater;
		public MyAdapter(Context c) {
    		myInflater = LayoutInflater.from(c);
    	}
		public void Change() {
			this.notifyDataSetChanged();
		}
		@Override
		public int getCount() {

			return takeaway_list_array.size();
		}

		@Override
		public Object getItem(int position) {

			return takeaway_list_array.get(position);
		}

		@Override
		public long getItemId(int position) {

			
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {

			final TagView tag;
			if(view == null){
				view = myInflater.inflate(R.layout.list_item, null);
				tag = new TagView((TextView)view.findViewById(R.id.username),
						(TextView)view.findViewById(R.id.listtime));
				view.setTag(tag);
			}else{
				tag = (TagView)view.getTag();
			}
			tag.username.setText(takeaway_list_array.get(position).username);
			tag.listtime.setText(sdf.format(takeaway_list_array.get(position).time));
			return view;
		}
		
	}
	
	public class TagView{
		TextView username,listtime;
		public TagView(TextView username,TextView listtime){
			this.username = username;
			this.listtime = listtime;
		}
	}
}
