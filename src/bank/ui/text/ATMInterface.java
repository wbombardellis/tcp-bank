package bank.ui.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bank.business.AccountOperationService;
import bank.business.BusinessException;
import bank.business.domain.ATM;
import bank.business.domain.Credentials;
import bank.business.domain.CurrentAccount;
import bank.ui.TextManager;
import bank.ui.UIAction;
import bank.ui.text.command.BalanceCommand;
import bank.ui.text.command.CellPhoneRechargeCommand;
import bank.ui.text.command.ClientLoginCommand;
import bank.ui.text.command.Command;
import bank.ui.text.command.DeleteFavoriteAction;
import bank.ui.text.command.DepositCommand;
import bank.ui.text.command.FavoritableAction;
import bank.ui.text.command.LogoutCommand;
import bank.ui.text.command.SaveAsFavoriteAction;
import bank.ui.text.command.StatementCommand;
import bank.ui.text.command.TransferCommand;
import bank.ui.text.command.WithdrawalCommand;

/**
 * @author Ingrid Nunes
 * 
 */
public class ATMInterface extends BankTextInterface {

	private final AccountOperationService accountService;
	
	protected final List<String> favoritableActions;
	protected final Map<String,FavoritableAction> favoriteActions;
	
	public ATMInterface(ATM atm, AccountOperationService accountOperationService) {
		super(atm);
		this.accountService = accountOperationService;
		this.favoritableActions = new ArrayList<>(8);
		this.favoriteActions = new HashMap<>();
		
		this.addAction("L", new ClientLoginCommand(this,accountOperationService), false);
		this.addAction("B", new BalanceCommand(this, accountOperationService), true);
		this.addAction("S", new StatementCommand(this, accountOperationService), true);
		this.addAction("D", new DepositCommand(this, accountOperationService), true);
		this.addAction("W",
				new WithdrawalCommand(this, accountOperationService), true);
		this.addAction("T", new TransferCommand(this, accountOperationService), true);
		this.addAction("R", new CellPhoneRechargeCommand(this, accountOperationService), true);
		this.addAction("O", new LogoutCommand(this), false);
		
	}

	protected void addFavoriteAction(String code, FavoritableAction action) {
		this.favoriteActions.put(code, action);
	}
	
	protected void removeFavoriteAction(String code) {
		this.favoriteActions.remove(code);
	}
	
	
	protected void addAction(String code, UIAction action, Boolean favoritable) {
		this.actions.put(code, action);
		if (favoritable)
			this.favoritableActions.add(code);
	}

	@Override
	public void login(Credentials credentials) {
		super.login(credentials);
		
		if (isLoggedIn()){
			//Add Favorite Actions
			Integer i = 1;
			try{
				List<FavoritableAction> favorites = this.accountService.getFavoriteActions(this.readBranchId(), this.readCurrentAccountNumber());
				for (FavoritableAction action : favorites) {
					action.setEnabled(true);
					action.setAccountOperationService(this.accountService);
					action.setBankInteferface(this);
					
					this.addFavoriteAction(i.toString(), action);
					i++;
				}
			}catch(Exception e){
				UIUtils.INSTANCE.handleUnexceptedError(e);
			}
		}
	}
	
	@Override
	public void logout() {
		super.logout();
		this.favoriteActions.clear();
	}
	
	
	@Override
	public Long readBranchId() {
		return isLoggedIn() ? ((CurrentAccount) getCredentials()).getId()
				.getBranch().getNumber() : 0;
	}

	@Override
	public Long readCurrentAccountNumber() {
		return isLoggedIn() ? ((CurrentAccount) getCredentials()).getId()
				.getNumber() : 0;
	}
	
	@Override
	public void createAndShowUI() {
		UIUtils uiUtils = UIUtils.INSTANCE;
		String commandKey = null;
		do {
			System.out.println();
			System.out.print(getMenu(uiUtils.getTextManager()));
			commandKey = uiUtils.readString(null);
			Command command = (Command) actions.get(commandKey);

			try {
				//If it is a common action
				if (command != null) {
					command.execute();
					
					if(this.favoritableActions.indexOf(commandKey) >= 0){
						if (command instanceof FavoritableAction){
							//Menu that prints options after the command (e.g. save as favorite operation)
							System.out.println( getMenuAfterCommandExecution(uiUtils.getTextManager()) );
							
							if (UIUtils.INSTANCE.readConfirmation(uiUtils.getTextManager().getText("option.Yes"),
																	uiUtils.getTextManager().getText("option.No"))){
								FavoritableAction com = (FavoritableAction)((FavoritableAction)command).clone();

								com.setEnabled(true);
								new SaveAsFavoriteAction(this, accountService, com).execute();
								addFavoriteAction(new Integer(favoriteActions.size()+1).toString(), com);
							}
						}
					}
				}else{
					//Maybe it is a favorite action 
					FavoritableAction favoriteCommand = (FavoritableAction) favoriteActions.get(commandKey);
					
					if (favoriteCommand != null){ //It is
						//Ask whether it will change the arguments or not
						System.out.println( uiUtils.getTextManager().getText("message.confirm.alterCommand") );
						
						if (UIUtils.INSTANCE.readConfirmation(uiUtils.getTextManager().getText("option.Yes"),
																uiUtils.getTextManager().getText("option.No"))){
							//If arguments will be altered, then execute everything again
							FavoritableAction com = (FavoritableAction)(favoriteCommand.clone());
							com.execute();
							//Ask whether to save alterations or not
							System.out.println( uiUtils.getTextManager().getText("message.confirm.saveCommandAlteration") );
							
							if (UIUtils.INSTANCE.readConfirmation(uiUtils.getTextManager().getText("option.Yes"),
																	uiUtils.getTextManager().getText("option.No"))){
								//If so, remove the previous and add the new one
								new DeleteFavoriteAction(this, accountService, favoriteCommand).execute();
								removeFavoriteAction(commandKey);
								
								com.setEnabled(true);
								new SaveAsFavoriteAction(this, accountService, com).execute();
								addFavoriteAction(commandKey, com);
							}
						}
						else{
							//Argument won't be altered, so, run the ready command
							favoriteCommand.executePreset();
						}
					}
						
				}
					
			} catch (BusinessException be) {
				System.out.println(uiUtils.getTextManager().getText(
						be.getMessage(), be.getArgs()));
				log.warn(be);
			} catch (Exception e) {
				uiUtils.handleUnexceptedError(e);
			}
		} while (!EXIT_CODE.equals(commandKey));
		if (isLoggedIn()) {
			logout();
		}
	}
	
	@Override
	protected String getMenu(TextManager textManager) {
		StringBuffer sb = new StringBuffer();
		sb.append(textManager.getText("message.options", EXIT_CODE, false))
				.append(":\n");
		for (String key : actions.keySet()) {
			UIAction action = actions.get(key);
			if (action.isEnabled()) {
				sb.append(key)
						.append(" - ")
						.append(textManager.getText(action.getClass()
								.getSimpleName())).append("\n");
			}
		}
		
		for (String key : favoriteActions.keySet()) {
			FavoritableAction action = favoriteActions.get(key);
			if (action.isEnabled()) {
				sb.append(key)
						.append(" - ")
						.append(textManager.getText(action.getClass()
								.getSimpleName()))
						.append(" - ")
						.append(action.getAuxiliarInfoText())
						.append("\n");
			}
		}

		sb.append(textManager.getText("message.choose.option")).append(": ");

		return sb.toString();
	}
	
	protected String getMenuAfterCommandExecution(TextManager textManager){
		StringBuffer sb = new StringBuffer();
		sb.append(textManager.getText("message.confirm.favoriteCommand", EXIT_CODE, false));
		
		return sb.toString();
	}

}
