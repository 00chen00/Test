package com.wan3456.sdk.bean;

public class PaymentInfo {
	
	private int roleLevel;//角色等级
	private String gameRole;//角色等级
	private int serverId;//区服id		
	private String extraInfo;//支付拓展信息
	private int amount;//支付金额 ：元
	private String serverName;//区服名称
	private String itemName;//道具名称
	private int count;//道具数量
	private int ratio;//兑换比例，如（1元=10金币）（非定额时填写）
	@Override
	public String toString() {
		return "PaymentInfo [roleLevel=" + roleLevel + ", gameRole=" + gameRole
				+ ", serverId=" + serverId + ", extraInfo=" + extraInfo
				+ ", amount=" + amount + ", serverName=" + serverName
				+ ", itemName=" + itemName + ", count=" + count + ", ratio="
				+ ratio + "]";
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getRatio() {
		return ratio;
	}

	public void setRatio(int ratio) {
		this.ratio = ratio;
	}


	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
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

}
