function radioGetValue(name,type) {
	var all = $('[name="' + name + '"]');
	var i = 0,q=-0;
	if(typeof  type != "undefined" && type == 1){
		for ( i = 0; i < all.length; i++) {
			if (all[i].checked) {
				if(all[i].value=="1"){
					var allName=$(all[i].parentNode.parentNode).find(".nameA");
					var allData=$(all[i].parentNode.parentNode).find(".dataA");
					var nameA=[];
					var dataA=[];
					for(q=  0;q<allName.length;q++){
						nameA.push(allName[q].value);
					}
					for(q=  0;q<allData.length;q++){
						dataA.push(allData[q].value);
					}
					return all[i].value+"_"+$(all[i]).attr("data-bufValue")+"_"+encodeURIComponent(JSON.stringify(nameA))+"_"+encodeURIComponent(JSON.stringify(dataA));
				}
			}
		}
	}
	for ( i = 0; i < all.length; i++) {
		if (all[i].checked) {
			if(all[i].value=="-1"){
				return all[i].value + "_" + $("#" + all[i].getAttribute("forEleId")).val();
			}else{
				return all[i].value + "_" + $(all[i]).attr("data-bufValue");
			}
			
		}
	}

	return "n";
}

function checkboxGetValue(name) {
	var all = $('[name="' + name + '"]');
	var str = "";
	for (var i = 0; i < all.length; i++) {
		if (all[i].checked) {
			if (all[i].value == "-1") {
				str = str + ",-1_" + $("#" + all[i].getAttribute("forEleId")).val();
			} else {
				str = str + "," + all[i].value + "_" + $(all[i]).attr("data-bufValue");
			}

		} else {
			str = str + "," + "n"
		}
	}
	str = str.substring(1);
	return str
}