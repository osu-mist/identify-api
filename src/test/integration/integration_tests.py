import json
import sys
import unittest

import utils


class TestStringMethods(unittest.TestCase):

    # Test GET /osuID
    def test_osuID(self):
        # Test no osuID param
        no_osu_id = utils.get_osu_id(None)
        validate_response(self, no_osu_id, 400, message="No query parameters provided")

        # Test length of osuID =/= 9
        wrong_len = utils.get_osu_id("0")
        validate_response(self, wrong_len, 400, message="osuID must be 9 digits")

        # Test invalid osuID
        invalid_osu_id = utils.get_osu_id(config["invalid_osu_id"])
        validate_response(self, invalid_osu_id, 404)

        # Test valid osuID
        valid_osu_id = utils.get_osu_id(config["valid_osu_id"])
        validate_response(self, valid_osu_id, 200, "person")

    # Test GET /proxID
    def test_proxID(self):
        pass

    # Test GET /getOSUID
    def test_getOSUID(self):
        pass


def validate_response(self, res, code=None, res_type=None, message=None):
    if code:
        self.assertEqual(res.status_code, code)
    if res_type:
        self.assertEqual(res.json()["data"]["type"], res_type)
    if message:
        self.assertTrue(len(res.json()) == 1)
        self.assertIn(message, res.json()[0]["developerMessage"])


if __name__ == "__main__":
    namespace, args = utils.parse_args()
    config = json.load(open(namespace.input_file))
    utils.set_local_vars(config)
    sys.argv = args
    unittest.main()
