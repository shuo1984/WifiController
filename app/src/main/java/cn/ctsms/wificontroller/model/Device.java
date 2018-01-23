package cn.ctsms.wificontroller.model;

/**
 * Created by Shuo on 2018/1/23.
 */

public enum Device {

    Vehical(0,"vehicle"),
    Fan(1,"fan");

    private int code;
    private String desc;

    Device(int code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public String toString(){
        return this.desc;
    }

}
