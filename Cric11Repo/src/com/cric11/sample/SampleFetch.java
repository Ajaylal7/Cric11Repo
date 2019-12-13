//$Id$
package com.cric11.sample;

import java.util.Iterator;
import java.util.List;

import com.cric11.hibernate.HibernateUtil;

public class SampleFetch {
	
	public static String fetchData(String id) {
		List<Sample> sampleData = (List<Sample>) HibernateUtil.getRowsByCriteria(Sample.class, "id", id);
		Iterator<?> itr = sampleData.iterator();
		while (itr.hasNext()) {
			Sample sample  = (Sample) itr.next();
			return sample.getTime();
		}
		return null;
	}

}
