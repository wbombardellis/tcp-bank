package bank.ui.text.command;

import bank.business.AccountManagementService;
import bank.business.domain.CurrentAccount;
import bank.business.domain.Deposit;
import bank.ui.text.BankTextInterface;
import bank.ui.text.UIUtils;

public class ManageSubmitedDepositsCommand extends Command
{
	private AccountManagementService accountManagementService;
	
	public ManageSubmitedDepositsCommand(BankTextInterface bankInterface, AccountManagementService accountManagementService)
	{
		super(bankInterface);
		this.accountManagementService = accountManagementService;
	}

	public void execute() throws Exception
	{
		UIUtils uiUtils = UIUtils.INSTANCE;
		
		Deposit toManage = accountManagementService.getFirstSubmitedDeposit();
		
		System.out.println(getTextManager().getText("message.deposit.information"));
		System.out.println(uiUtils.propertyToString("envelope", toManage.getEnvelope()));
		System.out.println(uiUtils.propertyToString("account.number", toManage.getAccount().getId().getNumber()));
		System.out.println(uiUtils.propertyToString("amount", toManage.getAmount()));
		System.out.println(uiUtils.propertyToString("locationNumber", toManage.getLocation().getNumber()));
		
		System.out.print(getTextManager().getText("message.validate.deposit"));
		boolean validate = uiUtils.readConfirmation("S", "N");
		
		accountManagementService.removeFromSubmitedList(toManage);
		CurrentAccount currentAccount = accountManagementService.getCurrentAccount(toManage.getAccount().getId());
		currentAccount.removeFromSubmitedDeposits(toManage);
		
		if (validate == true)
		{
			currentAccount.depositAmount(toManage);
		}
		else
		{
			currentAccount.addToRejectedDeposits(toManage);
		}
	}
	
}
