package com.example.ordermeals;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class UI1_splash_screen extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui1_splash_screen);
        
        new Thread(new Runnable(){

			@Override
			public void run() {

				try{
					Thread.sleep(3000);
					startActivity(new Intent().setClass(UI1_splash_screen.this, UI2_login_page.class));
					finish();
				}catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        	
        }).start();
    }
}
