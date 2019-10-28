package com.example.intents;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    enum InputType {
        PHONE, URL, COORDS;
    }

    InputType inputType;
    RadioGroup radioGroup;
    EditText editText;
    boolean auto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radioGroup = findViewById(R.id.radioGroup);
        editText = findViewById(R.id.editText);
    }

    public void onClickCheck(View view) {
        auto = ((CheckBox) view).isChecked();


        if (auto) {
            findViewById(R.id.radioPhone).setEnabled(false);
            findViewById(R.id.radioUrl).setEnabled(false);
            findViewById(R.id.radioCoords).setEnabled(false);
        } else {
            findViewById(R.id.radioPhone).setEnabled(true);
            findViewById(R.id.radioUrl).setEnabled(true);
            findViewById(R.id.radioCoords).setEnabled(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickButton(View view) {
        String text = editText.getText().toString();
        if (auto) {
            if (text.matches("(.*)[a-zA-Zа-яА-я](.*)")) inputType = InputType.URL;
            else if (text.contains(",")) inputType = InputType.COORDS;
            else inputType = InputType.PHONE;
        } else {
            switch (radioGroup.getCheckedRadioButtonId()) {
                case R.id.radioPhone:
                    inputType = InputType.PHONE;
                    break;
                case R.id.radioUrl:
                    inputType = InputType.URL;
                    break;
                case R.id.radioCoords:
                    inputType = InputType.COORDS;
                    break;
                default:
                    return;
            }
        }

        Intent intent = null;
        Uri uri;
        switch (inputType) {
            case URL:
                if (!text.startsWith("http"))
                    text = "https://" + text;
                uri = Uri.parse(text);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                break;
            case COORDS:
                uri = Uri.parse("geo:" + text);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                break;
            case PHONE:
                uri = Uri.parse("tel:" + text);
                intent = new Intent(Intent.ACTION_CALL, uri); // или ACTION_VIEW для перехода к вводу номера телефора
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    String msg = "У приложения нет доступа к управлению звонками";
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    return;
                }
        }

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            String msg = "Не получается обработать намерение";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }
}
