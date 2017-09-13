var fs = require('fs');

fs.writeFile("./api/private.key", "FA2C92624616E09CBBEFFE0ACB09E7B9342F03F9C8B18E0A2F6B68809CF8BDB8", function(err) {
    if(err) {
        return console.log(err);
    }

    console.log("Private key created");
}); 