var express = require("express");
var router = express.Router();
var requestHelper = require("../helpers/requestHelper");

router.get("/:password", function(req, res){
	try {
		var password = req.params.password;
		//TODO Replace with db stored password later before we go live live
		if(password != null && password == "tempdevpassword"){
			res.sendFile("/logs/verb.log", { root: "./" });
		}
        
	} catch (err){
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
});

module.exports = router;