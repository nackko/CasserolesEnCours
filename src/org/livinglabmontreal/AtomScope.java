package org.livinglabmontreal;

import com.google.api.client.util.Key;

public class AtomScope {
	
	@Key("@type")
    public String type;
	
	public static AtomScope newScope(String type) {
		AtomScope scope = new AtomScope();
		scope.type = type;
		return scope;
	}
}
