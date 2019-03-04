package com.example.ordermeals;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class UI2_login_page extends ActionBarActivity {
	
	private Context mContext;
	private EditText account_et,password_et;
	private TextView wrong_tv;
	private Button exit_bt,login_bt,register_bt;
	private Spinner status;
	private String storeID;
	private String[] statusStr = {"點餐/結帳人員","廚師"};
	private ArrayAdapter<String> statuslist;
	private int whichstatus;
	public static String havemeals = "";
	final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private ProgressDialog progressdialog;
	private Handler myHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui2_login_page);
		mContext = this.getApplicationContext();
		FindView();
		SetButtonEvent();
		
		statuslist = new ArrayAdapter<String>(UI2_login_page.this,R.layout.login_page_spinner, statusStr);
		status.setAdapter(statuslist);
		status.setOnItemSelectedListener(new OnItemSelectedListener(){
		    public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
		    	whichstatus = position;
		    }
		    public void onNothingSelected(AdapterView<?> arg0) {}
		});
			
	}
	
	private void FindView(){
		account_et = (EditText)findViewById(R.id.account_et);
		password_et = (EditText)findViewById(R.id.password_et);
		wrong_tv = (TextView)findViewById(R.id.wrong_tv);
		exit_bt = (Button)findViewById(R.id.exit_bt);
		login_bt = (Button)findViewById(R.id.login_bt);
		register_bt = (Button)findViewById(R.id.register_bt);
		status = (Spinner)findViewById(R.id.status);
	}
	
	public void SetButtonEvent(){
		exit_bt.setOnClickListener(buttonLister);
		login_bt.setOnClickListener(buttonLister);
		register_bt.setOnClickListener(buttonLister);
	}
	
	private OnClickListener buttonLister = new OnClickListener(){
		
		@Override
		public void onClick(View view) {
			// TODO �۰ʲ��ͪ���k Stub
			switch(view.getId()){
			case R.id.exit_bt:
				ExitApp();break;
			case R.id.login_bt:
				Login();break;
			case R.id.register_bt:
				ToRegisterPage();break;
			}
		}
	};
	private void Login(){
		if(isConnected()){
			wrong_tv.setVisibility(View.INVISIBLE);
			progressdialog = ProgressDialog.show(UI2_login_page.this, "登入", "登入中 , 請稍後...");
			wrong_tv.setVisibility(View.INVISIBLE);
			new Thread(new Runnable(){

				@Override
				public void run() {

					Database.Connect();
					storeID = Database.VerifyAccount(account_et.getText().toString(),
							password_et.getText().toString());
					if(!storeID.equals("non-Verify")){
						
						if(whichstatus == 0){
							if(havemeals.equals("Y")){
								ToOrderPage();
							}else if(havemeals.equals("N")){
								progressdialog.dismiss();
								remind();
							}
						}
						else if(whichstatus == 1)
							ToCookPage();
					}else{
						ThreadCommunication();
					}
					Database.Close();
					progressdialog.dismiss();
				}
				
			}).start();
		}else{
			wrong_tv.setText("網路未連線!");
			wrong_tv.setVisibility(View.VISIBLE);
		}
	}
	private void remind(){
		myHandler.post(new Runnable(){

			@Override
			public void run() {
				// TODO 自動產生的方法 Stub
				new AlertDialog.Builder(UI2_login_page.this)
						.setTitle("提醒")
						.setMessage("您的帳號尚未擁有任何餐點，將前往上傳餐點。")
						.setPositiveButton("確定",new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO 自動產生的方法 Stub
								ToUploadPage();
							}
						}).show();
			}

		});
	}

	private void ToUploadPage(){
		Intent intent = new Intent(UI2_login_page.this,UI9_upload_meals.class);
		Bundle bundle = new Bundle();
		bundle.putString("storeID", storeID);
		intent.putExtras(bundle);
		this.startActivity(intent);
	}
	private void ThreadCommunication(){
		myHandler.post(new Runnable(){

			@Override
			public void run() {
				// TODO 自動產生的方法 Stub
				wrong_tv.setText("帳號或密碼錯誤!");
				wrong_tv.setVisibility(View.VISIBLE);
			}

		});
	}
	private void ExitApp(){
		new AlertDialog.Builder(UI2_login_page.this)
				.setTitle("結束")
				.setMessage("您確定要結束嗎?")
				.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				}).show();
	}
	private void ToRegisterPage(){
		Intent intent = new Intent(UI2_login_page.this,UI3_register_page.class);
		this.startActivity(intent);
	}
	private void ToOrderPage(){
		Date dt=new Date();
		Intent intent = new Intent(UI2_login_page.this,UI4_order_page.class);
		Bundle bundle = new Bundle();
		bundle.putString("account",account_et.getText().toString());
		bundle.putString("storeID", storeID);
		bundle.putString("login_time", sdf.format(dt));
		intent.putExtras(bundle);
		this.startActivity(intent);
	}
	private void ToCookPage(){
		Intent intent = new Intent(UI2_login_page.this,UI5_cook_page.class);
		Bundle bundle = new Bundle();
		bundle.putString("storeID", storeID);
		intent.putExtras(bundle);
		this.startActivity(intent);
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
