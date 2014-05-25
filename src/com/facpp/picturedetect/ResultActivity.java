package com.facpp.picturedetect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Random;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

public class ResultActivity extends Activity {
	private int[] resourceArray = new int[4];
	private ImageView resultImage;
	private String gender;
	private double smiling;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
		resourceArray = getIntent().getIntArrayExtra("resource");
		gender = getIntent().getStringExtra("gender");
		smiling = getIntent().getDoubleExtra("smiling", 0.0);
		resultImage = (ImageView)findViewById(R.id.resultView);
//		resultImage.setDrawingCacheEnabled(true);
	}
	
	@Override
	protected void onResume() {
		Resources resource = getResources();
		Drawable[] layers = new Drawable[5];
		layers[0] = resource.getDrawable(resourceArray[3]);
		layers[1] = resource.getDrawable(resourceArray[0]);
		layers[2] = resource.getDrawable(resourceArray[1]);
		layers[3] = resource.getDrawable(resourceArray[2]);
//		for (int i=0; i<=3;i++) {
////			Log.d("hello",resourceArray[i]);
//			layers[3-i] = resource.getDrawable(resourceArray[i]); 
//
//		}
		
		String prefix = "s11_";
		if(gender.equals("Female")) {
			prefix = "s11_g_";
		}
		Field field;
		try {
			int smileValue = (int)(smiling)/25;
			if(smileValue == 4) smileValue = 3;
			field = R.drawable.class.getDeclaredField("s8_" + (new Random().nextInt(10) + smileValue*10));
			int resid = Integer.parseInt(field.get(null).toString());
			layers[3] = resource.getDrawable(resid); 
			
			field = R.drawable.class.getDeclaredField(prefix + new Random().nextInt(50));
			resid = Integer.parseInt(field.get(null).toString());
			layers[4] = resource.getDrawable(resid); 
			LayerDrawable layerDrawable = new LayerDrawable(layers); 
			resultImage.setImageDrawable(layerDrawable);
//			Bitmap resultBitmap = resultImage.getDrawingCache();
//			resultImage.setDrawingCacheEnabled(false);
//			File vFile = new File(Environment.getExternalStorageDirectory()+File.separator+"hdlm2"+File.separator+"photo.jpg");
//	    	if(!vFile.getParentFile().exists()) {
//				File vDirPath = vFile.getParentFile(); //new File(vFile.getParent());
//				vDirPath.mkdirs();
//	    	}
//	    	if (vFile.exists()) {
//	    		vFile.delete();
//	    	}
//	    	FileOutputStream out = new FileOutputStream(vFile);
//	    	resultBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
//    	   out.flush();
//    	   out.close();
//    	   Toast.makeText(getApplication(), "亲,图片已经保存到sd卡的hdlm2目录中的photo.jpg...",Toast.LENGTH_LONG).show();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.onResume();
	}
	
	


}
