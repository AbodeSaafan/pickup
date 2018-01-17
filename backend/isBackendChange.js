var spawn = require("cross-spawn");

var files = spawn.sync("git", ["diff", "--name-only"]);
if (!files.stdout) {
	console.log("Error when attempting to retrieve changes...")
	process.exit(1);
}
var backendChange = files.stdout.toString('utf8').trim().split('\n').filter((filename) => {
	return filename.startsWith("backend/");
});

if(backendChange){
	console.log("!! DO NOT BYPASS TEST RUN WITHOUT CONFIRMING WITH ABODE FIRST PLEASE !!");
	spawn.sync("npm", ["test"]);
	process.exit(0); 
}
process.exit(0);