var Toolbar = {}
Toolbar._onmouseover = function(ev){
	var l = ev.target || ev.srcElement;
	if(l.className == "topToolbarButton") l.className = "topToolbarButtonHover";
}
Toolbar._onmouseout = function(ev){
	var l = ev.target || ev.srcElement, to = ev.relatedTarget || ev.toElement;
	while(l && l.className != "topToolbarButtonHover") l = l.parentNode;
	while(to && to != l) to = to.parentNode;
	if(l && l != to) l.className = "topToolbarButton";
}