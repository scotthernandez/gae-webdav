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
import com.googlecode.objectify.annotation.Parent;

@Entity(name="files") @Cached @Indexed @Data @ToString
public class WebdavFile {
	@Parent Key<WebdavFolder> parent;
	@Id Long id;
	String name;
	Date created = new Date();
	Date lastModified;
	String contentType;
	byte[] data;

	@PrePersist
	void prePersist() {
		lastModified = new Date();
	}
}