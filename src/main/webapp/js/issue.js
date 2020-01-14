
function initSelect(priority,state,projectType){
    console.log("init:"+priority+" "+state)
    $("#priority").val(priority);
    $("#state").val(state);
    $("#projectType").val(projectType);
    layui.form.render('select');
}

layui.form.verify({
    eServiceId:[/ALPS[0-9]{8}|MAUI_[0-9]{8}/,'请填写正确的eService Id'],
    mail:[/[0-9a-zA-Z_-]+@(sagereal\.com|mobiwire\.com\.hk)/,'请输入正确的邮箱地址']
})

layui.laydate.render({elem: '#expectDate', type: 'date',
    format: 'yyyy-MM-dd'});

$("#form").submit(function () {
    console.log("form submit");
    $.post("/provider/issue/update", $("#form").serialize(), function (data) {
        console.log(data)
        if(data.code == "200"){
            alert("修改成功")
            window.location.href="/provider/push#tab1";
        }else{
            alert("修改失败")
        }
    })
    return false;
})

function deleteIssue(issueId) {
    console.log("delete click:"+issueId);
    $.get("/provider/issue/delete/"+issueId,function (data) {
        console.log(data)
        if(data.code == "200"){
            alert("删除成功")
            window.location.href="/provider/push#tab1";
        }else{
            alert("删除失败")
        }
    })
}