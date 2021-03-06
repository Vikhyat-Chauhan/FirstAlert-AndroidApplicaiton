package in.thenextmove.homehub;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import helpers.DBHelper;

public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    private ConstraintLayout add_button_LAYOUT;
    private TextView devicename_TEXTVIEW, status_TEXTVIEW, message_TEXTVIEW, add_button_TEXTVIEW;
    private EditText name_EDITTEXT,username_EDITTEXT,password_EDITTEXT,chipdid_EDITTEXT;
    BottomNavigationView navView;
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    boolean permission = false;
    int MY_PERMISSION;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    status_TEXTVIEW.setText(R.string.title_home);
                    ArrayList<String> usernames = dbHelper.getusernames();
                    for(int i = 0; i<usernames.size(); i++) {
                        Log.i("Debug", usernames.get(i));
                    }
                    return true;
                case R.id.navigation_dashboard:
                    status_TEXTVIEW.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    status_TEXTVIEW.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        status_TEXTVIEW = findViewById(R.id.status_Textview);
        status_TEXTVIEW.setBackgroundResource(R.drawable.rounded_background_blue);
        devicename_TEXTVIEW = findViewById(R.id.devicename_Textview);
        message_TEXTVIEW = findViewById(R.id.message_Textview);
        add_button_TEXTVIEW = findViewById(R.id.addbutton_TextView);
        chipdid_EDITTEXT = findViewById(R.id.chipid_editText);
        name_EDITTEXT = findViewById(R.id.name_editText);
        username_EDITTEXT = findViewById(R.id.username_editText);
        password_EDITTEXT = findViewById(R.id.password_editText);
        add_button_LAYOUT = findViewById(R.id.add_button_layout);

        dbHelper = new DBHelper(this, null, null, 1);

        add_button_TEXTVIEW.setText("Login");
        add_button_LAYOUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_button_LAYOUT.setPressed(true);
                getdata();
            }
        });

        getpermissions();
    }

    protected void getdata() {
        if(permission){
            Log.i("Debug", "Getting data");
            String chipid = (chipdid_EDITTEXT.getText().toString()).toUpperCase();
            String name = (name_EDITTEXT.getText().toString()).toUpperCase();
            String username = (username_EDITTEXT.getText().toString()).toUpperCase();
            String password = password_EDITTEXT.getText().toString();
            if (username.length() > 0 & password.length() > 0) {
                if (chipid.length() <= 0) {
                    if (password.contentEquals(dbHelper.getusernamepasswords(username))) {
                        Log.i("H.O.M.E", "Correct Password");
                        Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(this, DeviceActivity.class);
                        myIntent.putExtra("email", username);
                        startActivity(myIntent);
                    } else {
                        Log.i("H.O.M.E", "Incorrect Password");
                        Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
                        chipdid_EDITTEXT.setText("");
                        username_EDITTEXT.setText("");
                        password_EDITTEXT.setText("");
                        name_EDITTEXT.setText("");
                    }
                } else {
                    dbHelper.addCredentials(username, password, name, chipid);
                    Log.i("H.O.M.E", "Added To Database");
                    Intent myIntent = new Intent(this, DeviceActivity.class);
                    myIntent.putExtra("email", username);
                    startActivity(myIntent);
                }
            } else {
                if (username.length() <= 0) {
                    Toast.makeText(getApplicationContext(), "Enter Username", Toast.LENGTH_SHORT).show();
                }
                if (password.length() <= 0) {
                    Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
                }
                chipdid_EDITTEXT.setText("");
                username_EDITTEXT.setText("");
                password_EDITTEXT.setText("");
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Provide Permissions", Toast.LENGTH_SHORT).show();
            getpermissions();
        }
    }

    protected void getpermissions(){
        // Here, thisActivity is the current activity
        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) & (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED)){

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSION);Log.i("Debug",String.valueOf(MY_PERMISSION));
                        permission = true;

        } else {
            // Permission has already been granted
            permission = true;
        }
    }
}

