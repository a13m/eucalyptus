package com.eucalyptus.reporting.user;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.mortbay.log.Log;

import com.eucalyptus.entities.EntityWrapper;

public class ReportingAccountDao
{
	private static Logger LOG = Logger.getLogger( ReportingUserDao.class );

	private static ReportingAccountDao instance = null;
	
	public static synchronized ReportingAccountDao getInstance()
	{
		if (instance == null) {
			instance = new ReportingAccountDao();
			instance.loadFromDb();
		}
		return instance;
	}
	
	private final Map<String,String> accounts = new ConcurrentHashMap<String,String>();
	
	private ReportingAccountDao()
	{
		
	}

	public void addUpdateAccount(String id, String name)
	{
		if (id==null || name==null) throw new IllegalArgumentException("args cant be null");

		if (accounts.containsKey(id) && accounts.get(id).equals(name)) {
			return;
		} else if (accounts.containsKey(id)) {
			updateInDb(id, name);
			accounts.put(id, name);
		} else {
			try {
				addToDb(id, name);
				accounts.put(id, name);
			} catch (RuntimeException e) {
				LOG.error(e);
			}
		}
		
	}

	public String getAccountName(String id)
	{
		return accounts.get(id);
	}
	

	
	private void loadFromDb()
	{
		LOG.debug("Load accounts from db");
		
		EntityWrapper<ReportingAccount> entityWrapper =
			EntityWrapper.get(ReportingAccount.class);

		try {
			@SuppressWarnings("rawtypes")
			List reportingAccounts = (List)
				entityWrapper.createQuery("from ReportingAccount")
				.list();

			for (Object obj: reportingAccounts) {
				ReportingAccount Account = (ReportingAccount) obj;
				accounts.put(Account.getId(), Account.getName());
				LOG.debug("load account from db, id:" + Account.getId() + " name:" + Account.getName());
			}
				
			entityWrapper.commit();
		} catch (Exception ex) {
			LOG.error(ex);
			entityWrapper.rollback();
			throw new RuntimeException(ex);
		}			
	}
	
	private void updateInDb(String id, String name)
	{
		LOG.debug("Update reporting account in db, id:" + id + " name:" + name);

		EntityWrapper<ReportingAccount> entityWrapper =
			EntityWrapper.get(ReportingAccount.class);

		try {
			ReportingAccount reportingAccount = (ReportingAccount)
			entityWrapper.createQuery("from ReportingAccount where id = ?")
				.setString(0, id)
				.uniqueResult();
			reportingAccount.setName(name);
			entityWrapper.commit();
		} catch (Exception ex) {
			LOG.error(ex);
			entityWrapper.rollback();
			throw new RuntimeException(ex);
		}			
	}
	
	private void addToDb(String id, String name)
	{
		LOG.debug("Add reporting account to db, id:" + id + " name:" + name);
		
		EntityWrapper<ReportingAccount> entityWrapper =
			EntityWrapper.get(ReportingAccount.class);

		try {
			entityWrapper.add(new ReportingAccount(id, name));
			entityWrapper.commit();
		} catch (Exception ex) {
			LOG.error(ex);
			entityWrapper.rollback();
			throw new RuntimeException(ex);
		}					
	}

}
