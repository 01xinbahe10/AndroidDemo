var info = {
    "msg": "查询成功",
    "code": "200",
    "data": {
        "total": 6,
        "list": "[{\"NAME\": \"未完成\", \"VALUE\": 1000 }, {\"NAME\": \"已完成\", \"VALUE\": 20 }, {\"NAME\": \"超时完成\", \"VALUE\": 0 }, {\"NAME\": \"已取消\", \"VALUE\": 0 } ]"
    }
};
var info1 = "[{\"NAME\": \"未完成\", \"VALUE\": 0 }, {\"NAME\": \"已完成\", \"VALUE\": 0 }, {\"NAME\": \"超时完成\", \"VALUE\": 0 }, {\"NAME\": \"已取消\", \"VALUE\": 0 } ]";
function healthscheme (itemstr) {
    if(itemstr == null || itemstr.length == 0 ||  itemstr == undefined) {
//        itemstr = info.data.list;
          itemstr = info1;
    }

    var items = JSON.parse(itemstr);

    var kitems = [];
    var style = [{color:'#E9DDA3'},
                 {color:'#B1D5A7'},
                 {color:'#97B5D9'},
                 {color:'#FFA0A0'},
                 {color:'#62D7EC'},];
    
    items.forEach(function (item,index) {
                  var kitem = {};
                  kitem.name = item['NAME'];
                  kitem.value = item['VALUE'];
                  kitem.itemStyle = style[index];
                  kitems.push(kitem);
                  });
    var vue = new Vue({
      el:'#app',
      data:{
        list:'',        
      }
    });
    vue.list = kitems;

    setEcharts(kitems);
}

function setEcharts(list) {
    var names = [];
    list.forEach( function(element, index) {
        names.push(element.name);
    });


    var pie = echarts.init($('#pie')[0]);
    var option = {
        legend: {
            right : 'center',
            width:'100%',
            orient: 'horizontal',
            bottom: 0,
            itemWidth:14,
            itemHeight:14,
            itemGap:25,
            data:names,
        },
        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        series : [
            {
                name: '访问来源',
                type: 'pie',
                radius : '55%',
                center: ['50%', '40%'],
                label:{normal:{formatter:'{d}%'}},
                data:list,
                itemStyle: {
                    emphasis: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    };
    pie.setOption(option);
    setProcessView();
}

function setProcessView () {
    var p1 = new Progress({
        el:'canvas',
        deg:70, //进度
        timer:3,
        lineWidth:10,
        lineBgColor:'#f1f1f1',
        lineColor:'#B1D5A7',
        textColor:'#000',
        fontSize:30,
        circleRadius:100,
    });
}