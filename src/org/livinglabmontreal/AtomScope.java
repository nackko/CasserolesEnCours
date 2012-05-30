package org.livinglabmontreal;

import com.google.api.client.util.Key;

public class AtomScope {
	
	@Key("@type")
    public String type;
	
	@Key("@value")
    public String value;
	
	public static AtomScope newScope(String type) {
		AtomScope scope = new AtomScope();
		scope.type = type;
		return scope;
	}
	
	public static AtomScope newScope(String type, String value) {
		AtomScope scope = new AtomScope();
		scope.type = type;
		scope.value = value;
		return scope;
	}
}
