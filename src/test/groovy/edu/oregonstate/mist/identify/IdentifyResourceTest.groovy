package edu.oregonstate.mist.identify

import edu.oregonstate.mist.api.Error
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.identify.core.Attributes
import edu.oregonstate.mist.identify.db.IdentifyDAO
import groovy.mock.interceptor.MockFor
import org.junit.Test

import javax.ws.rs.core.Response

class IdentifyResourceTest {
    URI selfLinkBase = new URI("https://www.foo.com/")

    IdentifyResource identifyResource = new IdentifyResource(null, selfLinkBase)

    /**
     * Test that expected error messages for getByOSUID are returned
     */
    @Test
    public void testErrorsOSUID() {
        testSizeAndMessage("osuIDLength", identifyResource.getByOSUID("12345678"))
        testSizeAndMessage("noParams", identifyResource.getByOSUID(null))
    }

    /**
     * Test that expected error messages for getByPROXID are returned
     */
    @Test
    public void testErrorsPROXID() {
        testSizeAndMessage("facCodeLength", identifyResource.getByPROXID("12", "123456"))
        testSizeAndMessage("noParams", identifyResource.getByPROXID(null, null))
        testSizeAndMessage("partOfIDCard", identifyResource.getByPROXID("123", null))
        testSizeAndMessage("partOfIDCard", identifyResource.getByPROXID(null, "123456"))
        testSizeAndMessage("idCardNonDigit", identifyResource.getByPROXID("123", "abc"))
        testSizeAndMessage("idCardNonDigit", identifyResource.getByPROXID("abc", "123456"))
    }

    /**
     * Test that a related link is correct when an ID is present
     */
    @Test
    public void testPopulatedRelatedLink() {
        testRelatedLink("12345678")
    }

    /**
     * Test that a related link is null when id is null
     */
    @Test
    public void testNullRelatedLink() {
        testRelatedLink(null)
    }

    /**
     * Helper function to assert an array of errors contains the correct message
     * and its size is 1
     * @param messageIdentifier
     * @param response
     */
    private void testSizeAndMessage(String messageIdentifier, Response response) {
        List<Error> errors = response.entity
        String expectedMessage = getErrorMessage(messageIdentifier)

        assert errors.size() == 1
        assert errors[0].developerMessage == expectedMessage
    }

    /**
     * Get an error message from the properties in the Error class
     * @param property
     * @return
     */
    private String getErrorMessage(String property) {
        Error.prop.getProperty("identify.${property}")
    }

    /**
     * Assert a correct link is returned given an expected ID
     * @param id
     */
    private void testRelatedLink(String id) {
        def mockDAO = new MockFor(IdentifyDAO)

        ResourceObject testResourceObject = new ResourceObject(
                id: id,
                type: "person",
                attributes: new Attributes(
                        username: "smitfran",
                        firstName: "Frank",
                        lastName: "Smith"
                ),
                links: ["related": id]
        )

        mockDAO.demand.getByOSUID() { testResourceObject }
        def identifyResource = new IdentifyResource(mockDAO.proxyInstance(), selfLinkBase)
        identifyResource.getByOSUID("123456789")

        String expectedLink = id ? selfLinkBase.toString() +
                "directory/" + id : null

        assert expectedLink == testResourceObject.links['related']
    }
}
