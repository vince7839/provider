layui.laydate.render({elem: '#expectDate', type: 'date',
    format: 'yyyy-MM-dd',isInitValue:true,value:new Date()});

layui.laydate.render({elem: '#startDate', type: 'date',
    format: 'yyyy-MM-dd',isInitValue:true,value:new Date()});

layui.form.verify({
    eServiceId:[/ALPS[0-9]{8}|MAUI_[0-9]{8}/,'请填写正确的eService Id'],
    mail:[/[0-9a-zA-Z_-]+@(sagereal\.com|mobiwire\.com\.hk)/,'请输入正确的邮箱地址']
})

function queryCallback(json) {
    console.log(json.count);
    layui.laypage.render({
        elem: 'page'
        ,count: json.count
        ,curr: json.page+1
        ,jump: function (obj){
            console.log("json page:"+json.page+" curr:"+obj.curr)
            if(json.page != obj.curr - 1) {//为了防止查询回调时循环调用
                refresh(obj.curr - 1);
            }
        }
    });

    $("#tbody").empty();
    $.each(json.data,function (index,item){
        // console.log(item);
        var color = item.state == 'close' ? 'green' : 'red';
        var pColor = item.priority == 1 ? 'green' : item.priority == 2 ? 'orange' : 'red';
        var priorityText = item.priority == 1 ? "普通" : item.priority==2 ? "较高":"很高";
        var row =
            "<tr onclick='issueQuery("+item.issueId+")'>"
            + "<td onclick='window.event.stopPropagation()' style='text-align: center'><a style='color: #0000FF;text-decoration: underline' target='_black' href='https://eservice.mediatek.com/eservice-portal/elastic_search?keyword="+item.eserviceId+"'>"+item.eserviceId+"</a></td>"
            + "<td style='text-align: center;color:"+pColor+"'>"+priorityText+"</td>"
            + "<td style='text-align: center'>"+item.proposer+"</td>"
            + "<td style='text-align: center;color:"+color+"'>"+item.state+"</td>"
            + "<td style='text-align: center'>"+item.modifyDate+"</td>"
            + "</tr>";
        $("#tbody").append(row);
    })
}

function issueQuery(id) {
    console.log(id);
    window.location.href="/provider/issue/"+id;
}

$("#form").submit(function () {
    $.get("/provider/issue/add", $("#form").serialize(), function (data) {
        console.log(data)
        if(data.code == "200"){
            alert("添加成功")
            layui.element.tabChange('mytab', 'tab1');
            window.location.reload();
        }else{
            alert("添加失败")
        }
    })
    return false;
})

$("#mailForm").submit(function () {
    $.get("/provider/issue/remind", $("#mailForm").serialize(), function (data) {
        console.log(data)
        if(data.code == "200"){
            if(data.data == 0){
                alert("没有发现超过24小时未更新的提问人，所以没有发送")
            }else{
                alert("已经给"+data.data+"位提问人发送提醒邮件")
            }
        }else{
            alert("密码错误！")
        }
    })
    return false;
})

function refresh(page) {
    var proposer = $("#condition_proposer").val();
    var state = $("#condition_state").val();
    var url = "/provider/issue/page/"+page+"?proposer="+proposer+"&state="+state;
    console.log(url);
    $.get(url, queryCallback);
}

$(window).bind('hashchange', function() {
    var hash = location.hash;
    console.log(hash.substring(1));
    layui.element.tabChange('mytab', hash.substring(1));
});

layui.element.on('tab(mytab)', function(){
    location.hash = this.getAttribute('lay-id');
});

$(document).ready(function () {
    var hash = location.hash;
    console.log(hash.substring(1));
    layui.element.tabChange('mytab', hash.substring(1));
    $('#condition_proposer').bind('input propertychange', function() {
        refresh(0);
    });
    $('#condition_state').on('change',function(){
        console.log('state change')
        refresh(0);
    })
    refresh(0);
})