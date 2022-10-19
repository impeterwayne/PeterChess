package com.peterwayne.peterchess.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.imageview.ShapeableImageView;
import com.peterwayne.peterchess.R;
import com.peterwayne.peterchess.gui.GameSetup;

public class SetupActivity extends AppCompatActivity {
    private AppCompatButton btn_play_computer, btn_exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        addControls();
        addEvents();
    }

    private void addEvents() {
        btn_play_computer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGameSetupDialog();
            }
        });
    }

    private void openGameSetupDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_game_setup);
        Window window = dialog.getWindow();
        if (window==null)
        {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttrs = window.getAttributes();
        windowAttrs.gravity = Gravity.CENTER;
        window.setAttributes(windowAttrs);
        ShapeableImageView img_white = dialog.findViewById(R.id.img_white);
        ShapeableImageView img_black = dialog.findViewById(R.id.img_black);
        img_white.setStrokeWidth(5);
        img_white.setStrokeColor(getResources().getColorStateList(R.color.state_list,null));
        img_black.setStrokeColor(getResources().getColorStateList(R.color.state_list,null));
        AppCompatButton btn_start = dialog.findViewById(R.id.btn_start);
        AppCompatButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
        img_white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_white.setStrokeWidth(5);
                img_black.setStrokeWidth(0);
            }
        });
        img_black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_black.setStrokeWidth(5);
                img_white.setStrokeWidth(0);
            }
        });
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SetupActivity.this, MainActivity.class);
                if(img_white.getStrokeWidth()==5)
                {
                    intent.putExtra("playerType", GameSetup.HUMAN_TEXT);
                }else {
                    intent.putExtra("playerType", GameSetup.COMPUTER_TEXT);
                }
                startActivity(intent);
                dialog.dismiss();
                finish();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void addControls() {
        btn_play_computer = findViewById(R.id.btn_play_computer);
        btn_exit = findViewById(R.id.btn_exit);
    }
}