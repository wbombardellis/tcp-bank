package bank.ui.text.command;

import bank.business.AccountOperationService;
import bank.ui.UIAction;
import bank.ui.text.BankTextInterface;

public interface FavoritableAction extends UIAction {
	public void executePreset() throws Exception;
	
	public String getAuxiliarInfoText();
	
	public Object clone();
	
	public void setBankInteferface(BankTextInterface bankInterface);
	
	public void setAccountOperationService(AccountOperationService accountOperationService);
}
