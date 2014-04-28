package bank.business.domain;

import java.util.ArrayList;
import java.util.List;

import bank.business.BusinessException;
import bank.ui.text.command.FavoritableAction;

/**
 * @author Ingrid Nunes
 * 
 */
public class CurrentAccount implements Credentials {

	private double balance;
	private Client client;
	private List<Deposit> submitedDeposits;
	private List<Deposit> rejectedDeposits;
	private List<Deposit> verifiedDeposits;
	private CurrentAccountId id;
	private List<Transfer> transfers;
	private List<Withdrawal> withdrawals;
	private List<FavoritableAction> favoriteActions;
	private List<CellPhoneRecharge> cellPhoneRecharges;

	public CurrentAccount(Branch branch, long number, Client client)
	{
		this.id = new CurrentAccountId(branch, number);
		branch.addAccount(this);
		this.client = client;
		client.setAccount(this);
		this.submitedDeposits = new ArrayList<>();
		this.rejectedDeposits = new ArrayList<>();
		this.verifiedDeposits = new ArrayList<>();
		this.transfers = new ArrayList<>();
		this.withdrawals = new ArrayList<>();
		this.favoriteActions = new ArrayList<>();
		this.cellPhoneRecharges = new ArrayList<>();
	}

	public CurrentAccount(Branch branch, long number, Client client,
			double initialBalance)
	{
		this(branch, number, client);
		this.balance = initialBalance;
	}

	public Deposit deposit(OperationLocation location, long envelope,double amount) throws BusinessException
	{
		Deposit deposit = new Deposit(location, this, envelope, amount);
		
		// If the operation is from an ATM, submit it to verification
		if (location.getClass() == ATM.class)
		{
			this.submitedDeposits.add(deposit);
		}
		else // if it isn't, deposit instantly.
		{
			depositAmount(deposit);
		}		
		return deposit;
	}

	public void depositAmount(Deposit deposit) throws BusinessException
	{
		if (!isValidAmount(deposit.getAmount()))
		{
			throw new BusinessException("exception.invalid.amount");
		}
		else
		{
			this.verifiedDeposits.add(deposit);
			this.balance += deposit.getAmount();
		}
	}
	
	private void depositAmount(double amount) throws BusinessException
	{
		if (!isValidAmount(amount))
		{
			throw new BusinessException("exception.invalid.amount");
		}
		else
		{
			this.balance += amount;
		}
	}

	/**
	 * @return the balance
	 */
	public double getBalance() {
		return balance;
	}

	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * @return the deposits
	 */
	public List<Deposit> getDeposits() {
		return verifiedDeposits;
	}

	/**
	 * @return the id
	 */
	public CurrentAccountId getId() {
		return id;
	}

	public List<Transaction> getTransactions() {
		List<Transaction> transactions = new ArrayList<>(verifiedDeposits.size()
				+ withdrawals.size() + transfers.size());
		transactions.addAll(verifiedDeposits);
		transactions.addAll(withdrawals);
		transactions.addAll(transfers);
		transactions.addAll(cellPhoneRecharges);
		return transactions;
	}

	/**
	 * @return the transfers
	 */
	public List<Transfer> getTransfers() {
		return transfers;
	}

	/**
	 * @return the withdrawals
	 */
	public List<Withdrawal> getWithdrawals() {
		return withdrawals;
	}

	private boolean hasEnoughBalance(double amount) {
		return amount <= balance;
	}

	private boolean isValidAmount(double amount) {
		return amount > 0;
	}

	public Transfer transfer(OperationLocation location,
			CurrentAccount destinationAccount, double amount)
			throws BusinessException {
		withdrawalAmount(amount);
		destinationAccount.depositAmount(amount);

		Transfer transfer = new Transfer(location, this, destinationAccount,
				amount);
		this.transfers.add(transfer);
		destinationAccount.transfers.add(transfer);

		return transfer;
	}

	public Withdrawal withdrawal(OperationLocation location, double amount)
			throws BusinessException {
		withdrawalAmount(amount);

		Withdrawal withdrawal = new Withdrawal(location, this, amount);
		this.withdrawals.add(withdrawal);

		return withdrawal;
	}

	private void withdrawalAmount(double amount) throws BusinessException
	{
		if (!isValidAmount(amount)) {
			throw new BusinessException("exception.invalid.amount");
		}

		if (!hasEnoughBalance(amount)) {
			throw new BusinessException("exception.insufficient.balance");
		}

		this.balance -= amount;
	}
	
	public void addToSubmitedDeposits(Deposit deposit)
	{
		this.submitedDeposits.add(deposit);
	}
	
	public void removeFromSubmitedDeposits(Deposit deposit)
	{
		this.submitedDeposits.remove(deposit);
	}
	
	public void addToVerifiedDeposits(Deposit deposit)
	{
		this.verifiedDeposits.add(deposit);
	}
	
	public void addToRejectedDeposits(Deposit deposit)
	{
		this.rejectedDeposits.add(deposit);
	}

	public void addFavoriteAction(FavoritableAction action) {
		if (action != null){			
			if (this.favoriteActions.add(action) == false)
				throw new IllegalArgumentException("exception.invalid.commandArgument");
		}else{
			throw new IllegalArgumentException("exception.invalid.commandArgument");
		}
	}
	
	public void removeFavoriteAction(FavoritableAction action) {
		if (action != null){			
			if (this.favoriteActions.remove(action) == false)
				throw new IllegalArgumentException("exception.invalid.commandArgument");
		}else{
			throw new IllegalArgumentException("exception.invalid.commandArgument");
		}
	}

	public List<FavoritableAction> getFavoriteActions() {
		return this.favoriteActions;
	}
	
	public List<CellPhoneRecharge> getCellPhoneRecharges() {
		return cellPhoneRecharges;
	}
	
	public CellPhoneRecharge rechargeCellPhone(OperationLocation location,
			String phoneCarrier,String phoneNumber, double amount) throws BusinessException {
		withdrawalAmount(amount);
		
		CellPhoneRecharge cellPhoneRecharge = new CellPhoneRecharge(location, this, phoneCarrier, phoneNumber, amount);
		
		this.cellPhoneRecharges.add(cellPhoneRecharge);

		return cellPhoneRecharge;
	}

}
