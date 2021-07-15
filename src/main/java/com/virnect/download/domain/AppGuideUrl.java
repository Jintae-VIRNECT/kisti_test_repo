package com.virnect.download.domain;

public enum AppGuideUrl {
	REMOTE_USER_GUIDE("https://file.virnect.com/Guide/remote_user_guide.pdf"),
	MAKE_USER_GUIDE("https://file.virnect.com/Guide/make_user_guide.pdf"),
	VIEW_MOBILE_USER_GUIDE("https://file.virnect.com/Guide/view_mobile_user_guide.pdf"),
	VIEW_REALWEAR_USER_GUIDE("https://file.virnect.com/Guide/view_realwear_user_guide.pdf"),
	REMOTE_LINKFLOW_USER_GUIDE("https://file.virnect.com/Guide/remote_linkflow_user_guide.pdf"),
	TRACK_USER_GUIDE("");

	private String url;

	AppGuideUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}