package com.example.ordermeals;

import java.util.ArrayList;

import com.example.ordermeals.UI6_delivery_list.DListener;
import com.example.ordermeals.UI6_takeaway_list.TagView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class UI6_inside_list extends Fragment {
	public interface IListener{
		public void SetTable();
		public void CreatInside(int position);
		public void ReturnItem(int position);
	}
	private ListView listview;
	public static ArrayList<C1_inside_list> inside_list_array = new ArrayList<C1_inside_list>();
	public static MyAdapter myadapter;
	public Button setNumber;
	private IListener listener;
	public void onAttach(Activity activity){
		super.onAttach(activity);
		listener = (IListener) activity;
	}
	
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		ViewGroup rootview = (ViewGroup) inflater.inflate(
				R.layout.ui6_inside_list, container, false);
		listview = (ListView)rootview.findViewById(R.id.inside_listView);
		setNumber = (Button)rootview.findViewById(R.id.setNumber);
		myadapter = new MyAdapter(this.getActivity());
		listview.setAdapter(myadapter);
		setNumber.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick(View arg0) {

				listener.SetTable();
			}
			
		});
		listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {

				if(!inside_list_array.get(position).used)
					listener.CreatInside(position);
				else
					listener.ReturnItem(position);
			}
			
		});
		return rootview;
	}
	public void RemoveList(){
		listview.setAdapter(myadapter);
	}
	class MyAdapter extends BaseAdapter{
		private LayoutInflater myInflater;
		public MyAdapter(Context c) {
    		myInflater = LayoutInflater.from(c);
    	}
		public void Change(){
			this.notifyDataSetChanged();
		}
		@Override
		public int getCount() {

			return inside_list_array.size();
		}

		@Override
		public Object getItem(int position) {

			return inside_list_array.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {

			final TagView tag;
			if(view == null){
				view = myInflater.inflate(R.layout.inside_list_item, null);
				tag = new TagView((TextView)view.findViewById(R.id.tableid),(TextView)view.findViewById(R.id.tablestate));
				view.setTag(tag);
			}else{
				tag = (TagView)view.getTag();
			}
			tag.tableid.setText(inside_list_array.get(position).tableid+"");
			if(!inside_list_array.get(position).used)
				tag.tablestate.setText("空桌");
			else
				tag.tablestate.setText("使用中");

			return view;
		}
		
	}
	public class TagView{
		TextView tableid,tablestate;
		public TagView(TextView tableid,TextView tablestate){
			this.tableid = tableid;
			this.tablestate = tablestate;
		}
	}
}
