/*
 * Created on 5 Jan 2014 00:51:19 
 */
package bank.business.impl;

import java.util.Date;
import java.util.List;

import bank.business.AccountManagementService;
import bank.business.BusinessException;
import bank.business.domain.Branch;
import bank.business.domain.Client;
import bank.business.domain.CurrentAccount;
import bank.business.domain.CurrentAccountId;
import bank.business.domain.Deposit;
import bank.business.domain.Employee;
import bank.business.domain.OperationLocation;
import bank.data.Database;
import bank.util.RandomString;

/**
 * @author Ingrid Nunes
 * 
 */
public class AccountManagementServiceImpl implements AccountManagementService
{
	private final Database database;
	private RandomString random;

	public AccountManagementServiceImpl(Database database)
	{
		this.database = database;
		this.random = new RandomString(8);
	}

	@Override
	public CurrentAccount createCurrentAccount(long branch, String name,
			String lastName, int cpf, Date birthday, double balance) throws BusinessException
	{
		OperationLocation operationLocation = database.getOperationLocation(branch);
		if (operationLocation == null || !(operationLocation instanceof Branch))
		{
			throw new BusinessException("exception.invalid.branch");
		}

		Client client = new Client(name, lastName, cpf, random.nextString(),
				birthday);
		CurrentAccount currentAccount = new CurrentAccount(
				(Branch) operationLocation,
				database.getNextCurrentAccountNumber(), client, balance);

		database.save(currentAccount);

		return currentAccount;
	}

	@Override
	public Employee login(String username, String password) throws BusinessException
	{
		Employee employee = database.getEmployee(username);

		if (employee == null)
		{
			throw new BusinessException("exception.inexistent.employee");
		}
		if (!employee.getPassword().equals(password))
		{
			throw new BusinessException("exception.invalid.password");
		}

		return employee;
	}
	
	public Deposit getFirstSubmitedDeposit()
	{
		try
		{
			Deposit first = database.getFirstSubmitedDeposit();
			return first;
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	public List<Deposit> getSubmitedDepositsList()
	{
		try
		{
			List<Deposit> submitedList = database.getSubmitedDeposits();
			return submitedList;
		} 
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	public void removeFromSubmitedList(Deposit deposit)
	{
		database.removeFromSubmitedDeposits(deposit);
	}
	
	public CurrentAccount getCurrentAccount(CurrentAccountId currentAccountId)
	{
		return database.getCurrentAccount(currentAccountId);
	}

}
