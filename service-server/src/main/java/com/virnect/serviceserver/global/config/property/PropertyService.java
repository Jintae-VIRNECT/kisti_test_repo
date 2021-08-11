package com.virnect.serviceserver.global.config.property;

import java.util.Map;

public abstract class PropertyService {

	public Map<String, ?> propertiesSource;

	protected Object getValue(String propertyName, Object propertyValue) {
		Object value = null;
		if (propertiesSource != null) {
			Object valueObj = propertiesSource.get(propertyName);
			if (valueObj != null) {
				value = valueObj.toString();
			}
		} else {
			value = propertyValue;
		}
		return value;
	}
}
