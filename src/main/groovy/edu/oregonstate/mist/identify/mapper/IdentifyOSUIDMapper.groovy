package edu.oregonstate.mist.identify.mapper

import edu.oregonstate.mist.api.jsonapi.ResourceObject
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException

class IdentifyOSUIDMapper implements ResultSetMapper<ResourceObject> {

    public ResourceObject map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new ResourceObject(
                id: rs.getString("OSUID"),
                type: "person",
                attributes: ["osuID": rs.getString("OSUID")]
        )
    }
}

