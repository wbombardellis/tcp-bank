package bank.business.domain;

import java.util.Date;

public class CellPhoneRecharge extends Transaction {
	
	private String phoneCarrier;
	private String phoneNumber;
	private Date dateTime;
	
	public CellPhoneRecharge(OperationLocation location, CurrentAccount account,
			String phoneCarrier, String phoneNumber, double amount) {
		super(location, account, amount);

		this.phoneCarrier = phoneCarrier;
		this.phoneNumber = phoneNumber;
		this.dateTime = new Date(System.currentTimeMillis());
	}

	public String getPhoneCarrier() {
		return phoneCarrier;
	}

	public void setPhoneCarrier(String phoneCarrier) {
		this.phoneCarrier = phoneCarrier;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
}
