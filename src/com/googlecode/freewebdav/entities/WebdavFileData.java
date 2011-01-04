package com.googlecode.freewebdav.entities;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.PrePersist;

import lombok.Data;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

@Entity(name="file-data") @Cached @Unindexed @Data
public class WebdavFileData {
	@Id Long id;
	byte[] data;
	Date lastModified;

	private WebdavFileData() {}
	public WebdavFileData(byte[] d) {this(); data = d;}
	
	@PrePersist
	void prePersist() {
		lastModified = new Date();
	}
}