package com.example.contentprovidertest;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.contentprovidertest.providers.MessageProvider;
import com.example.contentprovidertest.sqlite.DBHelper;

public class MainActivity extends AppCompatActivity {
    EditText receiver, messageText, appName;
    Button insert, delete, view, update;
    DBHelper DB;
    public static int dbMode=1;
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
                }else{
                    ContentValues values=new ContentValues();
                    values.put(MessageProvider.receiver, receiverTXT);
                    values.put(MessageProvider.message, messageTXT);
                    values.put(MessageProvider.appName, appNameTXT);
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
                }else{
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setCancelable(true);
                    builder.setTitle("message list");
                    builder.setMessage(concatString);
                    builder.show();
                }
            }
        });
    }
}