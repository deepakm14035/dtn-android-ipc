package com.example.contentprovidertest;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.contentprovidertest.providers.MessageProvider;
import com.example.contentprovidertest.sqlite.DBHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    EditText receiver, messageText, appName;
    Button insert, delete, view, update, sendToSocket;
    DBHelper DB;
    public static int dbMode=2;
    private static String serverIP = "127.0.0.1";
    private static int port = 4444;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        receiver=findViewById(R.id.receiver);
        messageText=findViewById(R.id.message);
        appName=findViewById(R.id.app_name);

        insert=findViewById(R.id.btn_insert);
        view=findViewById(R.id.btn_view_messages);
        update=findViewById(R.id.btn_update_status);
        delete=findViewById(R.id.btn_delete);
        sendToSocket=findViewById(R.id.btn_add_to_socket);
        DB=new DBHelper(this);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String receiverTXT = receiver.getText().toString();
                String messageTXT = messageText.getText().toString();
                String appNameTXT = appName.getText().toString();
                if(dbMode==0) {
                    Boolean checkInsertStatus = DB.insertMessage(receiverTXT, messageTXT, appNameTXT);
                    if (checkInsertStatus) {
                        Toast.makeText(MainActivity.this, "New message added", Toast.LENGTH_SHORT);
                    } else {
                        Toast.makeText(MainActivity.this, "error adding message", Toast.LENGTH_SHORT);
                    }
                }else if(dbMode==1){
                    ContentValues values=new ContentValues();
                    values.put(MessageProvider.receiver, receiverTXT);
                    values.put(MessageProvider.message, messageTXT);
                    values.put(MessageProvider.appName, appNameTXT);
                    Uri uri=getContentResolver().insert(MessageProvider.CONTENT_URI, values);
                }else{
                    ContentValues values=new ContentValues();
                    values.put("data", messageTXT.getBytes());
                    values.put(MessageProvider.appName, appNameTXT);
                    values.put("destination", "APP");
                    //values.put(MessageProvider.appName, getApplicationContext().getPackageName());
                    Uri uri=getContentResolver().insert(MessageProvider.CONTENT_URI, values);
                    Toast.makeText(getBaseContext(), "new message added", Toast.LENGTH_SHORT).show();
                }
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String receiverTXT=receiver.getText().toString();
                String messageTXT=messageText.getText().toString();
                String appNameTXT=appName.getText().toString();
                Boolean checkUpdateStatus=DB.updateMessageData(receiverTXT, messageTXT, appNameTXT);
                if(checkUpdateStatus){
                    Toast.makeText(MainActivity.this, "message updated", Toast.LENGTH_SHORT);
                }
                else{
                    Toast.makeText(MainActivity.this, "error updating message", Toast.LENGTH_SHORT);
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String receiverTXT=receiver.getText().toString();
                String messageTXT=messageText.getText().toString();
                String appNameTXT=appName.getText().toString();
                Boolean checkDeleteStatus=DB.deleteMessageData(receiverTXT, messageTXT, appNameTXT);
                if(checkDeleteStatus){
                    Toast.makeText(MainActivity.this, "message deleted", Toast.LENGTH_SHORT);
                }
                else{
                    Toast.makeText(MainActivity.this, "error deleting message", Toast.LENGTH_SHORT);
                }
            }
        });



        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dbMode==0) {
                    Cursor cursor = DB.getAllMessages();
                    if (cursor.getCount() == 0) {
                        Toast.makeText(MainActivity.this, "no messages", Toast.LENGTH_SHORT);
                        return;
                    }
                    StringBuffer sb = new StringBuffer();
                    while (cursor.moveToNext()) {
                        sb.append("receiver: " + cursor.getString(1) + "\nMessage: " + cursor.getString(2) + "\nappName: " +
                                cursor.getString(3) + "\nstatus: " + cursor.getString(4) + "\n\n");
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setCancelable(true);
                    builder.setTitle("message list");
                    builder.setMessage(sb.toString());
                    builder.show();
                }else if(dbMode==1){
                    String[] projection=new String[]{"receiver", "message", "appName"};
                    Uri CONTENT_URL=Uri.parse("content://com.example.contentprovidertest.providers/messages");
                    String concatString="";
                    Cursor cursor = getContentResolver().query(CONTENT_URL, projection, null, null, null);
                    if (cursor.moveToFirst()) {
                        do {
                            String receiver = cursor.getString(0);
                            String message = cursor.getString(1);
                            String appName = cursor.getString(2);
                            concatString += receiver + ", " + message + ", " + appName + "\n";
                        } while (cursor.moveToNext());

                    } else {
                        concatString = "No Messages";
                    }
                    showAlertMessage("all messages", concatString);
                }else{
                    String[] projection=new String[]{"message", "appName"};
                    String selection="appName";
                    String[] selectionArgs=new String[]{getApplicationContext().getPackageName()};
                    Uri CONTENT_URL=Uri.parse("content://com.example.contentprovidertest.providers/messages");
                    String concatString="";
                    Cursor cursor = getContentResolver().query(CONTENT_URL, projection, selection, selectionArgs, null);
                    if (cursor.moveToFirst()) {
                        do {
                            byte[] message = cursor.getBlob(0);
                            //String appName = cursor.getString(2);
                            concatString += new String(message) + "\n";
                        } while (cursor.moveToNext());

                    } else {
                        concatString = "No Messages";
                    }
                    showAlertMessage("all messages", concatString);

                }
            }
        });

        sendToSocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread1().execute("");
            }
        });
    }

    private void showAlertMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    class Thread1  extends AsyncTask<String, Void, String> {
        public String doInBackground(String... data) {
            Socket socket;
            try {
                socket = new Socket(serverIP, port);
                PrintWriter output = new PrintWriter(socket.getOutputStream());
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String messageTXT = messageText.getText().toString();
                //Toast.makeText(MainActivity.this, "sending message to socket", Toast.LENGTH_SHORT);
                Log.d("deepakSocket", "sending message to socket");
                output.write("<header>\r\n\r\n"+messageTXT);
                output.flush();
                //new Thread(new Thread2()).start();
                output.close();
                Log.d("deepakSocket", "success sending message to socket");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("deepakSocket", e.getMessage());
                //Toast.makeText(MainActivity.this, "error sending message: "+e.getMessage(), Toast.LENGTH_SHORT);
            }
            //Toast.makeText(MainActivity.this, "success sending message to socket", Toast.LENGTH_SHORT);
            return "Success";
        }
    }
}