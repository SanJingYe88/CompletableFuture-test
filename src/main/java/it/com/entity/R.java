package it.com.entity;

import lombok.Data;

@Data
public class R {
    private String msg;
    private int code;
    private Object data;

    public static R ok(Object data){
        R r = new R();
        r.setCode(200);
        r.setData(data);
        return r;
    }

    public static R ok(){
        R r = new R();
        r.setCode(200);
        r.setMsg("success");
        return r;
    }

    public static R ok(String msg){
        R r = new R();
        r.setCode(200);
        r.setMsg(msg);
        return r;
    }

    public static R error(int code, String errorMsg){
        R r = new R();
        r.setMsg(errorMsg);
        r.setCode(code);
        return r;
    }
}
