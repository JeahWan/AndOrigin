package com.jeahwan.origin.utils;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.jeahwan.origin.App;

import java.util.ArrayList;
import java.util.List;

public class Validate {
    private List<ValidateItem> validateList;

    private Validate() {
        validateList = new ArrayList<>();
    }

    public static Validate build() {
        return new Validate();
    }

    public Validate add(EditText editText, String emptyTips, ValidateItem.ValidateListener validateListener) {
        validateList.add(new ValidateItem(editText, emptyTips, validateListener));
        return this;
    }

    public void execValidate(ValidateResultListener validateResultListener) {
        for (ValidateItem validate : validateList) {
            //判空
            if (!TextUtils.isEmpty(validate.emptyTips) && TextUtils.isEmpty(validate.editText.getText().toString())) {
                Toast.makeText(App.Companion.getInstance(), validate.emptyTips, Toast.LENGTH_SHORT).show();
                validateResultListener.onFail(validate.editText);
                return;
            }
            if (!TextUtils.isEmpty(validate.errorTips) && !validate.isPass) {
                Toast.makeText(App.Companion.getInstance(), validate.errorTips, Toast.LENGTH_SHORT).show();
                validateResultListener.onFail(validate.editText);
                return;
            }
            validateResultListener.onPass();
        }
    }

    public interface ValidateResultListener {
        void onPass();

        void onFail(EditText editText);
    }

    public static class ValidateItem {

        public String emptyTips;
        public String errorTips;
        public ValidateListener validateListener;
        public EditText editText;
        public boolean isPass;

        public ValidateItem(EditText editText, String emptyTips, ValidateListener validateListener) {
            this.editText = editText;
            this.emptyTips = emptyTips;
            this.validateListener = validateListener;
            if (validateListener != null) {
                this.isPass = validateListener.validate(editText.getText().toString());
                this.errorTips = validateListener.errorTips;
            }
        }

        public abstract static class ValidateListener {
            public String errorTips;

            public ValidateListener(String errorTips) {
                this.errorTips = errorTips;
            }

            public abstract boolean validate(String inputContent);
        }
    }
}
