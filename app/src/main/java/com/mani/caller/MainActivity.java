package com.mani.caller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class MainActivity extends AppCompatActivity {
    Switch connector;
    TextView status,list;
    Button file;
    ImageView call;
    ArrayList<String> phoneno=new ArrayList<String>();
    private static final String TAG = "ak47";
    Uri uri;
    int cno;
    private static final int READ_REQUEST_CODE = 42;
    TelephonyManager telephonyManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        demand();
        list.setMovementMethod(new ScrollingMovementMethod());
        lister();


    }
    private void demand()
    {
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            Log.e(TAG, "setxml: peremission prob");
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
        }else {
            Log.e(TAG, "onCreate: permission on");
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED)
        {
            Log.e(TAG, "setxml: peremission prob");
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CALL_PHONE},0);
        }else {
            Log.e(TAG, "onCreate: permission on to call");
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED)
        {
            Log.e(TAG, "setxml: peremission prob");
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE},0);
        }else {
            Log.e(TAG, "onCreate: permission on");
        }

    }
    private void init()
    {
        connector=findViewById(R.id.switch1);
        status=findViewById(R.id.editText2);
        list=findViewById(R.id.textView);
        file=findViewById(R.id.button2);
        call=findViewById(R.id.imageView);
        telephonyManager =
                (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        cno=-1;
    }
    private void lister()
    {
        PhoneStateListener callstatelistner=new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                super.onCallStateChanged(state, phoneNumber);
                Log.e(TAG,"state="+state);
                if(state==TelephonyManager.CALL_STATE_IDLE&&cno!=-1&&cno<phoneno.size())
                {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+phoneno.get(cno)));//change the number
                    startActivity(callIntent);

                }
                if(state==TelephonyManager.CALL_STATE_OFFHOOK)
                {
                    cno++;
                }
            }
        };
        telephonyManager.listen(callstatelistner,PhoneStateListener.LISTEN_CALL_STATE);
        Log.e(TAG,"listner");
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: " );
                managecall();
            }
        });
        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, READ_REQUEST_CODE);
                
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.
        Log.i(TAG, "hello");

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + ((Uri) uri).toString());
                Log.e(TAG, "onActivityResult: "+new File(uri.getPath()) );
                setxml();

            }

        }
    }
    private void setxml()
    {
        try {
            Log.e(TAG,"in try");
            InputStream in=getContentResolver().openInputStream(uri);
            Log.e(TAG, "setxml:"+String.valueOf(in) );
            Log.e(TAG,"input stream done");
            Log.e(TAG,String.valueOf(new File(uri.getPath())));
            Workbook wb=Workbook.getWorkbook(in);
            Log.e(TAG,"workbook");
            Sheet sheet=wb.getSheet(0);
            int r=sheet.getRows();
            Log.e(TAG,String.valueOf(r));
            int c=sheet.getColumns();
            Log.e(TAG,String.valueOf(c));
            String s="";
            for(int i=0;i<r;i++)
            {
                for (int j=0;j<c;j++)
                {
                    Cell num=sheet.getCell(j,i);
                    s+=num.getContents()+"\n";
                    phoneno.add(num.getContents().trim());

                }
            }
            Log.e(TAG, "setxml: "+phoneno.get(1));
            list.setText(s);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:

        }
    }
    private void managecall()
    {
        int size=phoneno.size();

        Log.e(TAG, "managecall: "+size );
        if(size==0)
        {
            Toast.makeText(MainActivity.this,"NO CONTACTS",Toast.LENGTH_LONG).show();
        }
        else
        {
            cno=0;
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+phoneno.get(cno)));//change the number
            startActivity(callIntent);
        }

    }


}
