function Desk(x, y, w, h,blockSize, canvas){
	this.x = x * blockSize;
	this.y = y * blockSize;
	this.h = h * blockSize;
	this.w = w * blockSize;
	this.blockSize = blockSize;
	this.canvas = canvas;
	this.color = this.defaultColor;
}

Desk.prototype.x = '';
Desk.prototype.y = '';
Desk.prototype.w = '';
Desk.prototype.h = '';
Desk.prototype.defaultColor = '#00ff00'; //Default color

//Doe alles wat nodig is om de kamer naar zijn default staat te zetten
Desk.prototype.reset = function(){
	this.color = this.defaultColor;
};

Desk.prototype.draw = function(){
	var ctx = this.canvas.getContext("2d");
	ctx.fillStyle = this.color;
	ctx.fillRect(this.x, this.y, this.w - 1, this.h - 1);
};
