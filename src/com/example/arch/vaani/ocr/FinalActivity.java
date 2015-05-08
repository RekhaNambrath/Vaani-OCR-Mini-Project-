package com.example.arch.vaani.ocr;

//import static android.support.v4.app.FragmentActivity.TAG;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;
//import com.mashape.p.hablaadictionarytranslationhablaav1.http.response.HttpResponse;
public class FinalActivity extends Activity {
	TextView recognised;
	TextView translated;
	ImageView imageDisp;
	public static File imgFile;
	String recognizedText;
	String choice;
	
	
	public static String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Vaani";
	public static final String lang="mal";
	private static final String TAG = "FinalActivity.java";
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_final);
		String myRef = this.getIntent().getStringExtra("name");
		choice= this.getIntent().getStringExtra("choice");
	    imgFile = new  File(myRef);
	   //String choice = this.getIntent().getStringExtra("choice");
	    String path1 =imgFile.getAbsolutePath();
	    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
	    imageDisp = (ImageView) findViewById(R.id.imageId);
	   
	   String[] paths = new String[] { DATA_PATH, DATA_PATH + "/tessdata/" };
		    for (String path : paths) {
		    	File dir = new File(path);
		    	if (!dir.exists()) {
		                dir.mkdirs();// Create the storage directory if it does not exist
		    	}
		    }
		    
		    if (!(new File(DATA_PATH + "/tessdata/" + lang + ".traineddata")).exists()) {
		    	AssetManager assetManager = getAssets();
		        String[] files; 
		        try {
	    	        files = assetManager.list("");
	    	    }
	    	catch (IOException e) {
	    	        Log.e("asset list", "Failed to get asset file list.", e);
	    	    }
	    	
		        try {
					files = assetManager.list("tessdata");
					 InputStream in = null;
					 OutputStream out = null;
					 
					 in = assetManager.open("tessdata/"+ lang + ".traineddata");
					 out = new FileOutputStream(DATA_PATH + "/tessdata/" + lang + ".traineddata");
					 copyFile(in, out);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					Log.e("write", "Failed to get asset file list.", e1);
				}
		    	
		    }
		    TessBaseAPI baseApi = new TessBaseAPI();
		   baseApi.init(DATA_PATH, lang);
		   baseApi.setImage(bitmap);
		   String recognizedText = baseApi.getUTF8Text();
		   recognizedText=correctText(recognizedText);
   	    recognised=(TextView)findViewById(R.id.textView1);
   	    translated=(TextView)findViewById(R.id.textView2);
   	    recognised.setText(recognizedText);
   	    translated.setText(readTxt(recognizedText));
   	    
   	 imageDisp.setImageBitmap(bitmap);
   	 
   	//Transliterator transliterator = new Transliterator(this.getContext());
}
	
public String correctText(String recognizedText)
{
	Context context = getApplicationContext();
	int duration = Toast.LENGTH_SHORT;
	int len,i,j,p = 0;
	String temp;
	StringBuilder builder;
	String text;
	String [] c = new String[1000];
	String [] a ={"േ", "െ", "ൈ", "ൊ", "ോ", "ൌ"};
	len= recognizedText.length();
    c = recognizedText.split("");
    for(i=0;i<(c.length-1);i++)
    {
    	for(j=0;j<a.length;j++)
        {
    		if(a[j].equals(c[i]))
    		{
    			p=1;
    			break;
    		}
		}
		if(p==1)
		{
		   temp = c[i];
		   c[i] = c[i+1];
		   c[i+1] = temp;
		   i++;
		   p=0;
		 }
		}		    
		builder = new StringBuilder();
        for(String s:c) {
            builder.append(s);
        }
        text= builder.toString();
        
	return text;
	
}
	
public String readTxt(String recognizedText)
{
	BufferedReader reader;
	String line = "Sorry.. Unable to translate text!";
	String[] array;
   	AssetManager assetManager = getAssets();
   	InputStream in;
   	try {
		in = this.getAssets().open("dict.txt");
		reader = new BufferedReader(new InputStreamReader(in));
		boolean i = true;
		line = reader.readLine(); 
		while(line!=null)
		{	//line = reader.readLine();
	      	array = line.split(":");
	      	if(array.length > 1 )
	      	{
	      		if(array[1].equals(recognizedText))
	      		{
	      			return array[0];
	      		}
	      		
	      	}
	      	line=reader.readLine();
	 
	   }		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		Log.e("translation", "error opening assets", e);
	}
    	 
   	return "Sorry couldnt translate!!";
}
	
	
	private void copyFile(InputStream in, OutputStream out) throws IOException 
	{
		byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1){
			out.write(buffer, 0, read);
		}
	}
}
