package org.livinglabmontreal;

import com.google.api.client.util.Key;

public class AtomCategory {
	
	@Key("@scheme")
    public String scheme;

    @Key("@term")
    public String term;

    public static AtomCategory newKind(String kind) {
      AtomCategory category = new AtomCategory();
      category.scheme = "http://schemas.google.com/g/2005#kind";
      category.term = "http://schemas.google.com/photos/2007#" + kind;
      return category;
    }

}
