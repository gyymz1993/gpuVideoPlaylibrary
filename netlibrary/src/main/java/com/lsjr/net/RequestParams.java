//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lsjr.net;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RequestParams {
    private final List<Part> params;
    private boolean urlEncoder;

    public RequestParams() {
        this.params = new ArrayList();
    }

    public void addFormDataPart(String key, String value) {
        if(value == null) {
            value = "";
        }

        Part part = new Part(key, value);
        if(!TextUtils.isEmpty(key) && !this.params.contains(part)) {
            this.params.add(part);
        }

    }

    public void addFormDataPart(String key, int value) {
        this.addFormDataPart(key, String.valueOf(value));
    }

    public void addFormDataPart(String key, long value) {
        this.addFormDataPart(key, String.valueOf(value));
    }

    public void addFormDataPart(String key, float value) {
        this.addFormDataPart(key, String.valueOf(value));
    }

    public void addFormDataPart(String key, double value) {
        this.addFormDataPart(key, String.valueOf(value));
    }

    public void addFormDataPart(String key, boolean value) {
        this.addFormDataPart(key, String.valueOf(value));
    }

    public void addFormDataParts(List<Part> params) {
        this.params.addAll(params);
    }




    public void urlEncoder() {
        this.urlEncoder = true;
    }

    public boolean isUrlEncoder() {
        return this.urlEncoder;
    }


    public void clear() {
        this.params.clear();
    }

    public List<Part> getFormParams() {
        return this.params;
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        Iterator var2 = this.params.iterator();

        Part part;
        String key;
        while(var2.hasNext()) {
            part = (Part)var2.next();
            key = part.getKey();
            String value = part.getValue();
            if(result.length() > 0) {
                result.append("&");
            }

            result.append(key);
            result.append("=");
            result.append(value);
        }


        return result.toString();
    }

    //
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//



    public final class Part {
        private String key;
        private String value;

        public Part(String key, String value) {
            this.setKey(key);
            this.setValue(value);
        }


        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }


        protected void setKey(String key) {
            if(key == null) {
                this.key = "";
            } else {
                this.key = key;
            }

        }

        protected void setValue(String value) {
            if(value == null) {
                this.value = "";
            } else {
                this.value = value;
            }

        }

        @Override
        public boolean equals(Object o) {
            if(o != null && o instanceof Part) {
                Part part = (Part)o;
                return part == null?false:TextUtils.equals(part.getKey(), this.getKey()) && TextUtils.equals(part.getValue(), this.getValue());
            } else {
                return false;
            }
        }
    }

}
