function Desk(roomNumber, deskNumber, points, color, strokeWidth, strokeColor){
	// alert('creating desk ');
	this.roomNumber = roomNumber;
	this.deskNumber = deskNumber;
	this.points = points;
	this.color = color;	
	this.strokeWidth = strokeWidth;
	this.strokeColor = strokeColor;
}

//Draw the desk
Desk.prototype.draw = function(svgId){
	// alert('svgElement');
	var svg = document.getElementById(svgId);
	var newElement = document.createElementNS("http://www.w3.org/2000/svg", 'polygon');
	newElement.setAttribute("points",this.points);
	newElement.style.strokeWidth = this.strokeWidth;
	newElement.style.stroke = this.strokeColor;
	newElement.style.fill = this.color;
	
	//Custom properties
	newElement.roomNumber = this.roomNumber;
	newElement.deskNumber = this.deskNumber;
	
	//ClickListener
	newElement.addEventListener("click", function(event) {
		var targetElement =  event.target || event.srcElement;
		targetElement.style.fill = '#ff00ff';
		alert('Room: ' + targetElement.roomNumber + '; Desk: ' + targetElement.deskNumber);
	}, false);
	
	svg.appendChild(newElement);
};
