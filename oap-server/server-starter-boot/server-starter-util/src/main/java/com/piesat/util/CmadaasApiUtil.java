package com.piesat.util;

import com.alibaba.fastjson.JSONObject;


import java.util.List;

public class CmadaasApiUtil {
    private static final String Key = "sodCF5881E4AA6D4235D16";
    private static final String cmadaasUrl = ConfigUtil.getProperty("urls", "cmadaas");

    /**
     * 获取所有用户信息
     * @return
     */
    public static List<JSONObject> getAllUserInfo(){
        String userInfo = HttpUtil.doGet(cmadaasUrl+"api/rest/user/getAllUserInfo?key="+Key);
        List<JSONObject> list = JSONObject.parseArray(userInfo, JSONObject.class);
        return list;
    }

    /**
     * 根据用户id获取用户信息
     * @param userId
     * @return
     */
    public static JSONObject getUserInfo(String userId){
        String userInfo = HttpUtil.doGet(cmadaasUrl+"api/rest/user/getUserInfo/"+userId+"?key="+Key);
        JSONObject object = JSONObject.parseObject(userInfo);
        return object;
    }
    /**
     * 获取部门ztree
     * @return
     */
    public static List<JSONObject> getDeptTree(){
        String userInfo = HttpUtil.doGet(cmadaasUrl+"api/rest/dept/getDeptTree?key="+Key);
        List<JSONObject> list = JSONObject.parseArray(userInfo, JSONObject.class);
        return list;
    }

    /**
     * 根据部门获取用户信息
     * @return
     */
    public static List<JSONObject> getUsersInfoByDept(String deptCode){
        String userInfo = HttpUtil.doGet(cmadaasUrl+"api/rest/user/getUsersInfoByDept?deptCode="+deptCode+"&key="+Key);
        List<JSONObject> list = JSONObject.parseArray(userInfo, JSONObject.class);
        return list;
    }


    /**
     * 获取部门用户ztree
     * @return
     */
    public static List<JSONObject> getDeptUser(){
        String userInfo = HttpUtil.doGet(cmadaasUrl+"api/rest/dept/getDeptUser?key="+Key);
        List<JSONObject> list = JSONObject.parseArray(userInfo, JSONObject.class);
        return list;
    }

    /**
     * 根据用户获取部门信息
     * @return
     */
    public static List<JSONObject> getDeptByUserId(String userId){
        String userInfo = HttpUtil.doGet(cmadaasUrl+"api/rest/dept/getDeptByUserId/"+userId+"?key="+Key);
        List<JSONObject> list = JSONObject.parseArray(userInfo, JSONObject.class);
        return list;
    }

    /**
     * 根据用户登录名获取用户信息
     * @param loginName
     * @return
     */
    public static JSONObject getUserByLoginName(String loginName){
        String userInfo = HttpUtil.doGet(cmadaasUrl+"api/rest/user/getUserByLoginName/"+loginName+"?key="+Key);
        JSONObject object = JSONObject.parseObject(userInfo);
        return object;
    }

    /**
     * 获取{用户id:用户}map
     * @return
     */
    public static JSONObject getUserMap(){
        String userInfo = HttpUtil.doGet(cmadaasUrl+"api/rest/user/getUserMap?key="+Key);
        JSONObject object = JSONObject.parseObject(userInfo);
        return object;
    }

    /**
     * 获取所有用户信息(包括部门名称)
     * @return
     */
    public static List<JSONObject> getUserDeptInfo(){
        String userInfo = HttpUtil.doGet(cmadaasUrl+"api/rest/user/getUserDeptInfo?key="+Key);
        List<JSONObject> list = JSONObject.parseArray(userInfo, JSONObject.class);
        return list;
    }

    /**
     * 获取所有部门信息
     * @return
     */
    public static List<JSONObject> getAllDeptInfo(){
        String userInfo = HttpUtil.doGet(cmadaasUrl+"api/rest/dept/getAllDeptInfo?key="+Key);
        List<JSONObject> list = JSONObject.parseArray(userInfo, JSONObject.class);
        return list;
    }

    public static void updateFlowById(String flowId,boolean isPass){
        try {
            String flag = isPass ? "2" : "-3";
            HttpUtil.doGet(cmadaasUrl+"api/rest/busFlow/updateFlowById?flowId="+flowId+"&nextLinkId="+flag);
        }catch (Exception e){
            System.err.print(e.getMessage());
        }
    }
    public static void updateFlowByIds(String flowId,String isPass){
        try {
            HttpUtil.doGet(cmadaasUrl+"api/rest/busFlow/updateFlowById?flowId="+flowId+"&nextLinkId="+isPass);
        }catch (Exception e){
            System.err.print(e.getMessage());
        }
    }
}
