package edu.oregonstate.mist.identify.db

import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.contrib.AbstractIdentifyDAO
import edu.oregonstate.mist.identify.mapper.IdentifyMapper
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper

@RegisterMapper(IdentifyMapper)
public interface IdentifyDAO extends Closeable {

    @SqlQuery("SELECT 1 FROM dual")
    Integer checkHealth()

    @SqlQuery(AbstractIdentifyDAO.getByOSUID)
    ResourceObject getByOSUID(@Bind("id") String id)

    @SqlQuery(AbstractIdentifyDAO.getByProxID)
    ResourceObject getByProxID(@Bind("id") String id)

    @Override
    void close()
}
