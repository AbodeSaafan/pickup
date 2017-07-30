var express = require('express');
var router = express.Router();

router.use('/login', require('./login_api'));
router.use('/register', require('./register_api'));
router.use('/profile/', require('./profile_api'));
// Add more statements like the above to include other api files


module.exports = router;