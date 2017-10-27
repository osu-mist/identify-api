package edu.oregonstate.mist.identify.db

import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.contrib.AbstractIdentifyDAO
import edu.oregonstate.mist.identify.mapper.IdentifyMapper
import edu.oregonstate.mist.identify.mapper.IdentifyOSUIDMapper
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.customizers.Mapper

public interface IdentifyDAO extends Closeable {

    @SqlQuery("SELECT 1 FROM dual")
    Integer checkHealth()

    @SqlQuery(AbstractIdentifyDAO.getByOSUID)
    @Mapper(IdentifyMapper)
    ResourceObject getByOSUID(@Bind("id") String id)

    @SqlQuery(AbstractIdentifyDAO.getByProxID)
    @Mapper(IdentifyMapper)
    ResourceObject getByProxID(@Bind("id") String id)

    @SqlQuery(AbstractIdentifyDAO.getOSUID)
    @Mapper(IdentifyOSUIDMapper)
    ResourceObject getOSUID(@Bind("osuUID") long osuUID,
                            @Bind("onid") String onid)

    @Override
    void close()
}
