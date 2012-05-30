package org.livinglabmontreal;

import com.google.api.client.util.Key;

public class AtomRole {
	
	@Key("@value")
    public String value;
	
	public static AtomRole newRole(String value) {
		AtomRole role = new AtomRole();
		role.value = value;
		return role;
	}
}
