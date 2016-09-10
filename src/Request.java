
public class Request {
	String invokingEvent;
	String ruleParameters;
	String resultToken;
	boolean eventLeftScope;
	String executionRoleArn;
	String configRuleArn;
	String configRuleName;
	String configRuleId;
	String accountId;
	String version;
	
	
	
	public Request(String invokingEvent, String ruleParameters,
			String resultToken, boolean eventLeftScope, String executionRoleArn,
			String configRuleArn,String configRuleName, String configRuleId, String accountId,
			String version) {
		super();
		this.invokingEvent = invokingEvent;
		this.ruleParameters = ruleParameters;
		this.resultToken = resultToken;
		this.eventLeftScope = eventLeftScope;
		this.executionRoleArn = executionRoleArn;
		this.configRuleArn = configRuleArn;
		this.configRuleName = configRuleName;
		this.configRuleId = configRuleId;
		this.accountId = accountId;
		this.version = version;
	}
	
	public Request(){
		
	}
	
	public String getInvokingEvent() {
		return invokingEvent;
	}
	public String getConfigRuleArn() {
		return configRuleArn;
	}

	public void setConfigRuleArn(String configRuleArn) {
		this.configRuleArn = configRuleArn;
	}

	public void setInvokingEvent(String invokingEvent) {
		this.invokingEvent = invokingEvent;
	}
	public String getRuleParameters() {
		return ruleParameters;
	}
	public void setRuleParameters(String ruleParameters) {
		this.ruleParameters = ruleParameters;
	}
	public String getResultToken() {
		return resultToken;
	}
	public void setResultToken(String resultToken) {
		this.resultToken = resultToken;
	}
	public boolean getEventLeftScope() {
		return eventLeftScope;
	}
	public void setEventLeftScope(boolean eventLeftScope) {
		this.eventLeftScope = eventLeftScope;
	}
	public String getExecutionRoleArn() {
		return executionRoleArn;
	}
	public void setExecutionRoleArn(String executionRoleArn) {
		this.executionRoleArn = executionRoleArn;
	}
	public String getConfigRuleName() {
		return configRuleName;
	}
	public void setConfigRuleName(String configRuleName) {
		this.configRuleName = configRuleName;
	}
	public String getConfigRuleId() {
		return configRuleId;
	}
	public void setConfigRuleId(String configRuleId) {
		this.configRuleId = configRuleId;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	
}
