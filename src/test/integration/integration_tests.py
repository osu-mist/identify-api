import json
import sys
import unittest

import utils


class TestStringMethods(unittest.TestCase):

    # Test
    def test_something(self):
        pass


def validate_response(self, res, code=None, res_type=None, message=None):
    if code:
        self.assertEqual(res.status_code, code)
    if res_type:
        self.assertEqual(res.json()["data"]["type"], res_type)
    if message:
        self.assertIn(message, res.json()["developerMessage"])


if __name__ == "__main__":
    namespace, args = utils.parse_args()
    config = json.load(open(namespace.input_file))
    utils.set_local_vars(config)
    sys.argv = args
    unittest.main()