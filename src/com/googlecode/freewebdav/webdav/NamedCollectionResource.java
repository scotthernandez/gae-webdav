package com.googlecode.freewebdav.webdav;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.PropFindableResource;
import com.bradmcevoy.http.Resource;

public abstract class NamedCollectionResource<T> extends AuthenticatedResource implements CollectionResource, PropFindableResource {
	private static final Logger log = Logger.getLogger(NamedCollectionResource.class.getName() );

	protected T _obj = null;
	protected Date _createDate = null;
	protected Map<String, Resource> _children = new HashMap<String, Resource>();

	protected void init(T obj) { _obj = obj; }
	protected void ensureChildren() {};
	protected NamedCollectionResource( String id, String name){ super(id, name);}
	protected NamedCollectionResource( T obj, String id, String name){ 
		super(id, name);
		_obj = obj;
		init(obj);
	}
	
	@Override
	public Date getCreateDate() {
		return (_createDate == null) ? new Date(): _createDate ;
	}

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
