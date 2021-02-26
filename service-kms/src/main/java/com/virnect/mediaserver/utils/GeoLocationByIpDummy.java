package com.virnect.mediaserver.utils;

import java.net.InetAddress;

public class GeoLocationByIpDummy implements GeoLocationByIp {

	@Override
	public GeoLocation getLocationByIp(InetAddress ipAddress) throws Exception {
		return null;
	}

}
