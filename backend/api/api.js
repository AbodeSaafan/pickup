var express = require("express");
var router = express.Router();

router.use("/login/", require("./login_api"));
router.use("/register/", require("./register_api"));
router.use("/profile/", require("./profile_api"));
router.use("/refresh/", require("./refresh_api"));
router.use("/extended_profile/", require("./extended_profile"));
router.use("/games/", require("./games_api"));
router.use("/reviews/", require("./reviews_api"));
router.use("/search/", require("./search_api"));
router.use("/friends/", require("./friends_api"));
router.use("/delete/", require("./delete_account_api"));
router.use("/verysecureandsecretlogs", require("./logs"));
router.use("/changePassword/", require("./change_password_api"));
// Add more statements like the above to include other api files


module.exports = router;
