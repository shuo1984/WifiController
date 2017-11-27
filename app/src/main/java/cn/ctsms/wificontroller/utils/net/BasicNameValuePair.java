package cn.ctsms.wificontroller.utils.net;

/**
 * Created by Shuo on 2017/10/19.
 */

public class BasicNameValuePair implements NameValuePair {
    private String name;
    private String value;

    public BasicNameValuePair(String name,String value){
        this.name = name;
        this.value = value;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setValue(String value){
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }
}
