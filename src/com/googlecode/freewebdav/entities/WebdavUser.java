package com.googlecode.freewebdav.entities;

import java.util.Date;

import javax.persistence.Id;

import lombok.Data;
import lombok.ToString;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;

@Entity(name="users") @Cached @Indexed @Data @ToString
public class WebdavUser {
	@Id String userId;
	String username, password;
	@Unindexed Date created = new Date();
}
