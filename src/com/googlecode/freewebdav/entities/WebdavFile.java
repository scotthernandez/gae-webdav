package com.googlecode.freewebdav.entities;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.PrePersist;

import lombok.Data;
import lombok.ToString;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;

@Entity(name="files") @Cached @Indexed @Data @ToString
public class WebdavFile implements WebdavItem, Cloneable {
	@Id Long id;
	Key<WebdavFolder> parent;
	String name;
	Date created = new Date();
	Date lastModified;
	String contentType;
	byte[] data;

	@Override
	protected Object clone() throws CloneNotSupportedException {
		WebdavFile wf = (WebdavFile) super.clone();
		wf.parent = null;
		wf.id = null;
		return wf;
	}
	
	public WebdavFile copy() {
		try {
			return (WebdavFile) clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@PrePersist
	void prePersist() {
		lastModified = new Date();
	}
}