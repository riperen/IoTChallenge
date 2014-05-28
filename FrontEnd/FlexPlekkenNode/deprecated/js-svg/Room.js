function Room(roomNumber, points, color, strokeWidth, strokeColor){
	alert('creating room ');
	this.roomNumber = roomNumber;
	this.points = points;
	this.color = color;	
	this.strokeWidth = strokeWidth;
	this.strokeColor = strokeColor;
	this.desks = new Array();
	this.fetchDesks();
}

Room.prototype.draw = function(svgId){
	alert('draw room');
	var svg = document.getElementById(svgId);
	var newElement = document.createElementNS("http://www.w3.org/2000/svg", 'polygon');
	newElement.setAttribute("points",this.points);
	newElement.style.strokeWidth = this.strokeWidth;
	newElement.style.stroke = this.strokeColor;
	newElement.style.fill = this.color;
	// alert(this.desks.length);
	svg.appendChild(newElement);

	for (var i =0; i< this.desks.length; i++){
		this.desks[i].draw(svgId);
	}

};

//Add desk to this room
Room.prototype.addDesk = function(desk){
	// alert("addDesk");
	//Only accept Desks
	if (Object.getPrototypeOf(desk) === Desk.prototype){
		this.desks.push(desk);
	}
};


//Methode om initieel de desks op te halen bij deze kamer
Room.prototype.fetchDesks = function(){
	// alert('fetchdesks');
	this.addDesk(new Desk(this.roomNumber,1,'100,100 100,150 200,150 200,100', '#ffff00', 2, '#000000'));
	this.addDesk(new Desk(this.roomNumber,2,'200,100 200,150 300,150 300,100', '#ffff00', 2, '#000000'));
	this.addDesk(new Desk(this.roomNumber,3,'100,150 100,250 150,250 150,150', '#ffff00', 2, '#000000'));

	//alert('desk added');
};
