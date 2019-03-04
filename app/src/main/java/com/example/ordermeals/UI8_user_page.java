package com.example.ordermeals;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UI8_user_page extends Activity {
	
	TextView account_tv,phone_tv,address_tv,login_tv;
	CheckBox nfuYN_cb;
	EditText phone_ed,address_ed;
	public static String phone;
	public static String address;
	public static String login;
	public static String nfuYN;
	private Handler myHandler = new Handler();
	final String phoneL = "0[1-9]([0-9]){7,8}";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui8_user_page);
		FindView();
		GetUserInfo();
		
	}
	public void GetUserInfo(){
		new Thread(new Runnable(){

			@Override
			public void run() {
				Database.Connect();
				Database.GetInfoFromDB();
				ThreadCommunication();
			}
		}).start();
	}
	
	private void ThreadCommunication(){//�ܧ�UI
		myHandler.post(new Runnable() {
			@Override
			public void run() {
				account_tv.setText(UI4_order_page.account);
				phone_ed.setText(phone);
				address_ed.setText(address);
				login_tv.setText(login);
				if(nfuYN.equals("Y"))
					nfuYN_cb.setChecked(true);
			}
		});
	}
	
	public void FindView(){
		account_tv = (TextView) findViewById(R.id.textView7);
		phone_tv = (TextView) findViewById(R.id.textView8);
		address_tv = (TextView) findViewById(R.id.textView9);
		login_tv = (TextView) findViewById(R.id.textView4);
		nfuYN_cb = (CheckBox) findViewById(R.id.checkBox1);
		phone_ed = (EditText) findViewById(R.id.editText1);
		address_ed = (EditText) findViewById(R.id.editText2);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ui8_user_page, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.edit) {
			if(!address_ed.getText().toString().isEmpty()&phone_ed.getText().toString().matches(phoneL)){

				new Thread(new Runnable() {

					public void run() {
						Database.Connect();
						Database.EditUserInfo(phone_ed.getText().toString(), address_ed.getText().toString(), nfuYN_cb.isChecked());
						Database.Close();
					}
				}).start();
				Toast.makeText(this, "更改完成",Toast.LENGTH_SHORT ).show();
			}else {
				Toast.makeText(this, "請檢查格式!住址不能為空白!\n市話:05XXXXXXX\n手機:09XXXXXXXX",Toast.LENGTH_SHORT ).show();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
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
