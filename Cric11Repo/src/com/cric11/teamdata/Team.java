//$Id$
package com.cric11.teamdata;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Team {

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getTeamDisplayName() {
		return teamDisplayName;
	}

	public void setTeamDisplayName(String teamDisplayName) {
		this.teamDisplayName = teamDisplayName;
	}

	public String getTeamImageUrl() {
		return teamImageUrl;
	}

	public void setTeamImageUrl(String teamImageUrl) {
		this.teamImageUrl = teamImageUrl;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long teamId;
	
	@Column(nullable=false,unique=true)
	private String teamName;
	
	@Column(nullable=false,unique=true)
	private String teamDisplayName;
	
	@Column(nullable=false,unique=true)
	private String teamImageUrl;
	
	
	
}
