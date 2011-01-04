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

@Entity(name="folders") @Cached @Indexed  @Data @ToString
public class WebdavFolder implements WebdavItem {
	@Id Long id;
	@SuppressWarnings("rawtypes")
	Key parent; //Key<User/Folder>
	String name;
	Date created = new Date();
	Date lastModified;
	
	@PrePersist
	void prePersist() {
		lastModified = new Date();
	}
}