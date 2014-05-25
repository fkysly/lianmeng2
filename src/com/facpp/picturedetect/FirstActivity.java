package com.facpp.picturedetect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class FirstActivity extends Activity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		
		this.getActionBar().hide();
		
		new Thread(){  
            public void run(){  
            	try {
					this.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	Intent intent = new Intent(FirstActivity.this, MainActivity.class);
        		startActivity(intent);
        		FirstActivity.this.finish();  
            }  
        }.start(); 
		
		
	}
	
	

}
