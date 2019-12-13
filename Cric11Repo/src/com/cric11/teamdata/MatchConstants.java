//$Id$
package com.cric11.teamdata;

public class MatchConstants {
	
	
	
	
	public static enum MatchStatus{
		
		FIXTURES(1,"Fixtures"),
		COMPLETED(2,"Completed"),
		LIVE(3,"Live");
		
		public Integer getStatusId() {
			return statusId;
		}

		public void setStatusId(Integer statusId) {
			this.statusId = statusId;
		}

		public String getMatchStatus() {
			return matchStatus;
		}

		public void setMatchStatus(String matchStatus) {
			this.matchStatus = matchStatus;
		}

		private Integer statusId;
		
		private String matchStatus;
		
		private MatchStatus(Integer statusId, String matchStatus) {
			this.statusId = statusId;
			this.matchStatus = matchStatus;
		}
		
		
		
		
		
		
	}

		

}
