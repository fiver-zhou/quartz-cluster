package com.mg;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class MyTest {

	public static void main(String[] args) throws SchedulerException {
		Scheduler sched = new StdSchedulerFactory().getScheduler();
		
		sched.start();

		String sId = sched.getSchedulerInstanceId();
		
		JobDetail job = newJob(SimpleRecoveryJob.class)
						.withIdentity("myJob", sId)
						.build();
		
		CronTrigger trigger = newTrigger()
					    .withIdentity("triger_1", sId)
					    .withSchedule(cronSchedule("0/10 * * * * ?"))
					    .forJob("myJob", sId)
					    .build();

		sched.scheduleJob(job, trigger);
	}
}