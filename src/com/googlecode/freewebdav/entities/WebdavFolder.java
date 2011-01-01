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
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.condition.IfNull;

@Entity(name="folders") @Cached @Indexed  @Data @ToString
public class WebdavFolder {
	@Parent Key<WebdavFolder> parent; //only set when user is null
	@NotSaved(IfNull.class) Key<WebdavUser> user; //only set when parent is null
	@Id Long id;
	String name;
	Date created = new Date();
	Date lastModified;
	
	@PrePersist
	void prePersist() {
		lastModified = new Date();
	}
}