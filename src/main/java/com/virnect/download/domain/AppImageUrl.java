package com.virnect.download.domain;

public enum AppImageUrl {
	REMOTE_MOBILE("https://file.virnect.com/resource/remote_android.png"),
	REMOTE_REALWEAR("https://file.virnect.com/resource/view_realwear.png"),
    REMOTE_LINKFLOW("https://file.virnect.com/resource/remote_linkflow.png"),
	VIEW_REALWEAR("https://file.virnect.com/resource/view_realwear.png"),
	VIEW_MOBILE("https://file.virnect.com/resource/view_android.png"),
	MAKE("https://file.virnect.com/resource/make_pc.png");

	private String url;

	AppImageUrl(String url){
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}
