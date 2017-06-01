package edu.oregonstate.mist.identify

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.api.Error
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.identify.db.IdentifyDAO
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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

    Logger logger = LoggerFactory.getLogger(IdentifyResource.class)

    private IdentifyDAO identifyDAO
    private URI endpointUri

    IdentifyResource(IdentifyDAO identifyDAO, URI endpointUri) {
        this.identifyDAO = identifyDAO
        this.endpointUri = endpointUri
    }

    @Timed
    @GET
    Response identify(@QueryParam('osuID') String osuID,
                      @QueryParam('facilityCode') String facilityCode,
                      @QueryParam('cardID') String cardID) {
        List<Error> errors = getErrors(osuID, facilityCode, cardID)

        if (errors) {
            Response.ResponseBuilder responseBuilder = Response.status(Response.Status.BAD_REQUEST)
            return responseBuilder.entity(errors).build()
        }

        def resultObject = new ResultObject()

        if (osuID && !facilityCode && !cardID) {
            resultObject.data = identifyDAO.getByOSUID(osuID)
        } else if (!osuID && facilityCode && cardID) {
            resultObject.data = identifyDAO.getByProxID(facilityCode + cardID)
        }

        if (!resultObject.data) {
            return notFound().build()
        }

        try {
            resultObject.data['links']['related'] =
                    endpointUri.toString() +
                    "directory/" +
                    resultObject.data['links']['related'].toString()
        } catch (NullPointerException) {
            logger.warn("Related link could not be added. This person might not have an OSUUID.")
        }

        ok(resultObject).build()
    }

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
        if (osuID && facilityCode && cardID) {
            addError("tooManyParams")
        }
        if (osuID && (cardID || facilityCode)) {
            addError("osuIDPlus")
        }
        if ((facilityCode && !cardID) || (!facilityCode && cardID)) {
            addError("partOfIDCard")
        }
        if ((facilityCode && !facilityCode.matches("[0-9]+")) ||
                (cardID && !cardID.matches("[0-9]+"))) {
            addError("idCardNonDigit")
        }

        errors
    }
}
