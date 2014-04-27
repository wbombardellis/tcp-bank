package bank.ui.text.command;

import bank.business.AccountOperationService;
import bank.business.domain.Deposit;
import bank.ui.text.BankTextInterface;
import bank.ui.text.UIUtils;

/**
 * @author Ingrid Nunes
 * 
 */
public class DepositCommand extends Command implements FavoritableAction {

	private AccountOperationService accountOperationService;
	private Long envelope;
	private Double amount;

	public DepositCommand(BankTextInterface bankInterface,
			AccountOperationService accountOperationService) {
		super(bankInterface);
		this.accountOperationService = accountOperationService;
	}
	
	public DepositCommand(BankTextInterface bankInterface,
			AccountOperationService accountOperationService,
			Long envelope, Double amount) {
		this(bankInterface, accountOperationService);
		this.envelope = envelope;
		this.amount = amount;
	}
	
	public DepositCommand(Long envelope, Double amount){
		super(null);
		this.accountOperationService = null;
		this.envelope = envelope;
		this.amount = amount;
	}

	public void setAccountOperationService(
			AccountOperationService accountOperationService) {
		this.accountOperationService = accountOperationService;
	}
	
	public void setBankInteferface(BankTextInterface bankInterface){
		this.bankInterface = bankInterface;
	}
	
	private void kernelExecute(Long branch, Long accountNumber, Long envelope, Double amount) throws Exception{
		Deposit deposit = accountOperationService.deposit(bankInterface
				.getOperationLocation().getNumber(), branch, accountNumber,
				envelope, amount);

		System.out.println(getTextManager().getText(
				"message.operation.succesfull"));
		System.out.println(getTextManager().getText("deposit") + ": "
				+ deposit.getAmount());
	}

	@Override
	public void execute() throws Exception {
		Long branch = bankInterface.readBranchId();
		Long accountNumber = bankInterface.readCurrentAccountNumber();
		Long envelope = UIUtils.INSTANCE.readLong("envelope");
		Double amount = UIUtils.INSTANCE.readDouble("amount");
		
		kernelExecute(branch, accountNumber, envelope, amount);
		
		//Save data to use again as favorite action
		this.envelope = envelope;
		this.amount = amount;
	}
	
	@Override
	public void executePreset() throws Exception{
		Long branch = bankInterface.readBranchId();
		Long accountNumber = bankInterface.readCurrentAccountNumber();
		
		kernelExecute(branch, accountNumber, this.envelope, this.amount);
	}

	@Override
	public String getAuxiliarInfoText() {
		return envelope.toString() + ", " + amount.toString();
	}
	
	@Override
	public DepositCommand clone(){
		return new DepositCommand(bankInterface, accountOperationService, envelope, amount);
	}

}