import json
import sys
import unittest

import utils


class TestStringMethods(unittest.TestCase):

    # Test GET /osuID
    def test_osuID(self):
        # Test no osuID param
        no_osu_id = utils.get_osu_id(None)
        validate_response(self, no_osu_id, 400, message=no_params_message)

        # Test length of osuID =/= 9
        wrong_len = utils.get_osu_id("0")
        validate_response(
            self, wrong_len, 400, message="osuID must be 9 digits"
        )

        # Test invalid osuID
        invalid_osu_id = utils.get_osu_id("000000000")
        validate_response(self, invalid_osu_id, 404)

        # Test valid osuID
        valid_osu_id = utils.get_osu_id(config["valid_osu_id"])
        validate_response(self, valid_osu_id, 200, "person")

    # Test GET /proxID
    def test_proxID(self):
        # Test no params
        no_params = utils.get_prox_id(None, None)
        validate_response(self, no_params, 400, message=no_params_message)

        # Test one param
        one_param = utils.get_prox_id(config["valid_facility_code"], None)
        validate_response(self, one_param, 400,
                          message="facilityCode and cardID must "
                                  "be used together"
                          )

        # Test non-digit characters in params
        non_digits = utils.get_prox_id("abc", "def")
        validate_response(self, non_digits, 400,
                          message="facilityCode and cardID may only contain "
                                  "digits"
                          )

        # Test length of facilityCode =/= 3 or 4
        wrong_length = utils.get_prox_id("12345", "678")
        validate_response(
            self, wrong_length, 400,
            message="facilityCode must be 3 or 4 digits"
        )

        # Test invalid params
        invalid_params = utils.get_prox_id("999", "999999999")
        validate_response(self, invalid_params, 404)

        # Test valid params
        valid_params = utils.get_prox_id(
            config["valid_facility_code"], config["valid_card_id"]
        )
        validate_response(self, valid_params, 200, "person")

    # Test GET /getOSUID
    def test_getOSUID(self):
        # Test no params
        no_params = utils.get_get_osu_id(None, None)
        validate_response(self, no_params, 400, message=provide_either_message)

        # Test both params
        both_params = utils.get_get_osu_id(
            config["valid_onid"], config["valid_osu_uid"]
        )
        validate_response(
            self, both_params, 400, message=provide_either_message
        )

        # Test invalid onid
        invalid_onid = utils.get_get_osu_id("invalidOnid", None)
        validate_response(self, invalid_onid, 404)

        # Test invalid osuUID
        invalid_osu_uid = utils.get_get_osu_id(None, "-1")
        validate_response(self, invalid_osu_uid, 404)

        # Test valid onid
        valid_onid = utils.get_get_osu_id(config["valid_onid"], None)
        validate_response(self, valid_onid, 200, "person")

        # Test valid osuUID
        valid_osu_uid = utils.get_get_osu_id(None, config["valid_osu_uid"])
        validate_response(self, valid_osu_uid, 200, "person")


def validate_response(self, res, code=None, res_type=None, message=None):
    if code:
        self.assertEqual(res.status_code, code)
    if res_type:
        self.assertEqual(res.json()["data"]["type"], res_type)
    if message:
        # Try either single error or array containing single error
        try:
            self.assertIn(message, res.json()["developerMessage"])
        except TypeError:
            self.assertTrue(len(res.json()) == 1)
            self.assertIn(message, res.json()[0]["developerMessage"])


if __name__ == "__main__":
    namespace, args = utils.parse_args()
    config = json.load(open(namespace.input_file))
    utils.set_local_vars(config)
    sys.argv = args

    no_params_message = "No query parameters provided"
    provide_either_message = "Provide either osuUID or onid"
    unittest.main()
