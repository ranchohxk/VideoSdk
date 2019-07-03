package com.hyq.hm.videosdk.bean;

/**
 * @author hxk <br/>
 * 功能：
 * 创建日期   2019/6/26
 * 修改者：
 * 修改日期：
 * 修改内容:
 */
public class JsonRootBean {

    private int code;
    private Data data;
    public void setCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

    public void setData(Data data) {
        this.data = data;
    }
    public Data getData() {
        return data;
    }

}