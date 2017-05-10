package com.andy.demo.analysis.bean;

public class LocalContact {
	private String name;
	private String phoneNumber;
	private String iconUrl;
	private boolean check = false;
	private String pinyin; //拼音全拼
    private String initials;//拼音首字母
    private String initial;
    private String contactsIdString;
	
	

    public String getContactsIdString() {
        return contactsIdString;
    }

    public void setContactsIdString(String contactsIdString) {
        this.contactsIdString = contactsIdString;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}
