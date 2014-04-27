package bank.ui.text.command;

import bank.business.AccountOperationService;
import bank.business.domain.CellPhoneRecharge;
import bank.business.domain.OperationLocation;
import bank.ui.text.BankTextInterface;
import bank.ui.text.UIUtils;

public class CellPhoneRechargeCommand extends Command implements FavoritableAction{

	private AccountOperationService accountOperationService;
	
	private String phoneCarrier;
	private String phoneNumber;
	private double amount;
	
	public CellPhoneRechargeCommand(BankTextInterface bankInterface, AccountOperationService accountOperationService) {
		super(bankInterface);
		
		this.accountOperationService = accountOperationService;
	}
	
	public CellPhoneRechargeCommand(BankTextInterface bankInterface, AccountOperationService accountOperationService,
			String phoneCarrier, String phoneNumber, double amount){
		this(bankInterface, accountOperationService);
		
		this.phoneCarrier = phoneCarrier;
		this.phoneNumber = phoneNumber;
		this.amount = amount;
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
		
		//Save arguments
		this.phoneCarrier = phoneCarrier;
		this.phoneNumber = phoneNumber;
		this.amount = amount;
		
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

	@Override
	public void executePreset() throws Exception {
		UIUtils uiUtils = UIUtils.INSTANCE;
		
		Long branch = bankInterface.readBranchId();
		Long accountNumber = bankInterface.readCurrentAccountNumber();

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

	@Override
	public String getAuxiliarInfoText() {
		return phoneCarrier + ", " + phoneNumber + ", " + amount;
	}

	@Override
	public void setBankInteferface(BankTextInterface bankInterface) {
		this.bankInterface = bankInterface;
	}

	@Override
	public void setAccountOperationService(
			AccountOperationService accountOperationService) {
		this.accountOperationService = accountOperationService;
	}
	
	@Override
	public CellPhoneRechargeCommand clone(){
		return new CellPhoneRechargeCommand(bankInterface, accountOperationService, phoneCarrier, phoneNumber, amount);
	}

}
