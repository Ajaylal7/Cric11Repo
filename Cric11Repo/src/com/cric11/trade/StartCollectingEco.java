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

import com.cric11.utility.Cric11Util;
import com.opensymphony.xwork2.ActionSupport;

public class StartCollectingEco extends ActionSupport implements ServletRequestAware,ServletResponseAware {
	
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
			
			JobDetail jobDetail = JobBuilder.newJob(EcoClass.class).withIdentity("mainSchedulerEco", "tradeEco").build();
			
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("mainTriggerEco", "tradetriggerEco")
					.withSchedule(CronScheduleBuilder.cronSchedule("0 20 09 * * ? *")).build();
			
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			scheduler.scheduleJob(jobDetail, trigger);
			
			
		}
		catch (Exception ex) {
			System.out.print(ex);
		}
		
		return null;
	}
	
	public String startAnalysis() {
		String invUrl = InvConstant.analysisUrl;
		try {
			for(int j = 0; j< InvConstant.nifty50StocksId.length; j++) {
				String current = null, previous = null;
				Boolean checkContinuity = true;
				String url = invUrl.replace("{id}", InvConstant.stockId[j].toString());
				String invRespObj = Cric11Util.getResponsefromUrl(url, "get", InvConstant.header, null, null);
			}
		}
		catch (Exception ex) {
			System.out.println(ex);
		}
		return null;
	}

}
