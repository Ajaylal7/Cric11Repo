//$Id$
package com.cric11.trade;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.opensymphony.xwork2.ActionSupport;

public class StartCollecting extends ActionSupport implements ServletRequestAware,ServletResponseAware {
	
	HttpServletRequest request;
	
	HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String execute() {
		try {
			Date date = new Date();
			Integer hour = date.getHours();
			Integer minute = date.getMinutes();

			date = new Date(date.getTime()-(5 * 60000));
			
			hour = date.getHours();
			minute = date.getMinutes();
			
			
			JobDetail jobDetail = JobBuilder.newJob(MainClass.class).withIdentity("mainScheduler", "trade").build();
			
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("mainTrigger", "tradetrigger")
					.withSchedule(CronScheduleBuilder.cronSchedule("30 30 09 * * ? *")).build();
			
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			scheduler.scheduleJob(jobDetail, trigger);
			
			JobDetail jobDetail2 = JobBuilder.newJob(MainConstant.class).withIdentity("mainScheduler2", "trade2").build();
			
			Trigger trigger2 = TriggerBuilder.newTrigger().withIdentity("mainTrigger2", "tradetrigger2")
					.withSchedule(CronScheduleBuilder.cronSchedule("0 30 15 * * ? *")).build();
			
			Scheduler scheduler2 = new StdSchedulerFactory().getScheduler();
			scheduler2.start();
			scheduler.scheduleJob(jobDetail2, trigger2);
			
		}
		catch (Exception ex) {
			System.out.print(ex);
		}
		
		return null;
	}

}
