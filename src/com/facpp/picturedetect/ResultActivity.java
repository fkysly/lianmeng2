package com.facpp.picturedetect;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class ResultActivity extends Activity {
	private int[] resourceArray = new int[4];
	private ImageView resultImage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
		resourceArray = getIntent().getIntArrayExtra("resource");
		resultImage = (ImageView)findViewById(R.id.resultView);
	}
	
	@Override
	protected void onResume() {
		Resources resource = getResources();
		Drawable[] layers = new Drawable[4];
		for (int i=0; i<=3;i++) {
//			Log.d("hello",resourceArray[i]);
			layers[3-i] = resource.getDrawable(resourceArray[i]); 

		}
		LayerDrawable layerDrawable = new LayerDrawable(layers); 
		resultImage.setImageDrawable(layerDrawable);
		super.onResume();
	}
	
	


}
