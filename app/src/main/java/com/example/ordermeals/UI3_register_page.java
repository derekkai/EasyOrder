package com.example.ordermeals;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UI3_register_page extends ActionBarActivity {
	
	private EditText address_et,account_et,password_et,password2_et,phone_et;
	private TextView address_wrong_tv,account_wrong_tv,password_wrong_tv,password2_wrong_tv,phone_wrong_tv;
	private CheckBox NFU; 
	
	private boolean addressright,accountright,passwordright,password2right,phoneright;
	final String accountL = "[a-zA-Z0-9]{4,12}",
			passwordL = "[a-z0-9]{4,8}",
			phoneL = "0[1-9]([0-9]){7,8}";
	private String NFUS = "N";
	private LinearLayout layout;
	
	private Handler myHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui3_register_page);
		FindView();
		SetEditTextEvent();
	}
	public void Register(View view){
		if(isConnected()){
			if(NFU.isChecked())
				NFUS = "Y";
			ClearFouse();
			System.out.println(addressright+" "+accountright+" "+passwordright+" "+password2right+" "+phoneright);
			if(addressright&&accountright&&passwordright&&password2right&&phoneright){
				new Thread(new Runnable(){
					
					@Override
					public void run() {

						Database.Connect();
						ThreadCommunication(Database.Register(account_et.getText().toString()
								,password_et.getText().toString()
								,address_et.getText().toString()
								,phone_et.getText().toString()
								,NFUS));
						Database.Close();
					}
					
				}).start();
			}else{
				
			}
		}else{
			Toast.makeText(this, "註冊失敗!請檢查網路狀態",Toast.LENGTH_SHORT ).show();
		}
	}
	private void ThreadCommunication(final boolean sucess){
		myHandler.post(new Runnable(){

			@Override
			public void run() {
				// TODO 自動產生的方法 Stub
				if(sucess){
					layout.setVisibility(View.VISIBLE);
					System.out.println("成功");
				}
				else{
					account_wrong_tv.setText("此帳號使用過!");
					account_wrong_tv.setVisibility(View.VISIBLE);
					System.out.println("失敗");
				}

			}

		});
	}
	private void ClearFouse(){
		address_et.clearFocus();
		account_et.clearFocus();
		password_et.clearFocus();
		password2_et.clearFocus();
		phone_et.clearFocus();
	}
	private void FindView(){
		address_et = (EditText)findViewById(R.id.address_et);
		account_et = (EditText)findViewById(R.id.account_et);
		password_et = (EditText)findViewById(R.id.password_et);
		password2_et = (EditText)findViewById(R.id.password2_et);
		phone_et = (EditText)findViewById(R.id.phone_et);
		
		address_wrong_tv = (TextView)findViewById(R.id.address_wrong_tv);
		account_wrong_tv = (TextView)findViewById(R.id.account_wrong_tv);
		password_wrong_tv = (TextView)findViewById(R.id.password_wrong_tv);
		password2_wrong_tv = (TextView)findViewById(R.id.password2_wrong_tv);
		phone_wrong_tv = (TextView)findViewById(R.id.phone_wrong_tv);
		
		NFU = (CheckBox)findViewById(R.id.NFU);
		layout = (LinearLayout)findViewById(R.id.linearLayout1);
	}
	private void SetEditTextEvent(){
		address_et.setOnFocusChangeListener(edittextListener);
		account_et.setOnFocusChangeListener(edittextListener);
		password_et.setOnFocusChangeListener(edittextListener);
		password2_et.setOnFocusChangeListener(edittextListener);
		phone_et.setOnFocusChangeListener(edittextListener);
	}
	private OnFocusChangeListener edittextListener = new OnFocusChangeListener(){

		@Override
		public void onFocusChange(View view, boolean hasFocus) {
			// TODO �۰ʲ��ͪ���k Stub
			switch(view.getId()){
			case R.id.address_et:
				if(hasFocus){
					address_wrong_tv.setVisibility(View.INVISIBLE);
					addressright = false;
				}else{
					if(!address_et.getText().toString().isEmpty()){
						address_wrong_tv.setVisibility(View.INVISIBLE);
						addressright = true;
					}else
						address_wrong_tv.setVisibility(View.VISIBLE);
				}
				break;
				
			case R.id.account_et:
				if(hasFocus){
					account_wrong_tv.setText("帳號格式錯誤!");
					account_wrong_tv.setVisibility(View.INVISIBLE);
					accountright = false;
				}else{
					if(account_et.getText().toString().matches(accountL)){
						account_wrong_tv.setVisibility(View.INVISIBLE);
						accountright = true;
					}else
						account_wrong_tv.setVisibility(View.VISIBLE);
				}
				break;
				
			case R.id.password_et:
				if(hasFocus){
					password_wrong_tv.setVisibility(View.INVISIBLE);
					passwordright = false;
				}else{
					if(password_et.getText().toString().matches(passwordL)){
						password_wrong_tv.setVisibility(View.INVISIBLE);
						passwordright = true;
					}else
						password_wrong_tv.setVisibility(View.VISIBLE);
				}
				break;
			
			case R.id.password2_et:
				if(hasFocus){
					password2_wrong_tv.setVisibility(View.INVISIBLE);
					password2right = false;
				}else{
					if(password2_et.getText().toString().equals(password_et.getText().toString())){
						password2_wrong_tv.setVisibility(View.INVISIBLE);
						password2right = true;
					}else
						password2_wrong_tv.setVisibility(View.VISIBLE);
				}
				break;
				
			case R.id.phone_et:
				if(hasFocus){
					phone_wrong_tv.setVisibility(View.INVISIBLE);
					phoneright = false;
				}else{
					if(phone_et.getText().toString().matches(phoneL)){
						phone_wrong_tv.setVisibility(View.INVISIBLE);
						phoneright = true;
					}else
						phone_wrong_tv.setVisibility(View.VISIBLE);
				}
				break;
			}
		}
	};
	
	private boolean isConnected(){
		  ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		  NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		  if (networkInfo != null && networkInfo.isConnected()) {
		    return true;
		  }
		  return false;
	}
}
