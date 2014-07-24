package uk.bl.odin.orcid.htmlmeta;

import java.util.Set;


import com.google.common.collect.LinkedHashMultimap;

public class AbstractMeta<T> {
	
		//linked to preserve author order.
		private final LinkedHashMultimap<T, String> properties = LinkedHashMultimap.create();

		public void put(T key, String value) {
			properties.put(key, value);
		}

		public Set<String> get(T key) {
			return properties.get(key);
		}
		
		public String getFirst(T key) {
			if (get(key)!=null && get(key).size()>0)
				return get(key).iterator().next();
			return null;
		}
		
		public boolean has(T key){
			return (get(key)!=null && get(key).size()>0);
		}
		
		public String toString(){
			return "["+this.getClass().getSimpleName()+"] "+properties.toString();
		}

}
