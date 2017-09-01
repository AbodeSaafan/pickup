var express = require('express');
var router = express.Router();

router.use('/login/', require('./login_api'));
router.use('/register/', require('./register_api'));
router.use('/profile/', require('./profile_api'));
router.use('/refresh/', require('./refresh_api'));
router.use('/extended_profile/', require('./extended_profile'));
router.use('/games/', require('./games_api'));
router.use('/reviews', requrie('./reviews_api'));
// Add more statements like the above to include other api files


module.exports = router;
