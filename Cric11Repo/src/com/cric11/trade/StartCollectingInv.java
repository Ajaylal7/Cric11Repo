//$Id$
package com.cric11.trade;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.cric11.hibernate.HibernateUtil;
import com.cric11.utility.Cric11Util;
import com.opensymphony.xwork2.ActionSupport;

public class StartCollectingInv extends ActionSupport implements ServletRequestAware,ServletResponseAware {
	
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
			System.out.print(InvConstant.getPrint());
			JobDetail jobDetail = JobBuilder.newJob(InvClass.class).withIdentity("mainSchedulerInv", "tradeInv").build();
			
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("mainTriggerInv", "tradetriggerInv")
					.withSchedule(CronScheduleBuilder.cronSchedule("06 20 09 * * ? *")).build();
			
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			scheduler.scheduleJob(jobDetail, trigger);
			
			JobDetail jobDetail2 = JobBuilder.newJob(InvConstant.class).withIdentity("mainSchedulerInv2", "tradeInv2").build();
			
			Trigger trigger2 = TriggerBuilder.newTrigger().withIdentity("mainTriggerInv2", "tradetriggerInv2")
					.withSchedule(CronScheduleBuilder.cronSchedule("0 0 16 * * ? *")).build();
			
			Scheduler scheduler2 = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			scheduler2.scheduleJob(jobDetail2, trigger2);
			
			
		}
		catch (Exception ex) {
			System.out.print(ex);
		}
		
		return null;
	}
	
	public String startAnalysis() {
		String invUrl = InvConstant.analysisUrl;
		try {
			//for(int j = 0; j< InvConstant.nifty50StocksId.length; j++)
			{
				String current = null, previous = null;
				Boolean checkContinuity = true;
				String url = EcoConstant.riseurl;
				String invRespObj = Cric11Util.getResponsefromUrl(url, "get", null, null, null);
			}
		}
		catch (Exception ex) {
			System.out.println(ex);
		}
		return null;
	}

}
