package com.example.nala.ui.webview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.nala.R;

public class MyWebView extends WebView {
    CustomizedSelectActionModeCallback actionModeCallback;
    public Context context;
    public MyWebView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context=context;
    }
    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        actionModeCallback = new CustomizedSelectActionModeCallback();
        return startActionModeForChild(this,actionModeCallback);

    }

    public class CustomizedSelectActionModeCallback implements ActionMode.Callback{

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contextual_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("Dictionary");
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // TODO Auto-generated method stub
            switch (item.getItemId()) {
                case R.id.dictionary:
                    clearFocus();
                    Toast.makeText(getContext(), "This is my test click", Toast.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // TODO Auto-generated method stub


            clearFocus(); // this  is not clearing the text in my device having version 4.1.2
            actionModeCallback=null;

        }

    }

}
