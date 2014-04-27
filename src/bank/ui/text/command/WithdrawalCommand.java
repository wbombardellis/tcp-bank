package bank.ui.text.command;

import bank.business.AccountOperationService;
import bank.business.domain.Withdrawal;
import bank.ui.text.BankTextInterface;
import bank.ui.text.UIUtils;

/**
 * @author Ingrid Nunes
 * 
 */
public class WithdrawalCommand extends Command implements FavoritableAction{

	private AccountOperationService accountOperationService;
	
	private Double amount;

	public WithdrawalCommand(BankTextInterface bankInterface,
			AccountOperationService accountOperationService) {
		super(bankInterface);
		this.accountOperationService = accountOperationService;
	}
	
	public WithdrawalCommand(BankTextInterface bankInterface,
			AccountOperationService accountOperationService,
			Double amount) {
		this(bankInterface, accountOperationService);
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
		Long branch = bankInterface.readBranchId();
		Long accountNumber = bankInterface.readCurrentAccountNumber();
		Double amount = UIUtils.INSTANCE.readDouble("amount");

		Withdrawal withdrawal = accountOperationService.withdrawal(
				bankInterface.getOperationLocation().getNumber(), branch,
				accountNumber, amount);
		
		//Save arguments
		this.amount = amount;

		System.out.println(getTextManager().getText(
				"message.operation.succesfull"));
		System.out.println(getTextManager().getText("withdrawal") + ": "
				+ withdrawal.getAmount());
	}

	@Override
	public void executePreset() throws Exception {
		Long branch = bankInterface.readBranchId();
		Long accountNumber = bankInterface.readCurrentAccountNumber();

		Withdrawal withdrawal = accountOperationService.withdrawal(
				bankInterface.getOperationLocation().getNumber(), branch,
				accountNumber, amount);

		System.out.println(getTextManager().getText(
				"message.operation.succesfull"));
		System.out.println(getTextManager().getText("withdrawal") + ": "
				+ withdrawal.getAmount());
	}

	@Override
	public String getAuxiliarInfoText() {
		return amount.toString();
	}
	
	@Override
	public WithdrawalCommand clone(){
		return new WithdrawalCommand(bankInterface, accountOperationService, amount);
	}

}
