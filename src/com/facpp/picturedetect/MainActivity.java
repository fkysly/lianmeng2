package com.facpp.picturedetect;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

/**
 * A simple demo, get a picture form your phone<br />
 * Use the facepp api to detect<br />
 * Find all face on the picture, and mark them out.
 * @author moon5ckq
 */
public class MainActivity extends Activity {
	
	public HashMap<String, Point> landmark;
	public int[] face_part; // five face part

	final private static String TAG = "MainActivity";
	final private int PICTURE_CHOOSE = 1;
	
	private ImageView imageView = null;
	private Bitmap img = null;
	private Button buttonDetect = null;
	
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;
	private ProgressDialog prodlg;
	
	private boolean max(float x, float y) {
		if (x > y) return true;
		return false;
	}
	
	private boolean min(float x, float y) {
		if (x < y) return true;
		return false;
	}
	
	private CutPoint maxAndMinPoint(Point[] points) {
		CutPoint cp = new CutPoint();
		for (Point point : points) {
			 	if (max(point.y, cp.max.y)) {
			 		cp.max.y = point.y;
			 	}
			 	if (max(point.x, cp.max.x)) {
			 		cp.max.x = point.x;
			 	}
			 	if (min(point.y, cp.min.y)) {
			 		cp.min.y = point.y;
			 	}
			 	if (min(point.x, cp.min.x)) {
			 		cp.min.x = point.x;
			 	}
			 	
			 }
			 return cp;
	}
	
	/**
	 +	 * cut Bitmap to four parts
	 +	 * @param bitmap
	 +	 * @param landmark
	 +	 * @return
	 +	 */
	 private int[] cut2four(Bitmap bitmap, HashMap<String, Point> landmark) {
	 CutPoint cp = new CutPoint();
	 int start_x, start_y, width, height; 
	 
	 // eyes
	 Point[] eyepoints = null;
	 eyepoints[0] = landmark.get("left_eye_bottom");
	 eyepoints[1] = landmark.get("left_eye_center");
	 eyepoints[2] = landmark.get("left_eye_left_corner");
	 eyepoints[3] = landmark.get("left_eye_lower_left_quarter");
	 eyepoints[4] = landmark.get("left_eye_lower_right_quarter");
	 eyepoints[5] = landmark.get("left_eye_pupil");
	 eyepoints[6] = landmark.get("left_eye_right_corner");
	 eyepoints[7] = landmark.get("left_eye_top");
	 eyepoints[8] = landmark.get("left_eye_upper_left_quarter");
	 eyepoints[9] = landmark.get("left_eye_upper_right_quarter");
	 cp = maxAndMinPoint(eyepoints);
	 start_x = (int)cp.min.x;
	 start_y = (int)cp.min.y;
	 width = (int)(cp.max.x - cp.min.x);
	 height = (int)(cp.max.y - cp.min.y);
	 Bitmap eyes = Bitmap.createBitmap(bitmap, start_x, start_y, width, height);
	 imageView.setImageBitmap(img);
	 
	 // eyebrow
	 
	 // lip
	 
	 // contour
	 
	 return null;
	 }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button button = (Button)this.findViewById(R.id.button1);
        button.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				//get a picture form your phone
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		        photoPickerIntent.setType("image/*");
		        startActivityForResult(photoPickerIntent, PICTURE_CHOOSE);
			}
		});
        
        
        buttonDetect = (Button)this.findViewById(R.id.button2);
        buttonDetect.setVisibility(View.INVISIBLE);
        buttonDetect.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				
				prodlg = new ProgressDialog(MainActivity.this);
				prodlg.setTitle("因为您比较帅\n检测时间较长");
				prodlg.show();
				FaceppDetect faceppDetect = new FaceppDetect();
				faceppDetect.setDetectCallback(new DetectCallback() {
					
					public void detectResult(JSONObject face_detect, JSONObject face_landmark) {
						//Log.v(TAG, face_detect.toString());
						
						//use the red paint
						Paint paint = new Paint();
						paint.setColor(Color.RED);
						paint.setStrokeWidth(Math.max(img.getWidth(), img.getHeight()) / 100f);

						//create a new canvas
						Bitmap bitmap = Bitmap.createBitmap(img.getWidth(), img.getHeight(), img.getConfig());
						Canvas canvas = new Canvas(bitmap);
						canvas.drawBitmap(img, new Matrix(), null);
						
						
						try {
							//find out all faces
							final int count = face_detect.getJSONArray("face").length();
							
							
							landmark = new HashMap<String, Point>();
							JSONObject ob;
							Point p = new Point();
							
							for (int i = 0; i < count; ++i) {
								float x, y, w, h;
								//get the center point
								x = (float)face_detect.getJSONArray("face").getJSONObject(i)
										.getJSONObject("position").getJSONObject("center").getDouble("x");
								y = (float)face_detect.getJSONArray("face").getJSONObject(i)
										.getJSONObject("position").getJSONObject("center").getDouble("y");

								//get face size
								w = (float)face_detect.getJSONArray("face").getJSONObject(i)
										.getJSONObject("position").getDouble("width");
								h = (float)face_detect.getJSONArray("face").getJSONObject(i)
										.getJSONObject("position").getDouble("height");
								
								//change percent value to the real size
								x = x / 100 * img.getWidth();
								w = w / 100 * img.getWidth() * 0.7f;
								y = y / 100 * img.getHeight();
								h = h / 100 * img.getHeight() * 0.7f;

								//draw the box to mark it out
								canvas.drawLine(x - w, y - h, x - w, y + h, paint);
								canvas.drawLine(x - w, y - h, x + w, y - h, paint);
								canvas.drawLine(x + w, y + h, x - w, y + h, paint);
								canvas.drawLine(x + w, y + h, x + w, y - h, paint);
								
								// get landmark
								ob = face_landmark.getJSONArray("result").getJSONObject(0)
										.getJSONObject("landmark");
								
								Iterator<String> keys = ob.keys();
								
								while (keys.hasNext()) {
									String key = (String) keys.next();
									p.x = (float)ob.getJSONObject(key).getDouble("x");
									p.y = (float)ob.getJSONObject(key).getDouble("y");
									
									//change percent value to the real size
									p.x = p.x / 100 * img.getWidth();
									p.y = p.y / 100 * img.getHeight();
									
									//draw the points to mark it out
									canvas.drawPoint(p.x, p.y, paint);
									
									landmark.put(key, p);
								}
							
							}
							
							//save new image
							img = bitmap;

							MainActivity.this.runOnUiThread(new Runnable() {
								
								public void run() {
									//show the image
									imageView.setImageBitmap(img);
									Toast.makeText(getApplication(), "Finished, "+ count + " faces.",Toast.LENGTH_SHORT).show();
									prodlg.dismiss();
								}
							});
							
						} catch (JSONException e) {
							e.printStackTrace();
							MainActivity.this.runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(getApplication(), "Error",Toast.LENGTH_SHORT).show();

								}
							});
						}
						
					}
				});
				faceppDetect.detect(img);
			}
		});
        
        imageView = (ImageView)this.findViewById(R.id.imageView1);
        imageView.setImageBitmap(img);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	
    	//the image picker callback
    	if (requestCode == PICTURE_CHOOSE) {
    		if (intent != null) {
    			//The Android api ~~~ 
    			//Log.d(TAG, "idButSelPic Photopicker: " + intent.getDataString());
    			Cursor cursor = getContentResolver().query(intent.getData(), null, null, null, null);
    			cursor.moveToFirst();
    			int idx = cursor.getColumnIndex(ImageColumns.DATA);
    			String fileSrc = cursor.getString(idx); 
    			//Log.d(TAG, "Picture:" + fileSrc);
    			
    			//just read size
    			Options options = new Options();
    			options.inJustDecodeBounds = true;
    			img = BitmapFactory.decodeFile(fileSrc, options);

    			//scale size to read
    			options.inSampleSize = Math.max(1, (int)Math.ceil(Math.max((double)options.outWidth / 1024f, (double)options.outHeight / 1024f)));
    			options.inJustDecodeBounds = false;
    			img = BitmapFactory.decodeFile(fileSrc, options);
    			
    			
    			imageView.setImageBitmap(img);
    			buttonDetect.setVisibility(View.VISIBLE);
    		}
    		else {
    			Log.d(TAG, "idButSelPic Photopicker canceled");
    		}
    	} else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
    		if (resultCode == RESULT_OK) {
    			//The Android api ~~~ 
    			//Log.d(TAG, "idButSelPic Photopicker: " + intent.getDataString());
    			//Log.d(TAG, "Picture:" + fileSrc);
    			
    			//just read size
    			Options options = new Options();
    			options.inJustDecodeBounds = true;
    			//scale size to read
    			options.inSampleSize = Math.max(1, (int)Math.ceil(Math.max((double)options.outWidth / 1024f, (double)options.outHeight / 1024f)));
    			options.inJustDecodeBounds = false;
    			img = BitmapFactory.decodeFile(fileUri.getPath(), options);
    			imageView.setImageBitmap(img);
    			buttonDetect.setVisibility(View.VISIBLE);
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
    	}
    }

    private class FaceppDetect {
    	DetectCallback callback = null;
    	
    	public void setDetectCallback(DetectCallback detectCallback) { 
    		callback = detectCallback;
    	}

    	public void detect(final Bitmap image) {
    		
    		new Thread(new Runnable() {
				
				public void run() {
					HttpRequests httpRequests = new HttpRequests("4480afa9b8b364e30ba03819f3e9eff5", "Pz9VFT8AP3g_Pz8_dz84cRY_bz8_Pz8M", true, false);
		    		//Log.v(TAG, "image size : " + img.getWidth() + " " + img.getHeight());
		    		
		    		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		    		float scale = Math.min(1, Math.min(600f / img.getWidth(), 600f / img.getHeight()));
		    		Matrix matrix = new Matrix();
		    		matrix.postScale(scale, scale);

		    		Bitmap imgSmall = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, false);
		    		//Log.v(TAG, "imgSmall size : " + imgSmall.getWidth() + " " + imgSmall.getHeight());
		    		
		    		imgSmall.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		    		byte[] array = stream.toByteArray();
		    		
		    		try {
		    			//detection-detect
						JSONObject face_detect = httpRequests.detectionDetect(new PostParameters().setImg(array));
						
						String face_id = null;
						
						// get face_id
						try {
							face_id = face_detect.getJSONArray("face").getJSONObject(0).getString("face_id");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						JSONObject face_landmark = httpRequests.detectionLandmark(new PostParameters().setFaceId(face_id));
						// 
						
						//finished , then call the callback function
						if (callback != null && face_id != null) {
							callback.detectResult(face_detect, face_landmark);
						}
					} catch (FaceppParseException e) {
						e.printStackTrace();
						MainActivity.this.runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(getApplication(), "Network Error",Toast.LENGTH_SHORT).show();

							}
						});
					}
					
				}
			}).start();
    	}
    }

    public void takephoto(View view) {
    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	File vFile = new File(Environment.getExternalStorageDirectory()+File.separator+"hdlm2"+File.separator+"img.jpg");

    	if(!vFile.getParentFile().exists()) {
			File vDirPath = vFile.getParentFile(); //new File(vFile.getParent());
			vDirPath.mkdirs();
    	}

    	fileUri = Uri.fromFile(vFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    
    
    
    interface DetectCallback {
    	void detectResult(JSONObject face_detect, JSONObject face_landmark);
	}
    
    private class Point {
    	 public float x;
    	 public float y;
    	 }
    	 
    	 private class CutPoint {
    	 public Point max;
    	 public Point min;
    	 }
}