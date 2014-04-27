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
public class SaveAsFavoriteAction  extends Command {

	private final AccountOperationService accountOperationService;
	private FavoritableAction command;

	public SaveAsFavoriteAction(BankTextInterface bankInterface,
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
		
		accountOperationService.addFavoriteAction(branch, accountNumber, command);
		
		System.out
				.println(getTextManager().getText("message.commandAddedFavorite"));
	}

}