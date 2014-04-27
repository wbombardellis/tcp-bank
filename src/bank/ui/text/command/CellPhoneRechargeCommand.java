package bank.ui.text.command;

import bank.business.AccountOperationService;
import bank.business.domain.CellPhoneRecharge;
import bank.business.domain.OperationLocation;
import bank.ui.text.BankTextInterface;
import bank.ui.text.UIUtils;

public class CellPhoneRechargeCommand extends Command {

	private final AccountOperationService accountOperationService;
	
	public CellPhoneRechargeCommand(BankTextInterface bankInterface, AccountOperationService accountOperationService) {
		super(bankInterface);
		
		this.accountOperationService = accountOperationService;
	}
	
	@Override
	public void execute() throws Exception {
		UIUtils uiUtils = UIUtils.INSTANCE;
		
		Long branch = bankInterface.readBranchId();
		Long accountNumber = bankInterface.readCurrentAccountNumber();
		String phoneCarrier = uiUtils.readString("phone.carrier");
		String phoneNumber = uiUtils.readString("phone.number");
		Double amount = uiUtils.readDouble("amount");

		OperationLocation operationLocation = this.bankInterface.getOperationLocation();
		
		CellPhoneRecharge cellPhoneRecharge = this.accountOperationService.rechargeCellPhone(operationLocation.getNumber(),
				branch, accountNumber, phoneCarrier, phoneNumber, amount);
		
		double balance = this.accountOperationService.getBalance(branch, accountNumber);
		
		System.out.println(getTextManager().getText(
				"message.operation.succesfull"));
		System.out.println(getTextManager().getText("balance") + ": "
				+ balance);
		System.out.println(getTextManager().getText("cell.phone.recharge"));
		System.out.println(uiUtils.propertyToString("locationNumber", cellPhoneRecharge
				.getLocation().getNumber()));
		System.out.println(uiUtils.propertyToString("dateTime", uiUtils.formatDateTime(cellPhoneRecharge
				.getDateTime())));
		System.out.println(uiUtils.propertyToString("phone.carrier", cellPhoneRecharge
				.getPhoneCarrier()));
		System.out.println(uiUtils.propertyToString("phone.number", cellPhoneRecharge
				.getPhoneNumber()));
		System.out.println(uiUtils.propertyToString("amount", cellPhoneRecharge
				.getAmount()));
		
		
		
	}

}
