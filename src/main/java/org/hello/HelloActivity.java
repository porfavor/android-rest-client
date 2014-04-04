package org.hello;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.joda.time.LocalTime;

import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;

public class HelloActivity extends Activity {
	public static String LOG_TAG = HelloActivity.class.getName();
	private TextView tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello_layout);
        
        
        
        tv = (TextView)this.findViewById(R.id.text_view);
    }

    @Override
    public void onStart() {
    	Log.d(LOG_TAG, "onStart");
        super.onStart();
        LocalTime currentTime = new LocalTime();
        TextView textView = (TextView) findViewById(R.id.text_view);
        textView.setText("The current local time is: " + currentTime);
    }
    
    @Override
    public void onResume(){
    	Log.d(LOG_TAG, "onResume");
        super.onResume();

    	//loadServer();
        loadServerHttpClient();
    }

    private void loadServerRestTpl(){
    	String url = "http://192.168.1.5:8080/mobile_rest_server/1/employee/1.action";

    	RestTemplate restTemplate = new RestTemplate();
//		restTemplate.getMessageConverters().add(
//                new StringHttpMessageConverter());
		restTemplate.getMessageConverters().add(new
				MappingJackson2HttpMessageConverter());

        Map<String, String> urlVariables = new HashMap<String, String>();
        urlVariables.put("Content-type", "application/json");

    	String resp = restTemplate.getForObject(url, String.class, urlVariables);

    	Log.d("LOG", resp.toString());
    	tv.setText(resp);

////		Map<String, String> urlVariables = new HashMap<String, String>();
////		urlVariables.put("Content-type", "application/xml");
////		try {
////			mTreeNodes = restTemplate.getForObject(url,
////					PDAGovernmentCollieryTreeNodes.class, urlVariables);
////			Log.i(LOG_TAG, "Destination info: " + mTreeNodes.toString());
////		} catch (Exception e) {
////			e.printStackTrace();
////			if (AnjianDownloadActivity.this.isFinishing())
////				return;
////			Message msg = mHandler.obtainMessage(MSG_NETWORK_ERROR);
////			msg.sendToTarget();
////			return;
////		} finally {
////			handle.finished();
////		}
    }

    private void loadServerHttpClient(){
//        HttpClient httpClient = new DefaultHttpClient();
//
//        GetMethod getMethod = new GetMethod("http://192.168.1.195:8080/mobile_rest_server/1/employee/1.action");
//        //getMethod.addHeader("content-type", "application/json");
//        Header header = new Header("content-type", "application/json");
//        getMethod.addRequestHeader(header);
//        
//        HttpMethodParams param = getMethod.getParams();
//		param.setContentCharset("UTF-8");
//        
//        //使用系统提供的默认的恢复策略
//		param.setParameter(HttpMethodParams.RETRY_HANDLER,
//                new DefaultHttpMethodRetryHandler());
//        try {
//            //执行getMethod
//            int statusCode = httpClient.executeMethod(getMethod);
//            if (statusCode != HttpStatus.SC_OK) {
//                System.err.println("Method failed: "
//                        + getMethod.getStatusLine());
//            }
//            //读取内容
//            byte[] responseBody = getMethod.getResponseBody();
//            //处理内容
//            //System.out.println(new String(responseBody));
//            tv.setText(new String(responseBody));
//        } catch (HttpException e) {
//            //发生致命的异常，可能是协议不对或者返回的内容有问题
//            System.out.println("Please check your provided http address!");
//            e.printStackTrace();
//        } catch (IOException e) {
//            //发生网络异常
//            e.printStackTrace();
//        } finally {
//            //释放连接
//            getMethod.releaseConnection();
//        }
    	
    	HttpClient client = new DefaultHttpClient();
    	HttpGet get = new HttpGet("http://192.168.1.195:8080/mobile_rest_server/1/employee/1.action");
    	get.setHeader("Accept", "application/json");
    	//get.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
    	//get.setHeader("Accept-Encoding", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");

		HttpResponse response;
		StringBuffer result = new StringBuffer();
		try {
			response = client.execute(get);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				throw new HttpException(String.valueOf(statusCode));
			}

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		tv.setText(result.toString());
		
		RespEmployee employee = JSON.parseObject(result.toString(), RespEmployee.class);
		Log.d(LOG_TAG, employee.toString());
    }
    
    public void upload(final String picName) throws Exception {
		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
/*
		String uploadUrl = url.replace("{serverip}", server_ip).replace(
				"{serverport}", server_port);
		uploadUrl = "http://www.liangkehu.com:28080/shop/test/upload.json";
*/
		HttpPost httppost = new HttpPost("");

		File file = new File(picName);

		// FileEntity reqEntity = new FileEntity(file, "binary/octet-stream");
		// reqEntity.setContentEncoding("utf-8");
		// httppost.setEntity(reqEntity);

		// httppost.setHeader("Content-Type", "multipart/form-data");

		org.apache.http.entity.mime.MultipartEntity multipartEntity = new MultipartEntity();
		ContentBody fileBody = new FileBody(file);
		multipartEntity.addPart("hello", fileBody);

		httppost.setEntity(multipartEntity);

		System.out.println("executing request " + httppost.getRequestLine());
		HttpResponse response = null;
		response = httpclient.execute(httppost);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity resEntity = response.getEntity();
			System.out.println(response.getStatusLine());
			if (resEntity != null) {
				System.out.println(EntityUtils.toString(resEntity));
				resEntity.consumeContent();
			}
		}
		httpclient.getConnectionManager().shutdown(); // TODO

	}
}