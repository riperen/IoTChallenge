function Room(w, h, blockSize, canvas){
	this.h = h;
	this.w = w;
	this.blockSize = blockSize;
	this.desks = new Array();
	this.canvas = canvas;
	this.fetchDesks();
}

//Initial values
Room.prototype.w = 0;
Room.prototype.h = 0;
Room.prototype.blockSize = 0;

Room.prototype.draw = function(){
	var ctx = this.canvas.getContext("2d");
	ctx.fillStyle = "#000000";
	ctx.fillRect(0, 0, this.w * this.blockSize , this.h * this.blockSize);
	
	//Teken te tafels
	for (var i =0; i < this.desks.length; i++){
		this.desks[i].draw();
	}
};

Room.prototype.addDesk = function(desk){
	//Alleen types Desk accepteren
	if (Object.getPrototypeOf(desk) === Desk.prototype){
		this.desks.push(desk);
	}
};

//Methode om initieel de desks op te halen bij deze kamer
Room.prototype.fetchDesks = function(){
	this.addDesk(new Desk(4,4,6,4, this.blockSize, this.canvas));
	this.addDesk(new Desk(4,8,4,6, this.blockSize, this.canvas));
	this.addDesk(new Desk(10,4,6,4, this.blockSize, this.canvas));
	this.addDesk(new Desk(12,8,4,6, this.blockSize, this.canvas));
};
