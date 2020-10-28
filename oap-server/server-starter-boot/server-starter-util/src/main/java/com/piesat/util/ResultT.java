package com.piesat.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author zzj
 * @program: backup
 * @描述 返回信息公共类
 * @创建时间 2019/4/8 14:25
 **/
public class ResultT<T> implements Serializable {
    private int code = ReturnCodeEnum.SUCCESS.getKey();
    private String msg;
    private T data;
    @JsonIgnore
    private int eiCode;
    @JsonIgnore
    private StringBuilder processMsg = new StringBuilder();
    @JsonIgnore
    private String kObject;
    @JsonIgnore
    private String kEvent;
    @JsonIgnore
    private String eventTrag;
    @JsonIgnore
    private String eventSuggest;
    public ResultT() {
    }

    public ResultT(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ResultT<T> success(String message, T data) {
        ResultT<T> result = new ResultT<>(ReturnCodeEnum.SUCCESS.getKey(), message, data);
        return result;
    }

    public static <T> ResultT<T> success(T data) {
        ResultT<T> result = new ResultT<>(ReturnCodeEnum.SUCCESS.getKey(), ReturnCodeEnum.SUCCESS.getValue(), data);
        return result;
    }

    public static <T> ResultT<T> success() {
        ResultT<T> result = new ResultT(ReturnCodeEnum.SUCCESS.getKey(), "", "");
        return result;
    }

    public static <T> ResultT<T> success(String message) {
        ResultT<T> result = new ResultT(ReturnCodeEnum.SUCCESS.getKey(), message, "");
        return result;
    }

    public static <T> ResultT<T> failed() {
        ResultT<T> result = new ResultT(ReturnCodeEnum.FIAL.getKey(), ReturnCodeEnum.FIAL.getValue(), "");
        return result;
    }

    public static <T> ResultT<T> failed(String message) {
        ResultT<T> result = new ResultT(ReturnCodeEnum.FIAL.getKey(), message, "");
        return result;
    }

    public static String logInfo(String msg, Object... params) {
        if (msg == null) throw new NullPointerException("msg");
        StringBuffer sb = new StringBuffer();
        final String delimiter = "{}";//定界符
        int cnt = 0;//括号出现的计数值
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                int tmpIndex = msg.indexOf(delimiter);
                if (tmpIndex == -1) {//不存在赋值
                    if (cnt == 0) {
                        sb.append(msg);
                    }
                    break;
                } else {//存在则进行赋值拼接
                    String str = msg.substring(0, tmpIndex);
                    msg = msg.substring((tmpIndex + 2), msg.length());
                    String valStr = params[i].toString();
                    sb.append(str)
                            .append(valStr);
                    cnt++;

                }
            }
            if(cnt==1){
                sb.append(msg);
            }
        } else {//param为空时
            sb.append(msg);
        }
        return sb.toString();
    }

    public boolean isSuccess() {
        return ReturnCodeEnum.SUCCESS.getKey() == code;
    }

    public void setErrorMessage(ReturnCodeEnum code) {
        this.code = code.getKey();
        this.msg = code.getValue();
        this.processMsg.append(code.getValue() + "\n");
    }

    public void setErrorMessage(String message) {
        this.code = ReturnCodeEnum.FIAL.getKey();
        this.msg = message;
        this.processMsg.append(message + "\n");
    }

    public void setErrorMessage(String format, Object... args) {
        this.code = ReturnCodeEnum.FIAL.getKey();
        String message = logInfo(format, args);
        this.msg = message;
        this.processMsg.append(message + "\n");
    }

    public void clearProcessMsg() {
        this.processMsg.setLength(0);
    }

    public void setSuccessMessage(String format, Object... args) {
        String message = logInfo(format, args);
        this.processMsg.append(message + "\n");
    }
    public void setSuccessMessage(String message) {
        this.processMsg.append(message + "\n");
    }

    public void setMessage(ReturnCodeEnum code, String message) {
        this.code = code.getKey();
        this.msg = message;
    }

    public void setMessage(String message) {
        this.msg = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ReturnT [code=" + code + ", msg=" + msg + ", result=" + data + "]";
    }

    public int getEiCode() {
        return eiCode;
    }

    public void setEiCode(int eiCode) {
        this.eiCode = eiCode;
    }

    public StringBuilder getProcessMsg() {
        return processMsg;
    }

    public void setProcessMsg(StringBuilder processMsg) {
        this.processMsg = processMsg;
    }

    public String getkObject() {
        return kObject;
    }

    public void setkObject(String kObject) {
        this.kObject = kObject;
    }

    public String getkEvent() {
        return kEvent;
    }

    public void setkEvent(String kEvent) {
        this.kEvent = kEvent;
    }

    public String getEventTrag() {
        return eventTrag;
    }

    public void setEventTrag(String eventTrag) {
        this.eventTrag = eventTrag;
    }

    public String getEventSuggest() {
        return eventSuggest;
    }

    public void setEventSuggest(String eventSuggest) {
        this.eventSuggest = eventSuggest;
    }
}
