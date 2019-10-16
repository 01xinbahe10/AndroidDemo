var info = {
    "msg": "查询成功",
    "code": "200",
    "data": {
        "total": 6,
        "list": [
                 {
                 "NAME": "未完成",
                 "VALUE": 2
                 },
                 {
                 "NAME": "已完成",
                 "VALUE": 1
                 },
                 {
                 "NAME": "超时完成",
                 "VALUE": 2
                 },
                 {
                 "NAME": "已取消",
                 "VALUE": 1
                 }
                 ]
    }
};

function warning (itemstr) {
    var json = JSON.stringify(itemstr);
    var items = JSON.parse(json);
    var kitems = [];
    let style = [{color:'#E9DDA3'},
                 {color:'#B1D5A7'},
                 {color:'#97B5D9'},];
    items.forEach(function (item,index) {
                  var kitem = {};
                  kitem.name = item['NAME'];
                  kitem.value = item.VALUE;
                  kitem.itemStyle = style[index];
                  kitems.push(kitem);
                  });
    
    window.wa = kitems;
}

function newPatients (itemstr) {
    var json = JSON.stringify(itemstr);
    var items = JSON.parse(json);

    var kitems = [];
    let style = [{color:'#E9DDA3'},
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
    window.np = kitems;
}

function workplan (itemstr) {
    alert(itemstr);

    return;

    var json = JSON.stringify(itemstr);
    var items = JSON.parse(json);
    var kitems = [];
    let style = [{color:'#E9DDA3'},
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
    window.wp = kitems;
}

function healthscheme (itemstr) {
    var json = JSON.stringify(itemstr);
    var items = JSON.parse(json);
    var kitems = [];
    let style = [{color:'#E9DDA3'},
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
    window.hs = kitems;
}

function test(str) {
    var json = JSON.stringify(str);
    alert(json);
}
