package com.googlecode.freewebdav.entities;

import javax.persistence.Id;

import lombok.Data;
import lombok.ToString;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;

@Entity(name="rootFolder") @Cached @Indexed @Data @ToString
public class WebdavRootFolder {
	@Id String userId;
}
