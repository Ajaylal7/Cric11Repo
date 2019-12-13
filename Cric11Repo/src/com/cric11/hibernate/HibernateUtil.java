//$Id$
package com.cric11.hibernate;

import java.io.IOException;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;

import com.cric11.utility.CachedData;

public class HibernateUtil implements Filter {
	
	public static ThreadLocal<Session> session = new ThreadLocal<>();
	public static ThreadLocal<Transaction> transaction = new ThreadLocal<>();
	
	
	private static ThreadLocal<Integer> num = new ThreadLocal<>();
	
	public static Transaction getTransaction() {
		return transaction.get();
	}

	public static void setTransaction(ThreadLocal<Transaction> transaction) {
		HibernateUtil.transaction = transaction;
	}

	public static Session getSession() {
		return session.get();
	}

	public static void setSession(ThreadLocal<Session> session) {
		HibernateUtil.session = session;
	}

	public static List<?> getRowsByCriteria(Class<?> tableName, String columnName, String value) {
		List<?> dataObj = null;
		try {
		Session session = HibernateUtil.getSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<?> query = builder.createQuery(tableName);
        Root root = query.from(tableName);
        query.select(root).where(builder.equal(root.get(columnName), value)).orderBy(builder.asc(root.get("id")));
        Query<?> queryCriteria = session.createQuery(query);
        
        dataObj = queryCriteria.list();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
        
        return dataObj;
	}
	
	public static List<?> getRowsByCriteriaAndContains(Class<?> tableName, String columnName, String value,String containColumnName,String containValue) {
		List<?> dataObj = null;
		try {
			Session session = CachedData.sessionFactory.openSession();
			CriteriaBuilder builder = session.getCriteriaBuilder();
	        CriteriaQuery<?> query = builder.createQuery(tableName);
	        Root root = query.from(tableName);
	        query.select(root).where(builder.equal(root.get(columnName), value),builder.like(root.get(containColumnName), containValue)).orderBy(builder.asc(root.get("id")));
	        Query<?> queryCriteria = session.createQuery(query);
	        dataObj = queryCriteria.list();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
        
        return dataObj;
	}
	
//	public static Boolean updateRow(Class<?> tableName, String valueColumn, String value, String criteriaColumn, String criteriaValue) {
//		
//	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException, ServletException {
 		session.set(CachedData.sessionFactory.openSession());
 		setNum(5);
		
		if(session.get()!=null) {
			session.get().beginTransaction();
		}
		Transaction transact = session.get().getTransaction();
		transaction.set(transact);
		arg2.doFilter(arg0, arg1);
		
		
		if(session.get() != null) {
			session.get().close();
			session.set(null);
		}
	}
	
	

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		System.out.println("haii");
	}

	public static Integer getNum() {
		return num.get();
	}

	public static void setNum(Integer number) {
		num.set(number);
	}

	
}
