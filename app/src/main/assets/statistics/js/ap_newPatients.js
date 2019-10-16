//var info = {
//    "msg": "查询成功",
//    "code": "200",
//    "data": {
//        "total": 6,
//        "list": [
//                 {
//                 "name": "未完成",
//                 "count": 0
//                 },
//                 {
//                 "name": "已完成",
//                 "count": 0
//                 },
//                 {
//                 "name": "超时完成",
//                 "count": 0
//                 },
//                 {
//                 "name": "已取消",
//                 "count": 0
//                 }
//                 ]
//    }
//};

//setTimeout(newPatients, 1000);
var info1 =  "[{\"name\": \"未完成\", \"count\": 0 }, {\"name\": \"已完成\", \"count\": 0 }, {\"name\": \"超时完成\", \"count\": 0 }, {\"name\": \"已取消\", \"count\": 0 } ]";

function newPatients (itemstr) {
    if(itemstr == null || itemstr.length == 0 || itemstr == undefined) {
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
                  kitem.name = item['name'];
                  kitem.value = item['count'];
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
  }