package bank.ui.text.command;

import bank.business.AccountOperationService;
import bank.business.domain.Transfer;
import bank.ui.text.BankTextInterface;
import bank.ui.text.UIUtils;

/**
 * @author Ingrid Nunes
 * 
 */
public class TransferCommand extends Command implements FavoritableAction{

	private AccountOperationService accountOperationService;
	
	private Long dstBranch;
	private Long dstAccountNumber;
	private Double amount;

	public TransferCommand(BankTextInterface bankInterface,
			AccountOperationService accountOperationService) {
		super(bankInterface);
		this.accountOperationService = accountOperationService;
	}
	
	public TransferCommand(BankTextInterface bankInterface,
			AccountOperationService accountOperationService,
			Long dstBranch, Long dstAccountNumber, Double amount) {
		
		this(bankInterface, accountOperationService);
		this.dstBranch = dstBranch;
		this.dstAccountNumber = dstAccountNumber;
		this.amount = amount;
	}
	
	public void setAccountOperationService(
			AccountOperationService accountOperationService) {
		this.accountOperationService = accountOperationService;
	}
	
	public void setBankInteferface(BankTextInterface bankInterface){
		this.bankInterface = bankInterface;
	}
	

	@Override
	public void execute() throws Exception {
		Long srcBranch = bankInterface.readBranchId();
		Long srcAccountNumber = bankInterface.readCurrentAccountNumber();

		Long dstBranch = UIUtils.INSTANCE.readLong("destination.branch");
		Long dstAccountNumber = UIUtils.INSTANCE
				.readLong("destination.account.number");

		Double amount = UIUtils.INSTANCE.readDouble("amount");

		Transfer transfer = accountOperationService.transfer(bankInterface
				.getOperationLocation().getNumber(), srcBranch,
				srcAccountNumber, dstBranch, dstAccountNumber, amount);
		
		//Save arguments to use posteriorly
		this.dstBranch = dstBranch;
		this.dstAccountNumber = dstAccountNumber;
		this.amount = amount;

		System.out.println(getTextManager().getText(
				"message.operation.succesfull"));
		System.out.println(getTextManager().getText("transfer") + ": "
				+ transfer.getAmount());
	}

	@Override
	public void executePreset() throws Exception {
		Long srcBranch = bankInterface.readBranchId();
		Long srcAccountNumber = bankInterface.readCurrentAccountNumber();
		
		Transfer transfer = accountOperationService.transfer(bankInterface
				.getOperationLocation().getNumber(), srcBranch,
				srcAccountNumber, dstBranch, dstAccountNumber, amount);
		
		System.out.println(getTextManager().getText(
				"message.operation.succesfull"));
		System.out.println(getTextManager().getText("transfer") + ": "
				+ transfer.getAmount());
	}

	@Override
	public String getAuxiliarInfoText() {
		return dstBranch.toString() + ", " + dstAccountNumber.toString() + ", " + amount.toString();
	}
	
	@Override
	public TransferCommand clone(){
		return new TransferCommand(bankInterface, accountOperationService, dstBranch, dstAccountNumber, amount);
	}

}