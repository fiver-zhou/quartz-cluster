package com.mg;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class SimpleRecoveryJob implements Job, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4307636221163601072L;

	public SimpleRecoveryJob() {
		
	}
	
	public void execute(JobExecutionContext context) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		
		System.out.println(sdf.format(new Date()) + "：" + context.getJobDetail().getKey());
	}
}