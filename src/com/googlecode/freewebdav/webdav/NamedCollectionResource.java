package com.googlecode.freewebdav.webdav;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.PropFindableResource;
import com.bradmcevoy.http.Resource;
import com.googlecode.freewebdav.entities.WebdavItem;

public abstract class NamedCollectionResource<T> extends AuthenticatedResource implements CollectionResource, PropFindableResource {
	private static final Logger log = Logger.getLogger(NamedCollectionResource.class.getName() );
	protected Map<String, Resource> _children = new HashMap<String, Resource>();

	protected void ensureChildren() {};
	
	protected NamedCollectionResource( WebdavItem item){ super(item);}

	@Override
	public Resource child(String name) {
		ensureChildren();

		if (_children != null && this._children.containsKey(name)) {
			log.finer("returning child " + name);
			return _children.get(name);
		}
		
		log.finer("returning NO child ");
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List getChildren() {
		ensureChildren();
		if (_children == null) return new ArrayList();

		return new ArrayList<Resource>(_children.values());
	}
}
