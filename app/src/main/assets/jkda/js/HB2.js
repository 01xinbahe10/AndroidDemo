/**
 * Created by CDCT on 2017/11/15.
 */
window.index = 2;

function page02() {
    var obj = {
        CJBJWS: checkboxDisease("CJBJWS"), //疾病
        CSJJWS: radioGetValue("CSJJWS", 1), //手术
        CWSJWS: radioGetValue("CWSJWS", 1), //外伤
        CSXJWS: radioGetValue("CSXJWS", 1) //输血
    }
    return obj;
}
$(".disease").change(function() {
    var more = $(this).parent().siblings(".more")[0];
    if ($(this).is(":checked")) {
        $(more).show();
    } else {
        $(more).hide();
    }
});
//疾病返回值
function checkboxDisease(name) {
    var disease = $('[name="' + name + '"]');
    var str = "";
    for (var i = 0; i < disease.length; i++) {
        var Time = $(disease[i]).parent().siblings(".more").find("input").val();
        if (disease[i].checked) {
            if (disease[i].value == "0") {
                str = str + "," + disease[i].value;
            } else if (disease[i].value == "9" || disease[i].value == "11") {
                str = str + "," + disease[i].value + "_" + "" + "_" + Time;
            } else if (disease[i].value == "12") {
                str = str + "," + disease[i].value + "_" + Time + "_" + "";
            } else {
                str = str + "," + disease[i].value + "_" + Time;
            }
        }
    }
    return str.substring(1);
}
$("#diseaseNone").change(function() {
    if ($(this).is(":checked")) {
        $(".disease").each(function() {
            $(this).prop("checked", false);
            $(this).parent().parent().find(".more").hide();
        });
    }
});
$(".disease").change(function() {
    if ($(this).is(":checked")) {
        $("#diseaseNone").prop("checked", false);
    }
});
$("#saveInfo").on("click", function() {
    ajax({
        success: function(data) {
            if (data.MSG.RES["RES.2"] == 1) {
                $.toolTipModel({
                    icon: "../img/com/smilingFace.png",
                    txt: "保存成功",
                    timeEnd: function() {
                        window.location.href = "HB3.html?timestamp=" + new Date().getTime();
                    }
                })
            }
            console.log(checkboxDisease("CJBJWS"));
        },
        className: "ArchiveInformation",
        method: "ArchiveInformationTwo",
        data: {
            CJBJWS: checkboxDisease("CJBJWS"), //疾病
            CSJJWS: radioGetValue("CSJJWS", 1), //手术
            CWSJWS: radioGetValue("CWSJWS", 1), //外伤
            CSXJWS: radioGetValue("CSXJWS", 1) //输血
        }
    });
});
// ajax({
// 	success:function(data){
// 		var Obj=getData(data);
// 		var arr=[
// 			{
// 				row:"CSJJWS",
// 				name:"CSJJWS",
// 				type:"radio2"
// 			},
// 			{
// 				row:"CWSJWS",
// 				name:"CWSJWS",
// 				type:"radio2"
// 				type:"radio2"
// 			},
// 			{
// 				row:"CSXJWS",
// 				name:"CSXJWS",
// 				type:"radio2"
// 			}
// 		];
// 		if(typeof Obj.length!="undefined"){
// 			for(var i = 0;i<Obj.length;i++){
// 				changeInfo(arr,Obj[i]);
// 			}
// 		}else{
// 			changeInfo(arr,Obj);
// 		}
// 		console.log(Obj);
// 		var CJBJWS = Obj[0].CJBJWS.split(",");
// 		for(var i = 0; i < CJBJWS.length; i ++){
// 			var checkBoxV = $(":checkbox[value=" + CJBJWS[i].split("_")[0] +"]");
// 			var diseaseT = checkBoxV.parent().siblings(".more");
// 			checkBoxV.attr("checked", true);
// 			diseaseT.show(CJBJWS);
// 			if(CJBJWS[i].split("_")[0] == "9" || CJBJWS[i].split("_")[0] == "11"){
// 				diseaseT.find(".choiceTime").val(CJBJWS[i].split("_")[2]);
// 			}else if(CJBJWS[i].split("_")[0] == "12"){
// 				diseaseT.find(".choiceTime").val(CJBJWS[i].split("_")[1]);
// 			}else{
// 				diseaseT.find(".choiceTime").val(CJBJWS[i].split("_")[1]);
// 			}
// 		}
// 	},
// 	className:"ArchiveInformation",
// 	method:"reArchiveInformation"
// });
(function() {
    var info = null;
    var info2 = null;
    var ele = null;
    $(document).on("touchstart", function(e) {
        var touchListNow = e.touches;
        var touchList = e.changedTouches;
        ele = typeof e.srcElement == "undefined" ? e.target : e.srcElement;
        if (touchListNow.length == 1) {
            info = {
                x: touchListNow[0].clientX,
                y: touchListNow[0].clientY
            }
        }
    });
    $(document).on("touchend", function(e) {
        var touchList = e.changedTouches;
        if (touchList.length == 1) {
            info2 = {
                x: touchList[0].clientX,
                y: touchList[0].clientY
            }
            if (Math.abs(info.x - info2.x) < 5 && Math.abs(info.x - info2.x) < 5) {
                $(ele).trigger("myTouch");
            }
        }
    });
})();
(function() {
    var allName = ["CSJJWS", "CWSJWS", "CSXJWS"];
    var child = $("<div class='childInfo'><input class='nameA' placeholder='请输入名称' type='text' /><input class='dataA' type='date' > <span class='add'></span></div>");
    var child02 = $("<div class='childInfo'><input class='nameA' placeholder='请输入名称' type='text' /><input class='dataA' type='date' > <span class='cancel'></span></div>");
    $(document).on("myTouch", ".childInfo .add", function() {
        $(this.parentNode.parentNode).append(child02.clone());
    });
    $(document).on("myTouch", ".childInfo .cancel", function() {
        $(this.parentNode).remove();
    });
    $.each(allName, function() {
        var THIS = this;
        $("[name='" + this + "']").on("change", function() {
            if (this.value == "1" && this.checked) {
                if (!this.appEle) {
                    this.appEle = $("<div class='timeAndName'></div>");
                    this.appEle.append(child.clone());
                }
                $(this.parentNode.parentNode).append(this.appEle);
            } else {
                var all = $("[name='" + THIS + "']");
                for (var i = 0; i < all.length; i++) {
                    if (all[i].value == "1") {
                        if (all[i].appEle) {
                            all[i].appEle.remove()
                        }
                    }
                }
            }
        })
    })
})();