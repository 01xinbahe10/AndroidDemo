/**
 * Created by CDCT on 2017/11/14.
 */

window.index = 1;

function page01() {
	var date = new Date();
	var obj = {
		"CHZXM": $("#HB1userName").val(),
		"IHZXBBM": radioGetValue("sex"),
		"DCSNY": $("#DCSNY").val(),
		"CHZSFZH": $("#CHZSFZH").val(),
		"CCSD": $("#CCSD").val(),
		"CHZMZ": $("#CHZMZ").val(),
		"CHZSJ": $("#CHZSJ").val(),
		"CLXRXM": $("#CLXRXM").val(),
		"CLXRDH": $("#CLXRDH").val(),
		"CHZSG": $("#CHZSG").val(),
		"CHZTZ": $("#CHZTZ").val(),
		"IXXBM": $("#IXXBM").val(),
		"IRH": radioGetValue("YX"),
		"CWHCDBM": $("#CWHCDBM").val(),
		"CZYBM": $("#CZYBM").val(),
		"CYWGMS": checkboxGetValue("YWGMS"),
		"CHYZTBM": $("#CHYZTBM").val(),
		"CWHCDMC": $("#CWHCDBM").find("option:selected").text(),
		"CHYZTMC": $("#CHYZTBM").find("option:selected").text(),
		"CZYMC": $("#CZYBM").find("option:selected").text(),
		"IXXMC": $("#IXXBM").find("option:selected").text()
	};
	
	return obj;
}

$("#infoSave").on("click", function() {
	var date = new Date();
	if($("#CHZSFZH").val().length < 16 || $("#CHZSFZH").val().length > 19) {
		$.topModel({
			txt: "身份证输入错误"
		})
		return;
	}
	window.page01 = {
		"CHZXM": $("#HB1userName").val(),
		"IHZXBBM": radioGetValue("sex"),
		"DCSNY": $("#DCSNY").val(),
		"CHZSFZH": $("#CHZSFZH").val(),
		"CCSD": $("#CCSD").val(),
		"CHZMZ": $("#CHZMZ").val(),
		"CHZSJ": $("#CHZSJ").val(),
		"CLXRXM": $("#CLXRXM").val(),
		"CLXRDH": $("#CLXRDH").val(),
		"CHZSG": $("#CHZSG").val(),
		"CHZTZ": $("#CHZTZ").val(),
		"IXXBM": $("#IXXBM").val(),
		"IRH": radioGetValue("YX"),
		"CWHCDBM": $("#CWHCDBM").val(),
		"CZYBM": $("#CZYBM").val(),
		"CYWGMS": checkboxGetValue("YWGMS"),
		"CHYZTBM": $("#CHYZTBM").val(),
		"CWHCDMC": $("#CWHCDBM").find("option:selected").text(),
		"CHYZTMC": $("#CHYZTBM").find("option:selected").text(),
		"CZYMC": $("#CZYBM").find("option:selected").text(),
		"IXXMC": $("#IXXBM").find("option:selected").text()
	};
});

function checkReverse(a, b) {
	$(a).change(function() {
		if($(this).is(":checked")) {
			$(b).prop("checked", false);
		}
	});
	$(b).change(function() {
		if($(this).is(":checked")) {
			$(a).prop("checked", false);
		}
	});
}

checkReverse("#diseaseNone", ".YWGMS");