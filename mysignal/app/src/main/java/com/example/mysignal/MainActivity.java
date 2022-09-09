package com.example.mysignal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    static final Uri CONTENT_URL=Uri.parse("content://com.example.contentprovidertest.providers/messages");
    EditText receiver, messageText, appName;
    Button insert, delete, view, update;

    TextView messageListLabel;
    ContentResolver resolver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resolver=getContentResolver();

        receiver=findViewById(R.id.receiver);
        messageText=findViewById(R.id.message);
        appName=findViewById(R.id.app_name);

        insert=findViewById(R.id.btn_insert);
        view=findViewById(R.id.btn_view_messages);
        update=findViewById(R.id.btn_update_status);
        delete=findViewById(R.id.btn_delete);
        //grantUriPermission();
        getMessages();
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMessage();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMessages();
            }
        });
    }

    public void getMessages(){
        String[] projection=new String[]{"receiver", "message", "appName"};
        String messageList = "";
        //try {
            Cursor cursor = resolver.query(CONTENT_URL, projection, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    String receiver = cursor.getString(0);
                    String message = cursor.getString(1);
                    String appName = cursor.getString(2);
                    messageList += receiver + ", " + message + ", " + appName + "\n";
                } while (cursor.moveToNext());

            } else {
                messageList = "No Messages";
            }
        /*}catch (Exception e){
            Toast.makeText(getBaseContext(), e.getStackTrace().toString(), Toast.LENGTH_SHORT).show();
        }*/
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setTitle("message list");
        builder.setMessage(messageList);
        builder.show();

    }
    public void addMessage(){
        String receiverName=receiver.getText().toString();
        String message=messageText.getText().toString();
        String appNameText=appName.getText().toString();

        ContentValues values=new ContentValues();
        values.put("receiver", receiverName);
        values.put("message", message);
        values.put("appName", appNameText);

        resolver.insert(CONTENT_URL, values);

        getMessages();
    }
}