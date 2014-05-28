var http = require('http'),
	url = 'http://graph.facebook.com/517267866/?fields=picture',
	bericht = '';
	
http.get(url, function(res) {
    var body = '';

    res.on('data', function(chunk) {
        body += chunk;
    });

    res.on('end', function() {
        var fbResponse = JSON.parse(body)
        console.log("Got response: ", fbResponse.picture.data.url);
		bericht = fbResponse;
    });
}).on('error', function(e) {
      console.log("Got error: ", e);
});

console.log(bericht.picture);