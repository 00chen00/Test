package com.wan3456.sdk.bean;

public class SerInfo {
	public int roleLevel;
	public String gameRole;
	public int serverId;
	public String uid;
	public String serverName;
	@Override
	public String toString() {
		return "SerInfo [roleLevel=" + roleLevel + ", gameRole=" + gameRole
				+ ", serverId=" + serverId + ", uid=" + uid + ", serverName="
				+ serverName + "]";
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public int getRoleLevel() {
		return roleLevel;
	}
	public void setRoleLevel(int roleLevel) {
		this.roleLevel = roleLevel;
	}
	public String getGameRole() {
		return gameRole;
	}
	public void setGameRole(String gameRole) {
		this.gameRole = gameRole;
	}
	public int getServerId() {
		return serverId;
	}
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	
	

}
