package com.mongodb.memphis.placeholder.location;

import com.mongodb.memphis.placeholder.Placeholder;

/**
 * A class to bind data generators to a particular place in an existing
 * document.
 *
 * Used to avoid having to create a new object for every document (we can reuse
 * objects for efficiency).
 *
 * @author Mark Baker-Munton
 */
public interface PlaceholderLocation {

	void apply();

	Placeholder getPlaceholder();

}