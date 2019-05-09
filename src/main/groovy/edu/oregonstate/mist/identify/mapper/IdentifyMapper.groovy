package edu.oregonstate.mist.identify.mapper

import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.identify.core.Attributes
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper
import java.sql.ResultSet
import java.sql.SQLException

public class IdentifyMapper implements ResultSetMapper<ResourceObject> {

    public ResourceObject map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new ResourceObject(
            id: rs.getString("OSUUID"),
            type: "person",
            attributes: new Attributes(
                    osuID: rs.getString("OSUID"),
                    username: rs.getString("ONID"),
                    firstName: rs.getString("FIRST_NAME"),
                    lastName: rs.getString("LAST_NAME")
            ),
            links: ["related": rs.getString("OSUUID")]
        )
    }
}
