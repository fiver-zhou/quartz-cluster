package com.mg;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Used to test/show the clustering features of JDBCJobStore (JobStoreTX or
 * JobStoreCMT).
 * <p>
 * All instances MUST use a different properties file, because their instance
 * Ids must be different, however all other properties should be the same.
 * </p>
 * <p>
 * If you want it to clear out existing jobs & triggers, pass a command-line
 * argument called "clearJobs".
 * </p>
 * <p>
 * You should probably start with a "fresh" set of tables (assuming you may have
 * some data lingering in it from other tests), since mixing data from a
 * non-clustered setup with a clustered one can be bad.
 * </p>
 * <p>
 * Try killing one of the cluster instances while they are running, and see that
 * the remaining instance(s) recover the in-progress jobs. Note that detection
 * of the failure may take up to 15 or so seconds with the default settings.
 * </p>
 * <p>
 * Also try running it with/without the shutdown-hook plugin registered with the
 * scheduler. (org.quartz.plugins.management.ShutdownHookPlugin).
 * </p>
 * <p>
 * <i>Note:</i> Never run clustering on separate machines, unless their clocks
 * are synchronized using some form of time-sync service (such as an NTP
 * daemon).
 * </p>
 * 
 * @see SimpleRecoveryJob
 * @see SimpleRecoveryStatefulJob
 * @author James House
 */
public class ClusterExample {

	public void run(boolean inClearJobs, boolean inScheduleJobs) throws Exception {

		// First we must get a reference to a scheduler
		SchedulerFactory sf = new StdSchedulerFactory();
	    Scheduler sched = sf.getScheduler();
	    
		if (inClearJobs) {
			sched.clear();
		}

		if (inScheduleJobs) {

			System.out.println("------- Scheduling Jobs ------------------");

			String schedId = sched.getSchedulerInstanceId();
			int count = 1;

			JobDetail job = newJob(SimpleRecoveryJob.class).withIdentity("job_" + count, schedId).requestRecovery()
					.build();

			CronTrigger trigger = newTrigger().withIdentity("triger_" + count, schedId)
					.withSchedule(cronSchedule("0/10 * * * * ?")).forJob("job_" + count, schedId).build();

			sched.scheduleJob(job, trigger);
		}
		sched.start();
	}

	public static void main(String[] args) throws Exception {
		boolean clearJobs = false;
		boolean scheduleJobs = true;

		ClusterExample example = new ClusterExample();
		example.run(clearJobs, scheduleJobs);
	}
}