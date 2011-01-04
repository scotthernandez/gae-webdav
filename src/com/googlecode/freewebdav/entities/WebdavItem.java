package com.googlecode.freewebdav.entities;

import java.util.Date;

public interface WebdavItem {
	Date getCreated();
	Date getLastModified();
	String getName();
}
