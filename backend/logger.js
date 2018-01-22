//// http://tostring.it/2014/06/23/advanced-logging-with-nodejs/
var winston = require("winston");
winston.emitErrs = true;

var logger = new winston.Logger({
	transports: [
		new winston.transports.File({
			name: "verbose-file",
			level: "verbose",
			filename: "logs/verb.log",
			handleExceptions: true,
			json: true,
			maxsize: 10485760, //10MB
			maxFiles: 1,
			colorize: false
		}),
		new winston.transports.File({
			name: "info-file",
			level: "info",
			filename: "logs/info.log",
			handleExceptions: true,
			json: true,
			maxsize: 10485760, //10MB
			maxFiles: 1,
			colorize: false
		}),
		new winston.transports.Console({
			level: "info",
			handleExceptions: true,
			json: false,
			colorize: true
		})
	],
	exitOnError: false
});

module.exports = logger;