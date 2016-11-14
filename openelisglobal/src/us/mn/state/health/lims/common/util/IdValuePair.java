package us.mn.state.health.lims.common.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * Nothing special, just for when id's and values should be encapsulated. 
 * 
 * N.B. This is very light weight, if you want to stick it in a hash and want to use something
 * other than identity of equals then over-ride equals and hash.
 */
public class IdValuePair implements Serializable{

	private static final long serialVersionUID = 1L;
	private String id;
	private String value;
	
	public IdValuePair( String id, String value){
		this.setId(id);
		this.setValue(value);
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static void sortByValue( List<IdValuePair> list){
		if( !list.isEmpty()) {
			Collections.sort(list, new Comparator<IdValuePair>() {
				@Override
				public int compare(IdValuePair o1, IdValuePair o2) {
					return o1.getValue().compareTo(o2.getValue());
				}
			});
		}
	}
}

