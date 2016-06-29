package edu.rolc.ollie;

import android.app.Activity;
import android.content.Intent;

public class DocHelper {
    public static final int READ_REQUEST_CODE = 42;

    public static void performFileSearch(Activity curActivity) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        curActivity.startActivityForResult(intent, READ_REQUEST_CODE);
    }
}
