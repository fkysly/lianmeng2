package com.facpp.picturedetect;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

public class Transefer {
	
	public static final int SW = 50;
	
	public static final int EYE_N = 42;  //s5 42
	
	public static final int EYEBROW_N = 16; //s6 16
	
	public static final int LIP_N = 41; //s8 41
	
	public static final int COU_N = 17; //s4 17
	
	public static Bitmap[] eyebitmap, eyebrowbitmap, lipbitmap, coubitmap;
	
	public static int[][] eyebitmaph, eyebrowbitmaph, lipbitmaph, coubitmaph;
	
	public static int[] transfertoCartoon(Bitmap[] bitmaps, Context context){
		
		int[] result = new int[4];
		
		eyebitmap = new Bitmap[EYE_N];
		eyebitmaph = new int[EYE_N][bitmaps[0].getWidth() * bitmaps[0].getHeight()];
		
		eyebrowbitmap = new Bitmap[EYEBROW_N];
		eyebrowbitmaph = new int[EYEBROW_N][bitmaps[1].getWidth() * bitmaps[1].getHeight()];
		
		lipbitmap = new Bitmap[LIP_N];
		lipbitmaph = new int[LIP_N][bitmaps[2].getWidth() * bitmaps[2].getHeight()];
		
		coubitmap = new Bitmap[COU_N];
		coubitmaph = new int[COU_N][bitmaps[3].getWidth() * bitmaps[3].getHeight()];
		Field field = null;
		int resourceId = 0;
		
		try {
			for (int i = 0; i < EYE_N; i++) {
				field = R.drawable.class.getDeclaredField("cut_s5_" + i);
				resourceId = Integer.parseInt(field.get(null).toString());
				eyebitmap[i] = BitmapFactory.decodeResource(context.getResources(), resourceId);
				if (eyebitmaph[i] != null) {
					eyebitmaph[i] = getHistogram(eyebitmap[i]);
				}
			}
			
			for (int i = 0; i < LIP_N; i++) {
				field = R.drawable.class.getDeclaredField("cut_s8_" + i);
				resourceId = Integer.parseInt(field.get(null).toString());
				lipbitmap[i] = BitmapFactory.decodeResource(context.getResources(), resourceId);
				if (lipbitmaph[i] != null) {
					lipbitmaph[i] = getHistogram(lipbitmap[i]);
				}
			}
			
			for (int i = 0; i < EYEBROW_N; i++) {
				field = R.drawable.class.getDeclaredField("cut_s6_" + i);
				resourceId = Integer.parseInt(field.get(null).toString());
				eyebrowbitmap[i] = BitmapFactory.decodeResource(context.getResources(), resourceId);
				if (eyebrowbitmaph[i] != null) {
					eyebrowbitmaph[i] = getHistogram(eyebrowbitmap[i]);
				}
			}
			
			for (int i = 0; i < COU_N; i++) {
				field = R.drawable.class.getDeclaredField("cut_s4_" + i);
				resourceId = Integer.parseInt(field.get(null).toString());
				coubitmap[i] = BitmapFactory.decodeResource(context.getResources(), resourceId);
				Log.d("hello","s4_" + i+"");
				if (coubitmaph[i] != null) {
					coubitmaph[i] = getHistogram(coubitmap[i]);
				}
			}
		//Log.d("hello", resourceId + "");
		int[] bitmaph;
		for(int i = 0; i < 4; i++) {
			binary(bitmaps[i]);
			bitmaph = getHistogram(bitmaps[i]);
			switch (i) {
			case 0:
				result[i] = compare(bitmaps[i], bitmaph, eyebitmap, eyebitmaph, EYE_N);
				field = R.drawable.class.getDeclaredField("s5_" + result[i]);
				break;
			case 1:
				result[i] = compare(bitmaps[i], bitmaph, eyebrowbitmap, eyebrowbitmaph, EYEBROW_N);
				field = R.drawable.class.getDeclaredField("s6_" + result[i]);
				break;
			case 2:
				result[i] = compare(bitmaps[i], bitmaph, lipbitmap, lipbitmaph, LIP_N);
				field = R.drawable.class.getDeclaredField("s8_" + result[i]);
				break;
			case 3:
				result[i] = compare(bitmaps[i], bitmaph, coubitmap, coubitmaph, COU_N);
				field = R.drawable.class.getDeclaredField("s4_" + result[i]);
				break;
			}
			

			resourceId = Integer.parseInt(field.get(null).toString());
			result[i] = resourceId;
		}
		
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
		return result;
	}
	
	private static void binary(Bitmap bitmap){
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();
		
		int[][] gray = new int[width][height];
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				gray[i][j] = getGray(bitmap.getPixel(i, j));
			}
		}
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (getAverageColor(gray, i, j, width, height) > SW) {
					bitmap.setPixel(i, j, Color.BLACK);
				} else {
					bitmap.setPixel(i, j, Color.WHITE);
				}
			}
		}
	}
	
	private static int compare(Bitmap mybitmap, int[] mybitmaph, Bitmap[] catbitmap, int[][] catbitmaph, int Cat_n){
		double similarity = 0.0, maxSimilarity = 0.0;
		int maxPos = 0;
		float total1 = 0.0f;
		float total2 = 0.0f;
		
		total1 = mybitmap.getWidth();
		for (int i = 0; i < Cat_n; i++) {
			similarity = 0.0;
			
			total2 = catbitmap[i].getWidth();
			
			for (int j = 0; j < catbitmaph[i].length; j++) {
				similarity += Math.sqrt(((double)mybitmaph[j]) / total1 * ((double)catbitmaph[i][j]) / total2);
			}
			
			if (similarity > maxSimilarity) {
				maxSimilarity = similarity;
				maxPos = i;
			}
		}
		
		return maxPos; 
	}

	public static int getGray(int rgb){
		int r=Color.red(rgb);
		int g=Color.green(rgb);
		int b=Color.blue(rgb);
		int top=(r+g+b)/3;
		return top;
	}
	
	public static int  getAverageColor(int[][] gray, int x, int y, int w, int h)
    {
        int rs = gray[x][y]
                      	+ (x == 0 ? 255 : gray[x - 1][y])
			            + (x == 0 || y == 0 ? 255 : gray[x - 1][y - 1])
			            + (x == 0 || y == h - 1 ? 255 : gray[x - 1][y + 1])
			            + (y == 0 ? 255 : gray[x][y - 1])
			            + (y == h - 1 ? 255 : gray[x][y + 1])
			            + (x == w - 1 ? 255 : gray[x + 1][ y])
			            + (x == w - 1 || y == 0 ? 255 : gray[x + 1][y - 1])
			            + (x == w - 1 || y == h - 1 ? 255 : gray[x + 1][y + 1]);
        return rs / 9;
    }
	
	private static int[] getHistogram(Bitmap bitmap) {
		int[] srcPixels = new int[bitmap.getWidth() * bitmap.getHeight()];
		
			bitmap.getPixels(srcPixels, 0, bitmap.getWidth(), 0, 0,
					bitmap.getWidth(), bitmap.getHeight());
			
		int[] itensity = new int[256];
		
		for (int i = 0; i < itensity.length; i++) {
			itensity[i] = 0;
		}
		
		int index = 0;
		int r, g, b, gray;
		for ( int row = 0; row < bitmap.getHeight(); row++ ) {
			for ( int col = 0; col < bitmap.getWidth(); col++ ) {
				index = row * bitmap.getWidth() + col;
				r = Color.red(srcPixels[index]);
				g = Color.green(srcPixels[index]);
				b = Color.blue(srcPixels[index]);
				gray = (int) (0.299 * (double) r)
						+ (int) (0.587 * (double) g)
						+ (int) (0.114 * (double) b);
				itensity[gray]++;
			}
		}
		
		return itensity;
	}

}
