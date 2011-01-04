package com.googlecode.freewebdav.webdav;

import java.io.IOException;
import java.io.InputStream;

import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.MakeCollectionableResource;
import com.bradmcevoy.http.PutableResource;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.googlecode.freewebdav.entities.WebdavFolder;
import com.googlecode.freewebdav.entities.WebdavUser;
import com.googlecode.objectify.Key;

@SuppressWarnings("unchecked")
public class RootResource extends NamedCollectionResource<WebdavUser> implements MakeCollectionableResource, PutableResource {
	protected RootResource(WebdavUser wu) { super(wu); }
	
	@Override
	protected void ensureChildren() {
		Key<WebdavFolder> rootKey = ofy.query(WebdavFolder.class).filter("parent", ofy.getFactory().getKey(item)).getKey();
		for(WebdavFolder wf : ofy.query(WebdavFolder.class).filter("parent", rootKey))
			_children.put(wf.getName(), inject(new FolderResource(wf)));
	}

	@Override
	public CollectionResource createCollection(String newName) throws NotAuthorizedException, ConflictException, BadRequestException {
		Key<WebdavFolder> rootKey = ofy.query(WebdavFolder.class).filter("parent", ofy.getFactory().getKey(item)).getKey();
		return FolderResource.createFolder(ofy, rootKey, newName);
	}

	@Override
	public Resource createNew(String newName, InputStream inputStream, Long length, String contentType) throws IOException, ConflictException, NotAuthorizedException, BadRequestException {
		Key<WebdavFolder> rootKey = ofy.query(WebdavFolder.class).filter("parent", ofy.getFactory().getKey(item)).getKey();
		return FolderResource.createFile(ofy, rootKey, newName, inputStream, length, contentType);
	}
}
