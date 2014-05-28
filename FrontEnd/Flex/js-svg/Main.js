var room = null;
function init(){
	// alert('init');
	room = new Room(getParameterByName('roomNumber'),'0,0 0,500 500,500 500,300 400,300 400,0', '#00ff00', 5, '#000000');
	// alert('room created');
	room.draw('room');
}

//Read parameter from queryString of current url
function getParameterByName(name)
{
  name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
  var regexString = "[\\?&]" + name + "=([^&#]*)";
  var regex = new RegExp(regexString);
  var found = regex.exec(window.location.search);
  if(found == null)
    return "";
  else
    return decodeURIComponent(found[1].replace(/\+/g, " "));
}
