package com.googlecode.freewebdav.webdav;

import com.googlecode.freewebdav.entities.WebdavFolder;
import com.googlecode.freewebdav.entities.WebdavUser;

@SuppressWarnings("unchecked")
public class RootResource extends NamedCollectionResource<WebdavUser>{
	protected RootResource(WebdavUser wu) { super(wu, wu.getUserId(), wu.getUsername()); }
	
	@Override
	protected void ensureChildren() {
		for(WebdavFolder wf : ofy.query(WebdavFolder.class).ancestor(null).filter("user", ofy.getFactory().getKey(_obj)))
			_children.put(wf.getName(), inject(new FolderResource(wf)));
	}

}
