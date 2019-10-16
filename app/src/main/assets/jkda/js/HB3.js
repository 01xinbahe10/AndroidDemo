/**
 * Created by CDCT on 2017/11/14.
 */

window.index = 3;

function page03() {
	var obj = {
        "CFQJZB":checkboxGetValue("father"),
        "CMQJZB":checkboxGetValue("mather"),
        "CXDJZB":checkboxGetValue("XDJM"),
        "CZNJZB":checkboxGetValue("ZL"),
        "CYCBS":radioGetValue("YCBS"),
        "IJKZK":radioGetValue("JKZK"),
        "ITZZT":radioGetValue("ITZZT"),
        "IPLZT":radioGetValue("IPLZT"),
        "IGJTT":radioGetValue("IGJTT"),
        "CMXBS":checkboxGetValue("MXBS"),
        "CYYAH":checkboxGetValue("CYYAH"),
        "CYCBS":radioGetValue("YCBS"),
	}
	return obj;
}


$(".HB3maxBox .selectFamily .item").on("click",function(){
    var parent= $(this).parent();
    if(typeof this.show=="undefined"||this.show==false){
        this.show=true;
        parent.find(".selectBox").css({
            display:"block"
        });
        $(this).addClass("change");
    }else{
        this.show=false;
        parent.find(".selectBox").css({
            display:""
        });
        $(this).removeClass("change");
    }
});
$($(".HB3maxBox .selectFamily .item")[0]).trigger("click");
$("#btnAdd3").on("click",function(){
    var data= {
        "CFQJZB":checkboxGetValue("father"),
        "CMQJZB":checkboxGetValue("mather"),
        "CXDJZB":checkboxGetValue("XDJM"),
        "CZNJZB":checkboxGetValue("ZL"),
        "CYCBS":radioGetValue("YCBS"),
        "IJKZK":radioGetValue("JKZK"),
        "ITZZT":radioGetValue("ITZZT"),
        "IPLZT":radioGetValue("IPLZT"),
        "IGJTT":radioGetValue("IGJTT"),
        "CMXBS":checkboxGetValue("MXBS"),
        "CYYAH":checkboxGetValue("CYYAH"),
        "CYCBS":radioGetValue("YCBS"),
    };
    ajax({
        data:data,
        success:function(data){
            if(data.MSG.RES["RES.2"]==1){
                $.toolTipModel({
                	   icon:"../img/com/smilingFace.png",
                    txt:"保存成功",
                    timeEnd:function(){
                       window.location.href="HB4.html?timestamp=" + new Date().getTime();
                    }
                })
            }
         
        },
        method:"ArchiveInformationThree",
        className:"ArchiveInformation"
    })

});
// ajax({
//     success:function(data){
//         var Obj=getData(data);
//         console.log(Obj)
//         var arr=[
//             {
//                 row:"CFQJZB",
//                 name:"father",
//                 type:"checkbox"
//             },
//             {
//                 row:"CMQJZB",
//                 name:"mather",
//                 type:"checkbox"
//             },
//             {
//                 row:"CXDJZB",
//                 name:"XDJM",
//                 type:"checkbox"
//             },
//             {
//                 row:"CZNJZB",
//                 name:"ZL",
//                 type:"checkbox"
//             },
//             {
//                 row:"CJZYCBS",
//                 name:"YCBS",
//                 type:"radio"
//             },
//             {
//                 row:"IJKZK",
//                 name:"JKZK",
//                 type:"radio"
//             },
//             {
//                 row:"IPLZT",
//                 name:"ITZZT",
//                 type:"radio"
//             },
//             {
//                 row:"ITZZT",
//                 name:"IPLZT",
//                 type:"radio"
//             },
//             {
//                 row:"IGJTT",
//                 name:"IGJTT",
//                 type:"radio"
//             },
//             {
//                 row:"CMXBS",
//                 name:"MXBS",
//                 type:"checkbox"
//             },
//             {
//                 row:"CYYAH",
//                 name:"CYYAH",
//                 type:"checkbox"
//             },
//             {
//                 row:"CYCBS",
//                 name:"YCBS",
//                 type:"radio"
//             }
//         ];
//         if(typeof Obj.length!="undefined"){
//             for(var i = 0;i<Obj.length;i++){
//                 changeInfo(arr,Obj[i]);
//             }
//         }else{
//             changeInfo(arr,Obj);
//         }
//         console.log(Obj);
//     },
//     className:"ArchiveInformation",
//     method:"reArchiveInformation"
// });

function checkReverse(a,b) {
	$(a).change(function(){
		if($(this).is(":checked")){
			$(b).prop("checked", false);
		}
	});
	$(b).change(function(){
		if($(this).is(":checked")){
			$(a).prop("checked", false);
		}
	});
}
checkReverse(".diseaseNone",".father");
checkReverse(".diseaseNone1",".mather");
checkReverse(".diseaseNone2",".XDJM");
checkReverse(".diseaseNone3",".ZL");
checkReverse(".diseaseNone4",".MXBS");

