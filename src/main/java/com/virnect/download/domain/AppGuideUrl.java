package com.virnect.download.domain;

public enum AppGuideUrl {
	REMOTE_USER_GUIDE("https://file.virnect.com/Guide/remote_user_guide.pdf"),
	MAKE_USER_GUIDE("https://file.virnect.com/Guide/make_user_guide.pdf"),
	VIEW_MOBILE_USER_GUIDE("https://file.virnect.com/Guide/view_mobile_user_guide.pdf"),
	VIEW_REALWARE_USER_GUIDE("https://file.virnect.com/Guide/view_realwear_user_guide.pdf");

	private String url;

	AppGuideUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}
