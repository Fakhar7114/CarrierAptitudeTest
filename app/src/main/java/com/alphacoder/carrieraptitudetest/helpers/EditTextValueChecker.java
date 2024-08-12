package com.alphacoder.carrieraptitudetest.helpers;

import android.widget.EditText;

public class EditTextValueChecker {

    public static boolean hasValue(EditText editText){
        try{
            if (editText.getText().toString().trim().isEmpty()){
                editText.setError("Empty field !!");
                return false;
            }
            else {
                return true;
            }

        }
        catch (Exception e){
            return false;
        }
    }

}
