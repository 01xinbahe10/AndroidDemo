/**
 * Created by CDCT on 2017/11/16.
 */
window.index = 4;

function page04() {
	var obj = {
		"IZFSQK": radioGetValue("IZFSQK"),
		"IHSDP": radioGetValue("IHSDP"),
		"ISFCZC": radioGetValue("ISFCZC"),
		"CRHYC": radioGetValue("CRHYC"),
		"ISFCSG": radioGetValue("ISFCSG"),
		"IYSQK": radioGetValue("IYSQK"),
		"IZZSJ": radioGetValue("IZZSJ"),
		"ICXFS": radioGetValue("ICXFS"),
		"ILWQK": radioGetValue("ILWQK"),
		"IDLQK": radioGetValue("IDLQK"),
		"CDLFS": checkboxGetValue("CDLFS"),
		"IYDSJ": radioGetValue("IYDSJ")
	};
	return obj;
}

$("#addSave4").on("click", function() {
	var data = {
		"IZFSQK": radioGetValue("IZFSQK"),
		"IHSDP": radioGetValue("IHSDP"),
		"ISFCZC": radioGetValue("ISFCZC"),
		"CRHYC": radioGetValue("CRHYC"),
		"ISFCSG": radioGetValue("ISFCSG"),
		"IYSQK": radioGetValue("IYSQK"),
		"IZZSJ": radioGetValue("IZZSJ"),
		"ICXFS": radioGetValue("ICXFS"),
		"ILWQK": radioGetValue("ILWQK"),
		"IDLQK": radioGetValue("IDLQK"),
		"CDLFS": checkboxGetValue("CDLFS"),
		"IYDSJ": radioGetValue("IYDSJ")
	};

	ajax({
		data: data,
		success: function(data) {
			console.log(data);
			if(data.MSG.RES["RES.2"] == 1) {
				$.toolTipModel({
					icon: "../img/com/smilingFace.png",
					txt: "保存成功",
					timeEnd: function() {
						window.location.href = "HB5.html?timestamp=" + new Date().getTime();
					}
				})
			}
		},
		method: "ArchiveInformationFour",
		className: "ArchiveInformation"
	})

});

// ajax({
// 	success: function(data) {
// 		var Obj = getData(data);
// 		var arr = [{
// 				row: "IZFSQK",
// 				name: "IZFSQK",
// 				type: "radio"
// 			},
// 			{
// 				row: "IHSDP",
// 				name: "IHSDP",
// 				type: "radio"
// 			},
// 			{
// 				row: "ISFCZC",
// 				name: "ISFCZC",
// 				type: "radio"
// 			},
// 			{
// 				row: "CRHYC",
// 				name: "CRHYC",
// 				type: "radio"
// 			},
// 			{
// 				row: "ISFCSG",
// 				name: "ISFCSG",
// 				type: "radio"
// 			},
// 			{
// 				row: "IYSQK",
// 				name: "IYSQK",
// 				type: "radio"
// 			},
// 			{
// 				row: "IZZSJ",
// 				name: "IZZSJ",
// 				type: "radio"
// 			},
// 			{
// 				row: "ICXFS",
// 				name: "ICXFS",
// 				type: "radio"
// 			},
// 			{
// 				row: "ILWQK",
// 				name: "ILWQK",
// 				type: "radio"
// 			},
// 			{
// 				row: "IDLQK",
// 				name: "IDLQK",
// 				type: "radio"
// 			},
// 			{
// 				row: "CDLFS",
// 				name: "CDLFS",
// 				type: "checkbox"
// 			},
// 			{
// 				row: "IYDSJ",
// 				name: "IYDSJ",
// 				type: "radio"
// 			}

// 		];
// 		if(typeof Obj.length != "undefined") {
// 			for(var i = 0; i < Obj.length; i++) {
// 				changeInfo(arr, Obj[i]);
// 			}
// 		} else {
// 			changeInfo(arr, Obj);
// 		}
// 		console.log(Obj);
// 	},
// 	className: "ArchiveInformation",
// 	method: "reArchiveInformation"
// });