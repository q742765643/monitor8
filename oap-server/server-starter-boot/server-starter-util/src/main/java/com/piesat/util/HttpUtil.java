package com.piesat.util;

//import com.sun.istack.internal.logging.Logger;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;






/** 后台请求发送工具
*@description
*@author weiliguo
*@date
*
*/
public class HttpUtil {
	
//	private static Logger logger = Logger.getLogger(HttpUtil.class);
	
	/**
	 *  发送get请求
	 * @description 
	 * @author weiliguo
	 * @date 2018-11-15 09:10
	 * @param url
	 * @return
	 */
	public static String doGet(String url) {
		
		String resultStr = null;
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		try {
			client = HttpClients.createDefault();
			
//			logger.info("开始发送get请求 , 请求地址 : "+url);
			HttpGet request = new HttpGet(url);
			response = client.execute(request);
			//请求发送成功
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//				logger.info("get请求发送成功 , 请求地址 :"+url+" ");
				resultStr = EntityUtils.toString(response.getEntity());
			}else {
//				logger.config("get请求发送失败 :"+response.getStatusLine().getStatusCode()+",请求地址 :"+url);
			}
			
		} catch (Exception e) {
//			logger.config("get请求异常 ,请求地址 :"+url +"异常信息 :"+e.getMessage());
		} finally {
			try {
				if(client != null) {
					client.close();
				}
				if(response !=null ) {
					response.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			
			
		}
		return resultStr;
	}
	

}
