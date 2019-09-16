package edu.oregonstate.mist.identify

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.api.Error
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.identify.db.IdentifyDAO
import groovy.transform.TypeChecked

import javax.annotation.security.PermitAll
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("identify")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@TypeChecked
class IdentifyResource extends Resource {

    private IdentifyDAO identifyDAO
    private URI endpointUri

    IdentifyResource(IdentifyDAO identifyDAO, URI endpointUri) {
        this.identifyDAO = identifyDAO
        this.endpointUri = endpointUri
    }

    @Path("getOSUID")
    @Timed
    @GET
    Response getOSUID(@QueryParam('onid') String onid, @QueryParam('osuUID') long osuUID) {
        if ((!onid && !osuUID) || (onid && osuUID)) {
            return badRequest("Provide either osuUID or onid as query parameters.").build()
        }

        ResourceObject osuIDResourceObject = identifyDAO.getOSUID(osuUID, onid)

        if (!osuIDResourceObject) {
            notFound().build()
        } else {
            ok(new ResultObject(data: osuIDResourceObject)).build()
        }
    }

    @Path("osuID")
    @Timed
    @GET
    Response getByOSUID(@QueryParam('osuID') String osuID) {
        createResponse(osuID, null, null)
    }

    @Path("proxID")
    @Timed
    @GET
    Response getByPROXID(@QueryParam('facilityCode') String facilityCode,
                         @QueryParam('cardID') String cardID) {
        createResponse(null, facilityCode, cardID)
    }

    /**
     * Get response data from supplied parameters
     * @param osuID
     * @param facilityCode
     * @param cardID
     * @return
     */
    public Response createResponse(String osuID,
                                    String facilityCode,
                                    String cardID) {
        List<Error> errors = getErrors(osuID, facilityCode, cardID)

        if (errors) {
            Response.ResponseBuilder responseBuilder = Response.status(Response.Status.BAD_REQUEST)
            return responseBuilder.entity(errors).build()
        }

        ResultObject resultObject = new ResultObject()

        if (osuID && !facilityCode && !cardID) {
            resultObject.data = identifyDAO.getByOSUID(osuID)
        } else if (!osuID && facilityCode && cardID) {
            String proxID

            proxID = "${facilityCode}-${cardID}"
            resultObject.data = identifyDAO.getByProxID(proxID)
        }

        if (!resultObject.data) {
            notFound().build()
        } else {
            addRelatedLinks(resultObject)
            ok(resultObject).build()
        }
    }

    /**
     * Helper method to add related links
     * @param resultObject
     */
    private void addRelatedLinks(ResultObject resultObject) {
        resultObject.data['links']['related'] =
                resultObject.data['links']['related'] ? endpointUri.toString() +
                        "directory/" +
                        resultObject.data['links']['related'] : null
    }

    /**
     * Get a list of Error objects from query parameters.
     * @param osuID
     * @param facilityCode
     * @param cardID
     * @return
     */
    private List<Error> getErrors(String osuID, String facilityCode, String cardID) {
        List<Error> errors = []

        def addError = {String property ->
            errors.add(Error.badRequest(
                    Error.prop.getProperty("identify.${property}")
            ))
        }

        if (!osuID && !facilityCode && !cardID) {
            addError("noParams")
        }
        if (facilityCode && !(3 <= facilityCode.length() && facilityCode.length() <= 4)) {
            addError("facCodeLength")
        }
        if (osuID && (osuID.length() != 9)) {
            addError("osuIDLength")
        }
        if ((facilityCode && !cardID) || (!facilityCode && cardID)) {
            addError("partOfIDCard")
        }
        if ((facilityCode && !facilityCode.matches("[0-9]+")) ||
                (cardID && !cardID.matches("[0-9]+"))) {
            addError("idCardNonDigit")
        }
        if (facilityCode?.length() == 4 && cardID?.length() != 7) {
            addError("idCardLength")
        }

        errors
    }
}
