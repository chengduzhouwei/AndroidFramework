package org.zw.android.framework.version;

/**
 * 版本信息
 * 
 * @author zhouwei
 *
 */
public class VersionInfo {

	/** 主版本号 */
	private String majorVersion;

	/** 子版本号 */
	private String accessoryVersion;

	/** 描述 */
	private String description;

	/** 地址 */
	private String url;

	public String getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(String majorVersion) {
		this.majorVersion = majorVersion;
	}

	public String getAccessoryVersion() {
		return accessoryVersion;
	}

	public void setAccessoryVersion(String accessoryVersion) {
		this.accessoryVersion = accessoryVersion;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
