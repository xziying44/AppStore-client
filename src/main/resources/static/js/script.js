const User = {
    login : function () {
        let account = $("#login-account").val();
        let password = $("#login-password").val();
        let load = layer.load(1, {
            shade: [0.3,'#fff'] //0.1透明度的白色背景
        });
        $.post("user/login", {
            account : account,
            password : password
        }, function (data) {
            const json = $.parseJSON(data)
            if (json.code === 0){
                // 登录成功跳转
                $.get("user/initialization", function () {
                    location.href = "plugin.html"
                })

            } else {
                layer.close(load)
                alert(json.msg)
            }
        })
    }
}

$(function () {
    // 启动完成时
    $("#login").click(User.login)
})