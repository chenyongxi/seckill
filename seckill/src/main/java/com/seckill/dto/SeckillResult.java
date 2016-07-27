package com.seckill.dto;

/**
 * 封装json结果,返回的类型都是这个类型
 * 
 * @author lenovo
 *
 * @param <T>
 */
public class SeckillResult<T> {

	public SeckillResult(boolean success, T data) {
		this.success = success;
		this.data = data;
	}

	public SeckillResult(boolean success, String error) {
		super();
		this.success = success;
		this.error = error;
	}

	public SeckillResult(boolean success, T data, String error) {
		this.success = success;
		this.data = data;
		this.error = error;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	// 返回结果是否成功
	private boolean success;
	// 返回的数据
	private T data;
	// 错误信息
	private String error;

}
