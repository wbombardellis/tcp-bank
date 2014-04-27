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
public class DeleteFavoriteAction  extends Command {

	private final AccountOperationService accountOperationService;
	private FavoritableAction command;

	public DeleteFavoriteAction(BankTextInterface bankInterface,
			AccountOperationService accountOperationService,
			FavoritableAction command) {
		super(bankInterface);
		this.accountOperationService = accountOperationService;
		
		this.command = (FavoritableAction)command;
	}

	@Override
	public void execute() throws Exception {
		Long branch = bankInterface.readBranchId();
		Long accountNumber = bankInterface.readCurrentAccountNumber();
		
		accountOperationService.removeFavoriteAction(branch, accountNumber, command);
	}

}