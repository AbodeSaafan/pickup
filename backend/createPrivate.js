var logger = require("./logger");
var fs = require("fs");

// eslint-disable-next-line no-console
console.log("If you are using this server for production, You need to create a private key and save it to the following location ./api/private.key.");
// eslint-disable-next-line no-console
console.log("Please ensure that your private key is safe and NOT published online.");

// Dev testing only
fs.writeFile("./api/private.key", "FA2C92624616E09CBBEFFE0ACB09E7B9342F03F9C8B18E0A2F6B68809CF8BDB8", function(err) {
	if(err) {
		return logger.verbose(err);
	}

	logger.info("Private key created");
}); 