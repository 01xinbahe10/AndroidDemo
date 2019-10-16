/**
 * Created by CDCT on 2017/11/14.
 */

window.index = 5;

function page05() {
	var obj = {
		"ISFBDXY":radioGetValue("ISFBDXY"),
        "ISFXY":radioGetValue("ISFXY"),
        "IXYSL":radioGetValue("IXYSL"),
        "ISFXJY":radioGetValue("ISFXJY"),
        "IXYSJ":radioGetValue("IXYSJ"),
        "IJCYJ":radioGetValue("IJCYJ"),
        "IYJZL":radioGetValue("IYJZL"),
        "IYJL":radioGetValue("IYJL"),
        "ISFXJJ":radioGetValue("ISFXJJ"),
        "ISFJLCP":radioGetValue("ISFJLCP"),
        "ISFSHCS":radioGetValue("ISFSHCS"),
        "ISFXQYY":radioGetValue("ISFXQYY"),
        "ISMZT":radioGetValue("ISMZT"),
        "ISMSJ":radioGetValue("ISMSJ"),
        "ISFAY":radioGetValue("ISFAY")
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

$("#addSave5").on("click",function(){
    var data= {
        "ISFBDXY":radioGetValue("ISFBDXY"),
        "ISFXY":radioGetValue("ISFXY"),
        "IXYSL":radioGetValue("IXYSL"),
        "ISFXJY":radioGetValue("ISFXJY"),
        "IXYSJ":radioGetValue("IXYSJ"),
        "IJCYJ":radioGetValue("IJCYJ"),
        "IYJZL":radioGetValue("IYJZL"),
        "IYJL":radioGetValue("IYJL"),
        "ISFXJJ":radioGetValue("ISFXJJ"),
        "ISFJLCP":radioGetValue("ISFJLCP"),
        "ISFSHCS":radioGetValue("ISFSHCS"),
        "ISFXQYY":radioGetValue("ISFXQYY"),
        "ISMZT":radioGetValue("ISMZT"),
        "ISMSJ":radioGetValue("ISMSJ"),
        "ISFAY":radioGetValue("ISFAY")
    };

    ajax({
        data:data,
        success:function(data){
            if(data.MSG.RES["RES.2"]==1){
                $.toolTipModel({
                    icon:"../img/com/smilingFace.png",
                    txt:"保存成功",
                    timeEnd:function(){
                        window.location.href="GRZX.html?timestamp=" + new Date().getTime();
                    }
                })
            }
            console.log(data);
        },
        method:"ArchiveInformationfive",
        className:"ArchiveInformation"
    })

});
// ajax({
//     success:function(data){
//         var Obj=getData(data);
//         var arr=[
//             {
//                 row:"ISFBDXY",
//                 name:"ISFBDXY",
//                 type:"radio"
//             },
//             {
//                 row:"ISFXY",
//                 name:"ISFXY",
//                 type:"radio"
//             },
//             {
//                 row:"IXYSL",
//                 name:"IXYSL",
//                 type:"radio"
//             },
//             {
//                 row:"IXYSJ",
//                 name:"IXYSJ",
//                 type:"radio"
//             },
//             {
//                 row:"ISFXJY",
//                 name:"ISFXJY",
//                 type:"radio"
//             },
//             {
//                 row:"IJCYJ",
//                 name:"IJCYJ",
//                 type:"radio"
//             },
//             {
//                 row:"IYJZL",
//                 name:"IYJZL",
//                 type:"radio"
//             },
//             {
//                 row:"IYJL",
//                 name:"IYJL",
//                 type:"radio"
//             },
//             {
//                 row:"ISFXJJ",
//                 name:"ISFXJJ",
//                 type:"radio"
//             },
//             {
//                 row:"ISFJLCP",
//                 name:"ISFJLCP",
//                 type:"radio"
//             },
//             {
//                 row:"ISFSHCS",
//                 name:"ISFSHCS",
//                 type:"radio"
//             },
//             {
//                 row:"ISFXQYY",
//                 name:"ISFXQYY",
//                 type:"radio"
//             },
//             {
//                 row:"ISMZT",
//                 name:"ISMZT",
//                 type:"radio"
//             },
//             {
//                 row:"ISMSJ",
//                 name:"ISMSJ",
//                 type:"radio"
//             },
//             {
//                 row:"ISFAY",
//                 name:"ISFAY",
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
