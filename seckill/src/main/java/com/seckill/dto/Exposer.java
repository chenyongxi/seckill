package com.seckill.dto;

/**
 * 暴露秒杀地址DTO
 * 
 * @author lenovo
 *
 */
public class Exposer {

	@Override
	public String toString() {
		return "Exposer [exposed=" + exposed + ", md5=" + md5 + ", seckillId="
				+ seckillId + ", now=" + now + ", start=" + start + ", end="
				+ end + "]";
	}


	private boolean exposed;

	// 一种加密措施
	private String md5;

	private String seckillId;

	// 系统当前时间（毫秒）
	private long now;

	private long start;
	private long end;



	public boolean isExposed() {
		return exposed;
	}

	public void setExposed(boolean exposed) {
		this.exposed = exposed;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getSeckillId() {
		return seckillId;
	}

	public void setSeckillId(String seckillId) {
		this.seckillId = seckillId;
	}

	public long getNow() {
		return now;
	}

	public void setNow(long now) {
		this.now = now;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public Exposer(boolean exposed, String md5, String seckillId, long now,
			long start, long end) {
		this.exposed = exposed;
		this.md5 = md5;
		this.seckillId = seckillId;
		this.now = now;
		this.start = start;
		this.end = end;
	}
	
	public Exposer(boolean exposed, String md5, String seckillId) {
		this.exposed = exposed;
		this.md5 = md5;
		this.seckillId = seckillId;
	}
	
	public Exposer(boolean exposed, String seckillId, long now,
			long start, long end) {
		this.exposed = exposed;
		this.seckillId = seckillId;
		this.now = now;
		this.start = start;
		this.end = end;
	}
	
	
	public Exposer(boolean exposed, String seckillId){
		this.exposed = exposed;
		this.seckillId = seckillId;
	}

}
