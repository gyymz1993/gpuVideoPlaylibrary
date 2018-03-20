package com.lsjr.bean;


public class Result {
	/**
	 * 通用的Http Result Code http 请求返回的结果码 <br/>
	 * 0表示一般性错误</br> 1-100表示成功</br> 大于100000表示一些详细的错误</br>
	 */
	public final static int CODE_ERROE = 0;// 未知的错误 或者系统内部错误
	public final static int CODE_SUCCESS = 1;// 正确的Http请求返回状态码

	public static final String ONSUCCESS = "success";
	public static final String RESULT_MSG = "msg";
	public static final String DATA = "data";

	public int resultCode;
	public String resultMsg;



	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}


}
