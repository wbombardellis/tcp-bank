/*
 * Created on 6 Jan 2014 14:30:10 
 */
package bank.ui.text.command;

import bank.business.AccountOperationService;
import bank.ui.text.BankTextInterface;

/**
 * @author ingrid
 *
 */
public class BalanceCommand  extends Command implements FavoritableAction {

	private AccountOperationService accountOperationService;

	public BalanceCommand(BankTextInterface bankInterface,
			AccountOperationService accountOperationService) {
		super(bankInterface);
		this.accountOperationService = accountOperationService;
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
		
		Double balance = accountOperationService.getBalance(branch, accountNumber);
		
		System.out
				.println(getTextManager().getText("balance") + ": "
				+ balance);
	}

	@Override
	public void executePreset() throws Exception {
		execute();
	}

	@Override
	public String getAuxiliarInfoText() {
		return "";
	}
	
	@Override
	public BalanceCommand clone(){
		return new BalanceCommand(bankInterface, accountOperationService);
	}

}