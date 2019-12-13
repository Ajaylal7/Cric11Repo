//$Id$
package com.cric11.utility;

public class Cric11Constants {

	public static final String SUCCESS = "success";
	public static final String STATUS_CODE = "status_code";
	public static final String STATUS_STRING = "status_string";
	public static final String TIME_TAKEN = "time_taken";
	public static enum ErrorMessages{
		
		API_ALL_SUCCESS(10000,"all.success.response"),	
		API_ALL_FAILURE(30000,"all.failure.response"); 

		private int errorCode;
		private String errorString;
		public int getErrorCode() {
			return errorCode;
		}
		public void setErrorCode(int errorCode) {
			this.errorCode = errorCode;
		}
		public String getErrorString() {
			return errorString;
		}
		public void setErrorString(String errorString) {
			this.errorString = errorString;
		}
		private ErrorMessages(int errorCode, String errorString) {
			this.errorCode = errorCode;
			this.errorString = errorString;
		}
	}

}
